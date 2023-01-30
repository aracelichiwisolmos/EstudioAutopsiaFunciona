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

import java.util.List;

import org.vikamine.kernel.formula.ConstantParserElementBuilder;
import org.vikamine.kernel.formula.ParserElement;

/**
 * {@link AbstractConstantElementBuilder} is a builder for constant elements.
 * 
 * @author Tobias Vogele
 */
public abstract class AbstractConstantElementBuilder implements
	ConstantParserElementBuilder {

    protected String text;
    protected int precedence;
    protected Class type;
    protected Class[] argumentTypes = new Class[0];
    protected boolean openingBrace = false;
    protected boolean closingBrace = false;

    public AbstractConstantElementBuilder() {
	super();
    }

    @Override
    public String getText() {
	return text;
    }

    @Override
    public ParserElement createParserElement(String token,
	    List beforeParserElements) {
	ParserElement pe = new ParserElement();
	pe.setContent(createContent(), text);
	pe.setPrecedence(getPrecedence());
	pe.setType(getType());
	pe.setRequiredArgumentTypes(getArgumentTypes());
	pe.setClosingBrace(isClosingBrace());
	pe.setOpeningBrace(isOpeningBrace());
	return pe;
    }

    protected abstract Object createContent();

    public Class[] getArgumentTypes() {
	return argumentTypes;
    }

    public void setArgumentTypes(Class[] argumentTypes) {
	this.argumentTypes = argumentTypes;
    }

    public int getPrecedence() {
	return precedence;
    }

    public void setPrecedence(int precedence) {
	this.precedence = precedence;
    }

    public Class getType() {
	return type;
    }

    public void setType(Class type) {
	this.type = type;
    }

    public void setText(String text) {
	this.text = text;
    }

    public boolean isClosingBrace() {
	return closingBrace;
    }

    public void setClosingBrace(boolean closingBrace) {
	this.closingBrace = closingBrace;
    }

    public boolean isOpeningBrace() {
	return openingBrace;
    }

    public void setOpeningBrace(boolean openingBrace) {
	this.openingBrace = openingBrace;
    }
}
