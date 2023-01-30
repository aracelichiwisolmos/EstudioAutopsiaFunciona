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
 * Created on 17.03.2004
 */
package org.vikamine.kernel.formula;

import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Impelementation of {@link FormulaBooleanElement}.
 * 
 * @author Tobias Vogele
 */
public class FormulaBoolean implements FormulaBooleanElement {

    public static final FormulaBoolean TRUE = new FormulaBoolean(Boolean.TRUE);

    public static final FormulaBoolean FALSE = new FormulaBoolean(Boolean.FALSE);

    private Boolean value;

    /**
     * Creates a new FormulaNumber with 0 as value.
     */
    public FormulaBoolean() {
	this(Boolean.FALSE);
    }

    /**
     * Creates a new FormulaNumber object
     * 
     * @param value
     *            value of this FormulaElement
     */
    public FormulaBoolean(Boolean value) {
	setValue(value);
    }

    /**
     * Creation date: (14.08.2000 15:51:57)
     * 
     * @return Double-value of this FormulaElement
     */
    @Override
    public synchronized Boolean eval(EvaluationData data) {
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
	    return getValue().toString();
    }

    public Boolean getValue() {
	return value;
    }

    public void setValue(Boolean value) {
	this.value = value;
    }

    @Override
    public Set getAttributes() {
	return Collections.EMPTY_SET;
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", "boolean");
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
	    return value.equals(((FormulaBoolean) other).value);
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
