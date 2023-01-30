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

/**
 * The Class {@link DefaultAttribute} is the base class of all attributes.
 * 
 * @author atzmueller, lemmerich
 * @date june/july 08
 */
public abstract class DefaultAttribute extends DescribableDataObject implements
	Attribute {

    /** The Constant ARFF_ATTRIBUTE_IDENTIFIER. */
    protected static final String ARFF_ATTRIBUTE_IDENTIFIER = "@attribute";

    /**
     * Instantiates a new default attribute.
     */
    protected DefaultAttribute(String id) {
	super(id);
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#isDate() is false by default.
     * overwritten by e.g. DateAttribute
     */
    @Override
    public boolean isDate() {
	return false;
    }

    /*
     * implements/overrides:
     * 
     * @see
     * org.vikamine.kernel.data.Attribute#isMissingValue(org.vikamine.kernel
     * .data. DataRecord)
     */
    @Override
    public abstract boolean isMissingValue(DataRecord record);

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#isNominal() is false by default.
     * Overwritten by NominalAttribute
     */
    @Override
    public boolean isNominal() {
	return false;
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#isNumeric() is false by default.
     * Overwritten for NumericAttributes
     */
    @Override
    public boolean isNumeric() {
	return false;
    }

    /*
     * implements/overrides:
     * 
     * @see org.vikamine.kernel.data.Attribute#isString() is false by default.
     * Overwritten by StringAttributes
     */
    @Override
    public boolean isString() {
	return false;
    }

    /**
     * Overrides {@link #equals(Object)}
     * 
     * Enforce object identity - also in subclasses
     */
    @Override
    public final boolean equals(Object other) {
	if (this == other) {
	    return true;
	} else {
	    return false;
	}
    }
}
