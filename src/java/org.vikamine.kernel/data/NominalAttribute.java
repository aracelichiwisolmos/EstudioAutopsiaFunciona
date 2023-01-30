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
 * Created on 07.07.2006
 *
 */
package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.vikamine.kernel.util.VKMUtil;

/**
 * The Class {@link NominalAttribute} represents a nominal attribute.
 * 
 * @author atzmueller, lemmerich
 */
public class NominalAttribute extends DefaultAttribute implements
	Iterable<Value> {

    /** The raw values. */
    protected List<Object> rawValues;

    /** The value indices hash. Maps each value to its index */
    protected Hashtable<Object, Integer> valueIndicesHash;

    /** Strings longer than this will be stored compressed. */
    public static final int STRING_COMPRESS_THRESHOLD = 128;

    /**
     * Instantiates a new default nominal attribute.
     */
    protected NominalAttribute(String id) {
	super(id);
    }

    protected List<Value> allValues;

    /**
     * Instantiates a new default nominal attribute.
     * 
     * @param attributeName
     *            the attribute name
     * @param attributeValues
     *            the attribute values
     */
    public NominalAttribute(String id, List<String> attributeValues) {
	super(id);

	rawValues = new ArrayList<Object>(attributeValues.size());
	valueIndicesHash = new Hashtable<Object, Integer>(
		attributeValues.size());
	for (int i = 0; i < attributeValues.size(); i++) {
	    Object store = toStoredValue(attributeValues.get(i));
	    if (valueIndicesHash.containsKey(store)) {
		throw new IllegalArgumentException("A nominal attribute (" + id
			+ ") cannot" + " have duplicate labels (" + store
			+ ").");
	    }
	    rawValues.add(store);
	    valueIndicesHash.put(store, i);
	}
    }

    /**
     * Adds the value to this attribute.
     * 
     * @param value
     *            the value to be added
     */
    public final void addValue(String value) {
	Object store = toStoredValue(value);
	rawValues.add(store);
	valueIndicesHash.put(store, Integer.valueOf(rawValues.size() - 1));
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#copy()
     */
    @Override
    public NominalAttribute copy() {
	List<String> values = new ArrayList<String>(this.getValuesCount());
	for (int i = 0; i < this.getRawValuesCount(); i++) {
	    values.add(this.getRawValue(i));
	}
	NominalAttribute copiedNominalAttribute = new NominalAttribute(
		this.getId(), values);
	return copiedNominalAttribute;
    }

    /**
     * Gets the index of value.
     * 
     * @param value
     *            the value
     * 
     * @return the index of value
     */
    public int getIndexOfValue(String value) {
	Object store = toStoredValue(value);
	Integer val = valueIndicesHash.get(store);
	if (val == null)
	    return -1;
	else
	    return val.intValue();
    }

    /**
     * Gets the raw value.
     * 
     * @param valIndex
     *            the val index
     * 
     * @return the raw value
     */
    public String getRawValue(int valIndex) {
	Object val = rawValues.get(valIndex);
	// If we're storing strings compressed, uncompress it.
	if (val instanceof CompressedSerializedObject) {
	    val = ((CompressedSerializedObject) val).getObject();
	}
	return (String) val;
    }

    /**
     * Gets the raw values count.
     * 
     * @return the raw values count
     */
    protected int getRawValuesCount() {
	return rawValues.size();
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#getValue(org.vikamine.kernel .
     * data.DataRecord)
     */
    @Override
    public double getValue(DataRecord instance) {
	return instance.getValue(instance.getDataset().getIndex(this));
    }

    /*
     * implements/overrides:
     * 
     * @see
     * org.vikamine.kernel.data.DefaultAttribute#isMissingValue(org.vikamine
     * .kernel .data.DataRecord)
     */
    @Override
    public boolean isMissingValue(DataRecord record) {
	return Value.isMissingValue(record.getValue(this));
    }

    /**
     * Returns value, if value's length < STRING_COMPRESS_THRESHOLD, and a
     * CompressedSerializedObject to this value else.
     * 
     * @param value
     *            the value
     * 
     * @return the object
     */
    protected Object toStoredValue(String value) {
	Object store = value;
	if (value.length() > NominalAttribute.STRING_COMPRESS_THRESHOLD) {
	    try {
		store = new CompressedSerializedObject(value);
	    } catch (Exception ex) {
		Logger.getLogger(getClass().getName()).warning(
			"Couldn't compress attribute value - \n" + value + "\n"
				+ " storing uncompressed.");
	    }
	}
	return store;
    }

    /*
     * implements/overrides:
     * 
     * @see
     * org.vikamine.kernel.data.Attribute#usedValuesIterator(org.vikamine.kernel
     * .data .DataRecordSet)
     */
    @Override
    public Iterator<Value> usedValuesIterator(IDataRecordSet dataset) {
	boolean[] indices = new boolean[rawValues.size()];
	Arrays.fill(indices, false);
	for (DataRecord record : dataset) {
	    indices[(int) record.getValue(this)] = true;
	}
	List<Value> usedValues = new LinkedList<Value>();
	for (int i = 0; i < indices.length; i++) {
	    if (indices[i]) {
		usedValues.add(getNominalValue(i));
	    }
	}
	return usedValues.iterator();
    }

    @Override
    public String toString() {
	StringBuffer text = new StringBuffer();
	text.append(ARFF_ATTRIBUTE_IDENTIFIER).append(" ")
		.append(VKMUtil.quote(getId())).append(" ");
	text.append('{');
	for (Iterator<Value> iter = allValuesIterator(); iter.hasNext();) {
	    text.append(VKMUtil.quote(iter.next().toString()));
	    if (iter.hasNext())
		text.append(',');
	}
	text.append('}');
	return text.toString();
    }

    private void checkValuesInitialization() {
	if ((allValues == null) || (allValues.isEmpty())) {
	    throw new IllegalStateException("Nominal attribute " + this.getId()
		    + " was not initialized correctly - no values set.");
	}
    }

    public Value getNominalValue(int value) {
	checkValuesInitialization();
	return allValues.get(value);
    }

    public Value getNominalValue(DataRecord instance) {
	checkValuesInitialization();
	double value = getValue(instance);
	if (Double.isNaN(value)) {
	    return Value.missingValue(this);
	}
	return allValues.get((int) value);
    }

    public Value getNominalValueFromID(String valueID) {
	for (Iterator<Value> iter = allValuesIterator(); iter.hasNext();) {
	    Value val = iter.next();
	    if (val.getId().equals(valueID)) {
		return val;
	    }
	}
	NoSuchElementException ex = new NoSuchElementException("Value for "
		+ valueID + " not found in Attribute'" + getDescription()
		+ "'!");
	Logger.getLogger(getClass().getName()).throwing(getClass().getName(),
		"getNominalValue", ex);
	throw ex;
    }

    protected List<Value> getNominalValues() {
	checkValuesInitialization();
	return allValues;
    }

    public int getValuesCount() {
	checkValuesInitialization();
	return allValues.size();
    }

    @Override
    public boolean isNominal() {
	return true;
    }

    protected void setNominalValues(List<Value> nominalValues) {
	if ((nominalValues == null) || (nominalValues.isEmpty())) {
	    throw new IllegalStateException("Nominal attribute " + this.getId()
		    + " is set to null or empty new values.");
	}
	this.allValues = nominalValues;
    }

    /**
     * Iterates over all Values for this attribute, whether they are used or not
     */
    public Iterator<Value> allValuesIterator() {
	checkValuesInitialization();
	return allValues.iterator();
    }

    /**
     * Iterates over all Values for this attribute
     */
    @Override
    public Iterator<Value> iterator() {
	return allValuesIterator();
    }
}
