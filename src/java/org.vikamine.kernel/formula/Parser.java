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

import java.text.ParseException;
import java.util.List;

import org.vikamine.kernel.formula.exception.ASTBuildingException;
import org.vikamine.kernel.formula.exception.ValidateException;

/**
 * Provides our parser, containing a {@link Scanner}, {@link ASTBuilder}, and
 * {@link Validator}.
 * 
 * @author Tobias Vogele
 */
public class Parser {

    protected Scanner scanner;

    protected ASTBuilder astBuilder;

    protected Validator validator;

    public Parser() {
	super();
    }

    /**
     * Parses a string and returns a FormulaElement, which is the root-node of
     * an abstract syntax tree.
     * <p>
     * If the formula is not valid or the type of the formula is not the given
     * type, a ParseException is thrown.
     * 
     * @param text
     * @param expectedType
     * @return @throws ParseException
     */
    public ParserElement parse(String text, Class expectedType)
	    throws ParseException, ASTBuildingException, ValidateException {

	List parserElements = getScanner().scan(text);
	if (parserElements.isEmpty()) {
	    return null;
	}
	ParserElement ast = getASTBuilder().buildAST(parserElements);
	try {
	    getValidator().validate(ast, expectedType);
	} catch (ValidateException e) {
	    e.setAST(ast);
	    throw e;
	}

	return ast;
    }

    public ASTBuilder getASTBuilder() {
	if (astBuilder == null) {
	    astBuilder = new DefaultASTBuilder();
	}
	return astBuilder;
    }

    public void setASTBuilder(ASTBuilder astBuilder) {
	this.astBuilder = astBuilder;
    }

    public Scanner getScanner() {
	if (scanner == null) {
	    scanner = new DefaultScanner();
	}
	return scanner;
    }

    public void setScanner(Scanner scanner) {
	this.scanner = scanner;
    }

    public Validator getValidator() {
	if (validator == null) {
	    validator = new DefaultValidator();
	}
	return validator;
    }

    public void setValidator(Validator validator) {
	this.validator = validator;
    }
}
