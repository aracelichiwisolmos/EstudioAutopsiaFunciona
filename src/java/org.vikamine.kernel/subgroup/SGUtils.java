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

/*
 * Created on 18.11.2003
 * 
 */
package org.vikamine.kernel.subgroup;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.FilteringDataRecordIterator;
import org.vikamine.kernel.data.IncludingDataRecordFilter;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.statistics.ChiSquareStatistics;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * Set of utility functions for computing parameters of a subgroup {@link SG}.
 * 
 * @author atzmueller
 */
public class SGUtils {

    public static double calculateChi2ValueBetweenSelectorAndTarget(
	    SGNominalSelector sel, SGNominalSelector target,
	    DataView population, Options options) {
	SG sg = new SG(population, new SelectorTarget(target));
	SGDescription sgd = new SGDescription();
	sgd.add(sel);
	sg.setSGDescription(sgd);
	sg.createStatistics(options);
	SGStatisticsBinary statistics = (SGStatisticsBinary) sg.getStatistics();
	return calculateChi2OfSubgroup(statistics);
    }

    public static double calculateChi2OfSubgroup(SGStatisticsBinary sgStatistics) {
	double positives = sgStatistics.getPositives();
	double tp = sgStatistics.getTp();
	double fp = sgStatistics.getFp();
	double sgSize = sgStatistics.getSubgroupSize();
	double fn = positives - tp;
	double populationSize = sgStatistics.getDefinedPopulationCount();
	double tn = populationSize - sgSize - fn;

	// This value is equal to the value of the (squared) binomial quality
	// function
	return ChiSquareStatistics.computeFourFoldChiSqare(tp, fn, fp, tn);
    }

    /**
     * Returns the chi-square significance for the sg (description vs target)
     * given the Statistics
     * 
     * @param sgStatistics
     * @return
     */
    public static double calculateChi2Significance(
	    SGStatisticsBinary sgStatistics) {
	double chi2Value = SGUtils.calculateChi2OfSubgroup(sgStatistics);
	return SGUtils.calculateChi2SignificanceNiveau(chi2Value, 1);
    }

    public static double calculateChi2SignificanceNiveau(double chi2Value,
	    int degreesOfFreedom) {
	ChiSquaredDistribution distr = new ChiSquaredDistribution(
		degreesOfFreedom);
	try {
	    return 1 - distr.cumulativeProbability(chi2Value);
	} catch (MaxCountExceededException e) {
	    Logger.getLogger(SGUtils.class.getName()).warning(e.getMessage());
	}
	return Double.NaN;
    }

    public static double getChi2SignificanceNiveau(double tp, double fp,
	    double tn, double fn) {
	double chi2Value = ChiSquareStatistics.computeFourFoldChiSqare(tp, fp,
		tn, fn);
	return calculateChi2SignificanceNiveau(chi2Value, 1);
    }

    public static String toDiscretizedChi2SignificanceLevel(double sigNiveau) {
	String level = "";
	double[] levels = new double[] { 0.000001, 0.000001, 0.00001, 0.0001,
		0.001, 0.01, 0.05, 0.1, 0.2, 0.5, 0.8, 1.0 };
	for (int i = 0; i < levels.length; i++) {
	    if (sigNiveau <= levels[i]) {
		NumberFormat nFormat = NumberFormat.getInstance();
		nFormat.setMaximumFractionDigits(6);
		level = "<=" + nFormat.format(levels[i]);
		break;
	    }
	}
	if (level.equals("")) {
	    // throw new RuntimeException(
	    // "SigNiveau is not between 0.0 and 1.0, or valid level could be found!");
	}
	return level;
    }

    public static String createChi2SignificanceString(double value) {
	DecimalFormat formatter;
	if (value > 0.01) {
	    formatter = new DecimalFormat("0.00");
	} else if (value > 0.0001) {
	    formatter = new DecimalFormat("0.0000");
	} else if (value > 0.000000000001) {
	    formatter = new DecimalFormat("0.0000########");
	} else {
	    formatter = new DecimalFormat("0.00E0");
	}
	return formatter.format(value);
    }

