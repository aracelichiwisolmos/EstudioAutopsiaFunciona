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

package org.vikamine.kernel.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author lemmerich
 * @date 08/09
 * 
 *       This class is used to store important statistical charateristics from a
 *       Apache commons DescriptiveStatistics object without storing all the
 *       values.
 */
public class DescriptiveStatisticsDummy {

    private final double mean;

    private final double median;

    private final double min;

    private final double max;

    private final double std_var;

    private final double variance;

    private final double firstQuartil;

    private final double secondQuartil;

    public DescriptiveStatisticsDummy(DescriptiveStatistics stats) {
	mean = stats.getMean();
	median = stats.getPercentile(50);
	firstQuartil = stats.getPercentile(25);
	secondQuartil = stats.getPercentile(75);
	min = stats.getMin();
	max = stats.getMax();
	std_var = stats.getStandardDeviation();
	variance = stats.getVariance();
    }

    public double getFirstQuartil() {
	return firstQuartil;
    }

    public double getMax() {
	return max;
    }

    public double getMean() {
	return mean;
    }

    public double getMedian() {
	return median;
    }

    public double getMin() {
	return min;
    }

    public double getSecondQuartil() {
	return secondQuartil;
    }

    public double getStandardVariation() {
	return std_var;
    }

    public double getVariance() {
	return variance;
    }
}
