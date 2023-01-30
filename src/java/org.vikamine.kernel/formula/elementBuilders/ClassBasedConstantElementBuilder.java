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

/**
 * {@link ClassBasedConstantElementBuilder} is a builder for a settable constant
 * content class.
 * 
 * @author Tobias Vogele
 */
public class ClassBasedConstantElementBuilder extends
	AbstractConstantElementBuilder {

    private Class contentType;

    public ClassBasedConstantElementBuilder() {
	super();
    }

    @Override
    protected Object createContent() {
	try {
	    return getContentType().newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public Class getContentType() {
	return contentType;
    }

    public void setContentType(Class contentType) {
	this.contentType = contentType;
    }

    public ClassBasedConstantElementBuilder(Class contentType) {
	this.contentType = contentType;
    }

    public ClassBasedConstantElementBuilder(String txt, Class contentType) {
	this.contentType = contentType;
	setText(txt);
    }

}
