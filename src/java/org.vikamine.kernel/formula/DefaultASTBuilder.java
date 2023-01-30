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
package org.vikamine.kernel.formula;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.vikamine.kernel.formula.exception.ASTBuildingException;
import org.vikamine.kernel.formula.exception.MissingClosingBraceException;
import org.vikamine.kernel.formula.exception.MissingOpeningBraceException;
import org.vikamine.kernel.formula.exception.MissingOperandException;
import org.vikamine.kernel.formula.exception.MissingOperatorException;

/**
 * {@link DefaultASTBuilder} provides a default implementation of the
 * {@link ASTBuilder}.
 * 
 * @author Tobias Vogele
 */
public class DefaultASTBuilder implements ASTBuilder {

    protected boolean braceOpened;

    protected Stack operandStack = new Stack();

    protected Stack operatorStack = new Stack();

    public DefaultASTBuilder() {
	super();
    }

    public DefaultASTBuilder(boolean braceOpened) {
	this.braceOpened = braceOpened;
    }

    protected ParserElement buildAST(Iterator iter) throws EmptyStackException,
	    MissingClosingBraceException, MissingOpeningBraceException {
	boolean closed = false;
	while (iter.hasNext() && !closed) {
	    ParserElement pe = (ParserElement) iter.next();
	    if (pe.isOpeningBrace()) {
		operandStack.push(new DefaultASTBuilder(true).buildAST(iter));
	    } else if (pe.isClosingBrace()) {
		closed = true;
	    } else {
		if (pe.getRequiredArgumentsCount() > 0) {
		    // is Operator
		    while (lowerPrecedence(pe)) {
			reduce();
		    }
		    operatorStack.push(pe);
		} else {
		    operandStack.push(pe);
		}
	    }
	}
	while (!operatorStack.isEmpty()) {
	    reduce();
	}
	if (braceOpened && !closed) {
	    throw new MissingClosingBraceException("missing closing brace",
		    operandStack, operatorStack);
	} else if (!braceOpened && closed) {
	    throw new MissingOpeningBraceException("missing openingBrace",
		    operandStack, operatorStack);
	}

	return (ParserElement) operandStack.pop();
    }

    @Override
    public ParserElement buildAST(List parserElements)
	    throws ASTBuildingException {
	initialize();

	try {
	    Iterator iter = parserElements.iterator();
	    ParserElement ast = buildAST(iter);
	    if (!operandStack.isEmpty()) {
		operandStack.push(ast);
		throw new MissingOperatorException("missing operator",
			operandStack, operatorStack);
	    }
	    return ast;
	} catch (EmptyStackException e) {
	    throw new MissingOperandException("missing operands", operandStack,
		    operatorStack);
	}
    }

    protected void initialize() {
	operandStack = new Stack();
	operatorStack = new Stack();
	braceOpened = false;
    }

    public boolean isBraceOpened() {
	return braceOpened;
    }

    /**
     * Returns true, if the precedence of the given element is lower to the
     * precedence of the first element in the operator-stack.
     * <p>
     * As pseudo-code: <code>
     * return operatorStack().peek().precedence() >= pe.precedence()
     * </code>
     * <p>
     * If the operator-stack is empty, this method returns false.
     * 
     * @param pe
     * @return
     */
    protected boolean lowerPrecedence(ParserElement pe) {
	if (operatorStack.isEmpty()) {
	    return false;
	}
	ParserElement stack = (ParserElement) operatorStack.peek();
	return stack.getPrecedence() >= pe.getPrecedence();
    }

    /**
     * Takes one operator from the operator-stack and sets his operands from the
     * operand-stack. The resulting operator-with-operands is pushed on the
     * operand-stack.
     */
    protected void reduce() {
	ParserElement op = (ParserElement) operatorStack.peek(); // just look
	List children = new LinkedList();
	op.setChildren(children);
	children.add(operandStack.pop()); // right hand side
	if (op.getRequiredArgumentsCount() > 1) {
	    children.add(0, operandStack.pop()); // left hand side;
	}
	// remove the operator from stack not until we are sure, that there are
	// enough operands.
	operatorStack.pop();
	operandStack.push(op);
    }

    public void setBraceOpened(boolean braceOpened) {
	this.braceOpened = braceOpened;
    }
}
