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
import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.quality.constraints.IConstraint;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

public class SimpleDFS extends SDMethod {

    Collection<IConstraint> constraints;
    KBestSGSet result;

    @Override
    public String getName() {
	return "SimpleDFS";
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	result = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());

	searchInternal(initialSubgroup.getSGDescription().getSelectors(),
		getSelectorSet(initialSubgroup, task.getTarget()));
	return result;
    }

    private void searchInternal(List<SGSelector> prefix,
	    List<SGSelector> modificationSet) {
	// external cancel?
	if (isAborted()) {
	    return;
	}

	if (getMethodStats() != null) {
	    getMethodStats().increaseNodeCounter();
	}

	// create new subgroup with statistics
	SGDescription sgDescription = new SGDescription(
		new ArrayList<SGSelector>(prefix));
	SG subgroup = new SG(task.getOntology().getDataView(),
		task.getTarget(), sgDescription);
	subgroup.createStatistics();

	// check size constraints
	if (subgroup.getStatistics().getSubgroupSize() < task
		.getMinSubgroupSize()) {
	    return;
	}
	if (subgroup.getTarget().isBoolean()
		&& ((SGStatisticsBinary) subgroup.getStatistics()).getTp() < task
			.getMinTPSupportAbsolute()) {
	    return;
	}

	// System.out.println(sgDescription);
	double quality = task.getQualityFunction().evaluate(subgroup);

	if (result.isInKBestQualityRange(quality)) {
	    subgroup.createStatistics();
	    subgroup.setQuality(quality);
	    result.addByReplacingWorstSG(subgroup);
	}

	if (prefix.size() < task.getMaxSGDSize()) {
	    Iterator<SGSelector> modifyIter = modificationSet.iterator();
	    while (modifyIter.hasNext()) {
		SGSelector sel = modifyIter.next();
		prefix.add(sel);
		modifyIter.remove();
		searchInternal(prefix, new ArrayList<SGSelector>(
			modificationSet));
		prefix.remove(sel);
	    }
	}
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return false;
    }
}
