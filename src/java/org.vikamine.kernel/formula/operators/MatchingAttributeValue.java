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
 * Created on 12.09.2004
 */
package org.vikamine.kernel.formula.operators;

import java.util.HashSet;
import java.util.Set;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.constants.FormulaAttributePrimitive;
import org.vikamine.kernel.formula.constants.FormulaAttributeValuePrimitive;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Match for an attribute value in a data record.
 * 
 * @author Tobias Vogele
 */
public class MatchingAttributeValue implements FormulaBooleanElement {

    protected FormulaAttributePrimitive arg1 = null;

    protected FormulaAttributeValuePrimitive arg2 = null;

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", getSymbol());
	elem.setAttribute("attributeValue", "true");
	if (getArg1() != null) {
	    Element arg1Node = getArg1().createDOMNode(doc);
	    arg1Node.setAttribute("argIndex", "1");
	    elem.appendChild(arg1Node);
	}
	if (getArg2() != null) {
	    Element arg2Node = getArg2().createDOMNode(doc);
	    arg2Node.setAttribute("argIndex", "2");
	    elem.appendChild(arg2Node);
	}
	return elem;
    }

    @Override
    public synchronized Boolean eval(EvaluationData data) {
	if (!validArguments()) {
	    return null;
	} else {
	    DataRecord instance = data.getInstance();
	    return Boolean.valueOf(getArg2().getValue()
		    .isValueContainedInInstance(instance));
	}
    }

    /**
     * tries to evaluate all arguments and returns true, if all arguments are
     * successfully evaluated.
     * 
     * @param data
     * @return
     */
    public boolean validArguments() {
	if (getArg1() == null || getArg2() == null
		|| getArg2().getValue() == null) {
	    return false;
	}
	return getArg2().getValue().getAttribute()
		.equals(getArg1().getAttribute());
    }

    /**
     * Creation date: (14.08.2000 15:46:45)
     * 
     * @return first argument of the term
     */
    public FormulaAttributePrimitive getArg1() {
	if (arg1 == null)
	    return new FormulaAttributePrimitive();
	else
	    return arg1;
    }

    /**
     * Creation date: (14.08.2000 15:46:45)
     * 
     * @return second argument of the term
     */
    public FormulaAttributeValuePrimitive getArg2() {
	if (arg2 == null)
	    return new FormulaAttributeValuePrimitive(null);
	else
	    return arg2;
    }

    @Override
    public Set getAttributes() {
	Set set1 = getArg1().getAttributes();
	Set set2 = getArg2().getAttributes();
	Set result = set1;
	if (!(result instanceof HashSet)) {
	    result = new HashSet(set1);
	}
	result.addAll(set2);
	return result;
    }

    public String getSymbol() {
	return "=";
    }

    public void setArg1(FormulaAttributePrimitive arg1) {
	this.arg1 = arg1;
    }

    public void setArg2(FormulaAttributeValuePrimitive arg2) {
	this.arg2 = arg2;
    }

    @Override
    public String toString() {

	return "(" + (getArg1() != null ? getArg1().toString() : "") + " "
		+ getSymbol() + " "
		+ (getArg2() != null ? getArg2().toString() : "") + ")";

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
	    MatchingAttributeValue mav = (MatchingAttributeValue) other;
	    return getArg1().equals(mav.getArg1())
		    && getArg2().equals(mav.getArg2());
	}
    }

    @Override
    public int hashCode() {
	return computeHashCode();
    }

    @Override
    public int computeHashCode() {
	return getArg1().hashCode() + getArg2().hashCode();
    }
}
