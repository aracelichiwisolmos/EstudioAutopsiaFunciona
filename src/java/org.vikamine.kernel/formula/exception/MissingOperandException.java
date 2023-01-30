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

import java.util.Stack;

/**
 * Exception, when an operand is missing.
 * 
 * @author Tobias Vogele
 */
public class MissingOperandException extends ASTBuildingException {

    private static final long serialVersionUID = -864643357654825891L;

    public MissingOperandException() {
	super();
    }

    public MissingOperandException(String message, Stack operandStack,
	    Stack operatorStack) {
	super(message, operandStack, operatorStack);
    }

    public MissingOperandException(String message) {
	super(message);
    }
}