    public static String createUSChi2SignificanceStringForXSL(double value) {
	NumberFormat f = NumberFormat.getInstance(Locale.US);
	if (!(f instanceof DecimalFormat)) {
	    return Double.toString(value);
	} else {
	    DecimalFormat formatter = (DecimalFormat) f;
	    if (value > 0.01) {
		formatter.applyPattern("0.00");
	    } else if (value > 0.0001) {
		formatter.applyPattern("0.0000");
	    } else if (value > 0.000000000001) {
		formatter.applyPattern("0.0000########");
	    } else {
		formatter.applyPattern("0.00E0");
	    }
	    return formatter.format(value);
	}
    }

    public static List<SupportingFactor> computeSupportingFactors(SG subgroup,
	    List dmAttributes) {
	return computeSupportingFactors(subgroup, dmAttributes, false);
    }

    private static class PosNegInstanceFilter implements
	    IncludingDataRecordFilter {
	private final boolean positive;

	private final SG sg;

	PosNegInstanceFilter(boolean positive, SG sg) {
	    this.positive = positive;
	    this.sg = sg;
	}

	@Override
	public boolean isIncluded(DataRecord theInstance) {
	    SGTarget target = sg.getTarget();
	    if (sg.getStatistics()
		    .isInstanceDefinedForSubgroupVars(theInstance)) {
		boolean containsTarget = false;
		if (target instanceof BooleanTarget) {
		    containsTarget = ((BooleanTarget) target)
			    .isPositive(theInstance);
		} else if (target == null) {
		    containsTarget = true;
		} else {
		    throw new UnsupportedOperationException(
			    "No other targets, such as "
				    + target
				    + ", besides BooleanTargets are supported yet!");
		}
		return (containsTarget == positive);
	    }
	    return false;
	}
    }

    private static DataRecordIteration getNonClassPopulationIteration(
	    SG subgroup) {
	return new DataRecordIteration(new FilteringDataRecordIterator(subgroup
		.getPopulation().instanceIterator(), new PosNegInstanceFilter(
		false, subgroup)));
    }

    public static List<SupportingFactor> computeSupportingFactors(SG subgroup,
	    List dmAttributes, boolean includePrincipalFactors) {
	DataRecordIteration subgroupInstanceIteration = new DataRecordIteration(
		subgroup.subgroupInstanceIterator());

	return computeSupportingFactors(subgroup, dmAttributes,
		includePrincipalFactors, subgroupInstanceIteration);
    }

    private static double[] accumulateSupportingCounts(
	    Iterator<DataRecord> instanceIterator,
	    SGNominalSelector supportingSGSelector, SG subgroup) {
	double contained = 0.0;
	double notContained = 0.0;
	while (instanceIterator.hasNext()) {
	    DataRecord instance = instanceIterator.next();
	    if (subgroup.getStatistics().isAttributeDefinedInInstance(instance,
		    supportingSGSelector.getAttribute()))
		if (supportingSGSelector.isContainedInInstance(instance)) {
		    contained++;
		} else {
		    notContained++;
		}
	}

	return new double[] { contained, notContained };
    }

