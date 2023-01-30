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
 * The relative gain qf (order equivalent to Gain/Lift/AddedValue)
 * 
 * Can NOT be used for numeric targets.
 * 
 * @author lemmerich
 * @date 11/2011
 */
public class RelativeGainQF extends AbstractQFSimpleStatisticBased {

    private static final String ID = "RelativeGainQF";
    private static final String NAME = "Relative Gain";
    
    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    /**
     * This quality function is not applicable for numeric target!
     * @return Double.NaN;
     */
    public double evaluateNum(double sgMean, double subgroupSize,
	    double definedPopulationCount, double populationMean) {
	return Double.NaN;
    }

    @Override
    public AbstractQFSimpleStatisticBased clone() {
	return new RelativeGainQF();
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return (subgroup.getTarget() != null)
		&& (subgroup.getTarget().isBoolean());
    }

    @Override
    public double evaluateBin(double subgroupSize, double subgroupPositives,
	    double definedPopulationCount, double populationPositives) {
	double p = subgroupPositives / subgroupSize;
	double p0 = populationPositives / definedPopulationCount;

	if (p0 == 1.0) {
	    return (p == 1.0) ? 0 : Double.NEGATIVE_INFINITY;
	}
	return (p - p0) / ((1 - p0) * p0);
    }

}
