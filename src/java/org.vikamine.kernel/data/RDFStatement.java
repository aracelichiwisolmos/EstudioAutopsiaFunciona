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

/**
 * @author lemmerich
 * 
 * An {@link RDFStatement} is representing a triple (subject, predicate,
 * object).
 * 
 * @see a "primer" on RDF (Resource Description Framework) can be found here: <a
 *      href="http://www.w3.org/TR/2004/REC-rdf-primer-20040210/">RDF Primer</a>
 */
public class RDFStatement<V, U> {

    V subject;
    String predicate;
    U object;
    String source;

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public RDFStatement(V subject, String predicate, U object) {
	this(subject, predicate, object, null);
    }

    public RDFStatement(V subject, String predicate, U object,
	    String source) {
	super();
	this.subject = subject;
	this.predicate = predicate;
	this.object = object;
	this.source = source;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RDFStatement other = (RDFStatement) obj;
	if (object == null) {
	    if (other.object != null)
		return false;
	} else if (!object.equals(other.object))
	    return false;
	if (predicate == null) {
	    if (other.predicate != null)
		return false;
	} else if (!predicate.equals(other.predicate))
	    return false;
	if (subject == null) {
	    if (other.subject != null)
		return false;
	} else if (!subject.equals(other.subject))
	    return false;
	return true;
    }

    public U getObject() {
	return object;
    }

    public String getPredicate() {
	return predicate;
    }

    public V getSubject() {
	return subject;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((object == null) ? 0 : object.hashCode());
	result = prime * result
		+ ((predicate == null) ? 0 : predicate.hashCode());
	result = prime * result + ((subject == null) ? 0 : subject.hashCode());
	return result;
    }

    public void setObject(U object) {
	this.object = object;
    }

    public void setPredicate(String predicate) {
	this.predicate = predicate;
    }

    public void setSubject(V subject) {
	this.subject = subject;
    }

    @Override
    public String toString() {
	return "RDFStatement [subject=" + subject + ", predicate=" + predicate
		+ ", object=" + object + "]";
    }

    public boolean hasSource() {
	return source != null;
    }

}
