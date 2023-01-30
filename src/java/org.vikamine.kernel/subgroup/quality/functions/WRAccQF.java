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
 * 
 * @author atzmueller
 *
 */
public class WRAccQF extends AbstractQFSimpleStatisticBased {

    private static final String ID = "WRAccQF";
    private static final String NAME = "Weighted relative Accuracy";
    
    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public double evaluateNum(double subgroupSize, double subgroupMean,
	    double definedPopulationCount, double populationMean) {

	double subgroupQuality = (subgroupSize / definedPopulationCount)
		* (subgroupMean - populationMean);

	if (Double.isNaN(subgroupQuality)) {
	    subgroupQuality = Double.NEGATIVE_INFINITY;
	}
	return subgroupQuality;
    }

    @Override
    public double evaluateBin(double subgroupSize, double subgroupPositives,
	    double definedPopulationCount, double populationPositives) {	
	double p = subgroupPositives / subgroupSize;
	double p0 = populationPositives / definedPopulationCount;
	double subgroupQuality = (subgroupSize / definedPopulationCount)
		* (p - p0);

	if (Double.isNaN(subgroupQuality)) {
	    subgroupQuality = Double.NEGATIVE_INFINITY;
	}

	return subgroupQuality;
    }

    @Override
    public AbstractQFSimpleStatisticBased clone() {
	return new WRAccQF();
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return (subgroup.getTarget() != null) && (subgroup.getTarget().isBoolean());
    }

}
