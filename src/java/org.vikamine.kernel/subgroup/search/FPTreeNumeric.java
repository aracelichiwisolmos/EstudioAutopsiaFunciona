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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.search.FPNodeNumeric.FPTreeNodeNumeric;
import org.vikamine.kernel.subgroup.search.FPNodeNumeric.FPTreePathNumeric;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.NumericTarget;

class FPTreeNumeric {

    public List<FPNodeNumeric> frequentHeaderNodes;
    public FPTreeNodeNumeric root;
    private SDMapNumeric sdmap;

    /**
     * Used internally to build a conditional tree
     * 
     * @param frequentHeaderNodes
     * @param root
     * @param sdmap
     */
    private FPTreeNumeric(List<FPNodeNumeric> frequentHeaderNodes,
	    FPTreeNodeNumeric root, SDMapNumeric sdmap) {
	this.frequentHeaderNodes = frequentHeaderNodes;
	this.root = root;
	this.sdmap = sdmap;
    }

    public FPTreeNumeric(Collection<? extends SGSelector> selectorSet,
	    Iterable<DataRecord> instanceIteration, boolean pruneFrequentItems,
	    SDMapNumeric sdmap) {
	this.sdmap = sdmap;

	this.frequentHeaderNodes = createFrequentItemNodeList(selectorSet,
		instanceIteration, pruneFrequentItems);
	this.root = new FPTreeNodeNumeric(null);
	if (!frequentHeaderNodes.isEmpty()) {
	    for (Iterator<DataRecord> iter = instanceIteration.iterator(); iter
		    .hasNext();) {
		DataRecord inst = iter.next();
		insertIntoTree(inst, (NumericTarget) sdmap.target);
		if (sdmap.aborted) {
		    return;
		}
		// debuggerProxy.pass("buildingFPTree");
	    }
	}
    }

    private StringBuffer printTree(FPTreeNodeNumeric node, int depth,
	    StringBuffer buffy) {
	for (int i = 0; i < depth; i++) {
	    buffy.append("|");
	}
	if (node.sel != null) {
	    buffy.append(node.toString());
	}
	buffy.append("\n");
	for (Iterator iter = node.children.iterator(); iter.hasNext();) {
	    FPTreeNodeNumeric child = (FPTreeNodeNumeric) iter.next();
	    printTree(child, depth + 1, buffy);
	}
	return buffy;
    }

    @Override
    public String toString() {
	StringBuffer buffy = new StringBuffer();
	buffy.append("Frequent Selectors: " + frequentHeaderNodes);
	buffy = printTree(root, 0, buffy);
	return buffy.toString();
    }

    public Collection<Attribute> extractFrequentAttributes() {
	Set<Attribute> attributes = new HashSet<Attribute>();
	for (Iterator<FPNodeNumeric> iter = frequentHeaderNodes.iterator(); iter
		.hasNext();) {
	    FPNodeNumeric n = iter.next();
	    attributes.add(n.sel.getAttribute());
	}
	return attributes;
    }

    public FPNodeNumeric findMostUnfrequentFPNode(
	    Collection<SGSelector> selectors) {
	List<FPNodeNumeric> freqHeaderNodes = new ArrayList<FPNodeNumeric>(
		frequentHeaderNodes);
	sortFrequentNodesAscending(freqHeaderNodes);
	for (FPNodeNumeric n : freqHeaderNodes) {
	    if (selectors.contains(n.sel)) {
		return n;
	    }
	}
	return null;
    }

    static void sortFrequentNodesDescending(List<FPNodeNumeric> nodes) {
	Collections.sort(nodes, new Comparator<FPNodeNumeric>() {
	    @Override
	    public int compare(FPNodeNumeric n1, FPNodeNumeric n2) {
		if (n1.n == n2.n)
		    return 0;
		else
		    return n1.n < n2.n ? 1 : -1;
	    }
	});
    }

    static void sortFrequentNodesAscending(List<FPNodeNumeric> nodes) {
	Collections.sort(nodes, new Comparator<FPNodeNumeric>() {
	    @Override
	    public int compare(FPNodeNumeric n1, FPNodeNumeric n2) {
		if (n1.n == n2.n)
		    return 0;
		else
		    return n1.n > n2.n ? 1 : -1;
	    }
	});
    }

