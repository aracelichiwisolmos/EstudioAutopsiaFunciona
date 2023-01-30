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

package org.vikamine.kernel.formula;

import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Primitive formula element in number format
 * 
 * @author Tobias Vogele, atzmueller
 */
public class FormulaNumber implements FormulaNumberElement {

    private Double value;

    /**
     * Creates a new FormulaNubmer with 0 as value.
     */
    public FormulaNumber() {
	this(new Double(0));
    }

    /**
     * Creates a new FormulaNumber object
     * 
     * @param value
     *            value of this FormulaElement
     */
    public FormulaNumber(Double value) {
	setValue(value);
    }

    /**
     * Creation date: (14.08.2000 15:51:57)
     * 
     * @return Double-value of this FormulaElement
     */
    @Override
    public synchronized Double eval(EvaluationData data) {
	return getValue();
    }

    /**
     * @return String representation of this FormulaNumber-object
     */
    @Override
    public String toString() {
	if (getValue() == null)
	    return "";
	else
	    return trim(getValue());
    }

    /**
     * Method for formatting the double-value of this FormulaElement Used by
     * toString ()
     * 
     * Creation date: (15.08.2000 08:31:02)
     * 
     * @return formatted String-representation of this FormulaElement
     */
    private String trim(Object trimValue) {
	final int digits = 3;
	String text = trimValue.toString();
	int dot = text.indexOf(".");
	if (dot != -1) {
	    text = text.substring(0, Math.min(text.length(), dot + 1 + digits));
	}
	return text;
    }

    public Double getValue() {
	return value;
    }

    public void setValue(Double value) {
	this.value = value;
    }

    @Override
    public Set getAttributes() {
	return Collections.EMPTY_SET;
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", "number");
	elem.setAttribute("value", String.valueOf(value));
	return elem;
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
	    return value.equals(((FormulaNumber) other).value);
	}
    }

    @Override
    public int hashCode() {
	return computeHashCode();
    }

    @Override
    public int computeHashCode() {
	return value.hashCode();
    }
}
