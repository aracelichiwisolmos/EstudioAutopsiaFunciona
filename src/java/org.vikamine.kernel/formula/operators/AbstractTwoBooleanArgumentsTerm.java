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

import java.util.HashSet;
import java.util.Set;

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBoolean;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Support for two boolean arguments.
 * 
 * @author Tobias Vogele
 */
public abstract class AbstractTwoBooleanArgumentsTerm extends
	AbstractBooleanArgumentTerm {

    /** second argument of the term */
    protected FormulaBooleanElement arg2 = null;

    protected Boolean evaluatedArg2 = null;

    public AbstractTwoBooleanArgumentsTerm() {
	super();
    }

    /**
     * Creates a new term with its two arguments
     * 
     * @param arg1
     *            first argument
     * @param arg2
     *            second argument
     */
    public AbstractTwoBooleanArgumentsTerm(FormulaBooleanElement arg1,
	    FormulaBooleanElement arg2) {
	super(arg1);
	setArg2(arg2);
    }

    /**
     * Checks if term contains null (rek.) Creation date: (14.08.2000 17:05:38)
     * 
     * @return null, if one argument is "null", a "0"-Double else.
     */
    public Boolean evalArgument2(EvaluationData data) {
	if (getArg2() == null) {
	    return null;
	}
	evaluatedArg2 = (getArg2().eval(data));
	if (getEvaluatedArg2() == null)
	    return null;
	else
	    return getEvaluatedArg2();
    }

    /**
     * tries to evaluate all arguments and returns true, if all arguments are
     * successfully evaluated.
     * 
     * @param data
     * @return
     */
    @Override
    public boolean evalArguments(EvaluationData data) {
	if (!super.evalArguments(data)) {
	    return false;
	}
	return evalArgument2(data) != null;
    }

    /**
     * Creation date: (14.08.2000 15:46:45)
     * 
     * @return second argument of the term
     */
    public FormulaBooleanElement getArg2() {
	if (arg2 == null)
	    return FormulaBoolean.FALSE;
	else
	    return arg2;
    }

    public Boolean getEvaluatedArg2() {
	return evaluatedArg2;
    }

    public void setArg2(FormulaBooleanElement arg2) {
	this.arg2 = arg2;
    }

    @Override
    public String toString() {

	return "(" + (getArg1() != null ? getArg1().toString() : "") + " "
		+ getSymbol() + " "
		+ (getArg2() != null ? getArg2().toString() : "") + ")";

    }

    @Override
    public Set getAttributes() {
	Set set1 = super.getAttributes();
	Set set2 = getArg2().getAttributes();
	Set result = set1;
	if (!(result instanceof HashSet)) {
	    result = new HashSet(set1);
	}
	result.addAll(set2);
	return result;
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = super.createDOMNode(doc);
	if (getArg2() != null) {
	    Element arg2Node = getArg2().createDOMNode(doc);
	    arg2Node.setAttribute("argIndex", "2");
	    elem.appendChild(arg2Node);
	}
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
	    AbstractTwoBooleanArgumentsTerm term2 = (AbstractTwoBooleanArgumentsTerm) other;
	    return getArg1().equals(term2.getArg1())
		    && getArg2().equals(term2.getArg2());
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
