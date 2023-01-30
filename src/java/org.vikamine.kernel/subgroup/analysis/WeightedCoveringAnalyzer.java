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
 * Created on 29.06.2005
 *
 */
package org.vikamine.kernel.subgroup.analysis;

import java.util.Iterator;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.target.BooleanTarget;

/**
 * 
 * {@link WeightedCoveringAnalyzer} implements weighted covering, iteratively
 * taking the best subgroup as a start and reducing the each weight of a data
 * record accordingly to the number of subgroups it is covered by.
 * 
 * @author atzmueller
 * 
 */
public class WeightedCoveringAnalyzer {

    public WeightedCoveringAnalyzer() {
	super();
    }

    private void resetWeights(DataView population) {
	for (Iterator<DataRecord> iter = population.instanceIterator(); iter
		.hasNext();) {
	    DataRecord instance = iter.next();
	    instance.setWeight(1.0);
	}
    }

    private SG getBestCoveringSubgroup(SGSet sgSet, IQualityFunction qf) {
	double bestQuality = Double.NEGATIVE_INFINITY;
	SG bestSG = null;
	for (Iterator iter = sgSet.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    // SGStatistics stats = SGStatisticsBuilder
	    // .buildSGStatisticsWithCalculation(sg, sg.getStatistics()
	    // .getSConstraints());
	    double q = qf.evaluate(sg);
	    if (q > bestQuality) {
		bestSG = sg;
		bestQuality = q;
	    }
	}
	return bestSG;
    }

    private void adjustPopulationWeights(SG sg, DataView population) {
	for (Iterator<DataRecord> instanceIterator = population
		.instanceIterator(); instanceIterator.hasNext();) {
	    DataRecord instance = instanceIterator.next();
	    if (sg.getSGDescription().isMatching(instance)) {
		if (((BooleanTarget) sg.getTarget()).isPositive(instance)) {
		    instance.setWeight(1 / (1 + 1 / instance.getWeight()));
		}
	    }
	}
    }

    public SGSet getKBestCoveringSubgroups(int k, SGSet sgSet,
	    DataView population, IQualityFunction qf) {
	resetWeights(population);
	SGSet workingSet = SGSets.copySGSet(sgSet);
	SGSet result = SGSets.createSGSet();

	for (int i = 0; i < k; i++) {
	    SG sg = getBestCoveringSubgroup(workingSet, qf);
	    if (sg == null)
		break;
	    workingSet.remove(sg);
	    result.add(sg);
	    adjustPopulationWeights(sg, population);
	}

	resetWeights(population);
	return result;
    }
}
