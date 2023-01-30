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

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * (Unary) Minus operator term.
 * 
 * @author Tobias Vogele
 */
public class UnaryMinus extends AbstractNumberArgumentTerm implements
	FormulaNumberElement {

    public UnaryMinus() {
	super("-");
    }

    public UnaryMinus(FormulaNumberElement arg) {
	this();
	setArg1(arg);
    }

    @Override
    public synchronized Double eval(EvaluationData data) {
	if (!evalArguments(data)) {
	    return null;
	} else {
	    return new Double(0 - getEvaluatedArg1().doubleValue());
	}
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", "unaryMinus");
	if (getArg1() != null) {
	    Element arg1Node = getArg1().createDOMNode(doc);
	    arg1Node.setAttribute("argIndex", "1");
	    elem.appendChild(arg1Node);
	}
	return elem;
    }
}
