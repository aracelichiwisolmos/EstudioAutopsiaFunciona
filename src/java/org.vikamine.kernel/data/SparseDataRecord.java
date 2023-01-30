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

package org.vikamine.kernel.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * {@link SparseDataRecord} provides a sparse data representation (in contrast
 * to {@link FullDataRecord}).
 * 
 * @author atzmueller
 * 
 */

public class SparseDataRecord extends AbstractDataRecord {

    protected Map<Integer, Double> attributeValues = new HashMap<Integer, Double>();

    public SparseDataRecord() {
	super();
    }

    public SparseDataRecord(double weight) {
	super(weight);
    }

    protected SparseDataRecord(Map<Integer, Double> attributeValues,
	    double weight) {
	super(weight);
	this.attributeValues = attributeValues;
    }

    @Override
    public double getValue(int index) {
	Double result = attributeValues.get(index);
	if (result == null)
	    return 0;
	return result;
    }

    /**
     * Utility method for iterating for all non-zero attribute values (and the
     * corresponding attributes)
     * 
     * @return an iterator on the attributeValues Map, i.e., each entry in the
     *         Map.Entry denotes an attribute index/value pair
     */
    public Iterator<Map.Entry<Integer, Double>> getNonZeroAttributeValueEntriesIterator() {
	return attributeValues.entrySet().iterator();
    }

    @Override
    public DataRecord copy() {
	SparseDataRecord copy = new SparseDataRecord(getWeight());
	copy.attributeValues = new HashMap<Integer, Double>(attributeValues);
	copy.setDataset(getDataset());
	return copy;
    }

    @Override
    public synchronized void deleteAttributeAt(int index) {
	Map<Integer, Double> newAttributeValues = new HashMap<Integer, Double>();
	for (Map.Entry<Integer, Double> entry : attributeValues.entrySet()) {
	    if (entry.getKey() > index) {
		newAttributeValues.put(entry.getKey() - 1, entry.getValue());
	    } else {
		newAttributeValues.put(entry.getKey(), entry.getValue());
	    }
	}
	attributeValues = newAttributeValues;
    }

    @Override
    public synchronized void insertAttributeAt(int index) {
	Map<Integer, Double> newAttributeValues = new HashMap<Integer, Double>();
	for (Map.Entry<Integer, Double> entry : attributeValues.entrySet()) {
	    if (entry.getKey() >= index) {
		newAttributeValues.put(entry.getKey() + 1, entry.getValue());
	    } else {
		newAttributeValues.put(entry.getKey(), entry.getValue());
	    }
	}
	attributeValues = newAttributeValues;
	attributeValues.put(index, Value.MISSING_VALUE);
    }

    @Override
    public void setValue(int attIndex, double value) {
	attributeValues.put(attIndex, value);
    }

}
