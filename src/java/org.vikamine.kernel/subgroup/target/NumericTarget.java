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
package org.vikamine.kernel.subgroup.target;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * {@link SGTarget} for numeric {@link Attribute}s.
 * 
 * @author atzmueller
 * 
 */
public class NumericTarget implements SGTarget {

    private NumericAttribute mAttribute;
    private List<Attribute> mAttributeAsList;

    public NumericAttribute getNumericTargetAttribute() {
	return mAttribute;
    }

    protected NumericTarget() {
	super();
    }

    public NumericTarget(NumericAttribute attribute) {
	super();
	if (attribute == null) {
	    throw new NullPointerException(
		    "Cannot create target from null-Attribute");
	}
	mAttribute = attribute;
    }

    public NumericTarget(Ontology onto, String numericAttributeName) {
	if (onto == null) {
	    throw new NullPointerException("Ontology must not be null!");
	}
	Attribute att = onto.getAttribute(numericAttributeName);
	if (att == null) {
	    throw new NullPointerException(numericAttributeName
		    + " is not a valid attribute ID!");
	}
	if (!att.isNumeric()) {
	    throw new IllegalArgumentException(numericAttributeName
		    + " is not a numeric attribute!");
	}
	mAttribute = (NumericAttribute) att;
    }

    public NumericAttribute getAttribute() {
	return mAttribute;
    }

    @Override
    public Collection<Attribute> getAttributes() {
	if (mAttributeAsList == null) {
	    mAttributeAsList = Collections
		    .singletonList((Attribute) mAttribute);
	}
	return mAttributeAsList;
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public String getDescription(Describer er) {
	return er.createDescription(mAttribute);
    }

    @Override
    public String getId() {
	return "NumericTarget " + mAttribute.getId();
    }

    public boolean isDefined(DataRecord record) {
	return !record.isMissing(mAttribute);
    }

    public double getValue(DataRecord record) {
	return record.getValue(mAttribute);
    }

    @Override
    public String toString() {
	return "T: " + mAttribute;
    }

    @Override
    public boolean isBoolean() {
	return false;
    }

    @Override
    public boolean isNumeric() {
	return true;
    }
}
