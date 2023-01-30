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
 * {@link EqualFreqDiscretizer} implements equal frequency discretization, i.e.,
 * k segments having an equal frequency of contained values are created.
 * 
 * @author lemmerich
 * @date 04/2009
 */
public class EqualFreqDiscretizer extends AbstractDiscretizationMethod {

    private static final String NAME = "Equal Frequency Discretizer";

    public EqualFreqDiscretizer() {
	super();
    }

    public EqualFreqDiscretizer(int segmentsCount) {
	this.segmentsCount = segmentsCount;
    }

    public EqualFreqDiscretizer(DataView population, NumericAttribute na,
	    int segmentsCount) {
	this.population = population;
	this.attribute = na;
	this.segmentsCount = segmentsCount;
    }

    @Override
    public List<Double> getCutpoints() {
	// there are less different values in the dataset than segments for the
	// discretization => return all values separately
	if (attribute.getUsedValuesCount(population.dataset()) <= segmentsCount) {
	    List<Double> result = new ArrayList<Double>();
	    result.addAll(new AllValuesDiscretizer(population, attribute)
		    .getCutpoints());
	    return result;
	}

	// This sorts in ascending order and excludes missing values.
	List<DataRecord> sortedDataRecords = DiscretizationUtils
		.getSortedDataRecords(population, attribute, false, false);

	// this is the size, we want for each segment
	double segmentSizeOptimum = (sortedDataRecords.size())
		/ (double) segmentsCount;

	List<Double> cutpoints = new ArrayList<Double>(segmentsCount + 1);
	int counterSinceLastCandidate = 0;
	int counterSinceLastCutpoint = 0;
	double value = Double.NEGATIVE_INFINITY;
	double lastValue = Double.NEGATIVE_INFINITY;
	double secondLastDifferentValue = Double.NEGATIVE_INFINITY;
	for (DataRecord dr : sortedDataRecords) {
	    lastValue = value;
	    value = dr.getValue(attribute);
	    // the last instance had a different value than the current one.
	    // ==> candidate cutpoint
	    if (lastValue != value) {
		double differenceOfSegmentSizeForLastCandidate = (secondLastDifferentValue == Double.NEGATIVE_INFINITY) ? Double.POSITIVE_INFINITY
			: Math.abs(segmentSizeOptimum
				- counterSinceLastCutpoint);
		counterSinceLastCutpoint = counterSinceLastCutpoint
			+ counterSinceLastCandidate;
		double differenceOfSegmentSizeForThisCandidate = (lastValue == Double.NEGATIVE_INFINITY) ? Double.POSITIVE_INFINITY
			: Math.abs(segmentSizeOptimum
				- counterSinceLastCutpoint);
		// if the last cutpoint was better
		if (differenceOfSegmentSizeForLastCandidate < differenceOfSegmentSizeForThisCandidate) {
		    cutpoints.add((lastValue + secondLastDifferentValue) / 2);
		    if (cutpoints.size() == segmentsCount - 1) {
			return cutpoints;
		    }
		    counterSinceLastCutpoint = counterSinceLastCandidate;
		}
		counterSinceLastCandidate = 0;
		secondLastDifferentValue = lastValue;
	    }
	    counterSinceLastCandidate++;
	}

	return cutpoints;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public List<Double> getSortedSample() {
	// TODO Override this method or set the field
	// AbstractDiscretizationMethod.sortedSample to use this class with the
	// SoftMetaDiscretizer
	return null;
    }
}
