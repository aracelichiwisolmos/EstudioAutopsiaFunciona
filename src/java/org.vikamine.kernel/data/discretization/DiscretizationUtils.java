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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * {@link DiscretizationUtils} implements various utility functions for
 * discretization methods.
 * 
 * @author lemmerich
 * @date 04/2009
 */
public class DiscretizationUtils {

    private static class AttributeComparator implements Comparator<DataRecord>,
	    Serializable {

	private static final long serialVersionUID = 1473923143827865392L;
	Attribute att;
	boolean descending;

	public AttributeComparator(Attribute attribute, boolean descending) {
	    super();
	    this.att = attribute;
	    this.descending = descending;
	}

	@Override
	public int compare(DataRecord o1, DataRecord o2) {
	    int value = Double.compare(o1.getValue(att), o2.getValue(att));
	    return descending ? -value : value;

	}
    }

    public static List<DataRecord> getSortedDataRecords(
	    Iterable<DataRecord> population, Attribute att,
	    boolean includeMissings, boolean descending) {
	List<DataRecord> dataRecords = new LinkedList<DataRecord>();
	for (DataRecord dr : population) {
	    if (includeMissings || (!Value.isMissingValue(dr.getValue(att)))) {
		dataRecords.add(dr);
	    }
	}
	Collections.sort(dataRecords, new AttributeComparator(att, descending));

	return dataRecords;
    }

    /**
     * Returns a double containing the minimum value and the maximum value of
     * attribute na in the dataset drs. Length is always two.
     * 
     * @param drs
     * @param na
     * @return getMinMaxValue[0] => minimum Value; getMinMaxValue[1] => maximum
     *         Value
     */
    public static double[] getMinMaxValue(Iterable<DataRecord> drs,
	    NumericAttribute na) {
	// determine min value & max value
	double maxValue = Double.MIN_VALUE;
	double minValue = Double.MAX_VALUE;

	// the index of the attribute in the dataRecordSet; needs only once to
	// be computed.
	if (drs.iterator().hasNext()) {
	    int index = drs.iterator().next().getDataset().getIndex(na);
	    for (DataRecord record : drs) {
		double value = record.getValue(index);
		if (!Double.isNaN(value)) {
		    maxValue = Math.max(value, maxValue);
		    minValue = Math.min(value, minValue);
		}
	    }
	}
	return new double[] { minValue, maxValue };
    }

    public static double[] countsInPopulation(DataView dataView,
	    NumericAttribute na, List<Double> cutpoints) {
	if (cutpoints == null || cutpoints.size() == 0) {
	    return null;
	}

	double[] result = new double[cutpoints.size() + 1];
	Arrays.fill(result, 0);
	for (DataRecord dr : dataView) {
	    double value = dr.getValue(na);
	    for (int i = 0; i < result.length; i++) {
		double lowerBoundOfInterval = (i == 0) ? Double.NEGATIVE_INFINITY
			: cutpoints.get(i - 1);
		double upperBoundOfInterval = (i == cutpoints.size()) ? Double.POSITIVE_INFINITY
			: cutpoints.get(i);
		if ((value >= lowerBoundOfInterval)
			&& ((value < upperBoundOfInterval))) {
		    result[i]++;
		    break;
		}
	    }
	}
	return result;
    }

    public static double[] countsInSubgroup(DataView population,
	    NumericAttribute na, List<Double> cutpoints,
	    SGDescription sgDescription) {
	if (cutpoints == null || cutpoints.size() == 0) {
	    return null;
	}

	double[] result = new double[cutpoints.size() + 1];
	Arrays.fill(result, 0);
	for (DataRecord dr : population) {
	    double value = dr.getValue(na);
	    for (int i = 0; i < result.length; i++) {
		double lowerBoundOfInterval = (i == 0) ? Double.NEGATIVE_INFINITY
			: cutpoints.get(i - 1);
		double upperBoundOfInterval = (i == cutpoints.size()) ? Double.POSITIVE_INFINITY
			: cutpoints.get(i);
		if ((value >= lowerBoundOfInterval)
			&& ((value < upperBoundOfInterval))) {
		    if (sgDescription.isMatching(dr)) {
			result[i]++;
		    }
		    break;
		}
	    }
	}
	return result;
    }

    public static double[] countTargets(DataView population,
	    NumericAttribute na, List<Double> cutpoints, SGTarget target) {
	if (cutpoints == null || cutpoints.size() == 0) {
	    return null;
	}

	double[] result = new double[cutpoints.size() + 1];
	Arrays.fill(result, 0);

	if (target == null) {
	    return result;
	}

	for (DataRecord dr : population) {
	    double value = dr.getValue(na);
	    for (int i = 0; i < result.length; i++) {
		double lowerBoundOfInterval = (i == 0) ? Double.NEGATIVE_INFINITY
			: cutpoints.get(i - 1);
		double upperBoundOfInterval = (i == cutpoints.size()) ? Double.POSITIVE_INFINITY
			: cutpoints.get(i);
		if ((value >= lowerBoundOfInterval)
			&& ((value < upperBoundOfInterval))) {
		    if (target.isBoolean()) {
			if (((BooleanTarget) target).isPositive(dr)) {
			    result[i]++;
			}
		    } else {
			double valueNum = ((NumericTarget) target).getValue(dr);
			// sum up, if this value was not missing
			if (!Double.isNaN(valueNum)) {
			    result[i] += valueNum;
			}
		    }
		    break;
		}
	    }
	}
	return result;
    }

    public static void addCutpoint(List<Double> cutpointList, double newCutpoint) {
	if (cutpointList.contains(newCutpoint)) {
	    return;
	}
	for (int i = 0; i < cutpointList.size(); i++) {
	    if (cutpointList.get(i) > newCutpoint) {
		cutpointList.add(i, newCutpoint);
		return;
	    }
	}
	cutpointList.add(newCutpoint);
    }

    /**
     * Creates a random sample of specified size of the DataRecordSet provided.
     * 
     * @param set
     *            DataRecordSet to be resampled.
     * @param size
     *            Size of the DataRecordSet.
     * @param sampleSize
     *            Size of the sample.
     * @return Random Sample of the DataRecordSet provided.
     */
    public static Set<DataRecord> resample(IDataRecordSet set, final int size,
	    final int sampleSize) {
	HashSet<DataRecord> sample = new HashSet<DataRecord>();
	Random random = new Random();
	while (sample.size() < sampleSize)
	    sample.add(set.get(random.nextInt(size)));
	return sample;
    }

    /**
     * Write the values of the attributes in the records into a new list.
     * 
     * @param records
     *            List of DataRecords
     * @param attribute
     *            The attribute which value to use
     * @return List containing the values
     */
    public static List<Double> Records2Double(List<DataRecord> records,
	    Attribute attribute) {
	List<Double> doubles = new ArrayList<Double>();
	for (Iterator<DataRecord> it = records.iterator(); it.hasNext();)
	    doubles.add(it.next().getValue(attribute));
	return doubles;
    }
}
