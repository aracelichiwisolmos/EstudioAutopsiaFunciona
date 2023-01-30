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
 * Created on 24.03.2004
 *
 */
package org.vikamine.kernel.subgroup.analysis.causality;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.FilteringDataRecordIterator;
import org.vikamine.kernel.data.IncludingDataRecordFilter;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.statistics.ChiSquareStatistics;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.selectors.BooleanTargetAdapterSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.util.VKMUtil;

/**
 * {@link CausalSGAnalyzer} creates a causal subgroup network containing nodes
 * representing the individual subgroups. The net is contructed using the CCU
 * and CCC rules, see Cooper, 1997.
 * 
 * @author atzmueller
 * 
 */
public class CausalSGAnalyzer {

    private Ontology onto;

    public Ontology getOnto() {
	return onto;
    }

    public void setOnto(Ontology onto) {
	this.onto = onto;
    }

    private static class SGPair {
	private final Set pairSet;

	private SGPair(SG i, SG j) {
	    pairSet = new HashSet();
	    pairSet.add(i);
	    pairSet.add(j);
	}

	@Override
	public int hashCode() {
	    return pairSet.hashCode();
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
		SGPair otherPair = (SGPair) other;
		return this.pairSet.equals(otherPair.pairSet);
	    }
	}

	public boolean contains(SG sg) {
	    return pairSet.contains(sg);
	}

