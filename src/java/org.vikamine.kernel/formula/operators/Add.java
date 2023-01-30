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
 * Addition term
 * 
 * @author Tobias Vogele, atzmueller
 */
public class Add extends AbstractTwoNumberArgumentsTerm implements
	FormulaNumberElement {

    /**
     * Creates a new FormulaTerm with null-arguments.
     */
    public Add() {
	this(null, null);
    }

    /**
     * Creates a new FormulaTerm (arg1 + arg2)
     * 
     * @param _arg1
     *            first argument of the term
     * @param _arg2
     *            second argument of the term
     */
    public Add(FormulaNumberElement arg1, FormulaNumberElement arg2) {
	setArg1(arg1);
	setArg2(arg2);
	setSymbol("+");
    }

    /**
     * Adds the evaluation values of both arguments
     * 
     * @return added evaluated arguments
     */
    @Override
    public synchronized Double eval(EvaluationData data) {
	Double val1 = evalArgument1(data);
	Double val2 = evalArgument2(data);

	double arg1value = (val1 == null) ? (0.0) : val1.doubleValue();
	double arg2value = (val2 == null) ? (0.0) : val2.doubleValue();
	return new Double(arg1value + arg2value);
    }
}
