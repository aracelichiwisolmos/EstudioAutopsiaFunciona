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
 * Created on 07.11.2003
 * 
 */

package org.vikamine.kernel.subgroup.search;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.selectors.SGSelector;


/**
 * @author Atzmueller
 */
public class SDBeamSearch extends SDMethod {

    private static final String NAME = "SDBEAMSearch";

    protected boolean hasImproved = false;

    private int initialSubgroupDescriptionLength = 0;

    private List<SGSet> iterationSubgroupResults;

    public SDBeamSearch() {
	super();
    }

    private void expandBeamUsingAttribute(SG sg, SGSelector sel,
	    KBestSGSet resultSGSet) {
	SG currentSG = (SG) sg.clone();
	SGDescription currentSGD = currentSG.getSGDescription();
	if (addSGSelector(currentSGD, sel)) {
	    if (!resultSGSet.contains(currentSG)) {
		currentSG.createStatistics(getOptions());
		if (fulfillsMinSupport(currentSG)) {
		    currentSG.updateQuality(task.getQualityFunction());
		    if (doAddSubgroup(resultSGSet, currentSG)) {
			addSGToSGSet(resultSGSet, currentSG);
			hasImproved = true;
		    }
		}
	    }
	}
    }

    protected boolean addSGSelector(SGDescription sgd, SGSelector sel) {
	if (!sgd.contains(sel)) {
	    sgd.add(sel);
	    return true;
	}
	return false;
    }

    private KBestSGSet beam(KBestSGSet currentSGSet) {
	hasImproved = false;
	KBestSGSet resultSGSet = (KBestSGSet) SGSets.copySGSet(currentSGSet);
	Iterator subgroupIter = currentSGSet.iterator();
	while (subgroupIter.hasNext()) {
	    SG sg = (SG) subgroupIter.next();
	    SGDescription sgd = sg.getSGDescription();

	    if (isAborted()) {
		return resultSGSet;
	    }

	    if ((sgd.size() - initialSubgroupDescriptionLength) >= task
		    .getMaxSGDSize()) {
		break;
	    }
	    // use next unused selector
	    Set<SGSelector> unusedSelectors = new HashSet<SGSelector>(
		    task.searchSpace);
	    unusedSelectors.removeAll(sg.getDescription().getSelectors());
	    for (SGSelector nextSelector : unusedSelectors) {
		expandBeamUsingAttribute(sg, nextSelector, resultSGSet);
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

    private boolean fulfillsMinSupport(SG currentSG) {
	if (currentSG.getTarget().isBoolean()) {
	    SGStatisticsBinary statistics = (SGStatisticsBinary) currentSG
		    .getStatistics();
	    return fullfillsMinSupportBooleanTarget(statistics.getTp(),
		    currentSG.getStatistics().getSubgroupSize());
	} else {
	    return fullfillsMinSupportNumericTarget(currentSG.getStatistics()
		    .getSubgroupSize());
	}
    }

    protected boolean doAddSubgroup(KBestSGSet sgSet, SG currentSG) {
	return (sgSet.isInKBestQualityRange(currentSG.getQuality()))
		&& (!sgSet.contains(currentSG))
		&& ((!task.isSuppressStrictlyIrrelevantSubgroups()) || (!SGSets
			.isSGStrictlyIrrelevant(currentSG, sgSet)));
    }

    protected void searchInternal(SG initialSubgroup) {
	iterationSubgroupResults = new ArrayList();
	KBestSGSet currentSGSet = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());
	double initQual = task.getQualityFunction().evaluate(initialSubgroup);
	SG clonedInitial = (SG) initialSubgroup.clone();
	clonedInitial.setQuality(initQual);
	currentSGSet.add(clonedInitial);
	initialSubgroupDescriptionLength = initialSubgroup.getSGDescription()
		.size();

	int iteration = 1;
	do {
	    hasImproved = false;
	    KBestSGSet resultSGSet = beam(currentSGSet);
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
	for (Iterator<SGSet> iter = iterationSubgroupResults.iterator(); iter
		.hasNext();) {
	    SGSet sgSet = iter.next();
	    for (SG sg : sgSet) {
		if (!allIterationResultsSGSet.contains(sg)) {
		    allIterationResultsSGSet.add(sg);
		}
	    }
	}
	allIterationResultsSGSet.setName(getName() + " All");
	iterationSubgroupResults.add(0, allIterationResultsSGSet);
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	searchInternal(initialSubgroup);
	return SGSets.mergeSGSetsToKBestSGSet(iterationSubgroupResults,
		task.getMaxSGCount(), task.getMinQualityLimit());
    }

    /**
     * Must not be called before perform() method!
     * 
     * @return
     */
    public List<SGSet> getIterationResults() {
	if (iterationSubgroupResults == null) {
	    throw new IllegalStateException(
		    "Must perform subgroup search, before using Iteration results");
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
