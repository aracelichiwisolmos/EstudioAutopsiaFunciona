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
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.KBestSGSetBitSetBased;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsBuilder;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.util.BitSetUtils;

/**
 * @author lemmerich
 * @date Oct 2009
 */
public class BSD extends SDMethod {

    public static final String NAME = "BitSetSD";
    private static final boolean SORT = true;
    // private int minTpPruneCount = 0;

    private BooleanTarget target;
    public int nodeCounter = 0;
    private double totalPopulationSize;
    // private double p_0;
    private double definedPositives;
    private double definedNegatives;

    private Map<SGSelector, BitSet> selectorsNegatives;
    private Map<SGSelector, BitSet> selectorsPositives;
    private BitSet allTruePos;
    private BitSet allTrueNeg;

    private KBestSGSetBitSetBased result;
    // private KBestSGSet result2;
    private SGDescription initialSGDescription;

    private static class SelectorEstimate implements
	    Comparable<SelectorEstimate> {

	private final SGSelector sel;
	private final double estimate;
	private final List<SGSelector> allSelectors;
	private final BitSet bitSetPos;
	private final BitSet bitSetNeg;

	public SelectorEstimate(SGSelector sel, double estimate,
		List<SGSelector> allSelectors, BitSet bitsetPos,
		BitSet bitsetNeg) {
	    super();
	    this.sel = sel;
	    this.estimate = estimate;
	    this.allSelectors = allSelectors;
	    this.bitSetPos = bitsetPos;
	    this.bitSetNeg = bitsetNeg;
	}

	@Override
	public int compareTo(SelectorEstimate o) {
	    return -Double.compare(this.estimate, o.estimate);
	}

	@Override
	public String toString() {
	    return sel + ": " + estimate;
	}
    }

    @Override
    public String getName() {
	return NAME;
    }

    private List<SGSelector> getAllRelevantSelectors() {
	return new ArrayList<SGSelector>(selectorsPositives.keySet());
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	if (initialSubgroup.getTarget().isNumeric()) {
	    throw new InvalidTargetException(initialSubgroup.getTarget(), "");
	}
	initialize(initialSubgroup);
	nodeCounter = 0;

	mineBitSets();

	// System.out.println("mintpPrunCount: " + minTpPruneCount);
	// System.out.println("qualitySum: " + result.getQualitySum());
	return result;
    }

    private void mineBitSets() {
	mineBitSets((BitSet) allTruePos.clone(), (BitSet) allTrueNeg.clone(),
		0, new Stack<SGSelector>(), getAllRelevantSelectors());
    }

