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
package org.vikamine.kernel.formula.elementBuilders;

import java.lang.reflect.Method;

/**
 * {@link PrototypedConstantBuilder} builds constants according to a given
 * prototype object.
 * 
 * @author Tobias Vogele
 */
public class PrototypedConstantBuilder extends AbstractConstantElementBuilder {

    private Object prototype;
    private Method cloneMethod;

    public PrototypedConstantBuilder() {
	super();
    }

    @Override
    protected Object createContent() {
	try {
	    return getCloneMethod().invoke(getPrototype(), new Object[0]);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    protected Method getCloneMethod() throws SecurityException,
	    NoSuchMethodException {
	if (cloneMethod == null) {
	    Class cl = getPrototype().getClass();
	    cloneMethod = cl.getMethod("clone", new Class[0]);
	    cloneMethod.setAccessible(true);
	}
	return cloneMethod;
    }

    public Object getPrototype() {
	return prototype;
    }

    public void setPrototype(Object prototype) {
	this.prototype = prototype;
	cloneMethod = null;
    }

    public PrototypedConstantBuilder(Object prototype) {
	this.prototype = prototype;
    }

    public PrototypedConstantBuilder(String txt, Object prototype) {
	setText(txt);
	this.prototype = prototype;
    }

}