    public static List<SupportingFactor> computeSupportingFactors(SG subgroup,
	    List dmAttributes, boolean includePrincipalFactors,
	    DataRecordIteration instanceIteration) {
	DataRecordIteration tpInstanceIteration = new DataRecordIteration(
		new FilteringDataRecordIterator(instanceIteration.iterator(),
			new PosNegInstanceFilter(true, subgroup)));
	DataRecordIteration nonClassPopulationIteration = getNonClassPopulationIteration(subgroup);
	List<SupportingFactor> result = new LinkedList<SupportingFactor>();

	for (Iterator iter = dmAttributes.iterator(); iter.hasNext();) {
	    Attribute attribute = (Attribute) iter.next();
	    if (attribute.isNominal()
		    && isAttributeDependentChi2WithValues(tpInstanceIteration,
			    nonClassPopulationIteration,
			    (NominalAttribute) attribute)) {
		for (Iterator iterator = ((NominalAttribute) attribute)
			.allValuesIterator(); iterator.hasNext();) {
		    Value val = (Value) iterator.next();
		    SGNominalSelector supportingFactorSelector = new DefaultSGSelector(
			    attribute, val);
		    double[] positiveCounts = accumulateSupportingCounts(
			    tpInstanceIteration.iterator(),
			    supportingFactorSelector, subgroup);
		    double[] negativeCounts = accumulateSupportingCounts(
			    nonClassPopulationIteration.iterator(),
			    supportingFactorSelector, subgroup);
		    double chi2 = ChiSquareStatistics.computeFourFoldChiSqare(
			    positiveCounts[0], positiveCounts[1],
			    negativeCounts[0], negativeCounts[1]);
		    double chi2Threshold = determineChi2Threshold(2);
		    boolean isSignificant = chi2 > chi2Threshold;

		    if (isSignificant) {
			if ((includePrincipalFactors || (!subgroup
				.getSGDescription()
				.containsAttributeAsSelector(attribute)))
				&& (!subgroup.getTarget().getAttributes()
					.contains(attribute))) {
			    double phi = ChiSquareStatistics
				    .computePhiCoefficient(positiveCounts[0],
					    positiveCounts[1],
					    negativeCounts[0],
					    negativeCounts[1]);
			    if (phi > 0.0) {
				result.add(new SupportingFactor(
					supportingFactorSelector,
					positiveCounts[0], positiveCounts[1],
					negativeCounts[0], negativeCounts[1],
					phi));
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    /**
     * @param useStrictSIGTest
     * @param positiveInstanceIteration
     * @param negativeInstanceIteration
     * @param attribute
     * @param subgroupSupportingFactors
     * @param subgroupSupportingValues
     * @return
     */
    private static boolean isAttributeDependentChi2WithValues(
	    DataRecordIteration positiveInstanceIteration,
	    DataRecordIteration negativeInstanceIteration,
	    NominalAttribute attribute) {
	List tpDistributionValues = new LinkedList();
	List ncDistributionValues = new LinkedList();
	final int tpKey = 0;
	final int ncKey = 1;
	int totals[] = new int[2];

	// For the X^2-test cf. Lothar Sachs: Angewandte Statistik, pp. 592 (9th
	// Edition)

	// Note: for MC attributes we apply the basic X^2 formula;
	// HOWEVER, the sum of all fields in the X^2 table is
	// NOT NECESSARILY equal to the total number of cases,
	// since these are mc-attributes;
	// It _should_ work correctly, however.

	// counter for the "admissible" values in the values array
	int admissibleValueCounter = 0;
	for (Iterator iterator = attribute.allValuesIterator(); iterator
		.hasNext();) {
	    Value val = (Value) iterator.next();
	    SGNominalSelector factor = new DefaultSGSelector(attribute,
		    Collections.singleton(val));
	    // if (!(attribute.value(v)
	    // .equals(OntoConstants.CONST_UNKNOWN))) {
	    int countTPInstances = 0;
	    for (Iterator<DataRecord> tpInstanceIterator = positiveInstanceIteration
		    .iterator(); tpInstanceIterator.hasNext();) {
		DataRecord instance = tpInstanceIterator.next();
		if (factor.isContainedInInstance(instance)) {
		    countTPInstances++;
		}
	    }

	    int countNCInstances = 0;
	    for (Iterator<DataRecord> nonClassPopulationIterator = negativeInstanceIteration
		    .iterator(); nonClassPopulationIterator.hasNext();) {
		DataRecord instance = nonClassPopulationIterator.next();
		if (factor.isContainedInInstance(instance)) {
		    countNCInstances++;
		}
	    }

	    // X^2 is only applicable,
	    // if the expected counts are != 0! (L. Sachs, p. 594)
	    if ((countTPInstances != 0) || (countNCInstances != 0)) {
		admissibleValueCounter++;
		tpDistributionValues.add(Integer.valueOf(countTPInstances));
		totals[tpKey] += countTPInstances;
		ncDistributionValues.add(Integer.valueOf(countNCInstances));
		totals[ncKey] += countNCInstances;
	    }
	}

	final int tpAndNCSumKey = 2;

	int distributionArray[][] = new int[3][admissibleValueCounter];
	for (int i = 0; i < admissibleValueCounter; i++) {
	    distributionArray[tpKey][i] = ((Integer) tpDistributionValues
		    .get(i)).intValue();
	    distributionArray[ncKey][i] = ((Integer) ncDistributionValues
		    .get(i)).intValue();
	    distributionArray[tpAndNCSumKey][i] = distributionArray[tpKey][i]
		    + distributionArray[ncKey][i];
	}

	int total = totals[tpKey] + totals[ncKey];

	boolean isSignificant = false;
	if (admissibleValueCounter >= 2) {
	    double chi2 = 0;
	    // formula: Lothar Sachs: Angewandte Statistik, p. 593 (9th
	    // Edition)

	    double sum = 0;
	    for (int i = 0; i < 2; i++) {
		for (int j = 0; j < admissibleValueCounter; j++) {
		    sum += (Math.pow(distributionArray[i][j], 2) / (totals[i] * distributionArray[tpAndNCSumKey][j]));
		}
	    }

	    chi2 = total * (sum - 1);

	    double chi2Threshold = determineChi2Threshold(admissibleValueCounter);
	    isSignificant = chi2 > chi2Threshold;

	}
	return isSignificant;
    }

    /**
     * @param useStrictSIGTest
     * @param admissibleValueCounter
     * @return
     */
    private static double determineChi2Threshold(int admissibleValueCounter) {
	// 0.01 significance level
	double[] chi2ThresholdsForDegreesOfFreedomStrictSIGLevel = { 0, 6.63,
		9.21, 11.34, 13.28, 15.09, 16.81, 18.48, 20.09, 21.67, 23.21,
		24.72, 26.22, 27.69, 29.14, 30.58, 32.00, 33.41, 34.81, 36.19,
		37.57, 38.93, 40.29, 41.64, 42.98, 44.31, 45.64, 46.96, 48.28 };

	// 0.05 significance level
	double[] chi2ThresholdsForDegreesOfFreedomNormalSIGLevel = { 0, 3.84,
		5.99, 7.81, 0.49, 11.07, 12.59, 14.07, 15.51, 16.92, 18.31,
		19.68, 21.03, 22.36, 23.68, 25.00, 26.30, 27.59, 28.87, 30.14,
		31.41, 32.67, 33.92, 35.17, 36.42, 37.65, 38.89, 40.11, 41.34,
		42.56, 43.77 };

	double chi2Threshold = 0;
	int degreesOfFreedom = admissibleValueCounter - 1;
	if (degreesOfFreedom >= chi2ThresholdsForDegreesOfFreedomStrictSIGLevel.length) {
	    chi2Threshold = chi2ThresholdsForDegreesOfFreedomNormalSIGLevel[chi2ThresholdsForDegreesOfFreedomNormalSIGLevel.length - 1];
	} else {
	    chi2Threshold = chi2ThresholdsForDegreesOfFreedomNormalSIGLevel[degreesOfFreedom];
	}
	return chi2Threshold;
    }

    public static double computeOddsRatio(SGStatisticsBinary sgStats) {
	double tp = sgStats.getTp();
	double fp = sgStats.getFp();
	double fn = sgStats.getFn();
	double tn = sgStats.getTn();

	// apply correction ...
	if ((tp == 0) || (fp == 0) || (fn == 0) || (tn == 0)) {
	    return 0;
	} else {
	    double oddsRatio = (tp * tn) / (fp * fn);
	    return oddsRatio;
	}
    }

    public static double computeStandardDeviation(double base, double[] vals) {
	double stdev = 0;
	for (int i = 0; i < vals.length; i++) {
	    double val = vals[i];
	    stdev += Math.pow(base - val, 2);
	}
	return Math.sqrt(stdev / (vals.length - 1));
    }

    public static double computeMean(double[] values) {
	double mean = 0;
	for (int i = 0; i < values.length; i++) {
	    mean += values[i];
	}
	return mean / values.length;
    }

    public static double computeQualityStandardDeviation(SG sg, List strataSGs,
	    IQualityFunction qf) {
	double baseQuality = qf.evaluate(sg);
	double[] vals = new double[strataSGs.size()];
	int i = 0;
	for (Iterator iter = strataSGs.iterator(); iter.hasNext(); i++) {
	    SG stratumSG = (SG) iter.next();
	    double q = qf.evaluate(stratumSG);
	    vals[i] = q;
	}
	double meanQualityDeviation = computeStandardDeviation(baseQuality,
		vals);
	return meanQualityDeviation;
    }

    public static double computeOddsRatioStandardDeviation(SG sg, List strataSGs) {

	double baseQuality = computeOddsRatio((SGStatisticsBinary) sg
		.getStatistics());
	double[] vals = new double[strataSGs.size()];
	int i = 0;
	for (Iterator iter = strataSGs.iterator(); iter.hasNext(); i++) {
	    SG stratumSG = (SG) iter.next();
	    double q = computeOddsRatio((SGStatisticsBinary) stratumSG
		    .getStatistics());
	    vals[i] = q;
	}
	double stdDeviationOddsRatio = computeStandardDeviation(baseQuality,
		vals);
	return stdDeviationOddsRatio;
    }

    public static boolean isAttributeAssociatedWithSGDescription(
	    NominalAttribute att, SG sg) {
	if (!(att.isNominal())) {
	    return false;
	}
	List positiveInstances = new LinkedList();
	List negativeInstances = new LinkedList();
	for (Iterator<DataRecord> iter = sg.getPopulation().instanceIterator(); iter
		.hasNext();) {
	    DataRecord instance = iter.next();
	    if (sg.getStatistics().isSGSelectorSetDefinedInInstance(instance)) {
		if (sg.getSGDescription().isMatching(instance))
		    positiveInstances.add(instance);
		else
		    negativeInstances.add(instance);
	    }
	}
	return isAttributeDependentChi2WithValues(new DataRecordIteration(
		positiveInstances.iterator()), new DataRecordIteration(
		negativeInstances.iterator()), att);
    }

    /**
     * Returns the values of attribute a including missing values.
     * 
     * @param a
     *            An Attribute
     * @param subgroup
     *            The respective subgroup
     * @return An array containing the values of attribute a
     */
    private static double[] getValuesOfAttribute(SG subgroup, Attribute a) {
	double[] result = new double[(int) subgroup.getStatistics()
		.getSubgroupSize()];
	int i = 0;
	for (DataRecord record : subgroup) {
	    double val = record.getValue(a);
	    result[i] = val;
	    i++;
	}
	return result;
    }

    /**
     * Computes the mean of an attribute, given a subgroup. Does not handle
     * missing values
     * 
     * @param a
     *            An Attribute
     * @param subgroup
     *            The respective subgroup
     * @return the mean
     */
    public static double computeMean(Attribute a, SG subgroup) {
	double[] valuesOfAttribute = getValuesOfAttribute(subgroup, a);
	return computeMean(valuesOfAttribute);
    }

    /**
     * 
     * @param a
     *            An Attribute
     * @param subgroup
     *            The respective subgroup
     * @return The standard deviation
     */
    public static double computeStandardDeviation(Attribute a, SG subgroup) {
	double[] valuesOfAttribute = getValuesOfAttribute(subgroup, a);
	double mean = computeMean(valuesOfAttribute);
	return computeStandardDeviation(mean, valuesOfAttribute);
    }

    /**
     * Create descriptive statistics for a numeric attribute
     * 
     * @param newInput
     *            the iterator
     * @param att
     *            the numeric attribute
     * @return the descriptive statistics
     */
    public static DescriptiveStatistics createDescriptiveStats(
	    Iterable<DataRecord> newInput, NumericAttribute att) {
	DescriptiveStatistics stats = new DescriptiveStatistics();
	for (DataRecord dr : newInput) {
	    double value = dr.getValue(att);
	    if (!Value.isMissingValue(value)) {
		stats.addValue(value);
	    }
	}
	return stats;
    }

    /**
     * Create frequency information for an attribute
     * 
     * @param newInput
     *            the iterable
     * @param att
     *            the attribute
     * @return the frequency statistics
     */
    public static Frequency createFrequencyStats(Iterable<DataRecord> newInput,
	    Attribute att) {
	Frequency stats = new Frequency();
	for (DataRecord dr : newInput) {
	    stats.addValue(dr.getValue(att));
	}
	return stats;
    }
}
