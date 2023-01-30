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
 * Created on 10.03.2004
 */
package org.vikamine.kernel.formula.constants;

import java.util.Collections;
import java.util.Set;

import org.vikamine.kernel.formula.FormulaNumberElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link AbstractFormulaNumberPrimitive} provides the basis for formula
 * primitives.
 * 
 * @author Tobias Vogele
 */
public abstract class AbstractFormulaNumberPrimitive implements
	FormulaNumberElement {

    protected String name;

    public AbstractFormulaNumberPrimitive() {
	super();
    }

    public AbstractFormulaNumberPrimitive(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return getName();
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public Set getAttributes() {
	return Collections.EMPTY_SET;
    }

    @Override
    public Element createDOMNode(Document doc) {
	Element elem = doc.createElement("formula");
	elem.setAttribute("symbol", getName());
	return elem;
    }

    @Override
    public final boolean equals(Object other) {
	return isEqual(other);
    }

    @Override
    public abstract boolean isEqual(Object other);

    @Override
    public final int hashCode() {
	return computeHashCode();
    }

    @Override
    public abstract int computeHashCode();

}
