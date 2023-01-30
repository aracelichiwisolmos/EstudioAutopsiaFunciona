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

package org.vikamine.kernel.data.discretization;

import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * Discretizer applying the 3-4-5 rule, see Han and Kamber pp91 ff.
 * 
 * A recursion depth can be specified. A recursion depth of one means, the
 * entire range of values is partitioned by regarding the most significant
 * digit. A recursion depth of k means, that the subintervals are split
 * recursively down to the k. digit left of the most significant digit. The
 * default recursion depth is two.
 * 
 * @author Hagen Schwa�
 * 
 */
public class The345RuleDiscretizer extends AbstractDiscretizationMethod {

    private static final String NAME = "3-4-5 Rule Discretizer";

    private static final String REC_DEPTH = "rec-depth";

    private static final int DEF_RECDEPTH = 1;

    /**
     * Determine the number of digits left to the comma.
     * 
     * @param val
     *            A double value
     * @return The value's power of ten.
     */
    private static int power(final double val) {
	int res = (int) Math.floor(Math.log10(Math.abs(val)));
	return res;
    }

    /**
     * Returns the base of the most significant digit of an interval.
     * 
     * For [4 ; 300] it would be 100.
     * 
     * @param lowVal
     *            The lower boundary of the interval.
     * @param highVal
     *            The upper boundary of the interval.
     * @return The base of the most significant digit.
     */
    private static double sigDigitBase(final double lowVal, final double highVal) {
	double res = Math.pow(10, Math.max(power(highVal), power(lowVal)));
	return res == 0 ? 1 : res;
    }

    /**
     * Recursively discretize the subintervals.
     * 
     * Always keep track of the smallest and highest values.
     * 
     * The minVal value and the fivePercent value should not differ, when
     * calling recursively. If the minVal value is smaller than the lower
     * boundary l computed by the fivePercent value, there is an extra
     * discretization for the interval [minVal ; l]. Same is valid for maxVal
     * and ninetyFivePercent.
     * 
     * @param minVal
     *            The smallest value in the set of records
     * @param maxVal
     *            The highest value in the set of records
     * @param fivePercent
     *            5 percentile boundary
     * @param ninetyFivePercent
     *            95 percentile boundary
     * @param sigDigitBase
     *            The base of the most significant digit of the interval to
     *            discretize
     * @param cutPoints
     *            The list to add the cut points
     * @param recursions
     *            The number of recursions
     */
    private static void discretize(double minVal, double maxVal,
	    final double fivePercent, final double ninetyFivePercent,
	    double sigDigitBase, final List<Double> cutPoints, int recursions) {

	if (recursions == 0) {
	    cutPoints.add(maxVal);
	    return;
	}

	recursions--;

	final double lowerBoundaryDigit = Math
		.floor(fivePercent / sigDigitBase);
	final double upperBoundaryDigit = Math.ceil(ninetyFivePercent
		/ sigDigitBase);

	final double lowerBoundary = lowerBoundaryDigit * sigDigitBase;

	// extra discretization, if the minVal is smaller than the lower
	// boundary computed by the fivePercent value.
	if (minVal < lowerBoundary) {
	    discretize(minVal, lowerBoundary, minVal, lowerBoundary,
		    sigDigitBase(minVal, lowerBoundary), cutPoints, 1);
	    minVal = lowerBoundary;
	}

	final double upperBoundary = upperBoundaryDigit * sigDigitBase;

	// save informations here, if there needs to be an extra
	// discretization for the interval from the upper boundary to
	// maxVal
	boolean maxValPartition = false;
	double maxValSave = 0;
	if (maxVal > upperBoundary) {
	    maxValSave = maxVal;
	    maxVal = upperBoundary;
	    maxValPartition = true;
	}

	sigDigitBase /= 10;

	// apply the 3-4-5 rule here
	switch ((int) (upperBoundaryDigit - lowerBoundaryDigit)) {
	case 1:
	case 5:
	case 10:
	    equalWidthPartition(5, minVal, maxVal, lowerBoundary,
		    upperBoundary, sigDigitBase, cutPoints, recursions);
	    break;
	case 2:
	case 4:
	case 8:
	    equalWidthPartition(4, minVal, maxVal, lowerBoundary,
		    upperBoundary, sigDigitBase, cutPoints, recursions);
	    break;
	case 3:
	case 6:
	case 9:
	    equalWidthPartition(3, minVal, maxVal, lowerBoundary,
		    upperBoundary, sigDigitBase, cutPoints, recursions);
	    break;
	case 7:
	    twoThreeTwoPartition(minVal, maxVal, lowerBoundary, upperBoundary,
		    sigDigitBase, cutPoints, recursions);
	    break;
	case 0:
	    break;
	default:
	    discretize(minVal, maxVal, minVal, maxVal, sigDigitBase * 100,
		    cutPoints, recursions + 1);
	}

	// here goes the extra discretization if maxVal exceeds the upper
	// boundary
	if (maxValPartition) {
	    discretize(upperBoundary, maxValSave, upperBoundary, maxValSave,
		    sigDigitBase(upperBoundary, maxValSave), cutPoints, 1);
	}
    }

