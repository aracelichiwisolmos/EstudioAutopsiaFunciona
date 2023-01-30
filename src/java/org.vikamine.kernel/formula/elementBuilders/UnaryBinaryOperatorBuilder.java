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
 * {@link UnaryBinaryOperatorBuilder} performs for unary or binary operators.
 * 
 * @author Tobias Vogele
 */
public class UnaryBinaryOperatorBuilder implements ConstantParserElementBuilder {

    protected ConstantParserElementBuilder unaryDelegate;
    protected ConstantParserElementBuilder binaryDelegate;
    protected String text;

    public UnaryBinaryOperatorBuilder() {
	super();
    }

    @Override
    public String getText() {
	if (text != null) {
	    return text;
	} else if (getUnaryDelegate() != null
		&& getUnaryDelegate().getText() != null) {
	    return getUnaryDelegate().getText();
	} else if (getBinaryDelegate() != null) {
	    return getBinaryDelegate().getText();
	} else {
	    return null;
	}
    }

    @Override
    public ParserElement createParserElement(String token, List before) {
	if (shouldCreateBinary(before)) {
	    return createBinary(token, before);
	} else {
	    return createUnary(token, before);
	}
    }

    protected ParserElement createBinary(String token, List before) {
	return getBinaryDelegate().createParserElement(token, before);
    }

    protected ParserElement createUnary(String token, List before) {
	return getUnaryDelegate().createParserElement(token, before);
    }

    protected boolean shouldCreateBinary(List before) {
	if (before == null || before.isEmpty()) {
	    return false;
	}
	ParserElement last = (ParserElement) before.get(before.size() - 1);
	return last.getRequiredArgumentsCount() == 0;
    }

    public ConstantParserElementBuilder getBinaryDelegate() {
	return binaryDelegate;
    }

    public void setBinaryDelegate(ConstantParserElementBuilder binaryDelegate) {
	this.binaryDelegate = binaryDelegate;
    }

    public ConstantParserElementBuilder getUnaryDelegate() {
	return unaryDelegate;
    }

    public void setUnaryDelegate(ConstantParserElementBuilder unaryDelegate) {
	this.unaryDelegate = unaryDelegate;
    }

    public void setText(String text) {
	this.text = text;
    }
}
