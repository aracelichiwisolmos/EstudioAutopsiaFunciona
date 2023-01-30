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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.vikamine.kernel.util.DistinctionFinder;
import org.vikamine.kernel.util.VKMUtil;

/**
 * The Class {@link DateAttribute} for representing dates.
 * 
 * @author lemmerich
 */
public class DateAttribute extends DefaultAttribute {

    /** The Constant ARFF_ATTRIBUTE_DATE_IDENTIFIER. */
    static final String ARFF_ATTRIBUTE_DATE_IDENTIFIER = "date";

    /** The date format. */
    private SimpleDateFormat dateFormat;

    /** The used instances for attribute values. */
    private BitSet usedInstancesForAttributeValues;

    /** The weak dataset reference. */
    private WeakReference<IDataRecordSet> weakDatasetReference;

    /**
     * Instantiates a new date attribute.
     * 
     * @param attributeName
     *            the attribute name
     */
    public DateAttribute(String attributeName) {
	this(attributeName, null);
    }

    /**
     * Instantiates a new date attribute.
     * 
     * @param attributeName
     *            the attribute name
     * @param dateFormat
     *            the date format
     */
    public DateAttribute(String id, String dateFormat) {
	super(id);
	if (dateFormat != null) {
	    this.dateFormat = new SimpleDateFormat(dateFormat);
	} else {
	    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	}
	this.dateFormat.setLenient(false);
    }

    /*
     * implements/overrides: @see org.vikamine.kernel.data.Attribute#copy()
     */
    @Override
    public DateAttribute copy() {
	DateAttribute copiedDateAttribute = new DateAttribute(this.getId());
	copiedDateAttribute.usedInstancesForAttributeValues = usedInstancesForAttributeValues;
	copiedDateAttribute.weakDatasetReference = weakDatasetReference;
	return copiedDateAttribute;
    }

    /**
     * Creates the used instances for attribute values.
     */
    private void createUsedInstancesForAttributeValues() {
	usedInstancesForAttributeValues = new BitSet();
	List<Integer> sortedInstances = new LinkedList<Integer>();
	for (int i = 0; i < weakDatasetReference.get().getNumInstances(); i++) {
	    sortedInstances.add(Integer.valueOf(i));
	}
	Collections.sort(sortedInstances, new Comparator<Integer>() {
	    @Override
	    public int compare(Integer i1, Integer i2) {
		DataRecord inst1 = weakDatasetReference.get()
			.get(i1.intValue());
		DataRecord inst2 = weakDatasetReference.get()
			.get(i2.intValue());
		return Double.compare(inst1.getValue(DateAttribute.this),
			inst2.getValue(DateAttribute.this));
	    }

	});
	DistinctionFinder distinct = new DistinctionFinder();
	int previous = 0;
	for (Iterator<Integer> iter = sortedInstances.iterator(); iter
		.hasNext();) {
	    int i = (iter.next()).intValue();
	    DataRecord inst = weakDatasetReference.get().get(i);
	    distinct.addValue(inst.getValue(DateAttribute.this));
	    if (distinct.isNewValue()) {
		usedInstancesForAttributeValues.set(previous);
	    }
	    previous = i;
	}
	usedInstancesForAttributeValues.set(previous);
    }

    /**
     * Format date from (double)Date.
     * 
     * @param date
     *            the date
     * 
     * @return the string
     */
    public String formatDate(double date) {
	return dateFormat.format(new Date((long) date));
    }

    /*
     * implements/overrides: @see org.vikamine.kernel.data.Attribute#getValue
     * (org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public double getValue(DataRecord instance) {
	return instance.getValue(instance.getDataset().getIndex(this));
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultAttribute#isDate() Always true!
     */
    @Override
    public boolean isDate() {
	return true;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultAttribute#isMissingValue
     * (org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public boolean isMissingValue(DataRecord record) {
	return Value.isMissingValue(record.getValue(this));
    }

    /**
     * Parses the date.
     * 
     * @param string
     *            the string
     * 
     * @return the double
     * 
     * @throws ParseException
     *             the parse exception
     */
    public double parseDate(String string) throws ParseException {
	long time = dateFormat.parse(string).getTime();
	return time;
    }

    @Override
    public String toString() {
	StringBuffer text = new StringBuffer();
	text.append(ARFF_ATTRIBUTE_IDENTIFIER).append(" ")
		.append(VKMUtil.quote(getId())).append(" ");
	text.append(ARFF_ATTRIBUTE_DATE_IDENTIFIER).append(" ")
		.append(VKMUtil.quote(dateFormat.toPattern()));
	return text.toString();
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.Attribute#usedValuesIterator
     * (org.vikamine.kernel.data.DataRecordSet)
     */
    @Override
    public Iterator<Value> usedValuesIterator(IDataRecordSet dataset) {
	class NumericIterator implements Iterator<Value> {
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
		double value = inst.getValue(DateAttribute.this);
		long ms = (long) value;
		return SingleValue.createSimpleSingleValue(DateAttribute.this,
			value, Long.toString(ms));
	    }

	    private void nextBit() {
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

	if ((usedInstancesForAttributeValues == null)
		|| (weakDatasetReference == null)
		|| (!weakDatasetReference.get().equals(dataset))) {
	    weakDatasetReference = new WeakReference<IDataRecordSet>(dataset);
	    createUsedInstancesForAttributeValues();
	}
	return new NumericIterator();
    }

}
