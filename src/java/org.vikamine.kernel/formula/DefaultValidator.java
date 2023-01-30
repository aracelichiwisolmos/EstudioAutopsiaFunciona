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
 * Created on 19.03.2004
 */
package org.vikamine.kernel.formula;

import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.formula.exception.TypeMismatchException;
import org.vikamine.kernel.formula.exception.ValidateException;

/**
 * {@link DefaultValidator} implements our (default) validator of expressions,
 * i.e., ParserElements.
 * 
 * @author Tobias Vogele
 */
public class DefaultValidator implements Validator {

    private List converters;

    public DefaultValidator() {
	super();
    }

    @Override
    public void validate(ParserElement pe, Class type) throws ValidateException {
	if (!isValid(pe, type)) {
	    throw new TypeMismatchException(
		    "type mismatch. Expected: type, Real: " + pe.getType(), pe,
		    type, pe.getType());
	} else {
	    assert (pe.getChildren().size() == pe.getRequiredArgumentsCount()) : ("Wrong number of arguments. Expected: "
		    + pe.getRequiredArgumentsCount() + ", Real: " + pe
		    .getChildren().size());
	    int i = 0;
	    for (Iterator iter = pe.getChildren().iterator(); iter.hasNext(); i++) {
		ParserElement child = (ParserElement) iter.next();
		validate(child, pe.getRequiredArgumentTypes()[i]);
	    }
	}

    }

    private boolean isValid(ParserElement pe, Class type) {
	if (type == null || pe.getType() == type) {
	    return true;
	}
	if (converters == null) {
	    return false;
	}
	for (Iterator iter = converters.iterator(); iter.hasNext();) {
	    TypeConverter conv = (TypeConverter) iter.next();
	    if (conv.canConvert(pe.getType(), type)) {
		conv.convert(pe, type);
		return true;
	    }
	}
	return false;
    }

    public List getConverters() {
	return converters;
    }

    public void setConverters(List converters) {
	this.converters = converters;
    }
}
