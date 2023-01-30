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
 * Created on 10.09.2005
 *
 */
package org.vikamine.kernel.subgroup.analysis.causality;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.subgroup.SG;

/**
 * {@link CausalSGNet} represents a causality network of subgroups.
 * 
 * @author atzmueller
 * 
 */
public class CausalSGNet {

    private HashMap nodes = new HashMap();

    public CausalSGNet() {
	super();
    }

    public Collection getNodes() {
	return nodes.values();
    }

    public Collection getContainedSubgroups() {
	return nodes.keySet();
    }

    public void addNode(CausalSGNode node) {
	nodes.put(node.getSG(), node);
    }

    @Override
    public String toString() {
	return nodes.values().toString();
    }

    public CausalSGNode getNode(SG sg) {
	return (CausalSGNode) nodes.get(sg);
    }

    public void recalculateLevels() {
	Iterator iter = nodes.values().iterator();
	while (iter.hasNext()) {
	    CausalSGNode node = (CausalSGNode) iter.next();
	    if (node.getParents().isEmpty()
		    && ((!node.getChildren().isEmpty()) || (!node
			    .getApproxEqualSubgroups().isEmpty())))
		node.setLevel(0);
	}
	List sortedNodes = new LinkedList(nodes.values());
	Collections.sort(sortedNodes, new Comparator() {
	    @Override
	    public int compare(Object o1, Object o2) {
		CausalSGNode n1 = (CausalSGNode) o1;
		CausalSGNode n2 = (CausalSGNode) o2;
		if (n1.getLevel() == n2.getLevel())
		    return 0;
		else
		    return n1.getLevel() < n2.getLevel() ? -1 : 1;
	    }
	});

	for (Iterator iterator = sortedNodes.iterator(); iterator.hasNext();) {
	    CausalSGNode node = (CausalSGNode) iterator.next();
	    int currentLevel = node.getLevel();
	    if (node.getChildren() != null) {
		List children = new LinkedList(node.getChildren());
		if (node.getApproxEqualSubgroups() != null)
		    children.addAll(node.getApproxEqualSubgroups());
		for (Iterator childrenIterator = children.iterator(); childrenIterator
			.hasNext();) {
		    CausalSGNode child = null;
		    Object next = childrenIterator.next();
		    if (next instanceof SG)
			child = getNode((SG) next);
		    else
			child = (CausalSGNode) next;
		    child.setLevel(currentLevel + 1);
		}
	    }
	}

    }

    public void recalculateLevelsAllowingCircles() {
	// reset nodeLevels
	for (Iterator<CausalSGNode> iter = nodes.values().iterator(); iter
		.hasNext();) {
	    CausalSGNode node = iter.next();
	    node.setLevel(-1);
	}

	for (Iterator<CausalSGNode> iter = nodes.values().iterator(); iter
		.hasNext();) {
	    CausalSGNode node = iter.next();

	    Set children = new HashSet(node.getChildren());
	    children.addAll(node.getCCAssociations());
	    if (!children.isEmpty()) {
		if (node.getLevel() == -1)
		    node.setLevel(0);
		children = new HashSet(node.getChildren());
		if (node.getApproxEqualSubgroups() != null)
		    children.addAll(node.getApproxEqualSubgroups());

		for (Iterator childIter = children.iterator(); childIter
			.hasNext();) {
		    CausalSGNode child = null;
		    Object next = childIter.next();
		    if (next instanceof SG)
			child = getNode((SG) next);
		    else
			child = (CausalSGNode) next;
		    if (child.getLevel() == -1)
			child.setLevel(node.getLevel() + 1);
		}
	    }
	}
    }
}