    public boolean containsAnyAttribute(Set<Attribute> attributes) {
	for (FPNodeNumeric node : frequentHeaderNodes) {
	    for (Attribute a : attributes) {
		if (node.sel.getAttribute() == a)
		    return true;
	    }
	}
	return false;
    }

    public FPNodeNumeric getFPNodeWithSameAttributeAsSelector(SGSelector sel) {
	for (FPNodeNumeric n : frequentHeaderNodes) {
	    if (n.sel.getAttribute() == sel.getAttribute()) {
		return n;
	    }
	}
	return null;
    }

    public FPNodeNumeric getFPNodeForSelector(SGSelector sel) {
	for (FPNodeNumeric n : frequentHeaderNodes) {
	    if (n.sel == sel) {
		return n;
	    }
	}
	return null;
    }

    public FPTreeNumeric buildConditionalFPTree(FPNodeNumeric headerNode) {
	List<? extends FPTreePathNumeric> paths = headerNode
		.getAllPrefixPaths();
	FPTreeNumeric conditionalTree = buildConditionalFPTree(paths);
	return conditionalTree;
    }

    private FPTreeNumeric buildConditionalFPTree(
	    List<? extends FPTreePathNumeric> conditionalPaths) {
	Map<SGSelector, FPNodeNumeric> frequentNodesMap = collectFrequentNodesMapFromPaths(conditionalPaths);
	List<FPNodeNumeric> frequentNodes = pruneAndSortFrequentNodesMapDescending(
		frequentNodesMap, true);
	if (frequentNodes.isEmpty())
	    return null;

	FPTreeNumeric tree = new FPTreeNumeric(frequentNodes,
		new FPTreeNodeNumeric(null), sdmap);
	tree.insertPathsIntoConditionalTree(conditionalPaths, frequentNodes);
	return tree;
    }

    private void insertPathsIntoConditionalTree(List conditionalPaths,
	    List frequentNodes) {
	for (Iterator iter = conditionalPaths.iterator(); iter.hasNext();) {
	    FPTreePathNumeric path = (FPTreePathNumeric) iter.next();
	    FPTreeNodeNumeric activeNode = root;
	    for (Iterator iterator = frequentNodes.iterator(); iterator
		    .hasNext();) {
		FPNodeNumeric aFrequentNode = (FPNodeNumeric) iterator.next();
		SGSelector sel = aFrequentNode.sel;
		if (path.selectors.remove(sel)) {
		    // tests contains, and
		    // remove in one step
		    FPTreeNodeNumeric child = activeNode
			    .getChildForSelector(sel);
		    if (child == null) {
			child = new FPTreeNodeNumeric(sel);
			insertIntoFrequentNodes(aFrequentNode, child);
			activeNode.children.add(child);
			child.parent = activeNode;
		    }
		    child.sum += path.sum;
		    child.estimate += path.estimate;
		    child.n += path.n;
		    activeNode = child;
		}
		if (path.selectors.isEmpty())
		    break;
	    }
	}
    }

    private void insertIntoFrequentNodes(FPNodeNumeric freqNode,
	    FPTreeNodeNumeric node) {
	if (freqNode.sel == node.sel) {
	    freqNode.siblings.add(node);
	} else {
	    throw new IllegalStateException("Wrong freqNode for FPTreeNode ...");
	}
    }

    /**
     * does not prune on estimate, if resultSet == null
     */
    private List<FPNodeNumeric> pruneAndSortFrequentNodesMapDescending(
	    Map<SGSelector, FPNodeNumeric> frequentNodesMap,
	    boolean pruneFrequentItems) {
	// prune
	if (pruneFrequentItems) {
	    for (Iterator<FPNodeNumeric> iter = frequentNodesMap.values()
		    .iterator(); iter.hasNext();) {
		FPNodeNumeric node = iter.next();
		if (node.n < sdmap.task.getMinSubgroupSize()) {
		    iter.remove();
		    // optimistic estimate pruning
		} else {
		    if ((sdmap.result != null && (sdmap.isPruningEnabled() && !sdmap.result
			    .isInKBestQualityRange(node.estimate)))) {
			iter.remove();
		    }
		}
	    }
	}

	// sort
	List<FPNodeNumeric> result = new ArrayList<FPNodeNumeric>(
		frequentNodesMap.values());
	FPTreeNumeric.sortFrequentNodesDescending(result);
	return result;
    }

