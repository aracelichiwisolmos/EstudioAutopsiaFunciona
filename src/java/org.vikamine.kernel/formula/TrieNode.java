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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implementation of a trie data structure, for efficient access to
 * string-structured data.
 * 
 * @author Tobias Vogele
 */
public class TrieNode extends AbstractMap implements Comparable, Map.Entry {

    class TrieEntrySet extends AbstractSet {

	@Override
	public int size() {
	    return size;
	}

	@Override
	public Iterator iterator() {
	    return new TrieIterator();
	}

    }

    class NodeIterator implements Iterator {

	boolean returned = false;

	@Override
	public void remove() {
	    if (!returned) {
		throw new NoSuchElementException("must call add before remove");
	    }
	    removeValue();
	}

	@Override
	public boolean hasNext() {
	    return hasValue() && !returned;
	}

	@Override
	public Object next() {
	    if (!hasNext()) {
		throw new NoSuchElementException("no more elements");
	    }
	    returned = true;
	    return TrieNode.this;
	}
    }

    class TrieIterator implements Iterator {

	private Iterator childIterator;
	private TrieNode nextEntry;
	private TrieNode previousEntry;

	private int childIndex = 0;

	private boolean hasNext;

	TrieIterator() {
	    childIterator = new NodeIterator();
	    prepareNext();
	}

	@Override
	public void remove() {
	    // Note: The problem is, that remove() removes only the value, not
	    // the node.
	    // So the Tree will never shrink. if we fix this, note, that this
	    // will be a
	    // problem with next, because then the children-list can change.
	    if (previousEntry == null) {
		throw new NoSuchElementException("must call next before remove");
	    }
	    previousEntry.removeValue();
	}

	@Override
	public boolean hasNext() {
	    return hasNext;
	}

	@Override
	public Object next() {
	    if (!hasNext()) {
		throw new NoSuchElementException("no more elements");
	    }
	    previousEntry = nextEntry;
	    prepareNext();
	    return previousEntry;
	}

	private void prepareNext() {
	    if (!childIterator.hasNext()) {
		nextIterator();
	    }
	    if (childIterator.hasNext()) {
		nextEntry = (TrieNode) childIterator.next();
		hasNext = true;
	    } else {
		hasNext = false;
	    }
	}

	private void nextIterator() {
	    while (hasChildren() && childIndex < getChildren().size()
		    && !childIterator.hasNext()) {
		TrieNode nextNode = (TrieNode) getChildren().get(childIndex);
		childIterator = nextNode.entrySet().iterator();
		childIndex++;
	    }
	}

    }

    private TrieNode parent;

    private List children;

    private Object value;

    private char key = 0;

    private boolean hasValue = false;

    private int size = 0;

    public TrieNode() {
	super();
    }

    public TrieNode(char nodeKey) {
	setKey(nodeKey);
    }

    public TrieNode(char nodeKey, Object nodeValue) {
	setKey(nodeKey);
	setValue(nodeValue);
    }

    public List getChildren() {
	return children;
    }

    public void setChildren(List children) {
	this.children = children;
    }

    @Override
    public Object getValue() {
	return value;
    }

    @Override
    public Object setValue(Object data) {
	boolean oldHasValue = hasValue();
	Object old = getValue();
	this.value = data;
	hasValue = true;
	if (!oldHasValue) {
	    incrementSize();
	}
	return old;
    }

    public void removeValue() {
	boolean oldHasValue = hasValue();
	value = null;
	hasValue = false;
	if (oldHasValue) {
	    decrementSize();
	}
    }

    public TrieNode getParent() {
	return parent;
    }

    public void setParent(TrieNode parent) {
	this.parent = parent;
    }

    public char getNodeKey() {
	return key;
    }

    @Override
    public Object getKey() {
	return getKeyBuffer().toString();
    }

    protected StringBuffer getKeyBuffer() {
	StringBuffer b;
	if (getParent() != null) {
	    b = getParent().getKeyBuffer();
	} else {
	    b = new StringBuffer();
	}
	if (getNodeKey() != 0) {
	    b.append(getNodeKey());
	}
	return b;
    }

    public void setKey(char key) {
	this.key = key;
    }

    public Object removeChild(String childKeys) {
	TrieNode node = getNode(childKeys, false);
	if (node == null) {
	    return null;
	}
	Object old = node.getValue();
	node.removeValue();
	if (!node.hasChildren() && node.getParent() != null) {
	    node.getParent().removeChild(node);
	}
	return old;
    }

    protected boolean removeChild(TrieNode node) {
	boolean removed = getChildren().remove(node);
	if (removed) {
	    node.setParent(null);
	}
	if (!hasValue() && !hasChildren() && getParent() != null) {
	    getParent().removeChild(this);
	}
	return removed;
    }

    public boolean hasChildren() {
	return (getChildren() != null && !getChildren().isEmpty());
    }

    @Override
    public int compareTo(Object o) {
	return getNodeKey() - ((TrieNode) o).getNodeKey();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	} else if (obj == null || obj.getClass() != getClass()) {
	    return false;
	} else {
	    TrieNode t = (TrieNode) obj;
	    return getNodeKey() == t.getNodeKey();
	}
    }

    @Override
    public int hashCode() {
	return getNodeKey();
    }

    @Override
    public String toString() {
	return getNodeKey() + ": " + getValue();
    }

    public boolean hasValue() {
	return hasValue;
    }

    public TrieNode getNode(String childKey, boolean createIfNotExists) {
	// This is the only method, which walks through the tree.

	if (childKey.length() == 0) {
	    return this;
	}
	TrieNode node = getChild(childKey.charAt(0), createIfNotExists);
	if (node == null) {
	    return null;
	}
	return node.getNode(childKey.substring(1), createIfNotExists);
    }

    @Override
    public Object get(Object keyString) {
	String k = (String) keyString;
	TrieNode node = getNode(k, false);
	if (node == null) {
	    return null;
	} else {
	    return node.getValue();
	}
    }

    @Override
    public Object put(Object keyString, Object valueObject) {
	String k = (String) keyString;
	TrieNode node = getNode(k, true);
	Object old = node.getValue();
	node.setValue(valueObject);
	return old;
    }

    public TrieNode getChild(char childKey, boolean createIfNotExists) {
	TrieNode node = new TrieNode(childKey);
	if (getChildren() == null) {
	    // Keine Kinder da:
	    if (!createIfNotExists) {
		return null;
	    } else {
		setChildren(new ArrayList());
	    }
	}
	int pos = Collections.binarySearch(getChildren(), node);
	if (pos < 0) {
	    if (!createIfNotExists) {
		return null;
	    } else {
		pos = -pos - 1;
		getChildren().add(pos, node);
		node.setParent(this);
		return node;
	    }
	} else {
	    return (TrieNode) getChildren().get(pos);
	}
    }

    protected void incrementSize() {
	size++;
	if (getParent() != null) {
	    getParent().incrementSize();
	}
    }

    protected void decrementSize() {
	size--;
	if (getParent() != null) {
	    getParent().decrementSize();
	}
    }

    @Override
    public Set entrySet() {
	return new TrieEntrySet();
    }

    @Override
    public boolean containsKey(Object keyString) {
	String k = (String) keyString;
	TrieNode node = getNode(k, false);
	return node != null && node.hasValue();
    }

    @Override
    public Object remove(Object keyString) {
	String k = (String) keyString;
	TrieNode node = getNode(k, false);
	if (node == null) {
	    return null;
	}
	Object old = node.getValue();
	node.removeValue();
	return old;
    }
}
