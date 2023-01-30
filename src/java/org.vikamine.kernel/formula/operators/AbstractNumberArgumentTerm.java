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
 */
package org.vikamine.kernel.formula.operators;

import java.util.Set;

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.formula.FormulaNumber;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link FormulaElement} for a number argument term.
 * 
 * @author Tobias Vogele
 */
public abstract class AbstractNumberArgumentTerm implements FormulaElement {

    protected FormulaNumberElement arg1 = null;

    protected Double evaluatedArg1 = null;

    protected String symbol;

    public AbstractNumberArgumentTerm() {
	super();
    }

    public AbstractNumberArgumentTerm(String symb) {
	setSymbol(symb);
    }

    /**
     * Creates a new term with its two arguments
     * 
     * @param arg
     *            first argument
     */
    public AbstractNumberArgumentTerm(FormulaNumberElement arg) {
	setArg1(arg);
    }

    public Double evalArgument1(EvaluationData data) {
	if (getArg1() == null) {
	    return null;
	}

	evaluatedArg1 = (getArg1().eval(data));

	if (getEvaluatedArg1() == null) {
	    return null;
	} else
	    return getEvaluatedArg1();

    }

    /**
     * tries to evaluate all arguments and returns true, if all arguments are
     * successfully evaluated.
     * 
     * @param data
     * @return
     */
    public boolean evalArguments(EvaluationData data) {
	return evalArgument1(data) != null;
    }

    /**
     * Creation date: (14.08.2000 15:46:45)
     * 
     * @return first argument of the term
     */
    public FormulaNumberElement getArg1() {
	if (arg1 == null)
	    return new FormulaNumber(null);
	else
	    return arg1;
    }

    public Double getEvaluatedArg1() {
	return evaluatedArg1;
    }

    public String getSymbol() {
	return symbol;
    }

    @Override
    public String toString() {
	return "(" + getSymbol() + " "
		+ (getArg1() != null ? getArg1().toString() : "") + ")";

    }

    public void setSymbol(String symbol) {
	this.symbol = symbol;
    }

    public void setArg1(FormulaNumberElement arg1) {
	this.arg1 = arg1;
    }

    @Override
    public Set getAttributes() {
	return getArg1().getAttributes();
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", getSymbol());
	if (getArg1() != null) {
	    Element arg1Node = getArg1().createDOMNode(doc);
	    arg1Node.setAttribute("argIndex", "1");
	    elem.appendChild(arg1Node);
	}
	return elem;
    }

    @Override
    public boolean equals(Object other) {
	return isEqual(other);
    }

    @Override
    public int hashCode() {
	return computeHashCode();
    }

    public boolean isEqual(Object other) {
	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {

	    AbstractNumberArgumentTerm term2 = (AbstractNumberArgumentTerm) other;
	    return getArg1().equals(term2.getArg1());
	}
    }

    public int computeHashCode() {
	return getArg1().hashCode();
    }

}
