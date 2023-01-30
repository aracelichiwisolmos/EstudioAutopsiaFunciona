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
 * Created on 31.03.2004
 * 
 */
package org.vikamine.kernel.subgroup.analysis.causality;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.util.XMLUtils;

/**
 * Base class for a subgroup in the {@link CausalSGNet}.
 * 
 * @author atzmueller
 * 
 */
public class CausalSGNode {

    private int hashCode = 0;

    private Set children = new HashSet();

    private Set parents = new HashSet();

    private Set approxEqualSubgroups = new HashSet();

    private Set cCAssociations = new HashSet();

    private int level = -1;

    private SG sg = null;

    private static boolean trace = true;

    protected CausalSGNode(SG sg) {
	this.sg = sg;
    }

    public void addChild(CausalSGNode node) {
	children.add(node);
	node.addParent(this);
	if (trace) {
	    System.out.println("Added Causal Relation:\t"
		    + sg.getSGDescription().getDescription() + " --> "
		    + node.getSG().getSGDescription().getDescription());
	    if (node.getChildren().contains(this)) {
		System.out.println("Cycle:\t"
			+ sg.getSGDescription().getDescription() + " <<-->> "
			+ node.getSG().getSGDescription().getDescription());
	    }
	}
    }

    public void addCCAssociation(CausalSGNode node) {
	cCAssociations.add(node);
	node.cCAssociations.add(this);
	if (trace) {
	    System.out.println("Added Association\t"
		    + sg.getSGDescription().getDescription() + " --> "
		    + node.getSG().getSGDescription().getDescription());
	}
    }

    private void addParent(CausalSGNode parent) {
	parents.add(parent);
    }

    public SG getSG() {
	return sg;
    }

    public Set getChildren() {
	return children;
    }

    // public String toString() {
    // String result = " node= " + sg.toString() + "\n";
    // Object[] dep = children.toArray();
    // for (int i = 0; i < children.size(); i++) {
    // result += "\t\t depNodes[" + i + "]=" + dep[i].toString() + "\n";
    // }
    // return result;
    // }

    @Override
    public String toString() {
	return sg.getSGDescription().getDescription();
    }

    public String verbalize() {
	StringBuffer b = new StringBuffer("<html>");
	for (Iterator iter = getSG().getSGDescription().iterator(); iter
		.hasNext();) {
	    SGNominalSelector select = (SGNominalSelector) iter.next();
	    String selString = select.getDescription();
	    b.append(XMLUtils.convertToHTMLCompliantText(selString));
	    if (iter.hasNext()) {
		b.append("<br>");
	    }
	}
	b.append("</html>");
	return b.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	if (hashCode == 0) {
	    hashCode = sg.hashCode();
	}
	return hashCode;
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(int newLevel) {
	// if (level <= newLevel)
	level = Math.max(newLevel, newLevel);
	// else
	// throw new IllegalStateException("NewLevel: " + newLevel
	// + " < oldLevel: " + level + "!");

	// the levels of the children are set in CausalSGNet#recalculateLevels
	// Iterator iter = children.iterator();
	// while (iter.hasNext()) {
	// CausalSGNode child = (CausalSGNode) iter.next();
	// child.setLevel(level + 1);
	// }
    }

    @Override
    public boolean equals(Object other) {
	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    return sg.equals(((CausalSGNode) other).getSG());
	}
    }

    public Set getParents() {
	return parents;
    }

    public Set getApproxEqualSubgroups() {
	return approxEqualSubgroups;
    }

    public void setApproxEqualSubgroups(Set approxEqualSubgroups) {
	this.approxEqualSubgroups = approxEqualSubgroups;
    }

    public Set getCCAssociations() {
	return cCAssociations;
    }
}
