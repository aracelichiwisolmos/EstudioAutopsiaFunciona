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

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.formula.ParserElementBuilder;

/**
 * {@link AmbiguousTokenException} is an exception when a parsed token is
 * ambiguous.
 * 
 * @author Tobias Vogele
 */
public class AmbiguousTokenException extends ParseException {

    private static final long serialVersionUID = -960431827244204847L;

    protected String token;

    protected List possibleBuilder = new LinkedList();

    public AmbiguousTokenException(String s, int errorOffset) {
	super(s, errorOffset);
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public List getPossibleBuilder() {
	return possibleBuilder;
    }

    public void setPossibleBuilder(List possibleBuilder) {
	this.possibleBuilder = possibleBuilder;
    }

    public AmbiguousTokenException(String s, int errorOffset, String token,
	    List possibleBuilder) {
	super(s, errorOffset);
	this.token = token;
	this.possibleBuilder = possibleBuilder;
    }

    public AmbiguousTokenException(String s, int errorOffset, String token) {
	super(s, errorOffset);
	this.token = token;
    }

    public void addPossibleBuilder(ParserElementBuilder builder) {
	getPossibleBuilder().add(builder);

    }
}
