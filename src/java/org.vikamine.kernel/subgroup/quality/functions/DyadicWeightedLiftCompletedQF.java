/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2018 by Martin Atzmueller, Florian Lemmerich, and contributors.
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

import java.util.HashSet;
import java.util.Set;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.quality.AbstractQFStatisticBased;
import org.vikamine.kernel.util.VKMUtil;

/**
 * Implements the Dyadic Weighted Lift quality function.
 * 
 * @author atzmueller
 * 
 */
public class DyadicWeightedLiftCompletedQF extends AbstractQFStatisticBased {

    private static final String ID = "DyadicWeightedLiftQF";
    private static final String NAME = "DyadicWeightedLiftQF";

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
	return new DyadicWeightedLiftCompletedQF();
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return true;
    }

    @Override
    public double evaluate(SGStatistics statistics) {
	SG subgroup = statistics.getSubgroup();
	double weightedObservedDyads = statistics.getTargetQuantitySG() * statistics.getSubgroupSize();
	
	Set<String> nodes = new HashSet<String>();
	for (DataRecord record : subgroup) {
	    String valSrc = ((NominalAttribute) record.getDataset().getAttribute(0)).getNominalValue(record).getId();
	    String valDst = ((NominalAttribute) record.getDataset().getAttribute(1)).getNominalValue(record).getId();
	    nodes.add(valSrc);
	    nodes.add(valDst);
	}
	@SuppressWarnings("unused")
	int count = nodes.size();
	//System.out.println(count);
	double expectedDyads = VKMUtil.asList(subgroup.subgroupInstanceIterator()).size();

	return Math.abs(weightedObservedDyads / expectedDyads);
    }
}
