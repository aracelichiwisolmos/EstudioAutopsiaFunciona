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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.vikamine.kernel.formula.ParserElement;
import org.vikamine.kernel.formula.VariableParserElementBuilder;

/**
 * {@link DoubleElementBuilder} builds (numeric) double elements.
 * 
 * @author Tobias Vogele
 */
public class DoubleElementBuilder implements VariableParserElementBuilder {

    protected Class type = Double.class;

    public DoubleElementBuilder() {
	super();
    }

    @Override
    public boolean isValid(String text) {
	if (!text.matches("[\\d]+[\\,|\\.]?[\\d]*")) {
	    return false;
	} else {
	    return parseDouble(text) != null;
	}
    }

    protected Double parseDouble(String text) {
	try {
	    try {
		Number n = NumberFormat.getNumberInstance().parse(text);
		String localeDependentText = NumberFormat.getNumberInstance(
			Locale.US).format(n);
		return new Double(localeDependentText);
	    } catch (ParseException e) {
		// ignore
	    }
	    return null;
	} catch (NumberFormatException e) {
	    return null;
	}
    }

    @Override
    public boolean isValidStart(String substring) {
	if (!substring.endsWith(" ")) {
	    return isValid(substring);
	} else {
	    return false;
	}
    }

    @Override
    public ParserElement createParserElement(String token,
	    List beforeParserElements) {
	ParserElement pe = new ParserElement();
	pe.setType(getType());
	pe.setContent(createContent(token), token);
	return pe;
    }

    protected Object createContent(String token) {
	return parseDouble(token);
    }

    public Class getType() {
	return type;
    }

    public void setType(Class type) {
	this.type = type;
    }
}
