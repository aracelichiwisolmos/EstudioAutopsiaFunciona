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

package org.vikamine.kernel.subgroup.quality;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.ISubgroup;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.target.NumericTarget;


public abstract class AbstractRankedBasedQF implements IQualityFunction {

    @Override
    public boolean isApplicable(ISubgroup<DataRecord> subgroup) {
	if (!(subgroup instanceof SG)) {
	    return false;
	}
	SG sg = (SG) subgroup;
	return sg.getTarget() != null && sg.getTarget().isNumeric();
    }

    @Override
    public double evaluate(ISubgroup<DataRecord> subgroup) {
	// works, if applicable
	SG sg = (SG) subgroup;
	if (sg.getStatistics() == null) {
	    sg.createStatistics();
	}

	DataView dataView = sg.getPopulation();

	final NumericTarget target = (NumericTarget) sg.getTarget();

	List<DataRecord> drs = new ArrayList<DataRecord>();
	for (DataRecord dr : dataView) {
	    if (!Value.isMissingValue(target.getValue(dr))) {
		drs.add(dr);
	    }
	}
	Collections.sort(drs, new Comparator<DataRecord>() {
	    @Override
	    public int compare(DataRecord o1, DataRecord o2) {
		return -Double.compare(target.getValue(o1), target.getValue(o2));
	    }
	});

	double[] ranks = createRanks(drs, target);

	List<Double> ranksSG = new ArrayList<Double>();
	int i = 0;
	for (DataRecord dr : drs) {
	    if (sg.getSGDescription().isMatching(dr)) {
		ranksSG.add(ranks[i]);
	    }
	    i++;
	}

	return evaluateRanks((int) sg.getStatistics()
		.getDefinedPopulationCount(), ranksSG);
    }

    public abstract double evaluateRanks(int size, List<Double> ranksSG);

    protected double[] createRanks(List<DataRecord> drs, NumericTarget target) {
	double[] ranks = new double[drs.size()];

	int startOfCurrentGroup = 0;
	double lastValue = Double.MAX_VALUE;
	for (int i = 0; i < drs.size(); i++) {
	    double value = target.getValue(drs.get(i));
	    if (lastValue != value) {
		double rank = drs.size()
			- ((double) i - 1 + startOfCurrentGroup) / 2;
		for (int j = startOfCurrentGroup; j <= i - 1; j++) {
		    ranks[j] = rank;
		}
		startOfCurrentGroup = i;
	    }
	    lastValue = value;
	}
	// last Group
	double rank = drs.size()
		- ((double) drs.size() - 1 + startOfCurrentGroup) / 2;
	for (int j = startOfCurrentGroup; j <= (drs.size() - 1); j++) {
	    ranks[j] = rank;
	}
	return ranks;
    }

    @Override
    public abstract AbstractRankedBasedQF clone();

}
