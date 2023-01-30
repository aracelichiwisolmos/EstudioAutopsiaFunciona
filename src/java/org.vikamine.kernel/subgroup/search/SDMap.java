/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2020 by Martin Atzmueller, Florian Lemmerich, and contributors.
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
 * Created on 04.07.2005
 *
 */

package org.vikamine.kernel.subgroup.search;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.data.FilteringDataRecordIterator;
import org.vikamine.kernel.data.IncludingDataRecordFilter;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsBuilder;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.search.FPNode.FPTreeNode;
import org.vikamine.kernel.subgroup.search.FPNode.FPTreePath;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.FastSelector;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * SDMAP - Subgroup Discovery Method using an Adapted Frequent Pattern Growth
 * Algorithm
 * 
 * @author atzmueller
 * 
 */
public class SDMap extends SDMethod {

    private static class MissingAdjustment {
	public int posUndefined;

	public int negUndefined;

	public static MissingAdjustment EMPTY_MISSING_ADJUSTMENT = new MissingAdjustment();
    }

    private static class SDMapMissingSelector extends DefaultSGSelector {

	private int cachedHashCode = 0;

	public SDMapMissingSelector(Attribute a, Set values) {
	    super(a, values);
	}

	public SDMapMissingSelector(Attribute a, Value v) {
	    super(a, v);
	}

	@Override
	public boolean equals(Object other) {
	    if (other == null)
		return false;
	    if (other.getClass() != this.getClass())
		return false;
	    SDMapMissingSelector miss = (SDMapMissingSelector) other;
	    return this.getAttribute() == miss.getAttribute();
	}

	@Override
	public int hashCode() {
	    if (cachedHashCode == 0) {
		cachedHashCode = super.hashCode();
	    }
	    return cachedHashCode;
	}
    }

    public static final String NAME = "SDMAP";

    private static List<? extends List<SGSelector>> computeCombinations(
	    ArrayList<SGSelector> prefix, ArrayList<SGSelector> selectors,
	    int maxSize, ArrayList<ArrayList<SGSelector>> result,
	    List<SGSelector> conditionedSelectors) {
	result.add(prefix);
	if (prefix.size() == maxSize) {
	    return result;
	}
	ArrayList<SGSelector> newRemainingSelectors = (ArrayList<SGSelector>) selectors
		.clone();
	for (int i = 0; i < selectors.size(); i++) {
	    ArrayList<SGSelector> newSelectors = (ArrayList<SGSelector>) prefix
		    .clone();
	    SGSelector curSel = selectors.get(i);

	    if ((curSel instanceof NegatedSGSelector)
		    || ((!isAttributeContainedInSelectorSet(
			    curSel.getAttribute(), prefix) && (!isAttributeContainedInSelectorSet(
			    curSel.getAttribute(), conditionedSelectors))))) {
		newSelectors.add(curSel);
		newRemainingSelectors.remove(curSel);
		computeCombinations(newSelectors, newRemainingSelectors,
			maxSize, result, conditionedSelectors);
	    }
	}
	return result;
    }

    private static List<? extends List<SGSelector>> computeCombinations(
	    ArrayList<SGSelector> selectors, int maxSize,
	    List<SGSelector> conditionedSelectors) {
	return computeCombinations(new ArrayList<SGSelector>(), selectors,
		maxSize, new ArrayList<ArrayList<SGSelector>>(),
		conditionedSelectors);
    }

    private static boolean isAttributeContainedInSelectorSet(
	    Attribute attribute, List<SGSelector> selectors) {
	for (SGSelector sel : selectors) {
	    if (sel.getAttribute() == attribute) {
		return true;
	    }
	}
	return false;
    }

    private final boolean reorderByOptimisticEstimate = true;

    protected SGTarget target;

    double totalPopulationSize;

    double definedPositives;

    double definedNegatives;

    double populationMean;

    KBestSGSet result;

    public SDMap() {
	super();
    }