    private static Map<SGSelector, FPNodeNumeric> collectFrequentNodesMapFromPaths(
	    List<? extends FPTreePathNumeric> conditionalPaths) {
	// build frequent node list
	Map<SGSelector, FPNodeNumeric> frequentNodesMap = new HashMap<SGSelector, FPNodeNumeric>();
	for (Iterator iter = conditionalPaths.iterator(); iter.hasNext();) {
	    FPTreePathNumeric path = (FPTreePathNumeric) iter.next();
	    for (Iterator iterator = path.selectors.iterator(); iterator
		    .hasNext();) {
		SGSelector sel = (SGSelector) iterator.next();
		FPNodeNumeric node = frequentNodesMap.get(sel);
		if (node == null) {
		    node = new FPNodeNumeric(sel);
		    frequentNodesMap.put(sel, node);
		}
		node.sum += path.sum;
		node.estimate += path.estimate;
		node.n += path.n;
	    }
	}
	return frequentNodesMap;
    }

    private void insertIntoTree(DataRecord inst, NumericTarget target) {
	FPTreeNodeNumeric activeNode = root;
	for (FPNodeNumeric node : frequentHeaderNodes) {
	    SGSelector sel = node.sel;
	    if (sel.isContainedInInstance(inst)) {
		FPTreeNodeNumeric child = activeNode.getChildForSelector(sel);
		if (child == null) {
		    child = new FPTreeNodeNumeric(sel);
		    insertIntoFrequentNodes(node, child);
		    activeNode.children.add(child);
		    child.parent = activeNode;
		}
		child.n += inst.getWeight();
		child.sum += inst.getWeight() * target.getValue(inst);
		// child.estimate += target.getValue(inst);
		if (target.getValue(inst) > sdmap.populationMean) {
		    child.estimate += (target.getValue(inst) - sdmap.populationMean)
			    * inst.getWeight();
		}
		activeNode = child;
	    }
	}
    }

    private List<FPNodeNumeric> createFrequentItemNodeList(
	    Collection<? extends SGSelector> selectorSet,
	    Iterable<DataRecord> instanceIteration, boolean pruneFrequentItems) {
	Map<SGSelector, FPNodeNumeric> frequentNodesMap = new HashMap<SGSelector, FPNodeNumeric>();

	NumericTarget target = (NumericTarget) sdmap.target;
	for (Iterator<DataRecord> iter = instanceIteration.iterator(); iter
		.hasNext();) {
	    DataRecord inst = iter.next();
	    for (SGSelector sel : selectorSet) {
		if (sel.isContainedInInstance(inst)) {
		    FPNodeNumeric node = frequentNodesMap.get(sel);
		    if (node == null) {
			node = new FPNodeNumeric(sel);
			frequentNodesMap.put(sel, node);
		    }
		    node.n += inst.getWeight();
		    double value = target.getValue(inst);
		    node.sum += value * inst.getWeight();
		    if (value > sdmap.populationMean) {
			node.estimate += (value - sdmap.populationMean)
				* inst.getWeight();
		    }
		}
	    }
	    if (sdmap.aborted) {
		break;
	    }
	}
	return pruneAndSortFrequentNodesMapDescending(frequentNodesMap,
		pruneFrequentItems);
    }

    public boolean treeHasSinglePath() {
	FPTreeNodeNumeric node = root;
	while (!node.children.isEmpty()) {
	    if (node.children.size() > 1)
		return false;
	    node = node.children.get(0);
	}
	return true;
    }

    public List<FPNodeNumeric> getFrequentHeaderNodes() {
	return frequentHeaderNodes;
    }

}
