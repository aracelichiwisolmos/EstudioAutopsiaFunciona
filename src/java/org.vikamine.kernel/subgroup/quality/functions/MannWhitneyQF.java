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

public class MannWhitneyQF extends AbstractRankedBasedQF {

    private static final String ID = "Mann-Whitney QF";

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return ID;
    }

    @Override
    public double evaluateRanks(int dataViewSize, List<Double> ranksSG) {
	int sgSize = ranksSG.size();
	double rankSum = 0;
	for (Double i : ranksSG) {
	    rankSum += i;
	}
	return evaluateRanks(dataViewSize, sgSize, rankSum);
    }

    public double evaluateRanks(int dataViewSize, int sgSize, double rankSum) {
	int complementSize = dataViewSize - sgSize;

	return Math.sqrt((double) sgSize / (double) complementSize)
		* ((rankSum / sgSize) - (dataViewSize + 1) / 2.0);
    }

    @Override
    public MannWhitneyQF clone() {
	return new MannWhitneyQF();
    }

}
