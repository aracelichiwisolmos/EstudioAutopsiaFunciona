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

import org.vikamine.kernel.util.VKMUtil;

/**
 * Base representation of a record of a dataset.
 * 
 * @author atzmueller, lemmerich
 * 
 */

public abstract class AbstractDataRecord implements DataRecord {

    protected IDataRecordSet recordSet;

    protected double weight;

    private volatile int hashCode = 0;

    private long id;

    public AbstractDataRecord() {
	this(1);
    }

    public AbstractDataRecord(double weight) {
	super();
	this.weight = weight;
    }

    @Override
    public final IDataRecordSet getDataset() {
	return recordSet;
    }

    @Override
    public final long getID() {
	return id;
    }

    public void setID(long id) {
	this.id = id;
    }

    @Override
    public boolean equals(Object other) { // test fo equality of ID, same for
	// hashCode!
	if (this == other)
	    return true;
	else if ((other == null) || (getClass() != other.getClass())) {
	    return false;
	} else {
	    AbstractDataRecord otherInstance = (AbstractDataRecord) other;
	    return this.getID() == otherInstance.getID();
	}
    }

    @Override
    public int hashCode() {
	if (hashCode == 0) {
	    hashCode = Long.valueOf(getID()).hashCode();
	}
	return hashCode;
    }

    @Override
    public int getNumAttributes() {
	return getDataset().getNumAttributes();
    }

    @Override
    public final double getWeight() {
	return weight;
    }

    @Override
    public final void setWeight(double weight) {
	this.weight = weight;
    }

    @Override
    public String toString() {
	StringBuffer text = new StringBuffer();
	for (int i = 0; i < getNumAttributes(); i++) {
	    if (i > 0)
		text.append(",");
	    text.append(toString(i));
	}
	return text.toString();
    }

    protected final String toString(int attIndex) {
	StringBuffer text = new StringBuffer();
	if (isMissing(attIndex)) {
	    text.append("?");
	} else {
	    if (recordSet == null) {
		text.append(VKMUtil.doubleToString(getValue(attIndex), 6));
	    } else {
		if (recordSet.getAttribute(attIndex).isNominal()
			|| recordSet.getAttribute(attIndex).isString()
			|| recordSet.getAttribute(attIndex).isDate()) {
		    text.append(VKMUtil.quote(getStringValue(attIndex)));
		} else if (recordSet.getAttribute(attIndex).isNumeric()) {
		    text.append(VKMUtil.doubleToString(getValue(attIndex), 6));
		} else {
		    throw new IllegalStateException("Unknown attribute type");
		}
	    }
	}
	return text.toString();
    }

    @Override
    public final void setValue(int attIndex, String value) {
	int valIndex;
	if (recordSet == null) {
	    throw new IllegalStateException(
		    "Instance doesn't have access to a dataset!");
	}
	if (!recordSet.getAttribute(attIndex).isNominal()
		&& !recordSet.getAttribute(attIndex).isString()) {
	    throw new IllegalArgumentException(
		    "Attribute neither nominal nor string!");
	}
	NominalAttribute nomOrStringAttr = (NominalAttribute) recordSet
		.getAttribute(attIndex);

	valIndex = nomOrStringAttr.getIndexOfValue(value);
	if (valIndex == -1) {
	    if (nomOrStringAttr.isNominal()) {
		throw new IllegalArgumentException(
			"Value not defined for given nominal attribute!");
	    } else {
		nomOrStringAttr.addValue(value);
		valIndex = nomOrStringAttr.getIndexOfValue(value);
	    }
	}
	setValue(attIndex, valIndex);
    }

    @Override
    public final void setValue(Value value) {
	if (value instanceof SingleValue) {
	    ((SingleValue) value).setValue(this);
	}
    }

    @Override
    public final void setDataset(IDataRecordSet instances) {
	recordSet = instances;
    }

    @Override
    public final void setMissing(Attribute att) {
	setMissing(getDataset().getIndex(att));
    }

    @Override
    public final void setMissing(int attIndex) {
	setValue(attIndex, Value.MISSING_VALUE);
    }

    @Override
    public final void setValue(Attribute att, double value) {
	setValue(recordSet.getIndex(att), value);
    }

    @Override
    public final void setValue(Attribute att, String value) {
	if (!att.isNominal() && !att.isString()) {
	    throw new IllegalArgumentException(
		    "Attribute neither nominal nor string!");
	}

	NominalAttribute basicAttribute = (NominalAttribute) att;
	int valIndex = basicAttribute.getIndexOfValue(value);
	if (valIndex == -1) {
	    if (att.isNominal()) {
		throw new IllegalArgumentException(
			"Value not defined for given nominal attribute!");
	    }
	    // String Attribute
	    else {
		basicAttribute.addValue(value);
		valIndex = basicAttribute.getIndexOfValue(value);
	    }
	}
	setValue(getDataset().getIndex(basicAttribute), valIndex);
    }

    @Override
    public final boolean isMissing(Attribute att) {
	return att.isMissingValue(this);
    }

    @Override
    public final boolean isMissing(int attIndex) {
	return Value.isMissingValue(getValue(attIndex));
    }

    @Override
    public final double getValue(Attribute att) {
	return att.getValue(this);
    }

    @Override
    public final String getStringValue(Attribute att) {
	double value = getValue(att);
	if (att.isNominal() || att.isString()) {
	    return ((NominalAttribute) att).getNominalValue((int) value)
		    .getDescription();
	} else if (att.isDate()) {
	    return ((DateAttribute) att).formatDate(value);
	} else if (att.isNumeric()) {
	    return Value.isMissingValue(value) ? "?" : value + "";
	} else {
	    throw new IllegalArgumentException(
		    "Attribute isn't nominal, numeric, string or date!");
	}
    }

    @Override
    public final String getStringValue(int attIndex) {
	if (recordSet == null) {
	    throw new IllegalStateException(
		    "Instance doesn't have access to a dataset!");
	}
	return getStringValue(recordSet.getAttribute(attIndex));
    }

}
