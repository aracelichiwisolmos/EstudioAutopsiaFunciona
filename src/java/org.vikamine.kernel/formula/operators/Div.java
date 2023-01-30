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

package org.vikamine.kernel.formula.operators;

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaNumberElement;

/**
 * Division term
 * 
 * @author Tobias Vogele, atzmueller
 */
public class Div extends AbstractTwoNumberArgumentsTerm implements
	FormulaNumberElement {

    /**
     * Creates a new FormulaTerm with null-arguments.
     */
    public Div() {
	this(null, null);
    }

    /**
     * Creates a new FormulaTerm (arg1 / arg2)
     * 
     * @param _arg1
     *            first argument of the term
     * @param _arg2
     *            second argument of the term
     */
    public Div(FormulaNumberElement arg1, FormulaNumberElement arg2) {

	setArg1(arg1);
	setArg2(arg2);
	setSymbol("/");
    }

    /**
     * Divides the evaluation values of both arguments
     * 
     * @return divided evaluated arguments
     */
    @Override
    public synchronized Double eval(EvaluationData data) {
	if (!evalArguments(data))
	    return null;
	else
	    return new Double(getEvaluatedArg1().doubleValue()
		    / getEvaluatedArg2().doubleValue());
    }
}