    /**
     * Create the equal width partition for the interval.
     * 
     * @param count
     *            Number of partitions for the interval.
     * @param minVal
     *            The smallest value of the interval. It can be equal to the
     *            lower boundary of the interval, or it can be higher
     * @param maxVal
     *            The highest value to the interval. Can be smaller than or
     *            equal to the upper boundary
     * @param lowerBoundary
     *            The lower boundary of the interval. This value is computed and
     *            significant for applying the 3-4-5 rule. It always an often of
     *            the base of the significant digit of the caller.
     * @param upperBoundary
     *            The upper boundary.
     * @param sigDigitBase
     *            The base of the significant digit for the subintervals
     * @param cutPoints
     *            The list to add the cut points
     * @param recursions
     *            Number of recursions to go
     */
    private static void equalWidthPartition(final int count,
	    final double minVal, final double maxVal, double lowerBoundary,
	    double upperBoundary, final double sigDigitBase,
	    final List<Double> cutPoints, final int recursions) {

	final double width = (upperBoundary - lowerBoundary) / count;

	// Don't need intervals below minVal
	do {
	    upperBoundary = lowerBoundary + width;
	    lowerBoundary = upperBoundary;
	} while (minVal > upperBoundary);

	discretize(minVal, upperBoundary, minVal, upperBoundary, sigDigitBase,
		cutPoints, recursions);

	for (upperBoundary = lowerBoundary + width; upperBoundary < maxVal; upperBoundary = lowerBoundary
		+ width) {

	    discretize(lowerBoundary, upperBoundary, lowerBoundary,
		    upperBoundary, sigDigitBase, cutPoints, recursions);

	    lowerBoundary = upperBoundary;

	}

	discretize(lowerBoundary, maxVal, lowerBoundary, maxVal, sigDigitBase,
		cutPoints, recursions);
    }

    /**
     * Create a 2-3-2 partition of the interval.
     * 
     * @param minVal
     *            The smallest value of the interval. It can be equal to the
     *            lower boundary of the interval, or it can be higher
     * @param maxVal
     *            The highest value to the interval. Can be smaller than or
     *            equal to the upper boundary
     * @param lowerBoundary
     *            The lower boundary of the interval. This value is computed and
     *            significant for applying the 3-4-5 rule. It always an often of
     *            the base of the significant digit of the caller.
     * @param upperBoundary
     *            The upper boundary.
     * @param sigDigitBase
     *            The base of the significant digit for the subintervals
     * @param cutPoints
     *            The list to add the cut points
     * @param recursions
     *            Number of recursions to go
     */
    private static void twoThreeTwoPartition(final double minVal,
	    final double maxVal, double lowerBoundary, double upperBoundary,
	    final double sigDigitBase, final List<Double> cutPoints,
	    final int recursions) {

	final double width = (upperBoundary - lowerBoundary) / 7;

	upperBoundary = lowerBoundary + (2 * width);

	if (minVal < upperBoundary && upperBoundary < maxVal) {

	    discretize(minVal, upperBoundary, minVal, upperBoundary,
		    sigDigitBase, cutPoints, recursions);

	    lowerBoundary = upperBoundary;

	} else {

	    lowerBoundary = minVal;

	}

	upperBoundary += 3 * width;

	if (minVal < upperBoundary && upperBoundary < maxVal) {

	    discretize(lowerBoundary, upperBoundary, lowerBoundary,
		    upperBoundary, sigDigitBase, cutPoints, recursions);

	    lowerBoundary = upperBoundary;

	}

	discretize(lowerBoundary, maxVal, lowerBoundary, maxVal, sigDigitBase,
		cutPoints, recursions);
    }

    private int recDepth;

    public The345RuleDiscretizer() {
	recDepth = DEF_RECDEPTH;
    }

    public The345RuleDiscretizer(DataView population, NumericAttribute na,
	    int recDepth) {
	this.recDepth = recDepth;
	setPopulation(population);
	setAttribute(na);
    }

    /**
     * Creates the 3-4-5 rule discretizer specifying the recursion depth.
     * 
     * If no recursion depth is specified, the default recursion depth which is
     * 2 is used.
     * 
     * Create String array by splitting following String with regex ";":
     * 
     * "3-4-5-rule[; rec-depth = 2;]"
     * 
     * @param args
     *            String array as specified above.
     */
    public The345RuleDiscretizer(String[] args) {
	this();
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(REC_DEPTH)) {
		recDepth = Integer.parseInt(arg[1].trim());
	    }
	}
    }

    /**
     * Start here to discretize.
     * 
     * @param sortedRecords
     *            List of DataRecords sorted by the attribute's value.
     * @return The list of cut points.
     */
    private List<Double> discretize(List<DataRecord> sortedRecords) {
	final List<Double> cutPoints = new ArrayList<Double>();
	final double minVal = sortedRecords.get(0).getValue(attribute);
	cutPoints.add(minVal);
	final int fivePercentRecord = sortedRecords.size() / 20;
	final int ninetyFiveRecord = fivePercentRecord * 19;
	final double fivePercentVal = sortedRecords.get(fivePercentRecord)
		.getValue(attribute);
	final double ninetyFiveVal = sortedRecords.get(ninetyFiveRecord)
		.getValue(attribute);
	discretize(minVal, sortedRecords.get(sortedRecords.size() - 1)
		.getValue(attribute), fivePercentVal, ninetyFiveVal,
		sigDigitBase(fivePercentVal, ninetyFiveVal), cutPoints,
		recDepth);
	return cutPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getCutpoints
     * ()
     */
    @Override
    public List<Double> getCutpoints() {

	if ((population == null) || (attribute == null)
		|| (population.dataset().getIndex(attribute) < 0)
		|| (population.size() < 2)) {
	    return new ArrayList<Double>();
	}

	sortedSample = DiscretizationUtils.getSortedDataRecords(population,
	attribute, false, false);
	List<Double> cutPoints = discretize(sortedSample);
	return cutPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

}
