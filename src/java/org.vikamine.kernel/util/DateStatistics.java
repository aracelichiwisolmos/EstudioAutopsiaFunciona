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
 * Created on 25.10.2004
 */
package org.vikamine.kernel.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DateAttribute;

/**
 * Creates intervals for a given aggregation level for a {@link DateAttribute}.
 * 
 * @author atzmueller
 */
public class DateStatistics {

    public static final int AGGREGATION_LEVEL_ALL = -1;

    public static final int AGGREGATION_LEVEL_QUARTER = -2;

    public static class Interval {

	public GregorianCalendar begin;

	public GregorianCalendar end;

	public int instancesCount = 0;
    }

    private Attribute dateDMAttribute;

    /**
     * Must be Calender.DAY_OF_YEAR or Calender.WEEK_OF_YEAR or Calender.MONTH
     * or Calender.YEAR.
     */
    private int aggregationLevel;

    private Iterator<DataRecord> instances;

    private List intervals;

    private Date endDate;

    private Date startDate;

    public DateStatistics(Attribute att, int aggregationLevel, Date startDate,
	    Date endDate, Iterator<DataRecord> instances) {
	if (!(att).isDate()) {
	    throw new IllegalArgumentException("must be a date-attribute");
	}
	this.dateDMAttribute = att;
	this.aggregationLevel = aggregationLevel;
	this.instances = instances;
	this.startDate = startDate;
	this.endDate = endDate;
	intervals = buildIntervals();
    }

    public DateStatistics(Attribute att, Date startDate, Date endDate,
	    Iterator<DataRecord> instances) {
	this(att, AGGREGATION_LEVEL_ALL, startDate, endDate, instances);
    }

    private int getReducedAggregationLevel(int aggregationLevel) {
	if ((aggregationLevel == AGGREGATION_LEVEL_ALL)
		|| (aggregationLevel == AGGREGATION_LEVEL_QUARTER)) {
	    return Calendar.MILLISECOND;
	} else {

	    switch (aggregationLevel) {
	    case Calendar.YEAR:
	    case Calendar.MONTH:
	    case Calendar.WEEK_OF_YEAR:
	    case Calendar.DAY_OF_YEAR:
		return Calendar.MILLISECOND;
	    default:
		Logger.getLogger(getClass().getName())
			.throwing(
				getClass().getName(),
				"getReducedAggregationLevel",
				new IllegalArgumentException(
					"Unknown Aggregationlevel "
						+ aggregationLevel));
		throw new IllegalArgumentException("Unknown Aggregationlevel "
			+ aggregationLevel);
	    }
	}
    }

    private List buildEmptySortedIntervals() {
	List sortedIntervalList = new LinkedList();
	if (aggregationLevel != AGGREGATION_LEVEL_ALL) {
	    GregorianCalendar currentCal = new GregorianCalendar();
	    currentCal.setTime(startDate);
	    GregorianCalendar endCal = new GregorianCalendar();
	    endCal.setTime(endDate);
	    while (!currentCal.after(endCal)) {
		Interval interval = new Interval();
		interval.begin = (GregorianCalendar) currentCal.clone();
		if (aggregationLevel == AGGREGATION_LEVEL_QUARTER) {
		    currentCal.add(Calendar.MONTH, 3);
		} else {
		    currentCal.add(aggregationLevel, 1);
		}
		// in the last step (date), we use the endDate,
		// such that the computation is really modulo aggregationLevel
		if (!currentCal.after(endCal)) {
		    interval.end = (GregorianCalendar) currentCal.clone();
		    int reducedAggregationLevel = getReducedAggregationLevel(aggregationLevel);
		    interval.end.add(reducedAggregationLevel, -1);
		} else {
		    interval.end = (GregorianCalendar) endCal.clone();
		}
		sortedIntervalList.add(interval);
	    }
	} else if (aggregationLevel == AGGREGATION_LEVEL_ALL) {
	    GregorianCalendar currentCal = new GregorianCalendar();
	    currentCal.setTime(startDate);
	    GregorianCalendar endCal = new GregorianCalendar();
	    endCal.setTime(endDate);
	    Interval interval = new Interval();
	    interval.begin = currentCal;
	    interval.end = endCal;
	    sortedIntervalList.add(interval);
	} else {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(),
		    "buildEmptySortedIntervals",
		    new IllegalStateException("Unknown aggregationlevel "
			    + aggregationLevel));
	}
	return sortedIntervalList;
    }

    private List buildIntervals() {
	double[] values = buildValues();
	List sortedIntervalList = buildEmptySortedIntervals();

	for (int i = 0; i < values.length; i++) {
	    long time = (long) values[i];
	    if (isValid(time)) {
		Interval interval = getMatchingInterval(sortedIntervalList,
			time);
		interval.instancesCount++;
	    }
	}
	return sortedIntervalList;

    }

    private Interval getMatchingInterval(List sortedIntervalList, long time) {
	for (Iterator iter = sortedIntervalList.iterator(); iter.hasNext();) {
	    Interval interval = (Interval) iter.next();
	    if ((interval.begin.getTimeInMillis() <= time)
		    && (interval.end.getTimeInMillis() >= time)) {
		return interval;
	    }
	}
	throw new IllegalArgumentException("Time " + time
		+ " not found in interval list!");
    }

    private boolean isValid(long time) {
	if (startDate != null && time < startDate.getTime()) {
	    return false;
	}
	if (endDate != null && time > endDate.getTime()) {
	    return false;
	}
	return true;
    }

    private double[] buildValues() {
	List values = new LinkedList();
	while (instances.hasNext()) {
	    DataRecord inst = instances.next();
	    double val = inst.getValue(dateDMAttribute);
	    if (!Double.isNaN(val)) {
		values.add(new Double(val));
	    }
	}
	double[] array = new double[values.size()];
	int i = 0;
	for (Iterator iter = values.iterator(); iter.hasNext(); i++) {
	    Double d = (Double) iter.next();
	    array[i] = d.doubleValue();
	}
	Arrays.sort(array);
	return array;
    }

    public List getIntervals() {
	return intervals;
    }
}
