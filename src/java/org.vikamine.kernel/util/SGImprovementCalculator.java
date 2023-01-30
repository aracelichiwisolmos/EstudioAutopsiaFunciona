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

package org.vikamine.kernel.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

/**
 * Calcualtes the improvement of a subgroup w.r.t. all generalized descriptions,
 * and the according P value.
 * 
 * @author lemmerich
 * 
 */
public class SGImprovementCalculator {

    private final Map<SGDescription, SG> cache;

    public SGImprovementCalculator() {
	cache = new HashMap<SGDescription, SG>();
    }

    public double calculateMinImprovement(SG sg) {
	double minImprovement = Double.MAX_VALUE;
	List<List<SGSelector>> subDescriptions = SubsetGenerator
		.generateSubset(sg.getSGDescription().getSelectors());
	for (List<SGSelector> selList : subDescriptions) {
	    // exclude this sg itself
	    if (selList.size() < sg.getSGDescription().size()) {
		SGDescription sgDescr = new SGDescription();
		sgDescr.addAll(selList);
		SG subSG = cache.get(sgDescr);
		if (subSG == null) {
		    subSG = new SG(sg.getPopulation(), sg.getTarget(), sgDescr);
		    subSG.createStatistics(null);
		    cache.put(sgDescr, subSG);
		}
		// sg has longer description, i.e., is more specialized
		double improvement = sg.getStatistics().getDeviation()
			- subSG.getStatistics().getDeviation();
		minImprovement = Math.min(minImprovement, improvement);
	    }
	}
	return minImprovement;
    }

    public double calculateMaxPValueToSubsets(SG sg) {
	if (sg.getTarget().isNumeric())
	    throw new IllegalArgumentException(
		    "Not applicable for numeric target " + sg.getTarget());

	double maxPValue = Double.MIN_VALUE;
	List<List<SGSelector>> subDescriptions = SubsetGenerator
		.generateSubset(sg.getSGDescription().getSelectors());
	for (List<SGSelector> selList : subDescriptions) {
	    // exclude this sg itself
	    if (selList.size() < sg.getSGDescription().size()) {
		SGDescription sgDescr = new SGDescription();
		sgDescr.addAll(selList);
		SG subSG = cache.get(sgDescr);
		if (subSG == null) {
		    subSG = new SG(sg.getPopulation(), sg.getTarget(), sgDescr);
		    subSG.createStatistics(null);
		    cache.put(sgDescr, subSG);
		}

		// sg has largest description, i.e. is most specialized
		SGStatisticsBinary sgStats = (SGStatisticsBinary) sg
			.getStatistics();
		SGStatisticsBinary sg2Stats = (SGStatisticsBinary) subSG
			.getStatistics();
		double a = sgStats.getTp();
		double b = sg2Stats.getTp() - a;
		double c = sgStats.getFp();
		double d = sg2Stats.getFp() - c;
		double significanceOfDifference = SGUtils
			.getChi2SignificanceNiveau(a, b, c, d);
		maxPValue = Math.max(maxPValue, significanceOfDifference);
	    }
	}
	return maxPValue;
    }

}
