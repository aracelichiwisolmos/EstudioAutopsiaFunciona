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
 * Created on 18.03.2004
 */
package org.vikamine.kernel.formula;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link ParserElement} - Base class for all elements used in the parser.
 * 
 * @author Tobias Vogele
 */
public class ParserElement {

    protected Object content;

    protected String token;

    protected List children = new LinkedList();

    /**
     * true, if this elements is a opening brace.
     * <p>
     * A ParserElement must not be openingBrace AND a closingBrace.
     */
    protected boolean openingBrace = false;

    /**
     * true, if this elements is a opening brace.
     * <p>
     * A ParserElement must not be openingBrace AND a closingBrace.
     */
    protected boolean closingBrace = false;

    protected int precedence;

    protected Class type;

    protected Class[] requiredArgumentTypes = new Class[0];

    public ParserElement() {
	super();
    }

    public ParserElement(boolean opening, boolean closing) {
	if (opening && closing) {
	    throw new IllegalArgumentException(
		    "ParserElement can not be opening AND closing");
	}
	setOpeningBrace(opening);
	setClosingBrace(closing);
    }

    public List getChildren() {
	return children;
    }

    public void setChildren(List children) {
	this.children = children;
    }

    public Object getContent() {
	return content;
    }

    public void setContent(Object content, String token) {
	this.content = content;
	this.token = token;
    }

    public boolean isClosingBrace() {
	return closingBrace;
    }

    public boolean isOpeningBrace() {
	return openingBrace;
    }

    public void setClosingBrace(boolean closingBrace) {
	this.closingBrace = closingBrace;
    }

    public void setOpeningBrace(boolean openingBrace) {
	this.openingBrace = openingBrace;
    }

    public int getRequiredArgumentsCount() {
	return (getRequiredArgumentTypes() == null) ? 0
		: getRequiredArgumentTypes().length;
    }

    /**
     * Returns the precedence of this element. Higher numbers are higher
     * priorities.
     * 
     * @return
     */
    public int getPrecedence() {
	return precedence;
    }

    public void setPrecedence(int precedence) {
	this.precedence = precedence;
    }

    public Class[] getRequiredArgumentTypes() {
	return requiredArgumentTypes;
    }

    public void setRequiredArgumentTypes(Class[] requiredArgumentTypes) {
	this.requiredArgumentTypes = requiredArgumentTypes;
    }

    public Class getType() {
	return type;
    }

    public void setType(Class type) {
	this.type = type;
    }

    public String getToken() {
	return token;
    }
}
