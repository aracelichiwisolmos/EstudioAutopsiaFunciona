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
 * Created on 13.04.2004
 */
package org.vikamine.kernel.formula.constants;

import java.util.Collections;
import java.util.Set;

import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.persistence.ValuesMarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link FormulaAttributeValuePrimitive} provides the basis for primitives
 * involving attribute values.
 * 
 * @author Tobias Vogele
 */
public class FormulaAttributeValuePrimitive implements FormulaElement {

    private Value value;

    private String name;

    protected FormulaAttributeValuePrimitive() {
	super();
    }

    public FormulaAttributeValuePrimitive(Value value) {
	super();
	this.value = value;
    }

    @Override
    public Set getAttributes() {
	return Collections.EMPTY_SET;
    }

    public Value getValue() {
	return value;
    }

    public void setValue(Value value) {
	this.value = value;
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", "attributeValue");
	elem.setAttribute("attribute", value.getAttribute().getId());
	Element valueElement = doc.createElement("attributeValue");
	elem.appendChild(valueElement);
	new ValuesMarshaller().createValueNode(doc, valueElement, getValue());
	return elem;
    }

    @Override
    public String toString() {
	return value == null ? "" : (name == null ? value.getDescription()
		: getName());
    }

    @Override
    public boolean equals(Object other) {
	return isEqual(other);
    }

    public boolean isEqual(Object other) {
	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    return value.equals(((FormulaAttributeValuePrimitive) other).value);
	}
    }

    @Override
    public int hashCode() {
	return computeHashCode();
    }

    public int computeHashCode() {
	return value.hashCode();
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
