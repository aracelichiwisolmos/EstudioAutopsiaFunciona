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
 * Created on 11.08.2004
 *
 */
package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.util.VKMUtil;

/**
 * {@link DataRecordIteration} provides a reusable iterator over DataRecord
 * objects.
 * 
 * @author atzmueller
 */
public class DataRecordIteration implements Iterable<DataRecord> {

    private static abstract class SizableInstanceIterator implements
	    Iterator<DataRecord> {
	abstract int size();

	abstract SizableInstanceIterator copy();

	abstract List<DataRecord> getInstances();
    }

    private static class DataRecordSetBackedPopulationInstanceIterator extends
	    SizableInstanceIterator {

	private final IDataRecordSet dataRecordSet;
	private final Iterator<DataRecord> iterator;

	public DataRecordSetBackedPopulationInstanceIterator(
		IDataRecordSet dataRecordSet) {
	    super();
	    this.dataRecordSet = dataRecordSet;
	    this.iterator = dataRecordSet.iterator();
	}

	@Override
	public boolean hasNext() {
	    return iterator.hasNext();
	}

	@Override
	public DataRecord next() {
	    return iterator.next();
	}

	@Override
	public void remove() {
	    iterator.remove();
	}

	@Override
	int size() {
	    return dataRecordSet.getNumInstances();
	}

	@Override
	SizableInstanceIterator copy() {
	    return new DataRecordSetBackedPopulationInstanceIterator(
		    dataRecordSet);
	}

	@Override
	List<DataRecord> getInstances() {
	    return VKMUtil.asList(dataRecordSet.iterator());
	}

    }

    private static class CachedPopulationInstanceIterator extends
	    SizableInstanceIterator {

	private Iterator<DataRecord> referencedInstancesIterator;

	private List<DataRecord> referencedInstances;

	private int size = 0;

	private CachedPopulationInstanceIterator() {
	    super();
	}

	protected CachedPopulationInstanceIterator(
		Iterator<DataRecord> instanceIterator) {
	    super();
	    init(instanceIterator);
	}

	@Override
	CachedPopulationInstanceIterator copy() {
	    CachedPopulationInstanceIterator iter = new CachedPopulationInstanceIterator();
	    iter.referencedInstances = this.referencedInstances;
	    if (this.referencedInstances == null) {
		iter.referencedInstancesIterator = this.referencedInstancesIterator;
	    } else {
		iter.referencedInstancesIterator = iter.referencedInstances
			.iterator();
	    }
	    iter.size = this.size;
	    return iter;
	}

	@Override
	List<DataRecord> getInstances() {
	    return referencedInstances;
	}

	@Override
	public boolean hasNext() {
	    return referencedInstancesIterator.hasNext();
	}

	private void init(Iterator<DataRecord> theInstanceIterator) {
	    List<DataRecord> tmp = new LinkedList<DataRecord>();
	    while (theInstanceIterator.hasNext()) {
		DataRecord instance = theInstanceIterator.next();
		assert (instance != null);
		tmp.add(instance);
		size++;
	    }
	    referencedInstances = new ArrayList<DataRecord>(size);
	    referencedInstances.addAll(tmp);
	    referencedInstancesIterator = referencedInstances.iterator();
	}

	@Override
	public DataRecord next() {
	    return referencedInstancesIterator.next();
	}

	@Override
	public void remove() {
	    referencedInstancesIterator.remove();
	}

	@Override
	int size() {
	    return size;
	}
    }

    private SizableInstanceIterator instanceIterator;

    public DataRecordIteration(IDataRecordSet dataRecordSet) {
	super();
	this.instanceIterator = new DataRecordSetBackedPopulationInstanceIterator(
		dataRecordSet);
    }

    public DataRecordIteration(Iterator<DataRecord> instanceIterator) {
	super();
	if (instanceIterator instanceof SizableInstanceIterator) {
	    this.instanceIterator = (SizableInstanceIterator) instanceIterator;
	} else {
	    this.instanceIterator = new CachedPopulationInstanceIterator(
		    instanceIterator);
	}
    }

    @Override
    public Iterator<DataRecord> iterator() {
	return instanceIterator.copy();
    }

    /**
     * @return an UNMODIFIABLE (!) collection of the contained instances
     */
    public Collection<DataRecord> instances() {
	return Collections.unmodifiableCollection(instanceIterator
		.getInstances());
    }

    public int size() {
	return instanceIterator.size();
    }
}
