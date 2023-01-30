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

package org.vikamine.kernel.util;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an Iterator-implementation for iterating over the children of a
 * DOM-Node
 * 
 * @author Tobias Vogele
 * 
 */
public class ChildrenIterator implements Iterator {
    private NodeList children = null;
    private int actualPos = 0;
    private int numberOfChildren = 0;

    /**
     * Creates a new ChildrenIterator to iterate over the childrne of the given
     * DOM-Node
     * 
     * @param parent
     *            the parent-node of the children to iterate over
     */
    public ChildrenIterator(Node parent) {
	super();
	children = parent.getChildNodes();
	numberOfChildren = children.getLength();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     * 
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
	return actualPos < numberOfChildren;
    }

    /**
     * Returns the next element in the interation.
     * 
     * @return the next element in the interation.
     */
    @Override
    public Object next() {
	return children.item(actualPos++);
    }

    /**
     * does nothing here!
     */
    @Override
    public void remove() {
    }
}
