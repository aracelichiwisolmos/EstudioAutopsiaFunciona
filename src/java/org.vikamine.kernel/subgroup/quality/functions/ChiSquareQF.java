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
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.quality.AbstractQFStatisticBased;

/**
 * 
 * @author lemmerich
 *
 */
public class ChiSquareQF extends AbstractQFStatisticBased {

    @Override
    public String getID() {
	return "ChiSquareQF";
    }
    
    @Override
    public String getName() {
	return "Chi-Square";
    }

    @Override
    public double evaluate(SGStatistics statistics) {
	if (statistics == null || !statistics.isBinary()) {
	    throw new IllegalArgumentException(
		    "Statistics for subgroup were computed before or are not binary!");
	}

	double value = SGUtils
		.calculateChi2OfSubgroup((SGStatisticsBinary) statistics);

	if (Double.isNaN(value)) {
	    value = Double.NEGATIVE_INFINITY;
	}
	return value;
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return (subgroup.getTarget() != null) && (subgroup.getTarget().isBoolean());
    }

    @Override
    public ChiSquareQF clone() {
	return new ChiSquareQF();
    }

}
