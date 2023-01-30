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
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.search.FPNode.FPTreeNode;
import org.vikamine.kernel.subgroup.search.FPNode.FPTreePath;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;


class FPTree {

    public List<FPNode> frequentHeaderNodes;
    public FPTreeNode root;
    private SDMap sdmap;

    /**
     * Used internally to build a conditional tree
     * 
     * @param frequentHeaderNodes
     * @param root
     * @param sdmap
     */
    private FPTree(List<FPNode> frequentHeaderNodes, FPTreeNode root,
	    SDMap sdmap) {
	this.frequentHeaderNodes = frequentHeaderNodes;
	this.root = root;
	this.sdmap = sdmap;
    }

    public FPTree(Collection<? extends SGSelector> selectorSet,
	    Iterable<DataRecord> instanceIteration, boolean pruneFrequentItems,
	    SDMap sdmap) {
	this.sdmap = sdmap;

	this.frequentHeaderNodes = createFrequentItemNodeList(selectorSet,
		instanceIteration, pruneFrequentItems);
	this.root = new FPTreeNode(null);
	if (!frequentHeaderNodes.isEmpty()) {
	    for (Iterator<DataRecord> iter = instanceIteration.iterator(); iter
		    .hasNext();) {
		DataRecord inst = iter.next();
		insertIntoTree(inst, sdmap.target);
		if (sdmap.aborted) {
		    return;
		}
		// debuggerProxy.pass("buildingFPTree");
	    }
	}
    }

