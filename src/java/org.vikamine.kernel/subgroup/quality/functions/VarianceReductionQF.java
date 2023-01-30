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

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.quality.AbstractQFSimpleStatisticBased;

/**
 * The most used class of quality measures for subgroup discovery:
 * 
 * n^a * (t - t_0)
 * 
 * Subclassed for various
 * 
 * @author lemmerich
 * 
 */
public class VarianceReductionQF extends AbstractQFSimpleStatisticBased {

    private static final String ID = "VarianceReductionQF";

    public VarianceReductionQF() {
	super();
    }

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return getID();
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return subgroup.getTarget().isNumeric();
    }

    @Override
    public double evaluateNum(double subgroupSize, double sgMean,
	    double definedPopulationCount, double populationMean) {
	if (subgroupSize == 0 || subgroupSize == definedPopulationCount) {
	    return 0;
	}
	
	double diff = sgMean - populationMean;
	double complementSize = definedPopulationCount - subgroupSize;
	return diff * diff * subgroupSize / complementSize;
    }

    @Override
    public VarianceReductionQF clone() {
	return new VarianceReductionQF();
    }

    @Override
    public double evaluateBin(double subgroupSize, double subgroupPositives,
	    double definedPopulationCount, double populationPositives) {
	return Double.NaN;
    }
}