	@Override
	public String toString() {
	    return pairSet.toString();
	}
    }

    private static class SGTriple {
	private final Set triple;

	private SGTriple(SG i, SG j, SG k) {
	    triple = new HashSet();
	    triple.add(i);
	    triple.add(j);
	    triple.add(k);
	}

	@Override
	public int hashCode() {
	    return triple.hashCode();
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
		SGTriple otherPair = (SGTriple) other;
		return this.triple.equals(otherPair.triple);
	    }
	}

	// public boolean contains(SG sg) {
	// return triple.contains(sg);
	// }

	@Override
	public String toString() {
	    return triple.toString();
	}
    }

    private static class SGPairSet {

	private final Set pairs;

	public SGPairSet() {
	    super();
	    pairs = new HashSet();
	}

	// public SGPairSet(SGPairSet parent) {
	// super();
	// pairs = new HashSet(parent.pairs);
	// }

	private SGPair createPair(SG i, SG j) {
	    SGPair pair = new SGPair(i, j);
	    return pair;
	}

	public void add(SG i, SG j) {
	    SGPair pair = createPair(i, j);
	    pairs.add(pair);
	}

	// public void remove(SG i, SG j) {
	// SGPair pair = createPair(i, j);
	// pairs.remove(pair);
	// }

	public boolean contains(SG i, SG j) {
	    SGPair pair = createPair(i, j);
	    return pairs.contains(pair);
	}

	// /**
	// * @param eCTemp
	// */
	// public void addAll(SGPairSet eCOther) {
	// pairs.addAll(eCOther.pairs);
	//
	// }
	//
	// public void removeAll(SGPairSet eCOther) {
	// pairs.removeAll(eCOther.pairs);
	//
	// }

    }

    private static class Separators {

	private final HashMap separators;

	private Separators() {
	    super();
	    separators = new HashMap();
	}

	public void addSeparator(SG sep, SG a, SG b) {
	    SGPair pair = new SGPair(a, b);

	    List sepValues = (List) separators.get(sep);
	    if (sepValues == null)
		sepValues = new LinkedList();

	    sepValues.add(pair);
	    separators.put(sep, sepValues);
	}

	public boolean isSeparator(SG sep, SG a, SG b) {
	    List values = (List) separators.get(sep);
	    if (values != null) {
		for (int i = 0; i < values.size(); i++) {
		    SGPair pair = (SGPair) values.get(i);
		    if ((pair.contains(a)) && (pair.contains(b)))
			return true;
		}
	    }
	    return false;
	}

	@Override
	public String toString() {
	    StringBuffer result = new StringBuffer();
	    for (Iterator entryIter = separators.entrySet().iterator(); entryIter
		    .hasNext();) {
		Map.Entry entry = (Map.Entry) entryIter.next();
		SG seperator = (SG) entry.getKey();
		List separatedSGs = (List) entry.getValue();
		result.append("Sep: " + seperator.toString()
			+ "\n Separating:\n");
		for (Iterator iter = separatedSGs.iterator(); iter.hasNext();) {
		    SGPair separatedSGPair = (SGPair) iter.next();
		    result.append("\t" + separatedSGPair);
		}
		result.append("\n");
	    }
	    return result.toString();
	}
    }

    private double approximateEqualsThreshold = 0.8;

    // this is a (IMHO very) conservative threshold for conditional
    // independence testing (value taken from Kloesgen paper ...)
    private final double c1 = 1.0;

    private final SGPairSet excludedCausalitySet = new SGPairSet();

    private final SGPairSet conditionallyExcludedCausalitySet = new SGPairSet();

    private final Separators separators = new Separators();

    private final Collection directlyIncludedSubgroups;

    private final SG targetSG;

    private final DataView population;

    private CausalSGNet causalSGNet = null;

    private final Map approxEqualSubgroups = new HashMap();

    private final SGSet sgSet;

    private final IQualityFunction qf;

    private static boolean trace = true;

    public CausalSGAnalyzer(DataView population, SGSet sgSet,
	    BooleanTarget target, Options options, IQualityFunction qf) {
	super();
	this.population = population;
	this.sgSet = sgSet;
	this.directlyIncludedSubgroups = VKMUtil.asList(sgSet.iterator());
	this.qf = qf;
	targetSG = createTargetSG(target, options);
    }

    private boolean isConditionallyExcluded(SG i, SG j) {
	return conditionallyExcludedCausalitySet.contains(i, j);
    }

    private boolean isDirectlyExcluded(SG i, SG j) {
	return excludedCausalitySet.contains(i, j);
    }

    private boolean isDirectlyOrConditionallyExcluded(SG i, SG j) {
	return isDirectlyExcluded(i, j) || isConditionallyExcluded(i, j);
    }

    private static void trace(String msg) {
	if (trace) {
	    System.out.println(msg);
	}
    }

    private void addApproxEqualSubgroup(SG parent, SG approxEqualSG) {
	List approxEqualSgsForParent = (List) approxEqualSubgroups.get(parent);
	if (approxEqualSgsForParent == null) {
	    approxEqualSgsForParent = new LinkedList();
	    approxEqualSubgroups.put(parent, approxEqualSgsForParent);
	}
	approxEqualSgsForParent.add(approxEqualSG);
    }

    private SG createTargetSG(BooleanTarget target, Options options) {
	SG sg = new SG(population, target);
	SGDescription sgd = new SGDescription();
	sgd.add(new BooleanTargetAdapterSelector(target));
	sg.setSGDescription(sgd);
	sg.createStatistics(options);
	sg.setQuality(qf.evaluate(sg));
	return sg;
    }

    public void refine() {
	applySGCausalityStep0();
	applySGCausalityStep1();
	applySGCausalityStep2();
	applySGCausalityStep3();
	applySGCausalityStep4();
	applyCCURule();
	applyModifiedCCCRule();
	addAllDirectACausalEdges();
	addAllRemainingCCAssociations();
	recalculateNodeLevels();
    }

    private void recalculateNodeLevels() {
	// getCausalSGNet().recalculateLevels();
	getCausalSGNet().recalculateLevelsAllowingCircles();
    }

    private void applySGCausalityStep0() {
	for (Iterator outerIter = directlyIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    if (testIndependent(sgI, targetSG)) {
		excludedCausalitySet.add(sgI, targetSG);
		// outerIter.remove();
	    }
	}
    }

    private void applySGCausalityStep1() {
	List approxEqualSubgroups = new LinkedList();
	for (Iterator outerIter = directlyIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    if (sgI.getTarget().isNumeric()) {
		throw new IllegalArgumentException(
			"Not applicable for subgroups with numeric targets");
	    }
	    if (!approxEqualSubgroups.contains(sgI)) {
		for (Iterator innerIter = directlyIncludedSubgroups.iterator(); innerIter
			.hasNext();) {
		    SG sgJ = (SG) innerIter.next();
		    if (sgJ.getTarget().isNumeric()) {
			throw new IllegalArgumentException(
				"Not applicable for subgroups with numeric targets");
		    }
		    if ((!sgI.equals(sgJ))
			    && (!approxEqualSubgroups.contains(sgJ))) {
			if (isApproximateEqual(sgI, sgJ)) {
			    SG excludedSG = (SGUtils
				    .calculateChi2OfSubgroup((SGStatisticsBinary) sgI
					    .getStatistics()) > SGUtils
				    .calculateChi2OfSubgroup((SGStatisticsBinary) sgJ
					    .getStatistics())) ? sgJ : sgI;
			    addApproxEqualSubgroup(excludedSG.equals(sgI) ? sgJ
				    : sgI, excludedSG);
			    approxEqualSubgroups.add(excludedSG);
			    // exclude all causalities for the excluded group
			    // ...
			    // this is done by adding the subgroup to the approx
			    // equal group
			    // for (Iterator iter = directlyIncludedSubgroups
			    // .iterator(); iter.hasNext();) {
			    // SG sg = (SG) iter.next();
			    // if (sg.equals(excludedSG)) {
			    // excludedCausalitySet.add(sg, excludedSG);
			    // }
			    // }
			    // excludedCausalitySet.add(excludedSG, target);
			} else if (testIndependent(sgI, sgJ)) {
			    excludedCausalitySet.add(sgI, sgJ);
			}
		    }
		}
	    }
	}
	directlyIncludedSubgroups.removeAll(approxEqualSubgroups);
    }

    private void applySGCausalityStep2() {
	SGPairSet inhibitedSymmetricPairs = new SGPairSet();
	for (Iterator outerIter = directlyIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    for (Iterator middleIter = directlyIncludedSubgroups.iterator(); middleIter
		    .hasNext();) {
		SG sgJ = (SG) middleIter.next();
		if (!sgI.equals(sgJ)) {
		    if ((!isDirectlyOrConditionallyExcluded(sgI, targetSG))
			    || ((!isDirectlyOrConditionallyExcluded(sgJ,
				    targetSG)) || (!isDirectlyOrConditionallyExcluded(
				    sgI, sgJ)))
			    && (!inhibitedSymmetricPairs.contains(sgI, sgJ))) {
			if (testCondIndependent(sgI, targetSG, sgJ)) {
			    // test symmetric conditional independency
			    if (testCondIndependent(sgJ, targetSG, sgI)) {
				inhibitedSymmetricPairs.add(sgI, sgJ);
				// take the strongest relation
				double chi2SGI = calculateChiSquareValue(sgI,
					targetSG, targetSG.getPopulation());
				double chi2SGJ = calculateChiSquareValue(sgJ,
					targetSG, targetSG.getPopulation());
				if (chi2SGI < chi2SGJ) {
				    conditionallyExcludedCausalitySet.add(sgI,
					    targetSG);
				    separators.addSeparator(sgJ, sgI, targetSG);
				} else {
				    conditionallyExcludedCausalitySet.add(sgJ,
					    targetSG);
				    separators.addSeparator(sgI, sgJ, targetSG);
				}
			    } else {
				conditionallyExcludedCausalitySet.add(sgI,
					targetSG);
				separators.addSeparator(sgJ, sgI, targetSG);
			    }
			}
		    }
		}
	    }
	}
    }

    private void applySGCausalityStep3() {
	for (Iterator outerIter = directlyIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    for (Iterator innerIter = directlyIncludedSubgroups.iterator(); innerIter
		    .hasNext();) {
		SG sgJ = (SG) innerIter.next();
		if (!sgI.equals(sgJ)) {
		    if ((!isDirectlyOrConditionallyExcluded(sgI, sgJ))
			    || ((!isDirectlyOrConditionallyExcluded(sgI,
				    targetSG)) || (!isDirectlyOrConditionallyExcluded(
				    sgJ, targetSG)))) {
			if (testCondIndependent(sgI, sgJ, targetSG)) {
			    conditionallyExcludedCausalitySet.add(sgI, sgJ);
			    separators.addSeparator(targetSG, sgI, sgJ);
			}
		    }
		}
	    }
	}
    }

    private void applySGCausalityStep4() {
	for (Iterator outerIter = directlyIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    for (Iterator middleIter = directlyIncludedSubgroups.iterator(); middleIter
		    .hasNext();) {
		SG sgJ = (SG) middleIter.next();
		if (!sgI.equals(sgJ)) {
		    for (Iterator innerIter = directlyIncludedSubgroups
			    .iterator(); innerIter.hasNext();) {
			SG sgK = (SG) innerIter.next();
			if ((!sgJ.equals(sgK)) && (!sgI.equals(sgK)))
			    if ((!isDirectlyOrConditionallyExcluded(sgI, sgJ))
				    || ((!isDirectlyOrConditionallyExcluded(
					    sgI, sgK)) || (!isConditionallyExcluded(
					    sgJ, sgK)))) {

				if (testCondIndependent(sgI, sgJ, sgK)) {
				    conditionallyExcludedCausalitySet.add(sgI,
					    sgJ);
				    separators.addSeparator(sgK, sgI, sgJ);
				}
			    }
		    }
		}
	    }
	}
    }

    private void applyCCURule() {
	Collection allIncludedSubgroupsInNet = new LinkedList(
		VKMUtil.asList(sgSet.iterator()));
	Collection allIncludedSubgroups = new LinkedList(
		directlyIncludedSubgroups);
	allIncludedSubgroups.add(targetSG);
	allIncludedSubgroupsInNet.add(targetSG);
	initializeCausalNet(allIncludedSubgroupsInNet);
	Set triplesDone = new HashSet();

	for (Iterator outerIter = allIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    for (Iterator middleIter = allIncludedSubgroups.iterator(); middleIter
		    .hasNext();) {
		SG sgJ = (SG) middleIter.next();
		if ((!sgI.equals(sgJ))
			&& (excludedCausalitySet.contains(sgI, sgJ))) {
		    for (Iterator innerIter = allIncludedSubgroups.iterator(); innerIter
			    .hasNext();) {
			SG sgK = (SG) innerIter.next();
			SGTriple triple = new SGTriple(sgI, sgJ, sgK);
			if ((!triplesDone.contains(triple))
				&& (!sgJ.equals(sgK) && (!sgI.equals(sgK)))
				&& (!isDirectlyOrConditionallyExcluded(sgJ, sgK))
				&& (!isDirectlyOrConditionallyExcluded(sgI, sgK))
				&& (excludedCausalitySet.contains(sgI, sgJ))
				// implies testIndependent(sgI, sgJ)
				&& (!separators.isSeparator(sgK, sgI, sgJ))
			// implies (!testCondIndependent(sgI, sgJ, sgK))
			) {
			    // triplesDone.add(triple);
			    CausalSGNode nodeI = causalSGNet.getNode(sgI);
			    CausalSGNode nodeJ = causalSGNet.getNode(sgJ);
			    CausalSGNode nodeK = causalSGNet.getNode(sgK);

			    // if (!testIndependent(sgI, sgJ)) {
			    // int i = 0;
			    // }
			    // if (testCondIndependent(sgI, sgJ, sgK)) {
			    // int i = 0;
			    // }

			    trace("Applying CCU");
			    if (CausalSGNodeFactory
				    .isACausalSubgroup(sgK, onto)) {
				trace("Introducing acausal association!");
				nodeI.addChild(nodeK);
				nodeJ.addChild(nodeK);
			    } else {
				nodeI.addChild(nodeK);
				nodeJ.addChild(nodeK);
			    }
			    trace("-------------");
			}
		    }
		}
	    }
	}
    }

    /**
     * @param allIncludedSubgroups
     */
    private void initializeCausalNet(Collection allIncludedSubgroups) {
	causalSGNet = new CausalSGNet();
	for (Iterator iter = allIncludedSubgroups.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    CausalSGNode node;
	    if (sg.equals(targetSG)) {
		node = CausalSGNodeFactory.getInstance().createTargetSGNode(sg);
	    } else
		node = CausalSGNodeFactory.getInstance().createCausalSGNode(sg,
			onto);
	    Collection approxEquals = (Collection) approxEqualSubgroups.get(sg);
	    if (approxEquals == null)
		approxEquals = Collections.EMPTY_LIST;
	    node.setApproxEqualSubgroups(new HashSet(approxEquals));
	    causalSGNet.addNode(node);
	}
    }

    private void applyModifiedCCCRule() {
	Collection allIncludedSubgroups = new LinkedList(
		directlyIncludedSubgroups);
	allIncludedSubgroups.add(targetSG);

	Set triplesDone = new HashSet();
	for (Iterator outerIter = allIncludedSubgroups.iterator(); outerIter
		.hasNext();) {
	    SG sgI = (SG) outerIter.next();
	    for (Iterator middleIter = allIncludedSubgroups.iterator(); middleIter
		    .hasNext();) {
		SG sgJ = (SG) middleIter.next();
		if (!sgI.equals(sgJ)) {
		    for (Iterator innerIter = allIncludedSubgroups.iterator(); innerIter
			    .hasNext();) {
			SG sgK = (SG) innerIter.next();
			if ((!sgJ.equals(sgK)) && (!sgI.equals(sgK))) {
			    SGTriple triple = new SGTriple(sgI, sgJ, sgK);
			    if ((!triplesDone.contains(triple))
				    && (!isDirectlyOrConditionallyExcluded(sgI,
					    sgJ))
				    && (!isDirectlyOrConditionallyExcluded(sgJ,
					    sgK))
				    && (!excludedCausalitySet
					    .contains(sgI, sgK))
				    && (separators.isSeparator(sgJ, sgI, sgK))) {
				CausalSGNode nodeI = causalSGNet.getNode(sgI);
				CausalSGNode nodeJ = causalSGNet.getNode(sgJ);
				CausalSGNode nodeK = causalSGNet.getNode(sgK);

				boolean causalRelationAdded = false;
				trace("Trying to apply ModCCC");

				// I -> J -> K
				if (CausalSGNodeFactory.isACausalSubgroup(sgI,
					onto)
					|| ((getCausalSGNet().getNode(sgI)
						.getChildren().contains(nodeJ))
						&& (!nodeJ.getChildren()
							.contains(nodeK)) && (!nodeK
						.getChildren().contains(nodeJ)))) {
				    causalRelationAdded = true;
				    trace("Applying ModCCC");
				    if (!nodeI.getChildren().contains(nodeJ)) {
					nodeI.addChild(nodeJ);
				    }
				    if (CausalSGNodeFactory.isACausalSubgroup(
					    sgK, onto)) {
					trace("Applying ModCCC - Introducing acausal direction:");
				    }
				    nodeJ.addChild(nodeK);
				}

				// I <- J -> K (Alternative 1)
				if (CausalSGNodeFactory.isACausalSubgroup(sgJ,
					onto)
					|| ((getCausalSGNet().getNode(sgJ)
						.getChildren().contains(nodeI))
						&& (!nodeJ.getChildren()
							.contains(nodeK)) && (!nodeK
						.getChildren().contains(nodeJ)))) {
				    causalRelationAdded = true;
				    trace("Applying ModCCC");

				    if (!nodeJ.getChildren().contains(nodeI)) {
					nodeJ.addChild(nodeI);
				    }
				    if (CausalSGNodeFactory.isACausalSubgroup(
					    sgK, onto)) {
					trace("Applying ModCCC - Introducing acausal direction!");
				    }
				    nodeJ.addChild(nodeK);
				}

				// I <- J -> K (Alternative 2)
				if (CausalSGNodeFactory.isACausalSubgroup(sgJ,
					onto)
					|| ((getCausalSGNet().getNode(sgJ)
						.getChildren().contains(nodeK))
						&& (!nodeJ.getChildren()
							.contains(nodeI)) && (!nodeI
						.getChildren().contains(nodeJ)))) {
				    causalRelationAdded = true;
				    trace("Applying ModCCC");

				    if (!nodeJ.getChildren().contains(nodeK)) {
					nodeJ.addChild(nodeK);
				    }
				    if (CausalSGNodeFactory.isACausalSubgroup(
					    sgI, onto)) {
					trace("Applying ModCCC - Introducing acausal direction!");
				    }
				    nodeJ.addChild(nodeI);
				}

				// I <- J <- K
				if (CausalSGNodeFactory.isACausalSubgroup(sgK,
					onto)
					|| ((getCausalSGNet().getNode(sgK)
						.getChildren().contains(nodeJ))
						&& (!nodeJ.getChildren()
							.contains(nodeI)) && (!nodeI
						.getChildren().contains(nodeJ)))) {
				    causalRelationAdded = true;
				    trace("Applying ModCCC");

				    if (!nodeK.getChildren().contains(nodeJ)) {
					nodeK.addChild(nodeJ);
				    }
				    if (CausalSGNodeFactory.isACausalSubgroup(
					    sgJ, onto)) {
					trace("Applying ModCCC - Introducing acausal direction!");
				    }
				    nodeK.addChild(nodeJ);
				}

				// I -- J -- K
				if (!causalRelationAdded) {
				    trace("Applying ModCCC - Introducing bare associations");
				    nodeI.addCCAssociation(nodeJ);
				    nodeJ.addCCAssociation(nodeK);
				}

				trace("-------------");
			    }
			}
		    }
		}
	    }
	}
    }

    private void addAllDirectACausalEdges() {
	Collection allIncludedSubgroups = new LinkedList(
		directlyIncludedSubgroups);
	allIncludedSubgroups.add(targetSG);
	for (Iterator iter = allIncludedSubgroups.iterator(); iter.hasNext();) {
	    SG sgI = (SG) iter.next();
	    if (CausalSGNodeFactory.isACausalSubgroup(sgI, onto)) {
		for (Iterator iterator = allIncludedSubgroups.iterator(); iterator
			.hasNext();) {
		    SG sgJ = (SG) iterator.next();
		    if (!sgI.equals(sgJ)) {
			if (!isDirectlyOrConditionallyExcluded(sgI, sgJ)) {
			    if (!causalSGNet.getNode(sgI).getChildren()
				    .contains(causalSGNet.getNode(sgJ))) {
				trace("Adding direct causal edge!");
				causalSGNet.getNode(sgI).addChild(
					causalSGNet.getNode(sgJ));
			    }
			}
		    }
		}
	    }
	}
    }

    private void addAllRemainingCCAssociations() {
	Collection allIncludedSubgroups = new LinkedList(
		directlyIncludedSubgroups);
	allIncludedSubgroups.add(targetSG);
	for (Iterator outer = allIncludedSubgroups.iterator(); outer.hasNext();) {
	    SG sgI = (SG) outer.next();
	    for (Iterator inner = allIncludedSubgroups.iterator(); inner
		    .hasNext();) {
		SG sgJ = (SG) inner.next();
		if ((!sgI.equals(sgJ))
		// && (isDirectlyExcluded(sgI, sgJ))
			&& (!isConditionallyExcluded(sgI, sgJ))) {
		    CausalSGNode nodeI = causalSGNet.getNode(sgI);
		    CausalSGNode nodeJ = causalSGNet.getNode(sgJ);
		    if ((!nodeI.getChildren().contains(nodeJ))
			    && (!nodeJ.getChildren().contains(nodeI))
			    && (!nodeI.getCCAssociations().contains(nodeJ))
			    && (!nodeJ.getCCAssociations().contains(nodeJ))) {
			trace("Adding non-excluded association");
			nodeI.addCCAssociation(nodeJ);
		    }
		}
	    }
	}
    }

    private boolean isApproximateEqual(SG currentSG, SG otherSG) {
	// approxEqual = |S1 intersect S2|/(|S1|+|S2|-|S1 intersect S2|)
	double currentSGsize = currentSG.getStatistics().getSubgroupSize();
	double otherSGsize = otherSG.getStatistics().getSubgroupSize();
	double intersectSize = calculateIntersectionSize(currentSG, otherSG);
	double value = intersectSize
		/ (currentSGsize + otherSGsize - intersectSize);

	return (value > approximateEqualsThreshold);
    }

    private double calculateConditionedChiSquareValue(SG currentSG, SG otherSG,
	    SG conditionedSG, final boolean isPositiveConditioned) {
	final SGDescription conditionedSGDescription = conditionedSG
		.getSGDescription();
	return calculateChiSquareValueConditionedOnPopulation(currentSG,
		otherSG, conditionedSG.getPopulation().instanceIterator(),
		new IncludingDataRecordFilter() {
		    @Override
		    public boolean isIncluded(DataRecord instance) {
			if (conditionedSGDescription.isMatching(instance)) {
			    return isPositiveConditioned;
			} else {
			    return !isPositiveConditioned;
			}
		    }
		});
    }

    private double calculateChiSquareValue(SG a, SG b) {
	return calculateChiSquareValue(a, b, population);
    }

    public static double calculateChiSquareValue(SG a, SG b,
	    DataView refPopulation) {
	return calculateChiSquareValueConditionedOnPopulation(a, b,
		refPopulation.instanceIterator(),
		new IncludingDataRecordFilter() {
		    @Override
		    public boolean isIncluded(DataRecord instance) {
			return true;
		    }
		});
    }

    private static double calculateChiSquareValueConditionedOnPopulation(SG a,
	    SG b, Iterator<DataRecord> instanceIterator,
	    IncludingDataRecordFilter filter) {
	// Definitions by Kloesgen: S, T;
	// Four-Fold Table: X-Axis = T, Y-Axis = S; a, b, c, d, n!
	int countA = 0; // S -> a + b
	int countB = 0; // T -> a + c
	int countAAndB = 0; // ST -> a
	int countNotAAndNotB = 0; // not S and Not T -> d
	// int countTotal = 0; // N -> n

	for (FilteringDataRecordIterator instIter = new FilteringDataRecordIterator(
		instanceIterator, filter); instIter.hasNext();) {
	    DataRecord inst = instIter.next();
	    if (a.getStatistics().isInstanceDefinedForSubgroupVars(inst)
		    && b.getStatistics().isInstanceDefinedForSubgroupVars(inst)) {
		// countTotal++;
		if (a.getSGDescription().isMatching(inst)) {
		    countA++;
		}
		if (b.getSGDescription().isMatching(inst)) {
		    countB++;
		}
		if (a.getSGDescription().isMatching(inst)
			&& b.getSGDescription().isMatching(inst)) {
		    countAAndB++;
		} else if ((!a.getSGDescription().isMatching(inst))
			&& (!b.getSGDescription().isMatching(inst))) {
		    countNotAAndNotB++;
		}
	    }
	}

	int countAAndNotB = countA - countAAndB;
	int countNotAAndB = countB - countAAndB;
	return ChiSquareStatistics.computeFourFoldChiSqare(countAAndB,
		countNotAAndB, countAAndNotB, countNotAAndNotB);
    }

    private boolean testCondIndependent(SG currentSG, SG otherSG,
	    SG conditionedSG) {
	double chiQuadrat = calculateConditionedChiSquareValue(currentSG,
		otherSG, conditionedSG, true);
	chiQuadrat += calculateConditionedChiSquareValue(currentSG, otherSG,
		conditionedSG, false);

	return (chiQuadrat < 2 * c1);
    }

    public static int calculateIntersectionSize(SG currentSG, SG otherSG) {
	int size = 0;
	for (Iterator<DataRecord> subgroupInstanceIterator = currentSG
		.subgroupInstanceIterator(); subgroupInstanceIterator.hasNext();) {
	    DataRecord inst = subgroupInstanceIterator.next();
	    try {
		if (otherSG.getSGDescription().isMatching(inst))
		    size++;
	    } catch (NullPointerException e) {
		Logger.getLogger(CausalSGAnalyzer.class.getName()).throwing(
			CausalSGAnalyzer.class.getName(),
			"calculateIntersectionSize", e);
	    }
	}
	return size;
    }

    private boolean testIndependent(SG currentSG, SG otherSG) {
	if (calculateChiSquareValue(currentSG, otherSG) < c1)
	    return true;
	return false;
    }

    public double getApproximateEqualsThreshold() {
	return approximateEqualsThreshold;
    }

    public void setApproximateEqualsThreshold(double d) {
	approximateEqualsThreshold = d;
    }

    public CausalSGNet getCausalSGNet() {
	return causalSGNet;
    }

    protected SGSet getSgSet() {
	return sgSet;
    }
}
