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

import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBooleanElement;

/**
 * And term.
 * 
 * @author Tobias Vogele
 */
public class And extends AbstractTwoBooleanArgumentsTerm implements
	FormulaBooleanElement {

    public And() {
	this(null, null);
    }

    public And(FormulaBooleanElement arg1, FormulaBooleanElement arg2) {
	super(arg1, arg2);
	setSymbol("&");
    }

    /**
     * Evaluates the arguments with a logical AND as operation.
     * <p>
     * The arguments are evaluated lazily, which means, if the first argument is
     * false, the second argument will NOT be evaluated.
     */
    @Override
    public synchronized Boolean eval(EvaluationData data) {
	// Evaluate first value:
	if (evalArgument1(data) == null) {
	    return null;
	}
	// Lazy evaluation: if the first argument is false, don't evaluate
	// the second argument, but return false immediately
	if (!getEvaluatedArg1().booleanValue()) {
	    return Boolean.FALSE;
	}

	// Evaluate second value:
	if (evalArgument2(data) == null) {
	    return null;
	}
	return getEvaluatedArg2();
    }
}
