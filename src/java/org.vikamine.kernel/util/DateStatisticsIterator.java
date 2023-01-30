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
 * Created on 11.11.2004
 *
 */
package org.vikamine.kernel.util;

import java.util.Date;
import java.util.Iterator;

import org.vikamine.kernel.data.DateAttribute;
import org.vikamine.kernel.util.DateStatistics.Interval;

/**
 * Iterates over values and their occurrences for a {@link DateAttribute} with
 * given {@link DateStatistics}.
 * 
 * @author Tobias Vogele, atzmueller
 */
public class DateStatisticsIterator implements AttributeValuesIterator {

    private DateStatistics stat;

    private DateStatistics.Interval currentInterval = null;

    private Iterator intervalIterator;

    private int currentIndex = 0;

    public DateStatisticsIterator(DateStatistics stat) {
	this.stat = stat;
	intervalIterator = stat.getIntervals().iterator();
    }

    @Override
    public int getIterationValueCount() {
	return stat.getIntervals().size();
    }

    @Override
    public int getCurrentInstancesCount() {
	if (currentInterval == null) {
	    throw new IllegalStateException(
		    "must call next() at least once before");
	}
	return currentInterval.instancesCount;
    }

    public Date getCurrentIntervalStart() {
	Date begin = new Date();
	begin.setTime(currentInterval.begin.getTimeInMillis());
	return begin;
    }

    public Date getCurrentIntervalEndExclusive() {
	Date end = new Date();
	end.setTime(currentInterval.end.getTimeInMillis());
	return end;
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException("Remove is not supported");
    }

    @Override
    public boolean hasNext() {
	return intervalIterator.hasNext();
    }

    @Override
    public Object next() {
	currentInterval = (Interval) intervalIterator.next();
	currentIndex++;
	return new Double(currentIndex);
    }

}