    private MissingAdjustment accumulateMissingAdjustment(FPTree missingTree,
	    Collection<SGSelector> missingSelectors) {
	MissingAdjustment f = new MissingAdjustment();

	// Missing(A or B or C) = Missing(A) + Missing(B) + Missing(C) -
	// Missing(AnySubset(A, B, C))
	//
	// accumulate missing counts (union)
	for (SGSelector s : missingSelectors) {
	    FPNode n = missingTree.getFPNodeWithSameAttributeAsSelector(s);
	    if (n != null) {
		f.posUndefined += n.tp;
		f.negUndefined += n.getFP();
	    }
	}

	List missingSelectorsToTestAsStart = new ArrayList(missingSelectors);
	FPNode start = missingTree.findMostUnfrequentFPNode(missingSelectors);
	if (start != null) { // -> there are missings for any missing
	    // selectors!
	    missingSelectorsToTestAsStart.remove(start.sel);
	    subtractPrefixPathsRecursively(missingTree,
		    missingSelectorsToTestAsStart, missingSelectors, start, f);
	}
	return f;
    }

    private MissingAdjustment calculateMissingAdjustment(FPTree missingTree,
	    List refSelectors) {
	if (missingTree.root.children.isEmpty()) {
	    return MissingAdjustment.EMPTY_MISSING_ADJUSTMENT;
	}

	Set attributes = new HashSet();
	for (Iterator iter = refSelectors.iterator(); iter.hasNext();) {
	    SGSelector sel = (SGSelector) iter.next();
	    attributes.add(sel.getAttribute());
	}
	if (!missingTree.containsAnyAttribute(attributes)) {
	    return MissingAdjustment.EMPTY_MISSING_ADJUSTMENT;
	}
	Collection<SGSelector> missingSelectors = createMissingSelectorSet(attributes);
	return accumulateMissingAdjustment(missingTree, missingSelectors);
    }

    protected void convertSGDescription(SG sg) {
	SGDescription sgd = sg.getSGDescription();
	List<SGSelector> selectors = new ArrayList();
	for (Iterator iter = sgd.iterator(); iter.hasNext();) {
	    SGSelector sel = (SGSelector) iter.next();
	    if (sel instanceof FastSelector) {
		selectors.add(new DefaultSGSelector(sel.getAttribute(),
			((FastSelector) sel).getValues()));
	    } else {
		selectors.add(sel);
	    }
	}
	SGDescription converted = new SGDescription();
	converted.addAll(selectors);
	sg.setSGDescription(converted);
    }

    public final void convertSGDescriptions(SGSet result) {
	for (Iterator iter = result.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    convertSGDescription(sg);
	}
    }

    private Collection<SGSelector> createMissingSelectorSet(
	    Collection attributes) {
	Set selectors = new HashSet();
	for (Iterator iter = attributes.iterator(); iter.hasNext();) {
	    Attribute att = (Attribute) iter.next();
	    Boolean b = getOptions() == null ? null : getOptions()
		    .getBooleanAttributeOption(att,
			    Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE);
	    if ((b != null) && (b.booleanValue())) {
		SGNominalSelector sel = new SDMapMissingSelector(att,
			Value.missingValue(att));
		selectors.add(sel);
	    }
	}
	return selectors;
    }

    @Override
    public String getName() {
	return NAME;
    }

