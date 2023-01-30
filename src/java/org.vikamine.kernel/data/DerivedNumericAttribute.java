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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.vikamine.kernel.util.DistinctionFinder;

/**
 * The Class {@link DerivedNumericAttribute}, for generated numeric attributes.
 * 
 * @author atzmueller, lemmerich.
 */
public class DerivedNumericAttribute extends NumericAttribute implements
	DerivedAttribute {

    /** The formula. */
    private FormulaNumberElement formula;

    /** The cache. Maps the instances of DataRecords to their computed value */
    private Map<Long, Double> cache;

    /**
     * Instantiates a new derived numeric attribute.
     * 
     * @param attributeName
     *            the attribute name
     */
    protected DerivedNumericAttribute(String attributeName) {
	super(attributeName);
    }

    /*
     * implements/overrides:
     * 
     * @see
     * org.vikamine.kernel.data.DerivedAttribute#computeValue(org.vikamine.kernel
     * .data.DataRecord)
     */
    @Override
    public double computeValue(DataRecord instance) {
	EvaluationData data = new EvaluationData();
	data.setInstance(instance);
	return getFormula().eval(data).doubleValue();
    }

    /*
     * implements/overrides:
     * 
     * @see
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
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.DerivedAttribute#getFormula()
     */
    @Override
    public FormulaNumberElement getFormula() {
	return formula;
    }

    /*
     * implements/overrides:
     * 
     * @see
     * org.vikamine.kernel.data.DerivedAttribute#setFormula(org.vikamine.kernel
     * .formula.FormulaNumberElement)
     */
    @Override
    public void setFormula(FormulaNumberElement formula) {
	this.formula = formula;
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#getValue
     * (org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public double getValue(DataRecord instance) {
	return value(instance);
    }

    /**
     * Computes the value of this DerivedAttribute in the given DataRecord.
     * 
     * @param instance
     *            the DataRecord instance
     * 
     * @return the value of this DerivedAttribute in the given DataRecord.
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
     * @see
     * org.vikamine.kernel.data.NumericAttribute#usedValuesIterator(org.vikamine
     * .kernel.data.DataRecordSet)
     */
    @Override
    public Iterator<Value> usedValuesIterator(IDataRecordSet dataset) {
	List<Double> values = new LinkedList<Double>();
	for (DataRecord inst : dataset) {
	    values.add(new Double(value(inst)));
	}
	Collections.sort(values);
	List<Value> distinctValues = new LinkedList<Value>();
	DistinctionFinder distinct = new DistinctionFinder();
	for (Double value : values) {
	    distinct.addValue(value.doubleValue());
	    if (distinct.isNewValue()) {
		distinctValues.add(SingleValue.createSimpleSingleValue(this,
			value));
	    }
	}
	return distinctValues.iterator();
    }

    /*
     * implements/overrides: @see org.vikamine.kernel.data.Attribute#copy()
     */
    @Override
    public DerivedNumericAttribute copy() {
	DerivedNumericAttribute copiedNumericAttribute = new DerivedNumericAttribute(
		this.getId());
	copiedNumericAttribute.usedInstancesForAttributeValues = usedInstancesForAttributeValues;
	copiedNumericAttribute.weakDatasetReference = weakDatasetReference;
	copiedNumericAttribute.formula = formula;
	return copiedNumericAttribute;
    }

}
