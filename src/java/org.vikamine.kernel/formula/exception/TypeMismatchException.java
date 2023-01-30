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
 * Created on 24.03.2004
 */
package org.vikamine.kernel.formula.exception;

import org.vikamine.kernel.formula.ParserElement;

/**
 * {@link TypeMismatchException} provides support for parsing errors, when an
 * expected type of an operand does not match the parsed type.
 * 
 * @author Tobias Vogele
 */
public class TypeMismatchException extends ValidateException {

    private static final long serialVersionUID = 2519159185937693376L;

    protected Class expectedType;

    protected Class realType;

    protected ParserElement wrongElement;

    public Class getExpectedType() {
	return expectedType;
    }

    public void setExpectedType(Class expectedType) {
	this.expectedType = expectedType;
    }

    public Class getRealType() {
	return realType;
    }

    public void setRealType(Class realType) {
	this.realType = realType;
    }

    public ParserElement getWrongElement() {
	return wrongElement;
    }

    public void setWrongElement(ParserElement wrongElement) {
	this.wrongElement = wrongElement;
    }

    public TypeMismatchException() {
	super();
    }

    public TypeMismatchException(String message) {
	super(message);
    }

    public TypeMismatchException(String message, ParserElement wrongElement,
	    Class expectedType, Class realType, ParserElement ast) {
	super(message, ast);
	this.wrongElement = wrongElement;
	this.expectedType = expectedType;
	this.realType = realType;
    }

    public TypeMismatchException(String message, ParserElement wrongElement,
	    Class expectedType, Class realType) {
	super(message);
	this.wrongElement = wrongElement;
	this.expectedType = expectedType;
	this.realType = realType;
    }
}
