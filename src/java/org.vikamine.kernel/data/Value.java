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
 * Created on 23.03.2004
 *
 */
package org.vikamine.kernel.data;

import java.io.Serializable;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Logger;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.util.VKMUtil;
import org.w3c.dom.Element;

/**
 * A {@link Value} represents an attribute value.
 * 
 * @author atzmueller
 */
public abstract class Value extends DescribableDataObject {

    public static class CustomDiscretizedValue extends Value {

	private Attribute attribute;

	private final Attribute discretizedAtt;

	protected boolean leftOpen = true;

	protected double max = Double.POSITIVE_INFINITY;

	protected double min = Double.NEGATIVE_INFINITY;

	protected boolean rightOpen = false;

	private CustomDiscretizedValue(Attribute discretizedAtt,
		double minimum, double maximum) {
	    super(discretizedAtt.getId() + ":"
		    + VKMUtil.getFormattedDoubleString(minimum) + "-"
		    + VKMUtil.getFormattedDoubleString(maximum));
	    this.discretizedAtt = discretizedAtt;
	    setMin(minimum);
	    setMax(maximum);
	}

	@Override
	public int computeCachedHashCode() {
	    long tmp = Double.doubleToLongBits(max);
	    int code = (int) (tmp ^ (tmp >>> 32));
	    tmp = Double.doubleToLongBits(min);
	    int code2 = (int) (tmp ^ (tmp >>> 32));
	    return code ^ code2;
	}

	public boolean contains(double value) {
	    if (Double.isNaN(value)) {
		return Double.isNaN(getMin());
	    } else {
		if (getMin() < value && value < getMax()) {
		    return true;
		} else if (value == getMin() && !isLeftOpen()) {
		    return true;
		} else if (value == getMax() && !isRightOpen()) {
		    return true;
		} else {
		    return false;
		}
	    }
	}

	@Override
	public boolean equals(Object object) {
	    if (this == object)
		return true;
	    else if ((object == null) || (getClass() != object.getClass())) {
		return false;
	    } else {
		CustomDiscretizedValue d = (CustomDiscretizedValue) object;
		if (!equalValues(getMin(), d.getMin())) {
		    return false;
		}
		return equalValues(getMax(), d.getMax());
	    }
	}

	@Override
	public Attribute getAttribute() {
	    return attribute;
	}

	@Override
	public String getDescription() {
	    if (Double.isNaN(getMin())) {
		assert Double.isNaN(getMax());
		return CONST_MISSING_STRING;
	    } else {
		if (equalValues(getMin(), getMax())) {
		    return String.valueOf(getMin());
		}
		if (Double.isInfinite(getMin())) {
		    return "<= " + getMax();
		}
		if (Double.isInfinite(getMax())) {
		    return "> " + getMin();
		}
		return getMin() + " - " + getMax();
	    }
	}

	@Override
	public String getDescription(Describer d) {
	    return d.createDescription(this);
	}

	public Attribute getDiscretizedAttribute() {
	    return discretizedAtt;
	}

	@Override
	public String getId() {
	    StringBuffer valStrBuffer = new StringBuffer();
	    valStrBuffer.append(attribute);
	    valStrBuffer.append(leftOpen ? "(" : "[");
	    valStrBuffer.append(min);
	    valStrBuffer.append(",");
	    valStrBuffer.append(max);
	    valStrBuffer.append(rightOpen ? ")" : "]");
	    return valStrBuffer.toString();

	}

	public double getMax() {
	    return max;
	}

	public double getMin() {
	    return min;
	}

	protected double getValue(DataRecord instance) {
	    return instance.getValue(discretizedAtt);
	}

	public boolean isLeftOpen() {
	    return leftOpen;
	}

	@Override
	public boolean isMissingValue() {
	    return false;
	}

	public boolean isRightOpen() {
	    return rightOpen;
	}

	@Override
	public boolean isValueContainedInInstance(DataRecord instance) {
	    return contains(getValue(instance));
	}

	public void setAttribute(Attribute att) {
	    this.attribute = att;
	}

	public void setLeftOpen(boolean leftOpen) {
	    this.leftOpen = leftOpen;
	}

	public void setMax(double max) {
	    this.max = max;
	}

	public void setMin(double min) {
	    this.min = min;
	}

	public void setRightOpen(boolean rightOpen) {
	    this.rightOpen = rightOpen;
	}

	@Override
	public String toString() {
	    return getId();
	}
    }

    public static class MissingValue extends Value {

	private final Attribute attribute;

