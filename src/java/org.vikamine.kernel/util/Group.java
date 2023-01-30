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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages (hierarchical) groups, where subgroups are possible.
 * 
 * @author lemmerich
 *
 * @param <T>
 */

public class Group<T> {

    private final Set<T> elements;
    private final String id;
    private boolean root;
    private List<Group<T>> subsets;

    public Group(String id) {
	this.id = id;
	this.elements = new HashSet<T>();
	this.root = true;
    }

    public void addElement(T newElement) {
	elements.add(newElement);
    }

    public void addSubset(Group<T> newGroup) {
	if (subsets == null) {
	    subsets = new ArrayList<Group<T>>();
	}
	subsets.add(newGroup);

	newGroup.root = false;
    }

    private void appendDashes(StringBuffer result, int praefix) {
	for (int i = 0; i < praefix; i++) {
	    result.append("-");
	}
    }

    public Set<T> getDirectElements() {
	return elements;
    }

    public List<T> getElements() {
	Set<T> result = new HashSet<T>();
	result.addAll(elements);
	if (subsets != null) {
	    for (Group<T> sub : subsets) {
		result.addAll(sub.getElements());
	    }
	}
	ArrayList<T> resultList = new ArrayList<T>();
	resultList.addAll(result);
	return resultList;
    }

    public String getId() {
	return id;
    }

    public Set<Group<T>> getSubsets() {
	if (subsets == null) {
	    return null;
	}
	HashSet<Group<T>> result = new HashSet<Group<T>>();
	result.addAll(subsets);
	return result;
    }

    public List<Group<T>> getSubsetsAsList() {
	if (subsets == null) {
	    return null;
	}
	ArrayList<Group<T>> result = new ArrayList<Group<T>>(subsets.size());
	result.addAll(subsets);
	return result;
    }

    public boolean isRoot() {
	return root;
    }

    @Override
    public String toString() {
	return toString(0);
    }

    public String toString(int praefix) {
	StringBuffer result = new StringBuffer();
	appendDashes(result, praefix);
	if (praefix > 0) {
	    result.append(" ");
	}
	result.append(id);
	result.append("\n");
	for (T elem : elements) {
	    appendDashes(result, praefix);
	    result.append("+ ");
	    result.append(elem.toString());
	    result.append("\n");
	}

	if (subsets != null) {
	    for (Group<T> aSubset : subsets) {
		result.append(aSubset.toString(praefix + 1));
		result.append("\n");
	    }
	}

	return result.toString();
    }
}
