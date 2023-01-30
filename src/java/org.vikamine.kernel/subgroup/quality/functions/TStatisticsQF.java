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
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.quality.AbstractQFStatisticBased;
import org.vikamine.kernel.subgroup.quality.ExtendedNumericStatisticBasedQF;

public class TStatisticsQF extends AbstractQFStatisticBased implements
	ExtendedNumericStatisticBasedQF {

    private static final String ID = "T-Statistics";

    @Override
    public double evaluate(SGStatistics statistics) {
	if (!(statistics instanceof SGStatisticsNumeric)) {
	    return Double.NaN;
	}
	SGStatisticsNumeric numStats = (SGStatisticsNumeric) statistics;

	double n = numStats.getSubgroupSize();
	double m = numStats.getSGMean();
	double m_0 = numStats.getPopulationMean();
	double s_0 = numStats.getSgStdDeviation();
	return evaluate(n, m, m_0, s_0);
    }

    @Override
    public double evaluate(double sgSize, double meanSG, double meanPop,
	    double sdSG) {
	if (sgSize < 2) {
	    return 0;
	}
	return (Math.sqrt(sgSize) * (meanSG - meanPop)) / sdSG;
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
    public AbstractQFStatisticBased clone() {
	return new TStatisticsQF();
    }
}