    private void growAndTestPatterns(FPTree tree, FPTree missingTree,
	    SGDescription initialSGD) {
	if (getMethodStats() != null) {
	    getMethodStats().increaseNodeCounter();
	}
	if (aborted) {
	    return;
	}
	if (tree.root.children.isEmpty())
	    return;
	// only one path?
	if (tree.treeHasSinglePath()) {
	    testPatternsForSinglePath(tree, missingTree, initialSGD,
		    Collections.EMPTY_LIST);
	} else {
	    // the recursive step ...
	    Map<FPNode, SG> levelOneSubgroups = new HashMap<FPNode, SG>();
	    List<FPNode> frequentNodes = new ArrayList(tree.frequentHeaderNodes);
	    // sorting is redundant since we sort by optimistic estimate anyway
	    // (below)
	    if (!reorderByOptimisticEstimate)
		FPTree.sortFrequentNodesAscending(frequentNodes);
	    for (FPNode headerNode : frequentNodes) {
		SGSelector conditioningSelector = headerNode.sel;

		// construct and maybe add single frequent sgd
		List<SGSelector> newSelectors = new ArrayList();
		newSelectors.add(conditioningSelector);

		SG sg = new SG(getPopulation(), target);
		double tp = headerNode.tp;
		double n = headerNode.n;
		MissingAdjustment f = calculateMissingAdjustment(missingTree,
			newSelectors);
		if (target.isBoolean()) {
		    SGStatisticsBuilder.createSGStatistics(sg, tp, n - tp,
			    definedPositives - f.posUndefined, definedNegatives
				    - f.negUndefined, totalPopulationSize,
			    newSelectors.size(), getOptions());
		} else {
		    SGStatisticsBuilder.createSGStatisticsNumeric(sg, tp,
			    populationMean, n, totalPopulationSize);
		}
		// [TODO]: Actually, here we need to check if the subgroup has been correctly
		// initialized - the update quality below works with all standard quality functions
		// but if there is one that also takes the selectors into account then this will not work.
		sg.updateQuality(task.getQualityFunction());
		levelOneSubgroups.put(headerNode, sg);

		// if quality is good enough, then add sg to result
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    SGDescription newSGD = initialSGD.clone();
		    newSGD.addAll(newSelectors);
		    sg.setSGDescription(newSGD);
		    if ((!task.isSuppressStrictlyIrrelevantSubgroups())
			    || (!SGSets.isSGStrictlyIrrelevant(sg, result))) {
			addSubgroupToSGSet(result, sg);
			// [TODO]: Actually, at this point we also already know,
			// that the optimistic estimate of the subgroup will be
			// above the threshold as well ...
		    }
		}
	    }

	    List<Map.Entry<FPNode, SG>> sortedLevelOneSubgroups = new ArrayList<Map.Entry<FPNode, SG>>(
		    levelOneSubgroups.entrySet());
	    if (reorderByOptimisticEstimate)
		sortByOptimisticEstimateDescending(sortedLevelOneSubgroups);
	    for (Map.Entry<FPNode, SG> entry : sortedLevelOneSubgroups) {
		FPNode headerNode = entry.getKey();
		SG sg = entry.getValue();
		// if optimistic estimate is good enough, consider refinements
		SGStatistics statistics = sg.getStatistics();
		if (target.isNumeric()
			|| result
				.isInKBestQualityRange(getOptimisticEstimate(statistics))) {
		    FPTree conditionalFPTree = tree
			    .buildConditionalFPTree(headerNode);
		    if (conditionalFPTree != null)
			growAndTestPatternsRecursive(
				conditionalFPTree,
				missingTree,
				initialSGD,
				Arrays.asList(new SGSelector[] { headerNode.sel }),
				1);
		}
	    }
	}

    }

    private void growAndTestPatternsRecursive(FPTree tree, FPTree missingTree,
	    SGDescription initialSGdescription,
	    List<SGSelector> conditionedSelectors, int depth) {
	if (aborted) {
	    return;
	}
	if (tree.root.children.isEmpty() || (depth >= task.getMaxSGDSize()))
	    return;
	// only one path?
	if (tree.treeHasSinglePath()) {
	    testPatternsForSinglePath(tree, missingTree, initialSGdescription,
		    conditionedSelectors);
	} else {
	    // the recursive step ...
	    Map<FPNode, SG> refinements = new HashMap<FPNode, SG>();
	    List<FPNode> frequentNodes = new ArrayList(tree.frequentHeaderNodes);
	    if (!reorderByOptimisticEstimate)
		FPTree.sortFrequentNodesAscending(frequentNodes);
	    for (FPNode headerNode : frequentNodes) {
		SGSelector conditioningSelector = headerNode.sel;

		boolean allowsForMultipleSelectorsPerAttribute = (conditioningSelector instanceof NegatedSGSelector)
			|| (conditioningSelector instanceof NumericSelector);

		if (!allowsForMultipleSelectorsPerAttribute
			&& isAttributeContainedInSelectorSet(
				conditioningSelector.getAttribute(),
				conditionedSelectors)) {
		    continue;
		}

		// construct and maybe add single frequent sgd
		List<SGSelector> newSelectors = new ArrayList();
		newSelectors.addAll(conditionedSelectors);
		newSelectors.add(conditioningSelector);

		SG sg = new SG(getPopulation(), target);
		double tp = headerNode.tp;
		double n = headerNode.n;
		MissingAdjustment f = calculateMissingAdjustment(missingTree,
			newSelectors);
		if (target.isBoolean()) {
		    SGStatisticsBuilder.createSGStatistics(sg, tp, n - tp,
			    definedPositives - f.posUndefined, definedNegatives
				    - f.negUndefined, totalPopulationSize,
			    newSelectors.size(), getOptions());
		} else {
		    SGStatisticsBuilder.createSGStatisticsNumeric(sg, tp,
			    populationMean, n, totalPopulationSize);
		}
		sg.updateQuality(task.getQualityFunction());
		refinements.put(headerNode, sg);

		// if quality is good enough, then add sg to result
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    SGDescription newSGD = initialSGdescription.clone();
		    newSGD.addAll(newSelectors);
		    sg.setSGDescription(newSGD);
		    if ((!task.isSuppressStrictlyIrrelevantSubgroups())
			    || (!SGSets.isSGStrictlyIrrelevant(sg, result))) {
			addSubgroupToSGSet(result, sg);
		    }
		}
	    }
	    List<Map.Entry<FPNode, SG>> sortedRefinements = new ArrayList<Map.Entry<FPNode, SG>>(
		    refinements.entrySet());
	    if (reorderByOptimisticEstimate)
		sortByOptimisticEstimateDescending(sortedRefinements);
	    for (Map.Entry<FPNode, SG> entry : sortedRefinements) {
		FPNode headerNode = entry.getKey();
		SG sg = entry.getValue();
		// if optimistic estimate is good enough, consider refinements
		SGStatistics statistics = sg.getStatistics();
		if (target.isNumeric()
			|| result
				.isInKBestQualityRange(getOptimisticEstimate(statistics))) {
		    List<SGSelector> newSelectors = new ArrayList();
		    newSelectors.addAll(conditionedSelectors);
		    newSelectors.add(headerNode.sel);
		    FPTree conditionalFPTree = tree
			    .buildConditionalFPTree(headerNode);
		    if (conditionalFPTree != null)
			growAndTestPatternsRecursive(conditionalFPTree,
				missingTree, initialSGdescription,
				newSelectors, depth + 1);
		}
	    }
	}
    }

    private void initDefaultTargetShareAndPopulationSize(SG initialSubgroup) {
	SGStatistics stats = initialSubgroup.getStatistics();

	if (target instanceof BooleanTarget) {
	    SGStatisticsBinary statsBin = (SGStatisticsBinary) stats;
	    definedPositives = statsBin.getPositives();
	    definedNegatives = statsBin.getNegatives();
	    populationMean = statsBin.getP0();
	} else if (target instanceof NumericTarget) {
	    SGStatisticsNumeric statsNumeric = (SGStatisticsNumeric) stats;
	    definedPositives = statsNumeric.getPopulationMean()
		    * statsNumeric.getDefinedPopulationCount();
	    definedNegatives = statsNumeric.getDefinedPopulationCount()
		    - definedPositives;
	    populationMean = statsNumeric.getPopulationMean();
	}
	totalPopulationSize = stats.getDefinedPopulationCount();
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return true;
    }

    @Override
    protected SGSet search(final SG initialSubgroup) {
	this.target = initialSubgroup.getTarget();
	initDefaultTargetShareAndPopulationSize(initialSubgroup);

	// System.out.println("Building the tree(s) ...");
	// long buildStartTime = System.currentTimeMillis();

	// Note, that we do not need to check whether the target is defined
	// in the instances in the called methods explicitly, since we use
	// the initialSubgroup.subgroupInstanceIterator which ensures just that!
	boolean allowPrune = true;

	result = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());

	FPTree tree = new FPTree(getSelectorSet(initialSubgroup, target),
		new DataRecordIteration(initialSubgroup
			.subgroupInstanceIterator()),
		allowPrune, this);

	DataRecordIteration missingInstancesIteration = new DataRecordIteration(
		new FilteringDataRecordIterator(getPopulation()
			.instanceIterator(), new IncludingDataRecordFilter() {
		    @Override
		    public boolean isIncluded(DataRecord instance) {
			return initialSubgroup.getStatistics()
				.isInstanceDefinedForSubgroupVars(instance);
		    }
		}));
	FPTree missingTree = new FPTree(
		createMissingSelectorSet(tree.extractFrequentAttributes()),
		missingInstancesIteration, false, this);
	// System.out.println(tree);

	// System.out.println("Building took "
	// + String.valueOf(System.currentTimeMillis() - buildStartTime)
	// + " ms.");
	// System.out.println("Frequent Pattern Growth and Test ...");

	SGDescription initialSGDescription = initialSubgroup.getSGDescription();

	// do the real mining task
	growAndTestPatterns(tree, missingTree, initialSGDescription);

	convertSGDescriptions(result);

	// recalculate statistics for targets in case of numericTarget
	// this is necessary because in the algorithm only the basic components
	// were computed
	if (target instanceof NumericTarget) {
	    for (SG sg : result) {
		sg.createStatistics(getOptions());
	    }
	}

	return result;
    }

    protected void sortByOptimisticEstimateDescending(
	    List<Map.Entry<FPNode, SG>> refinements) {
	Collections.sort(refinements, new Comparator<Map.Entry<FPNode, SG>>() {
	    @Override
	    public int compare(Map.Entry<FPNode, SG> e1,
		    Map.Entry<FPNode, SG> e2) {
		double estimate1 = getOptimisticEstimate(e1.getValue()
			.getStatistics());
		double estimate2 = getOptimisticEstimate(e2.getValue()
			.getStatistics());

		return -(Double.compare(estimate1, estimate2));
	    }
	});
    }

    private void subtractPrefixPathsRecursively(FPTree missingTree,
	    Collection missingSelectorsToTestAsStart,
	    Collection missingSelectors, FPNode start, MissingAdjustment f) {
	// subtract double counts
	List<? extends FPTreePath> paths = start.getAllPrefixPaths();
	for (FPTreePath path : paths) {
	    if (path.containsAnySelectors(missingSelectors)) {
		f.posUndefined -= path.tp;
		f.negUndefined -= path.getFP();
	    }
	}

	// and now the recursion ...
	start = missingTree
		.findMostUnfrequentFPNode(missingSelectorsToTestAsStart);
	if (start != null) {
	    missingSelectorsToTestAsStart.remove(start.sel);
	    subtractPrefixPathsRecursively(missingTree,
		    missingSelectorsToTestAsStart, missingSelectors, start, f);
	}
    }

    private void testPatternsForSinglePath(FPTree tree, FPTree missingTree,
	    SGDescription sgd, List conditionedSelectors) {
	ArrayList selectors = new ArrayList();
	FPTreeNode node = tree.root;
	while (!node.children.isEmpty()) {
	    node = node.children.get(0);
	    selectors.add(node.sel);
	}
	List<? extends List<SGSelector>> powerSetOfSelectors = computeCombinations(
		selectors, task.getMaxSGDSize() - conditionedSelectors.size(),
		conditionedSelectors);
	for (List combinationOfSelectors : powerSetOfSelectors) {
	    if (!combinationOfSelectors.isEmpty()) {
		List newSelectors = new ArrayList();
		newSelectors.addAll(conditionedSelectors);
		newSelectors.addAll(combinationOfSelectors);
		if (newSelectors.size() > task.getMaxSGDSize()) {
		    continue;
		}

		SG sg = new SG(getPopulation(), target);
		FPNode mostUnfrequentNodeInCombination = tree
			.findMostUnfrequentFPNode(combinationOfSelectors);
		double tp = mostUnfrequentNodeInCombination.tp;
		double n = mostUnfrequentNodeInCombination.n;
		MissingAdjustment f = calculateMissingAdjustment(missingTree,
			newSelectors);

		if (target.isBoolean()) {
		    SGStatisticsBuilder.createSGStatistics(sg, tp, n - tp,
			    definedPositives - f.posUndefined, definedNegatives
				    - f.negUndefined, totalPopulationSize,
			    newSelectors.size(), getOptions());
		} else {
		    SGStatisticsBuilder.createSGStatisticsNumeric(sg, tp,
			    populationMean, n, totalPopulationSize);
		}
		sg.updateQuality(task.getQualityFunction());
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    SGDescription newSGD = sgd.clone();
		    newSGD.addAll(newSelectors);
		    sg.setSGDescription(newSGD);
		    if ((!task.isSuppressStrictlyIrrelevantSubgroups())
			    || (!SGSets.isSGStrictlyIrrelevant(sg, result))) {
			addSubgroupToSGSet(result, sg);
		    }
		}
	    }
	}
    }
}
