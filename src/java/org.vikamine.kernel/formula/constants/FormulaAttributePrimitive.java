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
 * Created on 12.04.2004
 */
package org.vikamine.kernel.formula.constants;

import java.util.Collections;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link FormulaAttributePrimitive} provides the basis for primitives involving
 * attributes.
 * 
 * @author Tobias Vogele, atzmueller
 */
public class FormulaAttributePrimitive implements FormulaNumberElement {

    private Attribute attribute;

    private String name = "";

    /**
     * this is performed for numeric attributes only, so we know that is is an
     * Attribute!
     */
    @Override
    public synchronized Double eval(EvaluationData data) {
	return new Double(data.getInstance().getValue(attribute));
    }

    public Attribute getAttribute() {
	return attribute;
    }

    public void setAttribute(Attribute attribute) {
	this.attribute = attribute;
    }

    public FormulaAttributePrimitive(Attribute attribute) {
	this.attribute = attribute;
	name = attribute.getDescription();
    }

    public FormulaAttributePrimitive() {
	super();
    }

    @Override
    public Set getAttributes() {
	return Collections.singleton(getAttribute());
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", "attribute");
	elem.setAttribute("attribute", getAttribute().getId());
	return elem;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return name;
    }

    @Override
    public boolean equals(Object other) {
	return isEqual(other);
    }

    @Override
    public boolean isEqual(Object other) {
	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    return attribute
		    .equals(((FormulaAttributePrimitive) other).attribute);
	}
    }

    @Override
    public int hashCode() {
	return computeHashCode();
    }

    @Override
    public int computeHashCode() {
	return attribute.hashCode();
    }
}
