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

package org.vikamine.kernel.subgroup.quality.functions;

import java.util.List;

import org.vikamine.kernel.subgroup.quality.AbstractRankedBasedQF;

public class AreaUnderCurveQF extends AbstractRankedBasedQF {

    private static final String ID = "Area-under-Curve QF";

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return ID;
    }

    @Override
    public double evaluateRanks(int definedPopulationCount,
	    List<Double> ranksSG) {
	long sgSize = ranksSG.size();
	double complementSize = definedPopulationCount - sgSize;

	double rankSum = 0;
	for (Double i : ranksSG) {
	    rankSum += i;
	}
	double rankSumAll = definedPopulationCount
		* (definedPopulationCount + 1) / 2;
	double rankSumComplement = rankSumAll - rankSum;

	return evaluateFromRankStatistics(sgSize, complementSize,
		rankSumComplement);
    }

    public double evaluateFromRankStatistics(long sgSize,
	    double complementSize, double rankSumComplement) {
	if (sgSize == 0 || complementSize == 0) {
	    return 0;
	}

	return (rankSumComplement - (complementSize * (complementSize + 1) / 2))
		/ (sgSize * complementSize);
    }

    @Override
    public AreaUnderCurveQF clone() {
	return new AreaUnderCurveQF();
    }
}
