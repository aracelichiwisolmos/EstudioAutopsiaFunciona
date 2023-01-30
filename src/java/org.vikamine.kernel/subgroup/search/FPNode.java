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

package org.vikamine.kernel.subgroup.search;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;


public class FPNode {
    public SGSelector sel;

    public double tp = 0;

    public double n;

    public List<FPTreeNode> siblings = new ArrayList();

    public FPNode(SGSelector sel) {
	super();
	this.sel = sel;
    }

    public double getFP() {
	return n - tp;
    }

    @Override
    public String toString() {
	return sel + "(tp:" + tp + ", n:" + n + ", fp:" + getFP() + ")";
    }

    public List<? extends FPTreePath> getAllPrefixPaths() {
	List<FPTreePath> paths = new ArrayList<FPTreePath>();
	boolean allowsForMultipleSelectorsPerAttribute = (sel instanceof NegatedSGSelector)
		|| (sel instanceof NumericSelector);

	for (FPTreeNode activeNode : siblings) {
	    FPTreePath path = new FPTreePath();
	    FPTreeNode parent = activeNode.parent;
	    while (parent.sel != null) {
		if (allowsForMultipleSelectorsPerAttribute
			|| (parent.sel.getAttribute() != sel.getAttribute())) {
		    path.selectors.add(parent.sel);
		}
		parent = parent.parent;
	    }
	    if (!path.selectors.isEmpty()) {
		path.tp = activeNode.tp;
		path.n = activeNode.n;
		paths.add(path);
	    }
	}
	return paths;
    }

    static class FPTreeNode extends FPNode {
	public FPTreeNode parent = null;

	public List<FPTreeNode> children = null;

	public FPTreeNode(SGSelector sel) {
	    super(sel);
	    children = new ArrayList();
	}

	public FPTreeNode getChildForSelector(SGSelector sel) {
	    for (FPTreeNode aChild : children) {
		if (aChild.sel == sel)
		    return aChild;
	    }
	    return null;
	}
    }

    static class FPTreePath {
	public Set<SGSelector> selectors;

	public double tp;

	public double n;

	public FPTreePath() {
	    super();
	    selectors = new HashSet<SGSelector>();
	}

	public double getFP() {
	    return n - tp;
	}

	@Override
	public String toString() {
	    return "(tp:" + tp + ", n:" + n + ", fp:" + getFP() + ")"
		    + selectors;
	}

	public boolean containsAnySelectors(Collection<SGSelector> selectors) {
	    for (SGSelector s : selectors) {
		if (selectors.contains(s))
		    return true;
	    }
	    return false;
	}
    }
}
