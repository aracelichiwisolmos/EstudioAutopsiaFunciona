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
 * Created on 27.01.2006
 *
 */

package org.vikamine.kernel.subgroup.search;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.SingleValue;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;


/**
 * @author atzmueller
 */
public class SDBeamSearchDisjunctive extends SDMethod {

    private static final String NAME = "SDBEAMSearchDisjunctive";

    protected boolean hasImproved = false;

    private List iterationSubgroupResults;

    public SDBeamSearchDisjunctive() {
	super();
    }

    private void expandBeamUsingAttribute(SG sg, Attribute attr,
	    KBestSGSet resultSGSet) {

	for (SGSelector sel : selectorsCache) {
	    if (sel.getAttribute().equals(attr)) {
		SG currentSG = (SG) sg.clone();
		SGDescription currentSGD = currentSG.getSGDescription();
		if (addSGSelector(currentSGD, attr, sel)) {
		    if (!resultSGSet.contains(currentSG)) {
			currentSG.createStatistics(getOptions());
			currentSG.updateQuality(task.getQualityFunction());
			if (doAddSubgroup(resultSGSet, currentSG)) {
			    addSGToSGSet(resultSGSet, currentSG);
			    hasImproved = true;
			}
		    }
		}
	    }
	}
    }

    private Set getAllValues(SGNominalSelector sel) {
	Set result = new HashSet();
	Value val = sel.getValues().iterator().next();
	if (val instanceof SingleValue) {
	    result.add(val);
	}
	return result;
    }

    protected boolean addSGSelector(SGDescription sgd, Attribute attr,
	    SGSelector sel) {
	if (sgd.containsAttributeAsSelector(attr)) {
	    SGNominalSelector contained = (SGNominalSelector) sgd
		    .removeSGSelectorForAttribute(attr);
	    if (sel.equals(contained)) {
		sgd.add(contained);
		return false;
	    } else {
		// TODO: possibly include non-nominal values as well ...
		Set selValues = getAllValues((SGNominalSelector) sel);
		Set containedValues = getAllValues(contained);
		// only allow adding smaller valuesets (selectors), i.e.,
		// only allow the removal of an internal disjunction!
		if (containedValues.size() - selValues.size() == 1) {
		    if (!containedValues.containsAll(selValues)) {
			sgd.add(contained);
			return false;
		    }
		}
	    }
	}
	if (!sgd.contains(sel)) {
	    sgd.add(sel);
	    return true;
	}
	return false;
    }

    private KBestSGSet beam(KBestSGSet currentSGSet, SGTarget target) {
	hasImproved = false;
	KBestSGSet resultSGSet = (KBestSGSet) SGSets.copySGSet(currentSGSet);
	Iterator subgroupIter = currentSGSet.iterator();
	while (subgroupIter.hasNext()) {
	    SG sg = (SG) subgroupIter.next();
	    SGDescription sgd = sg.getSGDescription();

	    if (sgd.size() >= task.getMaxSGDSize()) {
		break;
	    }
	    // find first unused attribute
	    for (Iterator iter = task.getAttributes().iterator(); iter
		    .hasNext();) {
		Attribute attr = (Attribute) iter.next();
		if (isValidAttribute(attr, target)) {
		    expandBeamUsingAttribute(sg, attr, resultSGSet);
		}
	    }
	}
	return resultSGSet;
    }

    private void addSGToSGSet(KBestSGSet resultSGSet, SG currentSG) {
	resultSGSet.addByReplacingWorstSG(currentSG);
	if (task.isSuppressStrictlyIrrelevantSubgroups()) {
	    SGSets.removeIrrelevantSubgroupsFromSGSet(resultSGSet);
	}
    }

    protected boolean doAddSubgroup(KBestSGSet sgSet, SG currentSG) {
	return (sgSet.isInKBestQualityRange(currentSG.getQuality()))
		&& (!sgSet.contains(currentSG))
		&& ((!task.isSuppressStrictlyIrrelevantSubgroups()) || (!SGSets
			.isSGStrictlyIrrelevant(currentSG, sgSet)));
    }

    protected boolean isValidAttribute(Attribute attr, SGTarget target) {
	return (attr.isNominal())
		&& (!target.getAttributes().contains(attr) && ((task
			.getAttributes() == null) || (task.getAttributes()
			.contains(attr))));
    }

    protected List searchInternal(SG initialSubgroup) {
	iterationSubgroupResults = new LinkedList();
	KBestSGSet currentSGSet = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());
	double initQual = task.getQualityFunction().evaluate(initialSubgroup);
	SG clonedInitial = (SG) initialSubgroup.clone();
	clonedInitial.setQuality(initQual);
	currentSGSet.add(clonedInitial);

	int iteration = 1;
	do {
	    hasImproved = false;
	    KBestSGSet resultSGSet = beam(currentSGSet,
		    initialSubgroup.getTarget());
	    if (hasImproved) {
		resultSGSet.setName(getName() + " i" + iteration);
		iterationSubgroupResults.add(resultSGSet);
		currentSGSet = resultSGSet;
		iteration++;
	    }
	} while (hasImproved);

	// construct the "all iterations" subgroup set
	SGSet allIterationResultsSGSet = SGSets.createKBestSGSet(
		KBestSGSet.DEFAULT_MAX_SG_COUNT, task.getMinQualityLimit());
	for (Iterator iter = iterationSubgroupResults.iterator(); iter
		.hasNext();) {
	    SGSet sgSet = (SGSet) iter.next();
	    for (Iterator iterator = sgSet.iterator(); iterator.hasNext();) {
		SG sg = (SG) iterator.next();
		if (!allIterationResultsSGSet.contains(sg)) {
		    allIterationResultsSGSet.add(sg);
		}
	    }
	}
	allIterationResultsSGSet.setName(getName() + " All");
	iterationSubgroupResults.add(0, allIterationResultsSGSet);
	return iterationSubgroupResults;
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	List<SGSet> multipleSets = searchInternal(initialSubgroup);
	return SGSets.mergeSGSetsToKBestSGSet(multipleSets,
		task.getMaxSGCount(), task.getMinQualityLimit());
    }

    /**
     * This method must not be called before perform()!
     * 
     * @return
     */
    public List<SGSet> getIterationResults() {
	if (iterationSubgroupResults == null) {
	    throw new IllegalStateException(
		    "getIterationsResults was called before results were generated!");
	}
	return iterationSubgroupResults;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return true;
    }
}
