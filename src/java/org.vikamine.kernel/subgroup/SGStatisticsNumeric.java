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

import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.discretization.DiscretizationUtils;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Implementation of {@link SGStatistics} object for numeric {@link SGTarget}.
 * 
 * @author lemmerich
 * @date 21.10.08
 */
public class SGStatisticsNumeric extends SGStatistics {

    protected double sgMean;
    protected double sgNormalizedMean;
    protected double populationMean;
    protected double populationNormalizedMean;
    protected double populationVariance = Double.NEGATIVE_INFINITY;

    protected double maxValuePopulation;
    protected double minValuePopulation;

    protected double maxValueSG;
    protected double minValueSG;

    protected double sgVariance = Double.NEGATIVE_INFINITY;
    protected double sgStdDeviation = Double.NEGATIVE_INFINITY;

    protected double sumOfTargetValuesPopulation;
    protected double sumOfTargetValuesSG;

    protected double sgMedian;
    protected double populationMedian;

    public SGStatisticsNumeric(SG subgroup) {
	super(subgroup);
    }

    public SGStatisticsNumeric(SG subgroup, Options options) {
	super(subgroup, options);
    }

    @Override
    protected void calculateStatistics() {
	minValuePopulation = Double.MAX_VALUE;
	maxValuePopulation = Double.MIN_VALUE;
	minValueSG = Double.MAX_VALUE;
	maxValueSG = Double.MIN_VALUE;
	sgMean = 0;
	populationMean = 0;

	SGTarget target = subgroup.getTarget();
	SGDescription description = subgroup.getSGDescription();

	for (DataRecord instance : subgroup.getPopulation()) {
	    double val = ((NumericTarget) target).getValue(instance);
	    if (!Double.isNaN(val)
		    && isSGSelectorSetDefinedInInstance(instance)) {
		definedPopulationCount += instance.getWeight();

		minValuePopulation = Math.min(minValuePopulation, val);
		maxValuePopulation = Math.max(maxValuePopulation, val);
		sumOfTargetValuesPopulation += val * instance.getWeight();
		if (description.isMatching(instance)) {
		    sumOfTargetValuesSG += val * instance.getWeight();
		    subgroupSize += instance.getWeight();
		    minValueSG = Math.min(minValuePopulation, val);
		    maxValueSG = Math.max(maxValuePopulation, val);
		}
	    } else {
		undefinedPopulationCount += instance.getWeight();
	    }
	}

	// normalize positives, negatives, tp and fp
	double subgroupRange = Math.max(Double.MIN_NORMAL, maxValueSG
		- minValueSG);
	double populationRange = Math.max(Double.MIN_NORMAL, maxValuePopulation
		- minValuePopulation);
	sgMean = sumOfTargetValuesSG / subgroupSize;
	populationMean = sumOfTargetValuesPopulation / definedPopulationCount;

	sgNormalizedMean = (sgMean - minValueSG) / subgroupRange;
	populationNormalizedMean = (populationMean - minValuePopulation)
		/ populationRange;

	// initiate "expensive" variables
	sgVariance = Double.NEGATIVE_INFINITY;
	populationVariance = Double.NEGATIVE_INFINITY;
	sgStdDeviation = Double.NEGATIVE_INFINITY;

	sgMedian = Double.NEGATIVE_INFINITY;
	populationMedian = Double.NEGATIVE_INFINITY;
    }

    public double getPopulationMedian() {
	if (Double.isInfinite(populationMedian)) {
	    computeMedian();
	}
	return populationMedian;
    }

