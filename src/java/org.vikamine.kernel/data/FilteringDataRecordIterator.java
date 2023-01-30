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
 * Created on 10.08.2004
 *
 */
package org.vikamine.kernel.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link FilteringDataRecordIterator} provides a mechanism to dynamically
 * filter a data record iterator given a specific filter.
 * 
 * @author atzmueller
 */
public class FilteringDataRecordIterator implements Iterator<DataRecord> {

    private final IncludingDataRecordFilter filter;

    private final Iterator<DataRecord> instanceIterator;

    private DataRecord nextInstance;

    public FilteringDataRecordIterator(Iterator<DataRecord> instanceIterator,
	    IncludingDataRecordFilter includingInstanceFilter) {
	super();
	this.filter = includingInstanceFilter;
	this.instanceIterator = instanceIterator;
    }

    @Override
    public boolean hasNext() {
	while (nextInstance == null && instanceIterator.hasNext()) {
	    DataRecord next = instanceIterator.next();
	    if (filter.isIncluded(next)) {
		nextInstance = next;
	    }
	}
	return (nextInstance != null);
    }

    @Override
    public DataRecord next() {
	if (hasNext()) {
	    DataRecord tmp = nextInstance;
	    assert (tmp != null);
	    nextInstance = null;
	    return tmp;
	} else {
	    throw new NoSuchElementException();
	}
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException(
		"FilteringDataRecordIterator can not remove Elements!");
    }
}
