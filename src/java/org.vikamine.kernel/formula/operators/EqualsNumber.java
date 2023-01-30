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
import org.vikamine.kernel.formula.FormulaBooleanElement;

/**
 * "=" term.
 * 
 * @author Tobias Vogele
 */
public class EqualsNumber extends AbstractTwoNumberArgumentsTerm implements
	FormulaBooleanElement {

    public EqualsNumber() {
	super("=");
    }

    @Override
    public synchronized Boolean eval(EvaluationData data) {
	if (!evalArguments(data))
	    return null;
	else
	    return Boolean
		    .valueOf(getEvaluatedArg1().doubleValue() == getEvaluatedArg2()
			    .doubleValue());
    }

}