    private void computeMedian() {
	NumericAttribute targetAttribute = ((NumericTarget) subgroup
		.getTarget()).getAttribute();
	List<DataRecord> sortedRecords = DiscretizationUtils
		.getSortedDataRecords(subgroup.getPopulation(),
			targetAttribute, false, false);
	// median of population
	if (getDefinedPopulationCount() > 0) {
	    // odd
	    if ((getDefinedPopulationCount() % 2) == 1) {
		populationMedian = sortedRecords.get(
			(int) getDefinedPopulationCount() / 2).getValue(
			targetAttribute);
		// even
	    } else {
		populationMedian = (sortedRecords.get(
			(int) getDefinedPopulationCount() / 2).getValue(
			targetAttribute) + sortedRecords.get(
			((int) getDefinedPopulationCount() / 2) - 1).getValue(
			targetAttribute)) / 2;
	    }
	} else {
	    populationMean = Double.NaN;
	}

	// median of sg
	sortedRecords = DiscretizationUtils.getSortedDataRecords(subgroup,
		targetAttribute, false, false);
	if (subgroupSize == 0) {
	    sgMedian = Double.NaN;
	} else {
	    // odd
	    if ((subgroupSize % 2) == 1) {
		sgMedian = sortedRecords.get((int) (subgroupSize / 2))
			.getValue(targetAttribute);
		// even
	    } else {
		sgMedian = (sortedRecords.get((int) subgroupSize / 2).getValue(
			targetAttribute) + sortedRecords.get(
			((int) subgroupSize / 2) - 1).getValue(targetAttribute)) / 2;
	    }
	}
    }

    public double getSGMedian() {
	if (Double.isInfinite(populationMedian)) {
	    computeMedian();
	}
	return sgMedian;
    }

    private void calculateVarianceAndStdVar() {
	double populationDeviation = 0;
	double sgDeviation = 0;
	for (Iterator<DataRecord> iter = subgroup.getPopulation()
		.instanceIterator(); iter.hasNext();) {
	    DataRecord instance = iter.next();

	    if (isInstanceDefinedForSubgroupVars(instance)) {
		SGTarget target = subgroup.getTarget();
		double val = ((NumericTarget) target).getValue(instance);
		populationDeviation += Math.pow(val - populationMean, 2);

		if (subgroup.getSGDescription().isMatching(instance)) {
		    sgDeviation += (Math.pow(val - sgMean, 2));
		}
	    }
	}
	populationVariance = populationDeviation / definedPopulationCount;
	sgVariance = sgDeviation / subgroupSize;
	sgStdDeviation = Math.sqrt(sgVariance);
    }

    public double getSumOfTargetValuesPopulation() {
	return sumOfTargetValuesPopulation;
    }

    public double getSumOfTargetValuesSG() {
	return sumOfTargetValuesSG;
    }

    public double getPopulationMean() {
	return populationMean;
    }

    public double getPopulationNormalizedMean() {
	return populationNormalizedMean;
    }

    public double getPopulationVariance() {
	if (Double.isInfinite(sgVariance)) {
	    calculateVarianceAndStdVar();
	}
	return populationVariance;
    }

    public double getSGMean() {
	return sgMean;
    }

    public double getSGNormalizedMean() {
	return sgNormalizedMean;
    }

    public double getSGVariance() {
	if (Double.isInfinite(sgVariance)) {
	    calculateVarianceAndStdVar();
	}
	return sgVariance;
    }

    public double getMaxValuePopulation() {
	return maxValuePopulation;
    }

    public double getMinValuePopulation() {
	return minValuePopulation;
    }

    @Override
    public String toString() {
	String basic = super.toString();
	String result = basic;
	result += basic + "Mean (SG): " + sgMean + "\n";
	result += basic + "Mean (Pop): " + populationMean + "\n";
	return result;
    }

    @Override
    public boolean isBinary() {
	return false;
    }

    @Override
    public double getDeviation() {
	return getSGMean() - getPopulationMean();
    }

    public double getSgStdDeviation() {
	if (Double.isInfinite(sgStdDeviation)) {
	    calculateVarianceAndStdVar();
	}
	return sgStdDeviation;
    }

    public void setSgStdDeviation(double sgStdDeviation) {
	this.sgStdDeviation = sgStdDeviation;
    }

    @Override
    public double getTargetQuantitySG() {
	return getSGMean();
    }

    @Override
    public double getTargetQuantityPopulation() {
	return getPopulationMean();
    }

    public double getMaxValueSG() {
	return maxValueSG;
    }

    public double getMinValueSG() {
	return minValueSG;
    }
}
