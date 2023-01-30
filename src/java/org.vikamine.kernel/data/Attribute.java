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
 * Created on 10.12.2004
 *
 */
package org.vikamine.kernel.data;

import java.util.Iterator;

import org.vikamine.kernel.Describable;

/**
 * The Interface {@link Attribute}, for a dataset attribute.
 * 
 * @author atzmueller, lemmerich
 * @date as interface 2008.06.26
 */
public interface Attribute extends Describable {

    /**
     * Copy. in fact that is a version of clone() (!)
     * 
     * @return the object
     */
    public Attribute copy();

    /*
     * (non-Javadoc)
     * 
     * @see org.vikamine.kernel.Describable#getId()
     */
    @Override
    public String getId();

    /**
     * Checks if is date.
     * 
     * @return true, if is date
     */
    public boolean isDate();

    /**
     * Checks if is missing value.
     * 
     * @param record
     *            the record
     * 
     * @return true, if is missing value
     */
    public abstract boolean isMissingValue(DataRecord record);

    /**
     * Checks if is nominal.
     * 
     * @return true, if is nominal
     */
    public boolean isNominal();

    /**
     * Checks if is numeric.
     * 
     * @return true, if is numeric
     */
    public boolean isNumeric();

    /**
     * Checks if is string.
     * 
     * @return true, if is string
     */
    public boolean isString();

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description);

    /**
     * Used values iterator.
     * 
     * @param dataset
     *            the dataset
     * 
     * @return the iterator<value>
     */
    public abstract Iterator<Value> usedValuesIterator(IDataRecordSet dataset);

    /**
     * Returns the value of this attribute in the given DataRecord
     * 
     * @param instance
     *            The DataRecord, from which this attributes value is resolved
     * @return the value of this attribute in the given DataRecord
     */
    public abstract double getValue(DataRecord instance);
}
