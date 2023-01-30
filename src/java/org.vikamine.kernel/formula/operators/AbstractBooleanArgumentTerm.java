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
package org.vikamine.kernel.formula.operators;

import java.util.Set;

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBoolean;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.FormulaElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link FormulaElement} for a boolean argument term.
 * 
 * @author Tobias Vogele
 */
public abstract class AbstractBooleanArgumentTerm implements FormulaElement {

    /** first argument of the term */
    protected FormulaBooleanElement arg1 = null;

    /**
     * Here the evaluation value will be stored while trying to evaluate the
     * term. It warrents, that the evaluation will be done only once.
     */
    protected Boolean evaluatedArg1 = null;

    protected String symbol = null;

    public AbstractBooleanArgumentTerm() {
	super();
    }

    public AbstractBooleanArgumentTerm(String symb) {
	super();
	setSymbol(symb);
    }

    /**
     * Creates a new term with its two arguments
     * 
     * @param arg1
     *            first argument
     * @param arg2
     *            second argument
     */
    public AbstractBooleanArgumentTerm(FormulaBooleanElement arg1) {
	setArg1(arg1);
    }

    /**
     * Checks if term contains null (rek.) Creation date: (14.08.2000 17:05:38)
     * 
     * @return null, if one argument is "null", a "0"-Double else.
     */
    public Boolean evalArgument1(EvaluationData data) {
	if (getArg1() == null) {
	    return null;
	}

	evaluatedArg1 = (getArg1().eval(data));

	if (getEvaluatedArg1() == null)
	    return null;
	else
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
	return (evalArgument1(data) != null);
    }

    /**
     * Creation date: (14.08.2000 15:46:45)
     * 
     * @return first argument of the term
     */
    public FormulaBooleanElement getArg1() {
	if (arg1 == null)
	    return FormulaBoolean.FALSE;
	else
	    return arg1;
    }

    /**
     * Creation date: (15.08.2000 08:26:48)
     * 
     * @return the evaluated value of the first argument
     */
    public Boolean getEvaluatedArg1() {
	return evaluatedArg1;
    }

    public String getSymbol() {
	return symbol;
    }

    public void setArg1(FormulaBooleanElement arg1) {
	this.arg1 = arg1;
    }

    /**
     * Sets the arithmetic symbol of the expression
     * 
     * Creation date: (15.08.2000 09:44:57)
     * 
     * @param newSymbol
     *            new arithmetic symbol
     */
    public void setSymbol(java.lang.String symbol) {
	this.symbol = symbol;
    }

    @Override
    public String toString() {

	return "(" + getSymbol() + " "
		+ (getArg1() != null ? getArg1().toString() : "") + ")";

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

    public int computeHashCode() {
	return getArg1().hashCode();
    }

    public boolean isEqual(Object other) {
	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    AbstractBooleanArgumentTerm term2 = (AbstractBooleanArgumentTerm) other;
	    return getArg1().equals(term2.getArg1());
	}
    }

}