    private void mineBitSets(BitSet currentBitSetPos, BitSet currentBitSetNeg,
	    int depth, List<SGSelector> currentSelectors,
	    List<SGSelector> relevantSelectors) {

	List<SelectorEstimate> optimisticEstimates = new ArrayList<SelectorEstimate>();
	List<SGSelector> newRelevantSelectors = new ArrayList<SGSelector>();

	for (SGSelector newSelector : relevantSelectors) {
	    if (aborted) {
		return;
	    }
	    BitSet nextBitSetPos = (BitSet) currentBitSetPos.clone();
	    nextBitSetPos.and(selectorsPositives.get(newSelector));
	    int tp = nextBitSetPos.cardinality();

	    // assume all n are positive, can we fulfill minSupport?
	    if (!task.fulfillsMinTPSupport(tp)) {
		continue;
	    }

	    if (task.isSuppressStrictlyIrrelevantSubgroups()
		    && nextBitSetPos.equals(currentBitSetPos)) {
		continue;
	    }

	    BitSet nextBitSetNeg = (BitSet) currentBitSetNeg.clone();
	    nextBitSetNeg.and(selectorsNegatives.get(newSelector));
	    int fp = nextBitSetNeg.cardinality();
	    int n = tp + fp;

	    // construct and maybe add single frequent sgd
	    List<SGSelector> newSelectors = new ArrayList<SGSelector>();
	    newSelectors.addAll(currentSelectors);
	    newSelectors.add(newSelector);

	    if (fullfillsMinSupportBooleanTarget(tp, n)) {
		nodeCounter++;
		SG sg = new SG(getPopulation(), target);
		SGStatisticsBuilder.createSGStatistics(sg, tp, n - tp,
			definedPositives, definedNegatives,
			totalPopulationSize, initialSGDescription.size()
				+ newSelectors.size(), getOptions());
		sg.updateQuality(task.getQualityFunction());
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    SGDescription newSGD = initialSGDescription
			    .clone();
		    newSGD.addAll(newSelectors);
		    sg.setSGDescription(newSGD);

		    if ((!task.isSuppressStrictlyIrrelevantSubgroups())
			    || (!result.isSGStrictlyIrrelevant(nextBitSetNeg,
				    nextBitSetPos))) {
			addSubgroupToSGSet(result, sg, nextBitSetNeg,
				nextBitSetPos);
		    }
		}

		double optEstimatedQuality = getOptimisticEstimate(n, tp,
			totalPopulationSize, definedPositives);
		if (result.isInKBestQualityRange(optEstimatedQuality)) {
		    // if (result.isInKBestQualityRange(optEstimatedQuality)) {
		    SelectorEstimate currentSelEstimate = new SelectorEstimate(
			    newSelector, optEstimatedQuality, newSelectors,
			    nextBitSetPos, nextBitSetNeg);
		    optimisticEstimates.add(currentSelEstimate);
		    newRelevantSelectors.add(newSelector);
		}
	    }
	}
	if (depth + 1 < task.getMaxSGDSize()) {
	    if (aborted) {
		return;
	    }
	    if (SORT) {
		Collections.sort(optimisticEstimates);
	    }
	    for (SelectorEstimate estimate : optimisticEstimates) {
		if (!result.isInKBestQualityRange(estimate.estimate)) {
		    continue;
		}
		// if (!result.isInKBestQualityRange(estimate.estimate)) {
		// continue;
		// }

		newRelevantSelectors.remove(estimate.sel);
		List<SGSelector> relevantForThisSelector = new ArrayList<SGSelector>();
		for (SGSelector sel : newRelevantSelectors) {
		    if (sel.getAttribute() != estimate.sel.getAttribute()) {
			relevantForThisSelector.add(sel);
		    }
		}
		if (relevantForThisSelector.isEmpty()) {
		    continue;
		}
		mineBitSets(estimate.bitSetPos, estimate.bitSetNeg, depth + 1,
			estimate.allSelectors, relevantForThisSelector);
	    }
	}
    }

    private void initializeBitSets(SG initialSubgroup) {
	List<DataRecord> positives = new ArrayList<DataRecord>();
	List<DataRecord> negatives = new ArrayList<DataRecord>();

	DataRecordIteration iteration = new DataRecordIteration(
		initialSubgroup.subgroupInstanceIterator());
	for (DataRecord dr : iteration) {

	    if (target.isPositive(dr)) {
		positives.add(dr);
	    } else {
		negatives.add(dr);
	    }
	}

	selectorsPositives = new HashMap<SGSelector, BitSet>();
	selectorsNegatives = new HashMap<SGSelector, BitSet>();

	for (SGSelector fSel : getSelectorSet(initialSubgroup, target)) {
	    if (aborted) {
		return;
	    }
	    createBitSets(fSel, positives, negatives);
	}
	createAllTrueBitSets(positives, negatives);
    }

    private void createAllTrueBitSets(List<DataRecord> iterationPos,
	    List<DataRecord> iterationNeg) {
	allTruePos = new BitSet(iterationPos.size());
	for (int i = 0; i < iterationPos.size(); i++) {
	    allTruePos.set(i);
	}
	allTrueNeg = new BitSet(iterationNeg.size());
	for (int i = 0; i < iterationNeg.size(); i++) {
	    allTrueNeg.set(i);
	}
    }

    private void createBitSets(SGSelector lws, List<DataRecord> positives,
	    List<DataRecord> negatives) {
	BitSet posBitSet = BitSetUtils.generateBitSet(lws, positives);

	// tp pruning
	int posCardinality = posBitSet.cardinality();
	if (task.fulfillsMinTPSupport(posCardinality)) {
	    BitSet negBitSet = BitSetUtils.generateBitSet(lws, negatives);
	    int negCardinality = negBitSet.cardinality();
	    if ((negCardinality + posCardinality) > task.getMinSubgroupSize()) {
		selectorsPositives.put(lws, posBitSet);
		selectorsNegatives.put(lws, negBitSet);
	    }
	}
    }

    private void initialize(SG initialSubgroup) {
	this.target = (BooleanTarget) initialSubgroup.getTarget();
	SGStatisticsBinary stats = (SGStatisticsBinary) initialSubgroup
		.getStatistics();
	definedNegatives = stats.getNegatives();
	definedPositives = stats.getPositives();
	totalPopulationSize = stats.getDefinedPopulationCount();
	// p_0 = definedPositives / totalPopulationSize;
	result = new KBestSGSetBitSetBased(getTask().getMaxSGCount(),
		task.getMinQualityLimit());
	initialSGDescription = initialSubgroup.getSGDescription();

	initializeBitSets(initialSubgroup);
    }

    protected boolean doAddSGToSGSet(KBestSGSet sgSet, SG sg,
	    BitSet nextBitSetAll, BitSet nextBitSetPositives) {
	return sgSet.isInKBestQualityRange(sg.getQuality())
		&& ((!task.isSuppressStrictlyIrrelevantSubgroups()) || (!result
			.isSGStrictlyIrrelevant(nextBitSetAll,
				nextBitSetPositives)));
    }

    protected void addSubgroupToSGSet(KBestSGSetBitSetBased result, SG sg,
	    BitSet nextBitSetNegatives, BitSet nextBitSetPositives) {
	result.addByReplacingWorstSG(sg, nextBitSetNegatives,
		nextBitSetPositives);
	if (task.isSuppressStrictlyIrrelevantSubgroups()) {
	    result.testAndRemoveIrrelevantSubgroupsFromSGSet();
	}
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return false;
    }
}
