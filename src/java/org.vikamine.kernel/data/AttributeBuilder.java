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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.converters.ARFFDataConverter;

/**
 * Base class for building attributes.
 * 
 * @author atzmueller
 */

public class AttributeBuilder {

    protected Attribute attribute;

    private static final int NOMINAL_VALUES_REQUIRED = 1;

    public void buildNumericAttribute(String attributeName) {
	attribute = new NumericAttribute(attributeName);
    }

    public void buildNominalAttribute(String attributeName,
	    List<String> rawValuesList) {
	attribute = new NominalAttribute(attributeName, rawValuesList);
    }

    public void buildStringAttribute(String attributeName) {
	attribute = new StringAttribute(attributeName);
    }

    public void buildDateAttribute(String attributeName, String dateFormat) {
	attribute = new DateAttribute(attributeName, dateFormat);
    }

    public Attribute getAttribute() {
	int fail = 0;
	// DefaultNominalAttributes have to be checked, if values are created
	if ((attribute.isNominal()) && (attribute instanceof NominalAttribute)) {
	    NominalAttribute attr = (NominalAttribute) attribute;
	    if ((attr.getNominalValues() == null)
		    || (attr.getNominalValues().size() != attr
			    .getRawValuesCount())) {
		fail += NOMINAL_VALUES_REQUIRED;
	    }
	}

	if (fail > 0) {
	    throw new IllegalStateException("Attribute not fully initialized!"
		    + (fail));
	}
	return attribute;
    }

    public void buildNominalValues() {
	List<Value> nominalValues = new LinkedList<Value>();
	NominalAttribute dna = (NominalAttribute) attribute;
	for (int i = 0; i < dna.getRawValuesCount(); i++) {
	    Value val = new SingleValue(dna, i, dna.getRawValue(i));
	    nominalValues.add(val);
	}
	((NominalAttribute) attribute).setNominalValues(nominalValues);
    }

    public void buildEmptyNominalValues() {
	List<Value> nominalValues = Arrays
		.asList(new Value[] { new SingleValue(
			ARFFDataConverter.EMPTY_ATTRIBUTE_VALUE) });
	((NominalAttribute) attribute).setNominalValues(nominalValues);
    }

}
