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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.vikamine.kernel.util.DistinctionFinder;
import org.vikamine.kernel.util.VKMUtil;

/**
 * The Class {@link NumericAttribute} is representing a numeric attribute.
 */
public class NumericAttribute extends DefaultAttribute {

    /** The Constant ARFF_ATTRIBUTE_NUMERIC_IDENTIFIER. */
    static final String ARFF_ATTRIBUTE_NUMERIC_IDENTIFIER = "numeric";

    /** The used instances for attribute values. */
    protected BitSet usedInstancesForAttributeValues;

    /** The weak dataset reference. */
    protected WeakReference<IDataRecordSet> weakDatasetReference;

    /**
     * Instantiates a new numeric attribute.
     * 
     * @param attributeName
     *            the attribute name
     */
    protected NumericAttribute(String id) {
	super(id);
    }

    /*
     * implements/overrides: @see org.vikamine.kernel.data.Attribute#copy()
     */
    @Override
    public NumericAttribute copy() {
	NumericAttribute copiedNumericAttribute = new NumericAttribute(
		this.getId());
	copiedNumericAttribute.usedInstancesForAttributeValues = usedInstancesForAttributeValues;
	copiedNumericAttribute.weakDatasetReference = weakDatasetReference;
	return copiedNumericAttribute;
    }

    /**
     * Creates the used instances for attribute values.
     */
    private void createUsedInstancesForAttributeValues() {
	usedInstancesForAttributeValues = new BitSet();
	List<Integer> sortedInstances = new ArrayList<Integer>();
	usedInstancesForAttributeValues.set(0, weakDatasetReference.get()
		.getNumInstances(), true);

	Collections.sort(sortedInstances, new Comparator<Integer>() {
	    @Override
	    public int compare(Integer i1, Integer i2) {
		DataRecord inst1 = weakDatasetReference.get()
			.get(i1.intValue());
		DataRecord inst2 = weakDatasetReference.get()
			.get(i2.intValue());
		return Double.compare(inst1.getValue(NumericAttribute.this),
			inst2.getValue(NumericAttribute.this));
	    }

	});
	DistinctionFinder distinct = new DistinctionFinder();
	int previous = 0;
	for (Iterator<Integer> iter = sortedInstances.iterator(); iter
		.hasNext();) {
	    int i = (iter.next()).intValue();
	    DataRecord inst = weakDatasetReference.get().get(i);
	    distinct.addValue(inst.getValue(NumericAttribute.this));
	    if (distinct.isNewValue()) {
		usedInstancesForAttributeValues.set(previous);
	    }
	    previous = i;
	}
	usedInstancesForAttributeValues.set(previous);
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#getValue
     * (org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public double getValue(DataRecord instance) {
	return instance.getValue(instance.getDataset().getIndex(this));
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.DefaultAttribute#getValuesCount()
     */
    public int getUsedValuesCount(IDataRecordSet dataset) {
	HashSet<Double> allUsedValues = new HashSet<Double>();
	for (DataRecord dr : dataset) {
	    allUsedValues.add(getValue(dr));
	}
	return allUsedValues.size();
	// if ((usedInstancesForAttributeValues == null)
	// || (weakDatasetReference == null)
	// || (!weakDatasetReference.get().equals(dataset))) {
	// weakDatasetReference = new WeakReference<DataRecordSet>(dataset);
	// createUsedInstancesForAttributeValues();
	// }
	// return usedInstancesForAttributeValues.cardinality();
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.DefaultAttribute#isMissingValue
     * (org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public boolean isMissingValue(DataRecord record) {
	return Value.isMissingValue(record.getValue(this));
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.DefaultAttribute#isNumeric()
     */
    @Override
    public boolean isNumeric() {
	return true;
    }

    @Override
    public String toString() {
	StringBuffer text = new StringBuffer();
	text.append(ARFF_ATTRIBUTE_IDENTIFIER).append(" ")
		.append(VKMUtil.quote(getId())).append(" ");
	text.append(ARFF_ATTRIBUTE_NUMERIC_IDENTIFIER);
	return text.toString();
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#usedValuesIterator
     * (org.vikamine.kernel.data.DataRecordSet)
     */
    @Override
    public Iterator<Value> usedValuesIterator(IDataRecordSet dataset) {
	if ((usedInstancesForAttributeValues == null)
		|| (weakDatasetReference == null)
		|| (!weakDatasetReference.get().equals(dataset))) {
	    weakDatasetReference = new WeakReference<IDataRecordSet>(dataset);
	    createUsedInstancesForAttributeValues();
	}
	return new NumericIterator();
    }

    private class NumericIterator implements Iterator<Value> {
	int currentBit;

	NumericIterator() {
	    currentBit = -1;
	    nextBit();
	}

	@Override
	public boolean hasNext() {
	    return (currentBit != -1)
		    && (currentBit < usedInstancesForAttributeValues.size());
	}

	@Override
	public SingleValue next() {
	    if (!hasNext()) {
		throw new NoSuchElementException("no more elements");
	    }
	    DataRecord inst = weakDatasetReference.get().get(currentBit);
	    nextBit();
	    double value = inst.getValue(NumericAttribute.this);
	    return SingleValue.createSimpleSingleValue(NumericAttribute.this,
		    value, VKMUtil.getFormattedDoubleString(value));
	}

	private void nextBit() {
	    // TODO: (FL: use BitSet.nextClearBit() here!?)
	    do {
		currentBit++;
	    } while (!usedInstancesForAttributeValues.get(currentBit)
		    && currentBit < usedInstancesForAttributeValues.size());
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException("Not supported");
	}
    }

    public Value getNumericValue(double value) {
	return SingleValue.createSimpleSingleValue(NumericAttribute.this,
		value, VKMUtil.getFormattedDoubleString(value));
    }

}