    private StringBuffer printTree(FPTreeNode node, int depth,
	    StringBuffer buffy) {
	for (int i = 0; i < depth; i++) {
	    buffy.append("|");
	}
	if (node.sel != null) {
	    buffy.append(node.toString());
	}
	buffy.append("\n");
	for (Iterator iter = node.children.iterator(); iter.hasNext();) {
	    FPTreeNode child = (FPTreeNode) iter.next();
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
	for (Iterator iter = frequentHeaderNodes.iterator(); iter.hasNext();) {
	    FPNode n = (FPNode) iter.next();
	    attributes.add(n.sel.getAttribute());
	}
	return attributes;
    }

    public FPNode findMostUnfrequentFPNode(Collection<SGSelector> selectors) {
	List<FPNode> freqHeaderNodes = new ArrayList<FPNode>(
		frequentHeaderNodes);
	sortFrequentNodesAscending(freqHeaderNodes);
	for (FPNode n : freqHeaderNodes) {
	    if (selectors.contains(n.sel)) {
		return n;
	    }
	}
	return null;
    }

    static void sortFrequentNodesDescending(List<FPNode> nodes) {
	Collections.sort(nodes, new Comparator<FPNode>() {
	    @Override
	    public int compare(FPNode n1, FPNode n2) {
		if (n1.n == n2.n)
		    return 0;
		else
		    return n1.n < n2.n ? 1 : -1;
	    }
	});
    }

    static void sortFrequentNodesAscending(List<FPNode> nodes) {
	Collections.sort(nodes, new Comparator<FPNode>() {
	    @Override
	    public int compare(FPNode n1, FPNode n2) {
		if (n1.n == n2.n)
		    return 0;
		else
		    return n1.n > n2.n ? 1 : -1;
	    }
	});
    }

    public boolean containsAnyAttribute(Set<Attribute> attributes) {
	for (FPNode node : frequentHeaderNodes) {
	    for (Attribute a : attributes) {
		if (node.sel.getAttribute() == a)
		    return true;
	    }
	}
	return false;
    }

    public FPNode getFPNodeWithSameAttributeAsSelector(SGSelector sel) {
	for (FPNode n : frequentHeaderNodes) {
	    if (n.sel.getAttribute() == sel.getAttribute()) {
		return n;
	    }
	}
	return null;
    }

    public FPNode getFPNodeForSelector(SGSelector sel) {
	for (FPNode n : frequentHeaderNodes) {
	    if (n.sel == sel) {
		return n;
	    }
	}
	return null;
    }

    public FPTree buildConditionalFPTree(FPNode headerNode) {
	List<? extends FPTreePath> paths = headerNode.getAllPrefixPaths();
	FPTree conditionalTree = buildConditionalFPTree(paths);
	return conditionalTree;
    }

    private FPTree buildConditionalFPTree(
	    List<? extends FPTreePath> conditionalPaths) {
	Map<SGSelector, FPNode> frequentNodesMap = collectFrequentNodesMapFromPaths(conditionalPaths);
	List<FPNode> frequentNodes = pruneAndSortFrequentNodesMapDescending(
		frequentNodesMap, true);
	if (frequentNodes.isEmpty())
	    return null;

	FPTree tree = new FPTree(frequentNodes, new FPTreeNode(null), sdmap);
	tree.insertPathsIntoConditionalTree(conditionalPaths, frequentNodes);
	return tree;
    }

    private void insertPathsIntoConditionalTree(List conditionalPaths,
	    List frequentNodes) {
	for (Iterator iter = conditionalPaths.iterator(); iter.hasNext();) {
	    FPTreePath path = (FPTreePath) iter.next();
	    FPTreeNode activeNode = root;
	    for (Iterator iterator = frequentNodes.iterator(); iterator
		    .hasNext();) {
		FPNode aFrequentNode = (FPNode) iterator.next();
		SGSelector sel = aFrequentNode.sel;
		if (path.selectors.remove(sel)) {
		    // tests contains, and
		    // remove in one step
		    FPTreeNode child = activeNode.getChildForSelector(sel);
		    if (child == null) {
			child = new FPTreeNode(sel);
			insertIntoFrequentNodes(aFrequentNode, child);
			activeNode.children.add(child);
			child.parent = activeNode;
		    }
		    child.tp += path.tp;
		    child.n += path.n;
		    activeNode = child;
		}
		if (path.selectors.isEmpty())
		    break;
	    }
	}
    }

    private void insertIntoFrequentNodes(FPNode freqNode, FPTreeNode node) {
	if (freqNode.sel == node.sel) {
	    freqNode.siblings.add(node);
	} else {
	    throw new IllegalStateException("Wrong freqNode for FPTreeNode ...");
	}
    }

    /**
     * does not prune on estimate, if resultSet == null
     */
    private List<FPNode> pruneAndSortFrequentNodesMapDescending(
	    Map<SGSelector, FPNode> frequentNodesMap, boolean pruneFrequentItems) {
	// prune
	if (pruneFrequentItems) {
	    KBestSGSet resultSet = sdmap.result;
	    for (Iterator<FPNode> iter = frequentNodesMap.values().iterator(); iter
		    .hasNext();) {
		FPNode node = iter.next();
		boolean isTargetBoolean = sdmap.target.isBoolean();
		// IQualityFunction qualityFunction = sdmap.task
		// .getQualityFunction();
		if (isTargetBoolean
			&& !sdmap.fullfillsMinSupportBooleanTarget(node.tp, node.n)) {
		    iter.remove();
		} else if (!isTargetBoolean
			&& (node.n < sdmap.task.getMinSubgroupSize())) {
		    iter.remove();
		    // optimistic estimate pruning
		} else {
		    double estimateQuality = sdmap.getOptimisticEstimate(
			    node.n, node.tp, sdmap.totalPopulationSize,
			    sdmap.definedPositives);
		    if ((isTargetBoolean)
			    && (resultSet != null && (!resultSet
				    .isInKBestQualityRange(estimateQuality)))) {
			iter.remove();
		    }
		}
	    }
	}

	// sort
	List<FPNode> result = new ArrayList<FPNode>(frequentNodesMap.values());
	FPTree.sortFrequentNodesDescending(result);
	return result;
    }

    private static Map<SGSelector, FPNode> collectFrequentNodesMapFromPaths(
	    List<? extends FPTreePath> conditionalPaths) {
	// build frequent node list
	Map<SGSelector, FPNode> frequentNodesMap = new HashMap<SGSelector, FPNode>();
	for (Iterator iter = conditionalPaths.iterator(); iter.hasNext();) {
	    FPTreePath path = (FPTreePath) iter.next();
	    for (Iterator iterator = path.selectors.iterator(); iterator
		    .hasNext();) {
		SGSelector sel = (SGSelector) iterator.next();
		FPNode node = frequentNodesMap.get(sel);
		if (node == null) {
		    node = new FPNode(sel);
		    frequentNodesMap.put(sel, node);
		}
		node.tp += path.tp;
		node.n += path.n;
	    }
	}
	return frequentNodesMap;
    }

    private void insertIntoTree(DataRecord inst, SGTarget target) {
	FPTreeNode activeNode = root;
	for (FPNode node : frequentHeaderNodes) {
	    SGSelector sel = node.sel;
	    if (sel.isContainedInInstance(inst)) {
		FPTreeNode child = activeNode.getChildForSelector(sel);
		if (child == null) {
		    child = new FPTreeNode(sel);
		    insertIntoFrequentNodes(node, child);
		    activeNode.children.add(child);
		    child.parent = activeNode;
		}
		child.n += inst.getWeight();
		if (target instanceof BooleanTarget) {
		    if (((BooleanTarget) target).isPositive(inst)) {
			child.tp += inst.getWeight();
		    }
		} else if (target instanceof NumericTarget) {
		    child.tp += ((NumericTarget) target).getValue(inst)
			    * inst.getWeight();
		}
		activeNode = child;
	    }
	}
    }

    private List<FPNode> createFrequentItemNodeList(
	    Collection<? extends SGSelector> selectorSet,
	    Iterable<DataRecord> instanceIteration, boolean pruneFrequentItems) {
	Map<SGSelector, FPNode> frequentNodesMap = new HashMap<SGSelector, FPNode>();

	SGTarget target = sdmap.target;
	for (Iterator<DataRecord> iter = instanceIteration.iterator(); iter
		.hasNext();) {
	    DataRecord inst = iter.next();
	    for (SGSelector sel : selectorSet) {
		if (sel.isContainedInInstance(inst)) {
		    FPNode node = frequentNodesMap.get(sel);
		    if (node == null) {
			node = new FPNode(sel);
			frequentNodesMap.put(sel, node);
		    }
		    node.n += inst.getWeight();
		    if (target instanceof BooleanTarget) {
			if (((BooleanTarget) target).isPositive(inst)) {
			    node.tp += inst.getWeight();
			}
		    } else if (target instanceof NumericTarget) {
			double value = ((NumericTarget) target).getValue(inst);
			node.tp += value * inst.getWeight();
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
	FPTreeNode node = root;
	while (!node.children.isEmpty()) {
	    if (node.children.size() > 1)
		return false;
	    node = node.children.get(0);
	}
	return true;
    }

    public List<FPNode> getFrequentHeaderNodes() {
	return frequentHeaderNodes;
    }

}
