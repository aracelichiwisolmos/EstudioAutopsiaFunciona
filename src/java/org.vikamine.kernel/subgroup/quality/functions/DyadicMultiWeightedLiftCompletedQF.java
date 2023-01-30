/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2021 by Martin Atzmueller, Florian Lemmerich, and contributors.
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

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.quality.AbstractQFStatisticBased;
import org.vikamine.kernel.util.VKMUtil;

/**
 * Implements the Dyadic Multigraph Weighted Lift quality function.
 * 
 * @author atzmueller
 * 
 */
public class DyadicMultiWeightedLiftCompletedQF extends AbstractQFStatisticBased {

    private static final String ID = "DyadicMultiWeightedLiftQF";
    private static final String NAME = "DyadicMultiWeightedLiftQF";

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public AbstractQFStatisticBased clone() {
	return new DyadicMultiWeightedLiftCompletedQF();
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return true;
    }

    @Override
    public double evaluate(SGStatistics statistics) {
	SG subgroup = statistics.getSubgroup();
	double weightedObservedDyads = statistics.getTargetQuantitySG() * statistics.getSubgroupSize();
	double expectedDyads = VKMUtil.asList(subgroup.subgroupInstanceIterator()).size();
	
	return Math.abs(weightedObservedDyads / expectedDyads);
    }
}
