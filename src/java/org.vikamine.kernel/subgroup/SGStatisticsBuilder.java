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
package org.vikamine.kernel.subgroup;

import org.vikamine.kernel.subgroup.target.NumericTarget;

/**
 * Builder for {@link SGStatistics}s: Binary, Numeric with/without recalculation
 * on the dataset.
 * 
 * @author mat
 * 
 */
public class SGStatisticsBuilder {

    public static SGStatistics buildSGStatisticsWithCalculation(SG subgroup,
	    Options constraints) {
	if (subgroup.getTarget() instanceof NumericTarget) {
	    return new SGStatisticsNumeric(subgroup, constraints);
	} else {
	    return new SGStatisticsBinary(subgroup, constraints);
	}
    }

    public static SGStatistics buildSGStatisticsWithoutCalculation(SG subgroup) {
	if (subgroup.getTarget() instanceof NumericTarget) {
	    return new SGStatisticsNumeric(subgroup);
	} else {
	    return new SGStatisticsBinary(subgroup);
	}
    }

    public static SGStatisticsBinary createFixedSGStatisticsBinaryWithNullConstraints(
	    SG subgroup, double definedPopulationCount,
	    double undefinedPopulationCount, double positives, double tp,
	    double subgroupSize) {
	if (!subgroup.getTarget().isBoolean()) {
	    throw new IllegalArgumentException(
		    "Not applicable for subgroups with numeric targets");
	}

	SGStatisticsBinary stats = (SGStatisticsBinary) buildSGStatisticsWithoutCalculation(subgroup);
	stats.definedPopulationCount = definedPopulationCount;
	stats.undefinedPopulationCount = undefinedPopulationCount;
	stats.positives = positives;
	stats.negatives = definedPopulationCount - positives;
	stats.subgroupSize = subgroupSize;
	stats.tp = tp;
	stats.fp = subgroupSize - tp;
	stats.options = null;

	subgroup.setStatistics(stats);
	return stats;
    }

    public static SGStatisticsNumeric createSGStatisticsNumeric(SG subgroup,
	    double sgSumOfValues, double popMean, double sgSize, double popSize) {
	SGStatisticsNumeric stats = new SGStatisticsNumeric(subgroup);
	stats.sumOfTargetValuesSG = sgSumOfValues;
	if (sgSize == 0) {
	    stats.sgMean = 0;
	} else {
	    stats.sgMean = sgSumOfValues / sgSize;
	}
	stats.populationMean = popMean;
	stats.subgroupSize = sgSize;
	stats.definedPopulationCount = popSize;

	subgroup.setStatistics(stats);
	return stats;
    }

    public static SGStatisticsNumeric createSGStatisticsNumericStdDev(
	    SG subgroup, double sgSumOfValues, double sgSumOfValueSquares,
	    double popMean, double sgSize, double popSize) {
	SGStatisticsNumeric stats = new SGStatisticsNumeric(subgroup);
	stats.sumOfTargetValuesSG = sgSumOfValues;
	if (sgSize == 0) {
	    stats.sgMean = 0;
	} else {
	    stats.sgMean = sgSumOfValues / sgSize;
	}
	stats.populationMean = popMean;
	stats.subgroupSize = sgSize;
	stats.definedPopulationCount = popSize;

	double variance = (sgSumOfValueSquares / sgSize)
		- ((sgSumOfValues / sgSize) * (sgSumOfValues / sgSize));

	stats.sgVariance = variance;
	stats.sgStdDeviation = Math.sqrt(variance);
	subgroup.setStatistics(stats);
	return stats;
    }

    public static SGStatistics createSGStatistics(SG subgroup, double tp,
	    double fp, double pos, double neg, double totalPopulationSize,
	    int descriptionLength, Options options) {
	if (!subgroup.getTarget().isBoolean()) {
	    throw new IllegalArgumentException(
		    "Not applicable for subgroups with numeric targets");
	}

	SGStatistics stats = buildSGStatisticsWithoutCalculation(subgroup);
	stats.options = options;
	stats.descriptionLength = descriptionLength;
	stats.definedPopulationCount = pos + neg;
	stats.undefinedPopulationCount = totalPopulationSize
		- stats.definedPopulationCount;

	if (subgroup.getTarget().isBoolean()) {
	    SGStatisticsBinary binStats = (SGStatisticsBinary) stats;
	    binStats.positives = pos;
	    binStats.negatives = neg;
	    stats.subgroupSize = tp + fp;
	    binStats.tp = tp;
	    binStats.fp = fp;
	}
	subgroup.setStatistics(stats);
	return stats;
    }

    public static void updateStatistics(SG subgroup, SGStatistics statistics) {
	subgroup.setStatistics(statistics);
    }
}
