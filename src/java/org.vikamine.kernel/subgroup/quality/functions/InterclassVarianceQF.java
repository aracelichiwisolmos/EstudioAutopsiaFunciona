/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2014 by Martin Atzmueller, Florian Lemmerich, and contributors.
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
public class InterclassVarianceQF extends AbstractQFStatisticBased {

    private static final String ID = "Interclass variance QF";

    public InterclassVarianceQF() {
	super();
    }

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return ID;
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return subgroup.getTarget().isNumeric();
    }

    @Override
    public double evaluate(SGStatistics statistics) {
	if (!(statistics instanceof SGStatisticsNumeric)) {
	    return Double.NaN;
	}
	SGStatisticsNumeric numStats = (SGStatisticsNumeric) statistics;

	double nSG = numStats.getSubgroupSize();
	double sumSG = numStats.getSumOfTargetValuesSG();
	double mSG = numStats.getSGMean();
	if (nSG == 0) {
	    return 0;
	}

	double nPop = numStats.getDefinedPopulationCount();
	double sumPop = numStats.getSumOfTargetValuesPopulation();
	double mPop = numStats.getPopulationMean();

	double nComplement = nPop - nSG;
	double sumComplement = sumPop - sumSG;
	if (nComplement == 0) {
	    return 0;
	}
	double meanComplement = sumComplement / nComplement;

	return evaluate(nSG, mSG, mPop, nComplement, meanComplement);
    }

    public double evaluate(double nSG, double mSG, double mPop,
	    double nComplement, double meanComplement) {
	return nSG * (mSG - mPop) * (mSG - mPop) + nComplement
		* (meanComplement - mPop) * (meanComplement - mPop);
    }

    @Override
    public AbstractQFStatisticBased clone() {
	return new InterclassVarianceQF();
    }
}
