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

import java.util.Iterator;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.vikamine.kernel.formula.constants.FormulaAttributePrimitive;

/**
 * Minimum value (of a collection).
 * 
 * @author Tobias Vogele
 */
public class MinValue extends AbstractAttributeArgumentTerm implements
	FormulaNumberElement {

    public MinValue() {
	super("minValue");
    }

    public MinValue(FormulaAttributePrimitive arg) {
	this();
	setArg1(arg);
    }

    @Override
    public synchronized Double eval(EvaluationData data) {
	if (getArg1() == null || data.getInstances() == null) {
	    return null;
	} else {
	    double min = Double.NaN;
	    for (Iterator iter = data.getInstances().iterator(); iter.hasNext();) {
		DataRecord inst = (DataRecord) iter.next();
		EvaluationData d = new EvaluationData();
		d.setInstance(inst);
		double value = getArg1().eval(data).doubleValue();
		if (Double.isNaN(min) || min > value) {
		    min = value;
		}
	    }
	    return new Double(min);
	}
    }

}