	MissingValue(Attribute att) {
	    super(Value.CONST_MISSING_STRING);
	    if (att == null) {
		Logger.getLogger(getClass().getName()).throwing(
			getClass().getName(), "MissingValue",
			new IllegalArgumentException("Illegal attribute argument - Attribute is null!"));
	    }
	    this.attribute = att;
	}

	@Override
	public int computeCachedHashCode() {
	    return attribute.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == this) {
		return true;
	    } else if (obj == null || obj.getClass() != getClass()) {
		return false;
	    } else {
		MissingValue that = (MissingValue) obj;
		return this.attribute.equals(that.attribute);
	    }
	}

	@Override
	public Attribute getAttribute() {
	    return attribute;
	}

	@Override
	public String getDescription() {
	    return CONST_MISSING_STRING;
	}

	@Override
	public String getDescription(Describer d) {
	    return d.createDescription(this);
	}

	@Override
	public String getId() {
	    return attribute.getId() + "aMissing";
	}

	@Override
	public boolean isMissingValue() {
	    return true;
	}

	@Override
	public boolean isValueContainedInInstance(DataRecord instance) {
	    if (instance.isMissing(attribute)) {
		return true;
	    } else {
		return false;
	    }
	}

	@Override
	public String toString() {
	    return getId();
	}

    }

    public static class UncompleteNominalValue extends Value {

	private final DerivedNominalValue value;

	private final Element condition;

	public UncompleteNominalValue(DerivedNominalValue val, Element condition) {
	    super(val.getId());
	    this.value = val;
	    this.condition = condition;
	}

	@Override
	public Attribute getAttribute() {
	    return null;
	}

	public Element getCondition() {
	    return condition;
	}

	@Override
	public String getDescription() {
	    return null;
	}

	@Override
	public String getDescription(Describer d) {
	    return d.createDescription(this);
	}

	@Override
	public String getId() {
	    return null;
	}

	public DerivedNominalValue getValue() {
	    return value;
	}

	@Override
	public boolean isMissingValue() {
	    return false;
	}

	@Override
	public boolean isValueContainedInInstance(DataRecord instance) {
	    return false;
	}

	@Override
	protected int computeCachedHashCode() {
	    return getValue().getId().hashCode();
	}

    }

    public static class ValueComparator implements Comparator<Value>,
	    Serializable {
	private static final long serialVersionUID = -4192351066855635055L;

	@Override
	public int compare(Value val1, Value val2) {
	    return Collator.getInstance().compare(val1.getId(), val2.getId());
	}
    }

    public static final String CONST_UNKNOWN = "MaU";
    public static final int CONST_NUM_UNKNOWN = -1;
    public static final String CONST_MISSING_STRING = "*Missing*";
    protected static final double MISSING_VALUE = Double.NaN;

    public static Value.CustomDiscretizedValue createCustomDiscretizedValue(
	    Attribute discretizedAtt, double min, double max) {
	return new Value.CustomDiscretizedValue(discretizedAtt, min, max);
    }

    public static String createMultiValueDefaultName(
	    Collection<SingleValue> values) {
	StringBuffer buff = new StringBuffer();
	buff.append("{");
	for (Iterator<SingleValue> iter = values.iterator(); iter.hasNext();) {
	    SingleValue sNValue = iter.next();
	    buff.append(sNValue.getId());
	    if (iter.hasNext()) {
		buff.append("; ");
	    }
	}
	buff.append("}");
	return buff.toString();
    }

    protected static boolean equalValues(double value1, double value2) {
	return equalValues(value1, value2, false);
    }

    public static boolean equalValues(double value1, double value2,
	    boolean withTolerance) {
	if (Double.isNaN(value1)) {
	    return Double.isNaN(value2);
	} else if (Double.isNaN(value2)) {
	    return false;
	} else {
	    if (withTolerance) {
		return VKMUtil.eq(value1, value2);
	    } else {
		return value1 == value2;
	    }
	}
    }

    public static boolean isMissingValue(double val) {
	return Double.isNaN(val);
    }

    public static boolean isUnknownValue(String value) {
	return CONST_UNKNOWN.equals(value);
    }

    public static boolean isUnknownValue(Value val) {
	return val.getId().equals(CONST_UNKNOWN);
    }

    public static double missingValue() {
	return MISSING_VALUE;
    }

    public static Value missingValue(Attribute attribute) {
	return new Value.MissingValue(attribute);
    }

    public abstract Attribute getAttribute();

    public abstract boolean isMissingValue();

    public boolean isDefaultValue() {
	return false;
    }

    public abstract boolean isValueContainedInInstance(DataRecord instance);

    public Value(String id) {
	super(id);
    }
}
