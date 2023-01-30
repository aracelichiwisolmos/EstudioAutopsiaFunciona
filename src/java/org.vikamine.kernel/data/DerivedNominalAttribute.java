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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vikamine.kernel.formula.FormulaNumberElement;

/**
 * The Class {@link DerivedNominalAttribute}, i.e., a nominal derived attribute.
 * 
 * @author atzmueller
 */
public class DerivedNominalAttribute extends NominalAttribute implements
	DerivedAttribute {

    /** The cache. Maps the instances of DataRecords to their computed value */
    private Map<Long, Double> cache;

    /** The cache. Maps the instances of DataRecords to their computed value */
    private final Map<String, Integer> valueToIndexCache;

    /** The formula. */
    private FormulaNumberElement formula;

    /**
     * Instantiates a new derived nominal attribute.
     * 
     * @param attributeID
     *            the attribute id
     * @param valuesList
     *            the list of values
     */
    public DerivedNominalAttribute(String id, List<Value> valuesList) {
	super(id);
	allValues = valuesList;
	rawValues = new ArrayList<Object>();
	valueIndicesHash = new Hashtable<Object, Integer>();
	valueToIndexCache = new HashMap<String, Integer>();
    }

    /**
     * Returns the value of the given instance for this attribute. This method
     * works exactly like the method instance.value(thisAttribute), if this
     * would work.
     * <p>
     * So in most cases the returned value is an integer with the index of the
     * value.
     * 
     * @param instance
     *            the instance
     * 
     * @return the double
     */
    @Override
    public double computeValue(DataRecord instance) {
	int i = 0;
	for (Iterator<Value> iter = allValuesIterator(); iter.hasNext(); i++) {
	    Value value = iter.next();
	    if (value.isValueContainedInInstance(instance)) {
		valueToIndexCache.put(value.getDescription(), i);
		return i;
	    }
	}
	return Double.NaN;
    }

    @Override
    public int getIndexOfValue(String s) {
	Integer integer = valueToIndexCache.get(s);
	if (integer == null) {
	    return -1;
	}
	return integer;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DerivedAttribute#createCache(java.util.Iterator)
     */
    @Override
    public void createCache(Iterator<DataRecord> populationInstanceIterator) {
	Map<Long, Double> newCache = new HashMap<Long, Double>();
	while (populationInstanceIterator.hasNext()) {
	    DataRecord inst = populationInstanceIterator.next();
	    newCache.put(inst.getID(), value(inst));
	}
	synchronized (this) {
	    cache = newCache;
	}
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DerivedAttribute#getFormula()
     */
    @Override
    public FormulaNumberElement getFormula() {
	return formula;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultNominalAttribute#getRawValue(int)
     */
    @Override
    // The DerivedNominalAttribute dont have rawValues
    public String getRawValue(int valIndex) {
	throw new UnsupportedOperationException(
		"Derived Attributes have no rawValues!");
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultNominalAttribute#getRawValuesCount()
     */
    @Override
    // The DerivedNominalAttribute does not have rawValues
    protected int getRawValuesCount() {
	return 0;
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DefaultNominalAttribute
     * #getValue(org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public double getValue(DataRecord instance) {
	return value(instance);
    }

    /*
     * implements/overrides: @see
     * org.vikamine.kernel.data.DerivedAttribute#setFormula
     * (org.vikamine.kernel.formula.FormulaNumberElement)
     */
    @Override
    public void setFormula(FormulaNumberElement formula) {
	this.formula = formula;
    }

    /**
     * Value.
     * 
     * @param instance
     *            the instance
     * 
     * @return the double
     */
    public synchronized double value(DataRecord instance) {
	if (cache != null) {
	    long id = instance.getID();
	    Double value = cache.get(id);
	    if (value == null) {
		value = new Double(computeValue(instance));
		cache.put(id, value);
	    }
	    return value.doubleValue();
	} else {
	    // Cache not created
	    return computeValue(instance);
	}
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
	    // TODO: implement copy for DerivedNominalAttribute
	    values.add(this.getRawValue(i));
	}
	NominalAttribute copiedNominalAttribute = new NominalAttribute(
		this.getId(), values);
	return copiedNominalAttribute;
    }

    @Override
    // TODO: refactor this ...
    public Iterator<Value> usedValuesIterator(IDataRecordSet dataset) {
	return allValuesIterator();
    }
}
