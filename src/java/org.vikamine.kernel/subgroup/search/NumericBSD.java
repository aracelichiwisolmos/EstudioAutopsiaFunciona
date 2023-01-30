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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.data.discretization.DiscretizationUtils;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.SGStatisticsBuilder;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.NumericTarget;

/**
 * @author lemmerich
 * @date Feb 2009
 */
public class NumericBSD extends SDMethod {

    private final String NAME = "Numeric BitSetSD";
    private static final boolean SORT = true;

    private NumericTarget target;
    // public int nodeCounter = 0;
    private double totalPopulationSize;
    private double popMean;

    private Map<SGSelector, BitSet> selectorsBitSet;
    // private BitSet positives;
    List<DataRecord> sortedDataRecords;
    double[] targetValues;
    private BitSet allTrue;

    private KBestSGSet result;
    private SGDescription initialSGDescription;

    private static class SelectorEstimate implements
	    Comparable<SelectorEstimate> {

	private final SGSelector sel;
	private final double estimate;
	private final List<SGSelector> allSelectors;
	private final BitSet bitSet;

	public SelectorEstimate(SGSelector sel, double estimate,
		List<SGSelector> allSelectors, BitSet bitset) {
	    super();
	    this.sel = sel;
	    this.estimate = estimate;
	    this.allSelectors = allSelectors;
	    this.bitSet = bitset;
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
	ArrayList<SGSelector> arrayList = new ArrayList<SGSelector>(
		selectorsBitSet.keySet());
	Collections.sort(arrayList, new Comparator<SGSelector>() {

	    @Override
	    public int compare(SGSelector o1, SGSelector o2) {
		return o1.toString().compareTo(o2.toString());
	    }
	});
	return arrayList;
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	initialize(initialSubgroup);
	if (getMethodStats() != null) {
	    getMethodStats().setSubgroupsComputedCounter(0);
	}

	SGSet result = mineBitSets();

	// System.out.println("NodeCounter:" + nodeCounter);
	// System.out.println("minQuality: " + result.getMinSGQuality());
	return result;
    }

    private KBestSGSet mineBitSets() {
	mineBitSets((BitSet) allTrue.clone(), 0, new Stack<SGSelector>(),
		getAllRelevantSelectors());
	return result;
    }

    private void mineBitSets(BitSet currentBitSet, int depth,
	    List<SGSelector> currentSelectors,
	    List<SGSelector> relevantSelectors) {

	if (isAborted()) {
	    return;
	}

	List<SelectorEstimate> optimisticEstimates = new ArrayList<SelectorEstimate>();
	List<SGSelector> newRelevantSelectors = new ArrayList<SGSelector>();

	for (SGSelector newSelector : relevantSelectors) {
	    BitSet nextBitSet = (BitSet) currentBitSet.clone();
	    nextBitSet.and(selectorsBitSet.get(newSelector));

	    if (getMethodStats() != null) {
		getMethodStats().increaseNodeCounter();
	    }

	    if (task.isSuppressStrictlyIrrelevantSubgroups()
		    && nextBitSet.equals(currentBitSet)) {
		continue;
	    }

	    int n = nextBitSet.cardinality();
	    if (n >= task.getMinSubgroupSize()) {
		double tp = sumUpTargetValues(nextBitSet);

		// construct and maybe add single frequent sgd
		List<SGSelector> newSelectors = new ArrayList<SGSelector>();
		newSelectors.addAll(currentSelectors);
		newSelectors.add(newSelector);

		SG sg = new SG(getPopulation(), target);
		SGStatisticsBuilder.createSGStatisticsNumeric(sg, tp, popMean,
			n, totalPopulationSize);
		sg.updateQuality(task.getQualityFunction());
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    SGDescription newSGD = initialSGDescription.clone();
		    newSGD.addAll(newSelectors);
		    sg.setSGDescription(newSGD);
		    if ((!task.isSuppressStrictlyIrrelevantSubgroups())
			    || (!SGSets.isSGStrictlyIrrelevant(sg, result))) {
			addSubgroupToSGSet(result, sg);
		    }
		}

		// double optEstimatedQuality = task.getQualityFunction()
		// .estimateQuality(sg.getStatistics());
		double optEstimatedQuality = Double.MAX_VALUE;
		if (result.isInKBestQualityRange(optEstimatedQuality)) {
		    SelectorEstimate currentSelEstimate = new SelectorEstimate(
			    newSelector, optEstimatedQuality, newSelectors,
			    nextBitSet);
		    optimisticEstimates.add(currentSelEstimate);
		    newRelevantSelectors.add(newSelector);
		}
	    }
	}
	if (depth + 1 < task.getMaxSGDSize()) {
	    if (SORT) {
		Collections.sort(optimisticEstimates);
	    }
	    for (SelectorEstimate estimate : optimisticEstimates) {
		if (!result.isInKBestQualityRange(estimate.estimate)) {
		    newRelevantSelectors.remove(estimate.sel);
		    continue;
		}

		newRelevantSelectors.remove(estimate.sel);
		List<SGSelector> relevantForThisSelector = new ArrayList<SGSelector>();
		for (SGSelector sel : newRelevantSelectors) {
		    boolean allowsForMultipleSelectorsPerAttribute = (sel instanceof NegatedSGSelector)
			    || (sel instanceof NumericSelector);

		    if (allowsForMultipleSelectorsPerAttribute
			    || sel.getAttribute() != estimate.sel
				    .getAttribute()) {
			relevantForThisSelector.add(sel);
		    }
		}
		if (relevantForThisSelector.isEmpty()) {
		    continue;
		}
		mineBitSets(estimate.bitSet, depth + 1, estimate.allSelectors,
			relevantForThisSelector);
	    }
	}
    }

