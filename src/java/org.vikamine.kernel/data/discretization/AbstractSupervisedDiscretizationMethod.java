/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2016 by Martin Atzmueller, Florian Lemmerich, and contributors.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.vikamine.kernel.data.discretization;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.search.InvalidTargetException;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Abstract base class for supervised discretization.
 * 
 * It provides an initializer for determining possible cut points which are
 * between different classes and different values.
 * 
 * Also it provides basic tests whether there is a boolean target set and
 * whether the population is big enough.
 * 
 * @author Hagen Schwa�
 * 
 */
public abstract class AbstractSupervisedDiscretizationMethod extends
	AbstractDiscretizationMethod {

    protected SGTarget target;

    public AbstractSupervisedDiscretizationMethod(final SGTarget target) {
	this.target = target;
    }

    public AbstractSupervisedDiscretizationMethod() {
    }

    @Override
    public void setTarget(SGTarget target) {
	this.target = target;
    }

    /**
     * Returns true, if discretization can proceed. If the attribute doesn't
     * exist or the population is too small, it returns false. If the target is
     * null or is not boolean, it throws an InvalidTargetException.
     * 
     * @return true, if no problems with the target, the population or the
     *         attribute to discretize occur.
     */
    boolean check() {

	if (target == null || !target.isBoolean()) {
	    throw new InvalidTargetException(target, getName()
		    + " can only use Boolean target!");
	}

	if ((population == null) || (attribute == null)
		|| (population.dataset().getIndex(attribute) < 0)
		|| (population.size() < 2)) {
	    return false;
	}

	return true;
    }

    /**
     * Abstract class for initializing instances into intervals of minimum size.
     * 
     * @author Hagen Schwa�
     * 
     */
    abstract class Initializer {

	private final BooleanTarget target;

	private int index;

	private DataRecord currentRecord;
	private DataRecord prevRecord;

	private int saveIndex;

	int sumNegatives;
	int sumPositives;

	private int saveSumPositives;
	private int saveSumNegatives;

	private boolean biClassBlock;

	private int count;

	Initializer(final BooleanTarget target) {
	    this.target = target;
	    index = 0;
	    saveIndex = -1;
	    nextRecord();
	    updateSum();
	    count = 1;
	}

	void initialize() {
	    while (hasNext()) {
		nextRecord();
		if (valueGrown()) {
		    if (classStayed() && biClassBlock == false) {
			savePrevRecord();
		    } else {
			createNewBlock();
			reset();
		    }
		} else {
		    if (classStayed() == false) {
			if (hasSavedRecord()) {
			    restoreSavedRecord();
			    createNewBlock();
			    reset();
			} else {
			    biClassBlock = true;
			}
		    }
		}
		updateSum();
	    }
	    finish();
	}

	int getCount() {
	    return count;
	}

	private void reset() {
	    biClassBlock = false;
	    saveIndex = -1;
	    count++;
	}

	abstract void finish();

	abstract void createNewBlock();

	private boolean hasNext() {
	    return index < sortedSample.size();
	}

	private void nextRecord() {
	    prevRecord = currentRecord;
	    currentRecord = sortedSample.get(index++);
	}

	double getValue() {
	    return currentRecord.getValue(attribute);
	}

	double getPrevValue() {
	    return prevRecord.getValue(attribute);
	}

	private boolean valueGrown() {
	    return currentRecord.getValue(attribute) > prevRecord
		    .getValue(attribute);
	}

	private boolean classStayed() {
	    return target.isPositive(currentRecord) == target
		    .isPositive(prevRecord);
	}

	private void savePrevRecord() {
	    saveIndex = index - 2;
	    saveSumNegatives = sumNegatives;
	    saveSumPositives = sumPositives;
	}

	private boolean hasSavedRecord() {
	    return saveIndex >= 0;
	}

	private void restoreSavedRecord() {
	    index = saveIndex;
	    sumNegatives = saveSumNegatives;
	    sumPositives = saveSumPositives;
	    currentRecord = sortedSample.get(index++);
	    nextRecord();
	}

	private void updateSum() {
	    if (target.isPositive(currentRecord)) {
		sumPositives++;
	    } else {
		sumNegatives++;
	    }
	}

	void resetSum() {
	    sumPositives = 0;
	    sumNegatives = 0;
	}
    }
}
