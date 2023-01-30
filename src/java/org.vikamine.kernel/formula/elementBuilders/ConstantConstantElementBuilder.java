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
package org.vikamine.kernel.formula.elementBuilders;

/**
 * Builder for constant elements.
 * 
 * @author Tobias Vogele
 */
public class ConstantConstantElementBuilder extends
	AbstractConstantElementBuilder {

    protected Object content;

    public ConstantConstantElementBuilder() {
	super();
    }

    @Override
    protected Object createContent() {
	return getContent();
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content) {
	this.content = content;
    }

    public ConstantConstantElementBuilder(Object cont) {
	setContent(cont);
    }

    public ConstantConstantElementBuilder(String txt, Object cont, Class type) {
	setText(txt);
	setContent(cont);
	setType(type);
    }

}