    private double sumUpTargetValues(BitSet bs) {
	double result = 0;
	for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
	    result += targetValues[i];
	}
	return result;
    }

    private void initializeBitSets(SG initialSubgroup) {
	DataRecordIteration iteration = new DataRecordIteration(
		initialSubgroup.subgroupInstanceIterator());

	sortedDataRecords = DiscretizationUtils.getSortedDataRecords(iteration,
		target.getAttribute(), false, true);
	createAllTrueBitSet();
	createTargetValues();
	selectorsBitSet = new HashMap<SGSelector, BitSet>();

	for (SGSelector sel : getSelectorSet(initialSubgroup, target)) {
	    if (isAborted()) {
		return;
	    }
	    createBitSets(sel, sortedDataRecords, iteration.size());
	}
    }

    private void createTargetValues() {
	targetValues = new double[sortedDataRecords.size()];
	int i = 0;
	for (DataRecord dr : sortedDataRecords) {
	    targetValues[i++] = target.getValue(dr);
	}
    }

    private void createAllTrueBitSet() {
	allTrue = new BitSet(sortedDataRecords.size());
	for (int i = 0; i < sortedDataRecords.size(); i++) {
	    allTrue.set(i);
	}
    }

    private void createBitSets(SGSelector sel, Iterable<DataRecord> iteration,
	    int size) {
	BitSet selectorBitSet = new BitSet(size);
	int i = 0;
	for (DataRecord dr : iteration) {
	    if (sel.isContainedInInstance(dr)) {
		selectorBitSet.set(i);
	    }
	    i++;
	}
	selectorsBitSet.put(sel, selectorBitSet);
    }

    private void initialize(SG initialSubgroup) {
	if (!initialSubgroup.getTarget().isNumeric()) {
	    throw new IllegalArgumentException(
		    "Algorithm not appropriate for boolean targets");
	}
	this.target = (NumericTarget) initialSubgroup.getTarget();
	SGStatisticsNumeric stats = (SGStatisticsNumeric) initialSubgroup
		.getStatistics();

	totalPopulationSize = stats.getDefinedPopulationCount();
	popMean = stats.getPopulationMean();
	// p_0 = definedPositives / totalPopulationSize;
	result = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());
	// result = new KBestSGSetBSBased(getMaxSGCount(),
	// getSGMinQualityLimit());
	initialSGDescription = initialSubgroup.getSGDescription();
	initializeBitSets(initialSubgroup);
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return false;
    }
}
