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

import java.util.logging.Logger;

import org.vikamine.kernel.util.VKMUtil;

/**
 * 
 * {@link SingleValue} represents a single value instance.
 * 
 * @author atzmueller
 *
 */
public class SingleValue extends Value {

    /**
     * creates a new SingleValue
     * 
     * @param attribute
     * @param val
     * @return
     */
    public static SingleValue createSimpleSingleValue(Attribute attribute,
	    double val) {
	SingleValue value = new SingleValue(attribute, val, attribute.getId()
		+ "-" + VKMUtil.getFormattedDoubleString(val));
	return value;
    }

    /**
     * creates a new SingleValue and sets the WrappedKnowledgeBaseObject with
     * given description
     * 
     * @param attribute
     * @param val
     * @param description
     * @return
     */
    public static SingleValue createSimpleSingleValue(Attribute attribute,
	    double val, String id) {
	SingleValue value = new SingleValue(attribute, val, id);
	return value;
    }

    private double value;

    private Attribute attribute;

    protected SingleValue(String id) {
	super(id);
	setDescription(id);
    }

    public SingleValue(Attribute attribute, double value, String id) {
	super(id);
	if (attribute == null) {
	    IllegalArgumentException ex = new IllegalArgumentException(
		    "Illegal attribute argument - Attribute is null!");
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "DefaultSingleValue", ex);
	    throw ex;
	}
	this.attribute = attribute;
	this.value = value;
	setDescription(id);
    }

    @Override
    public int computeCachedHashCode() {
	int hash = 17;
	hash = 37 * hash + attribute.hashCode();
	long tmp = Double.doubleToLongBits(value);
	hash = 37 * hash + (int) (tmp ^ (tmp >>> 32));
	return hash;
    }

    public double doubleValue() {
	return value;
    }

    @Override
    public boolean isDefaultValue() {
	return value == 0;
    }

    @Override
    public boolean equals(Object object) {
	if (this == object)
	    return true;
	else if ((object == null) || (getClass() != object.getClass())) {
	    return false;
	} else {
	    SingleValue other = (SingleValue) object;
	    if (!this.attribute.equals(other.attribute)) {
		return false;
	    }
	    return Value.equalValues(value, other.value);
	}
    }

    @Override
    public Attribute getAttribute() {
	return attribute;
    }

    @Override
    public String getDescription() {
	if ((attribute).isNumeric()) {
	    return VKMUtil.getFormattedDoubleString(doubleValue());
	} else {
	    return super.getDescription();
	}
    }

    @Override
    public String getId() {
	if ((attribute).isNumeric()) {
	    return String.valueOf(value);
	}
	return super.getId();
    }

    @Override
    public boolean isMissingValue() {
	return false;
    }

    @Override
    public boolean isValueContainedInInstance(DataRecord instance) {
	return Value.equalValues(value, instance.getValue(attribute));
    }

    /**
     * Sets the value of given value to this value.
     * 
     * @param instance
     */
    public void setValue(DataRecord instance) {
	if (attribute instanceof NominalAttribute) {
	    instance.setValue(attribute, value);
	} else {
	    throw new UnsupportedOperationException(
		    "This is only supported for BasicAttributes!");
	}
    }

    @Override
    public String toString() {
	return getId();
    }
}
