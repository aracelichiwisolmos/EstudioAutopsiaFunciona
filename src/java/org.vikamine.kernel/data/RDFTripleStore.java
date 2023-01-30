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

package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lemmerich
 * 
 *         A {@link RDFTripleStore} holds a set of {@link RDFStatement}s.
 * 
 * @see a "primer" on RDF (Resource Description Framework) can be found here: <a
 *      href="http://www.w3.org/TR/2004/REC-rdf-primer-20040210/">RDF Primer</a>
 */
public class RDFTripleStore {

    private final Set<RDFStatement> statements;

    public static final String ANY_OBJECT = "**ANY_OBJECT**";
    public static final String ANY_PROPERTY = ANY_OBJECT;
    public static List<TripleStoreListener> listeners;

    public RDFTripleStore() {
	statements = new HashSet<RDFStatement>();
	listeners = new ArrayList<TripleStoreListener>();
    }

    public void addStatement(RDFStatement s) {
	addStatements(Collections.singleton(s));
    }

    public void addStatements(Collection<RDFStatement> s) {
	statements.addAll(s);
	informListenerStatementsAdded(s);
    }

    public void addListener(TripleStoreListener listener) {
	listeners.add(listener);
    }

    public void removeListener(TripleStoreListener listener) {
	listeners.remove(listener);
    }

    /**
     * Iterates over the internal {@link Set} of {@link RDFStatement}s and
     * returns the first {@link RDFStatement} <code>(s',p',o')</code> matching:<br/>
     * 
     * (s' = s | s' = O) & (p' = p | p' = P) & (o' = O | o' = O)
     * <p/>
     * 
     * <b>Note:</b> The order of the internal {@link Set} ({@link HashSet}) is
     * not fixed. The returned value may not be the same for each call even if
     * the {@link RDFTripleStore} is not altered. It is recommended to use only,
     * if you can assume, there is only one matching statement.
     * 
     * @param subject
     *            <code>s</code>
     * @param predicate
     *            <code>p</code>
     * @param object
     *            <code>o</code>
     * 
     * @return see above
     */
    public RDFStatement getFirstMatchingStatement(Object subject,
	    String predicate, Object object) {
	for (RDFStatement s : statements) {
	    if (subject.equals(ANY_OBJECT) || s.getSubject().equals(subject)) {
		if (predicate.equals(ANY_PROPERTY)
			|| predicate.equals(s.getPredicate())) {
		    if (object.equals(ANY_OBJECT)
			    || object.equals(s.getObject())) {
			return s;
		    }
		}
	    }
	}
	return null;
    }

    public boolean contains(Object subject, String predicate, Object object) {
	return getFirstMatchingStatement(subject, predicate, object) != null;
    }

    public Set<RDFStatement> getStatements() {
	return Collections.unmodifiableSet(statements);
    }

    /**
     * Returns a set of {@link RDFStatement}s given the subject <code>s</code>,
     * predicate <code>p</code> and object <code>o</code>.
     * <p/>
     * 
     * Let<br/>
     * <code>O = {@link RDFTripleStore#ANY_OBJECT}</code> and<br/>
     * <code>P = {@link RDFTripleStore#ANY_PROPERTY}</code>.
     * <p/>
     * 
     * The returned set S is:<br/>
     * <code>S = {(s',p',o') | (s' = s | s' = O) & (p' = p | p' = P) & (o' = O | o' = O)}
     * 
     * @param subject
     *            s
     * @param predicate
     *            p
     * @param object
     *            o
     * 
     * @return the set S described above
     */
    public Set<RDFStatement> getStatements(Object subject, String predicate,
	    Object object) {
	Set<RDFStatement> result = new HashSet<RDFStatement>();
	for (RDFStatement s : statements) {
	    if (subject.equals(ANY_OBJECT) || s.getSubject().equals(subject)) {
		if (predicate.equals(ANY_PROPERTY)
			|| predicate.equals(s.getPredicate())) {
		    if (object.equals(ANY_OBJECT)
			    || object.equals(s.getObject())) {
			result.add(s);
		    }
		}
	    }
	}
	return result;
    }

    public Set<RDFStatement> getStatementsForFile(String filename) {
	Set<RDFStatement> result = new HashSet<RDFStatement>();
	for (RDFStatement s : statements) {
	    if (s.hasSource() && s.getSource().equals(filename)) {
		result.add(s);
	    }
	}
	return result;
    }

    private void informListenerStatementsAdded(Collection<RDFStatement> s) {
	Collection<RDFStatement> reportedStatements = new ArrayList<RDFStatement>();
	for (TripleStoreListener tsl : listeners) {
	    Collection<String> supportedTypes = tsl.getSupportedTypes();
	    for (RDFStatement aStatement : s) {
		if (supportedTypes.contains(TripleStoreListener.ANY)
			|| supportedTypes.contains(aStatement.getPredicate())) {
		    reportedStatements.add(aStatement);
		}
	    }
	    if (!reportedStatements.isEmpty()) {
		tsl.statementsAdded(reportedStatements);
		reportedStatements.clear();
	    }
	}
    }

    private void informListenerStatementsRemoved(Collection<RDFStatement> s) {
	Collection<RDFStatement> reportedStatements = new ArrayList<RDFStatement>();
	for (TripleStoreListener tsl : listeners) {
	    Collection<String> supportedTypes = tsl.getSupportedTypes();
	    for (RDFStatement aStatement : s) {
		if (supportedTypes.contains(TripleStoreListener.ANY)
			|| supportedTypes.contains(aStatement.getPredicate())) {
		    reportedStatements.add(aStatement);
		}
	    }
	    if (!reportedStatements.isEmpty()) {
		tsl.statementsRemoved(reportedStatements);
		reportedStatements.clear();
	    }
	}
    }

    public void removeStatement(RDFStatement s) {
	removeStatements(Collections.singleton(s));
    }

    public void removeStatements(Collection<RDFStatement> s) {
	statements.removeAll(s);
	informListenerStatementsRemoved(s);
    }

    @Override
    public String toString() {
	return statements.toString();
    }

}
