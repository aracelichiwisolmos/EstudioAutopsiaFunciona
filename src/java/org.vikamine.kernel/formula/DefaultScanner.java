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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.formula.exception.AmbiguousTokenException;
import org.vikamine.kernel.formula.exception.UnknownTokenException;

/**
 * {@link DefaultScanner} implements our (default) scanner.
 * 
 * @author Tobias Vogele
 */
public class DefaultScanner implements Scanner {

    protected static class RecognizedToken {
	public RecognizedToken(ParserElementBuilder builder, String token) {
	    this.builder = builder;
	    this.token = token;
	}

	protected ParserElementBuilder builder;

	protected String token;

	public ParserElementBuilder getBuilder() {
	    return builder;
	}

	public void setBuilder(ParserElementBuilder builder) {
	    this.builder = builder;
	}

	public String getToken() {
	    return token;
	}

	public void setToken(String token) {
	    this.token = token;
	}

	public int getLength() {
	    return getToken().length();
	}
    }

    protected TrieNode constantElements = new TrieNode();

    protected List variableElements = new LinkedList();

    public DefaultScanner() {
	super();
    }

    @Override
    public void registerVariableElement(
	    VariableParserElementBuilder elementBuilder) {
	variableElements.add(elementBuilder);
    }

    @Override
    public void registerConstantElement(
	    ConstantParserElementBuilder elementBuilder) {
	if (containsConstantElement(elementBuilder.getText())) {
	    throw new IllegalArgumentException(
		    "A element with this text already exists: "
			    + elementBuilder.getText());
	}
	constantElements.put(elementBuilder.getText(), elementBuilder);
    }

    public TrieNode getConstantElements() {
	return constantElements;
    }

    @Override
    public boolean containsConstantElement(String constantString) {
	TrieNode old = constantElements.getNode(constantString, false);
	return old != null && old.hasValue();
    }

    @Override
    public List scan(String text) throws ParseException {
	List recognizedTokens = scanTokens(text);
	List parserElements = buildParserElements(recognizedTokens);
	return parserElements;
    }

    protected List buildParserElements(List tokens)
	    throws UnknownTokenException {
	List parserElements = new ArrayList(tokens.size());
	for (Iterator iter = tokens.iterator(); iter.hasNext();) {
	    RecognizedToken tokk = (RecognizedToken) iter.next();
	    List before = Collections.unmodifiableList(parserElements);
	    ParserElement pe = tokk.getBuilder().createParserElement(
		    tokk.getToken(), before);
	    if ((pe.getType() != null)
		    && (pe.getType().equals(Exception.class))) {
		throw new UnknownTokenException("Unknown text "
			+ pe.getContent(), -1, pe.getToken());
	    }
	    parserElements.add(pe);
	}
	return parserElements;
    }

    protected List scanTokens(String text) throws ParseException {
	List elements = new LinkedList();
	for (int i = 0; i < text.length();) {
	    RecognizedToken token = scanNextToken(text.substring(i), i);
	    if (token != null) {
		elements.add(token);
		i += token.getLength();
	    } else if (!ignore(text.charAt(i))) {
		throw new UnknownTokenException("Unknown text: "
			+ text.substring(i), i, text.substring(i));
	    } else {
		i++;
	    }
	}
	return elements;
    }

    protected RecognizedToken scanNextToken(String text, int position)
	    throws ParseException {
	RecognizedToken constant = scanConstantToken(text);
	RecognizedToken variable = scanVariableToken(text, position);
	return getLongerToken(constant, variable, position);
    }

    protected RecognizedToken scanVariableToken(String text, int position)
	    throws ParseException {
	List lastValid = new LinkedList();
	List validSubstrings = new LinkedList(variableElements);

	for (int i = 0; i < text.length() && !validSubstrings.isEmpty(); i++) {
	    String substring = text.substring(0, i + 1);
	    List valid = new LinkedList();
	    for (Iterator iter = validSubstrings.iterator(); iter.hasNext();) {
		VariableParserElementBuilder builder = (VariableParserElementBuilder) iter
			.next();
		if (!builder.isValidStart(substring)) {
		    iter.remove();
		} else if (builder.isValid(substring)) {
		    valid.add(new RecognizedToken(builder, substring));
		}
		if (!valid.isEmpty()) {
		    lastValid = valid;
		}
	    }

	}
	// Ergebnis auswerten:
	if (lastValid.isEmpty()) {
	    return null;
	} else if (lastValid.size() == 1) {
	    return (RecognizedToken) lastValid.get(0);
	} else {
	    RecognizedToken t1 = (RecognizedToken) lastValid.get(0);
	    AmbiguousTokenException ex = new AmbiguousTokenException(
		    "ambiguous token: " + t1.getToken(), position,
		    t1.getToken());
	    for (Iterator iter = lastValid.iterator(); iter.hasNext();) {
		RecognizedToken tok = (RecognizedToken) iter.next();
		ex.addPossibleBuilder(tok.builder);
	    }
	    throw ex;
	}
    }

    protected RecognizedToken scanConstantToken(String text) {
	RecognizedToken lastToken = null;
	TrieNode node = constantElements;
	for (int i = 0; i < text.length() && node != null; i++) {
	    node = node.getChild(text.charAt(i), false);
	    if (node != null && node.hasValue()) {
		lastToken = new RecognizedToken(
			(ParserElementBuilder) node.getValue(), text.substring(
				0, i + 1));
	    }
	}
	return lastToken;
    }

    protected RecognizedToken getLongerToken(RecognizedToken t1,
	    RecognizedToken t2, int position) throws ParseException {
	if (t1 != null && t2 != null) {
	    if (t1.getLength() > t2.getLength()) {
		return t1;
	    } else if (t1.getLength() < t2.getLength()) {
		return t2;
	    } else {
		AmbiguousTokenException ex = new AmbiguousTokenException(
			"Text is ambivalent: " + t1.getToken(), position,
			t1.getToken());
		ex.addPossibleBuilder(t1.getBuilder());
		ex.addPossibleBuilder(t2.getBuilder());
		throw ex;
	    }
	} else if (t1 != null) {
	    return t1;
	} else {
	    return t2;
	}
    }

    /**
     * Returns true, if this character can remain unparsed without error. This
     * is probably true for whitespace.
     * 
     * @param c
     * @return
     */
    protected boolean ignore(char c) {
	return Character.isWhitespace(c);
    }

}
