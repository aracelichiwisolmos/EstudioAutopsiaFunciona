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

/*
 * Created on 24.03.2004
 * 
 */
package org.vikamine.kernel.subgroup;

import java.util.Collections;
import java.util.Iterator;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Implementation of {@link SGStatistics} for binary {@link SGTarget}s.
 * 
 * @author atzmueller
 */
public class SGStatisticsBinary extends SGStatistics {

    protected double positives;

    protected double negatives;

    // subgroup
    protected double tp;

    protected double fp;

    protected SGStatisticsBinary(SG subgroup) {
	super(subgroup);
    }

    @Override
    public double getTargetQuantitySG() {
	return getP();
    }

    @Override
    public double getTargetQuantityPopulation() {
	return getP0();
    }

    protected SGStatisticsBinary(SG subgroup, Options options) {
	super(subgroup, options);
    }

    @Override
    protected void calculateStatistics() {
	descriptionLength = subgroup.getSGDescription().size();

	for (Iterator<DataRecord> iter = subgroup.getPopulation()
		.instanceIterator(); iter.hasNext();) {
	    DataRecord instance = iter.next();

	    if (isInstanceDefinedForSubgroupVars(instance)) {
		definedPopulationCount++;
		SGDescription description = subgroup.getSGDescription();
		SGTarget target = subgroup.getTarget();

		if (target instanceof BooleanTarget) {
		    // target if fulfilled
		    if (((BooleanTarget) target).isPositive(instance)) {
			positives++;
			if (description.isMatching(instance)) {
			    tp += instance.getWeight();
			    subgroupSize += instance.getWeight();
			}

			// target is not fulfilled
		    } else {
			negatives++;
			if (description.isMatching(instance)) {
			    fp += instance.getWeight();
			    subgroupSize += instance.getWeight();
			}
		    }
		} else {
		    if (description.isMatching(instance)) {
			subgroupSize += instance.getWeight();
		    }
		}
	    } else {
		undefinedPopulationCount++;
	    }
	}
    }

    @Override
    public boolean isTargetDefinedInInstance(SGTarget target,
	    DataRecord instance) {
	if (treatMissingAsDefinedValueForAttributes(target.getAttributes())) {
	    return true;
	} else {
	    for (Attribute att : target.getAttributes()) {
		Value missing = Value.missingValue(att);
		if (missing.isValueContainedInInstance(instance)) {
		    return false;
		}
	    }
	    return true;
	}
    }

    @Override
    public boolean isSGSelectorSetDefinedInInstance(DataRecord instance) {
	for (Iterator iter = subgroup.getSGDescription().iterator(); iter
		.hasNext();) {
	    SGSelector sel = (SGSelector) iter.next();
	    Attribute att = sel.getAttribute();
	    if (!isAttributeDefinedInInstance(instance, att)) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public boolean isAttributeDefinedInInstance(DataRecord instance,
	    Attribute attribute) {
	return (!instance.isMissing(attribute) || treatMissingAsDefinedValueForAttributes(Collections
		.singletonList(attribute)));
    }

    @Override
    public boolean isInstanceDefinedForSubgroupVars(DataRecord instance) {
	if ((subgroup.getTarget() != null)
		&& (!isTargetDefinedInInstance(subgroup.getTarget(), instance))) {
	    return false;
	} else if (!isSGSelectorSetDefinedInInstance(instance)) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * @return the default target share
     */
    public double getP0() {
	return getPositives() / getDefinedPopulationCount();
    }

    /**
     * @return the target share in the subgroup
     */
    public double getP() {
	return tp / getSubgroupSize();
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the fp.
     */
    public double getFp() {
	return fp;
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the fn.
     */
    public double getFn() {
	return getPositives() - getTp();
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the tn.
     */
    public double getTn() {
	return getNegatives() - getFp();
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the negatives.
     */
    public double getNegatives() {
	return negatives;
    }

    @Override
    public double getDeviation() {
	return getP() - getP0();
    }

    /**
     * Returns the positive instances of the total population (which have
     * defined values in this subgroup). Usually (using no covering or instance
     * weights != 1.0) the value can just be cast to int.
     * 
     * @return Returns the positives.
     */
    public double getPositives() {
	return positives;
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the tp.
     */
    public double getTp() {
	return tp;
    }

    public double getSubgroupShareOfPositives() {
	return getTp() / (getTp() + getFn());
    }

    @Override
    public double getSubgroupShareOfPopulation() {
	return (getTp() + getFp()) / getDefinedPopulationCount();
    }

    @Override
    public String toString() {
	return "SGStatistics for ("
		+ SG.verbalizeSGAsPrologLikeRule(getSubgroup()) + ")\n"
		+ "DataView: " + definedPopulationCount + "("
		+ undefinedPopulationCount + ")\n" + "Positives: " + positives
		+ "\n" + "Negatives: " + negatives + "\n" + "P0: " + getP0()
		+ "\n" + "SubgroupSize: " + subgroupSize + "\n" + "TP: " + tp
		+ "\n" + "FP: " + fp + "\n" + "P: " + getP();
    }

    @Override
    public boolean isBinary() {
	return true;
    }
}
