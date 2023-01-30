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

public class FPNodeNumeric {
    public final SGSelector sel;
    public double sum = 0;
    public double estimate = 0;
    public double n = 0;

    public List<FPTreeNodeNumeric> siblings = new ArrayList<FPTreeNodeNumeric>();

    public FPNodeNumeric(SGSelector sel) {
	super();
	this.sel = sel;
    }

    @Override
    public String toString() {
	return sel + "(sum:" + sum + ", n:" + n + ", estimate:" + estimate
		+ ")";
    }

    public List<? extends FPTreePathNumeric> getAllPrefixPaths() {
	List<FPTreePathNumeric> paths = new ArrayList<FPTreePathNumeric>();
	boolean allowsForMultipleSelectorsPerAttribute = (sel instanceof NegatedSGSelector)
		|| (sel instanceof NumericSelector);

	for (FPTreeNodeNumeric activeNode : siblings) {
	    FPTreePathNumeric path = new FPTreePathNumeric();
	    FPTreeNodeNumeric parent = activeNode.parent;
	    while (parent.sel != null) {
		if (allowsForMultipleSelectorsPerAttribute
			|| (parent.sel.getAttribute() != sel.getAttribute())) {
		    path.selectors.add(parent.sel);
		}
		parent = parent.parent;
	    }
	    if (!path.selectors.isEmpty()) {
		path.sum = activeNode.sum;
		path.estimate = activeNode.estimate;
		path.n = activeNode.n;
		paths.add(path);
	    }
	}
	return paths;
    }

    static class FPTreeNodeNumeric extends FPNodeNumeric {
	public FPTreeNodeNumeric parent = null;

	public List<FPTreeNodeNumeric> children = null;

	public FPTreeNodeNumeric(SGSelector sel) {
	    super(sel);
	    children = new ArrayList();
	}

	public FPTreeNodeNumeric getChildForSelector(SGSelector sel) {
	    for (FPTreeNodeNumeric aChild : children) {
		if (aChild.sel == sel)
		    return aChild;
	    }
	    return null;
	}
    }

    static class FPTreePathNumeric {
	public Set<SGSelector> selectors;

	public double sum;
	public double estimate;
	public double n;

	public FPTreePathNumeric() {
	    super();
	    selectors = new HashSet<SGSelector>();
	}

	@Override
	public String toString() {
	    return "(sum:" + sum + ", n:" + n + ", sum:" + sum + ")"
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
