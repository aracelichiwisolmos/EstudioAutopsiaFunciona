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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.util.VKMUtil;

/**
 * The Class {@link StringAttribute} for handing attributes managing strings.
 * 
 * @author mat atzmueller, lemmerich
 */
public class StringAttribute extends NominalAttribute {

    /** The Constant ARFF_ATTRIBUTE_STRING_IDENTIFIER. */
    static final String ARFF_ATTRIBUTE_STRING_IDENTIFIER = "string";

    /**
     * Instantiates a new string attribute with given id.
     * 
     * @param attributeId
     *            the attribute's id
     */
    public StringAttribute(String attributeId) {
	super(attributeId);
	rawValues = new ArrayList<Object>();
	valueIndicesHash = new Hashtable<Object, Integer>();
    }

    /**
     * Adds a string value to this attribute.
     * 
     * @param value
     *            the value to be added to this attriute
     * 
     * @return the index, of the added value in this StringAttributes RawValues
     */
    public int addStringValue(String value) {
	cachedHashCodeIsUpToDate = false;
	Object store = toStoredValue(value);
	Integer index = valueIndicesHash.get(store);
	if (index != null) {
	    return index.intValue();
	} else {
	    int intIndex = rawValues.size();
	    rawValues.add(store);
	    valueIndicesHash.put(store, intIndex);
	    return intIndex;
	}
    }

    /**
     * Copies the value at position index of given StringAttribute to this
     * attribute
     * 
     * @param src
     *            source StringATtribute to copy from
     * @param index
     *            the index of the copied attribute
     */
    private void addStringValue(StringAttribute src, int index) {
	cachedHashCodeIsUpToDate = false;
	Object store = src.rawValues.add(index);
	Integer oldIndex = valueIndicesHash.get(store);
	if (oldIndex != null) {
	    return;
	} else {
	    int intIndex = rawValues.size();
	    rawValues.add(store);
	    valueIndicesHash.put(store, intIndex);
	    return;
	}
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultNominalAttribute#copy()
     */
    @Override
    public StringAttribute copy() {
	StringAttribute copiedStringAttribute = new StringAttribute(
		this.getId());
	for (int i = 0; i < this.getRawValuesCount(); i++) {
	    copiedStringAttribute.addStringValue(this, i);
	}
	return copiedStringAttribute;
    }

    /**
     * Recreates the nominal attribute values.
     */
    private void createFreshNominalAttributeValues() {
	if ((allValues == null) || (allValues.isEmpty())) {
	    // the String attribute case ...
	    allValues = new LinkedList<Value>();
	    for (int i = 0; i < getRawValuesCount(); i++) {
		String id = getRawValue(i);
		SingleValue val = SingleValue.createSimpleSingleValue(this, i,
			id);
		allValues.add(val);
	    }
	}
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.NominalAttribute#isNominal()
     */
    @Override
    public boolean isNominal() {
	return false;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultAttribute#isString()
     */
    @Override
    public boolean isString() {
	return true;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.NominalAttribute#allValuesIterator()
     */
    @Override
    public Iterator<Value> allValuesIterator() {
	createFreshNominalAttributeValues();
	return allValues.iterator();
    }

    @Override
    public Value getNominalValue(int value) {
	createFreshNominalAttributeValues();
	return allValues.get(value);
    }

    @Override
    public String toString() {
	StringBuffer text = new StringBuffer();
	text.append(ARFF_ATTRIBUTE_IDENTIFIER).append(" ")
		.append(VKMUtil.quote(getId())).append(" ");
	text.append(" STRING");
	return text.toString();
    }

    public void buildValues() {
	List<Value> nominalValues = new LinkedList<Value>();
	for (int i = 0; i < this.getRawValuesCount(); i++) {
	    Value val = new SingleValue(this, i, this.getRawValue(i));
	    nominalValues.add(val);
	}
	setNominalValues(nominalValues);
    }
}
