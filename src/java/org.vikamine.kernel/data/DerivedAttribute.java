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

import java.util.Iterator;

import org.vikamine.kernel.formula.FormulaNumberElement;

/**
 * The Interface {@link DerivedAttribute}: It is used for attributes that can be
 * dynamically added to a dataset.
 * 
 * @author atzmueller, lemmerich
 */
public interface DerivedAttribute extends Attribute {

    /**
     * Returns the value of the given instance for this attribute. This method
     * works exactly like the method instance.value(thisAttribute), if this
     * would work.
     * <p>
     * So in most cases the returned value is an integer with the index of the
     * value.
     * 
     * @param instance
     *            the DataRecord, for which the value is computed.
     * 
     * @return the computedValue
     */
    public double computeValue(DataRecord instance);

    /**
     * Creates the Cache for this attribute and these dataRecords. The cache
     * maps the instances of DataRecords to their computed value
     */
    public void createCache(Iterator<DataRecord> populationInstanceIterator);

    /**
     * Gets the formula for this derived attribute.
     * 
     * @return the formula
     */
    public FormulaNumberElement getFormula();

    /**
     * Sets the formula for this derived attribute.
     * 
     * @param formula
     *            the new formula
     */
    public void setFormula(FormulaNumberElement formula);

}
