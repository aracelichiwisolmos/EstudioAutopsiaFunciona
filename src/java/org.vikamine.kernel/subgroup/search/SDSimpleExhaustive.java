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
 * Created on 21.11.2004
 *
 */

package org.vikamine.kernel.subgroup.search;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;


/**
 * @author atzmueller
 * 
 */
public class SDSimpleExhaustive extends SDMethod {

    private boolean useInternalDisjunctions = false;

//    public static List computeSGExhaustiveSpecializations(SG initialSubgroup,
//	    SConstraints constraints) {
//	SDSimpleExhaustive eSearch = new SDSimpleExhaustive();
//	eSearch.setSConstraints(constraints);
//	return eSearch.createSpecializedSubgroups(initialSubgroup);
//    }

    private static final String NAME = "SDSimpleExhaustive";

    public SDSimpleExhaustive() {
	super();
    }

    private List eliminateMeaningLessSelectorsWithAllAttributeValues(List sels) {
	List result = new LinkedList();
	for (Iterator iter = sels.iterator(); iter.hasNext();) {
	    SGNominalSelector sel = (SGNominalSelector) iter.next();
	    if (!sel.isMaybeRedundant()) {
		result.add(sel);
	    }
	}
	return result;
    }

    private boolean containsInternalDisjunctios(List sels) {
	for (Iterator iter = sels.iterator(); iter.hasNext();) {
	    SGNominalSelector sel = (SGNominalSelector) iter.next();
	    if (sel.getValues().size() > 1) {
		return true;
	    }
	}
	return false;
    }

    private List createSpecializedSubgroups(SG initialSubgroup) {
	List powerSetOfAttributeSpace = SGGeneralizer
		.computeAllGeneralizationsOfSelectorSets(selectorsCache);
	System.out.println("No. hypotheses " + powerSetOfAttributeSpace.size());
	List result = new LinkedList();
	for (Iterator iter = powerSetOfAttributeSpace.iterator(); iter
		.hasNext();) {
	    List sels = (List) iter.next();
	    if (useInternalDisjunctions || (!containsInternalDisjunctios(sels))) {
		sels = eliminateMeaningLessSelectorsWithAllAttributeValues(sels);
		SG newSG = (SG) initialSubgroup.clone();
		newSG.getSGDescription().addAll(sels);
		result.add(newSG);
	    }
	}
	return result;
    }

    @Override
    protected SGSet search(SG initialSubgroup) {
	if (task.getAttributes().size() > 15) {
	    throw new IllegalArgumentException(
		    "SDSimpleExhaustive is only applicable for"
			    + " small sets of attributes to search, yet!");
	}

	KBestSGSet kBestSGSet = SGSets.createKBestSGSet(task.getMaxSGCount(),
		task.getMinQualityLimit());
	List subgroups = createSpecializedSubgroups(initialSubgroup);
	for (Iterator iter = subgroups.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    if (!kBestSGSet.contains(sg)) {
		sg.createStatistics(getOptions());
		sg.setQuality(task.getQualityFunction().evaluate(sg));
		if (kBestSGSet.isInKBestQualityRange(sg.getQuality())) {
		    kBestSGSet.addByReplacingWorstSG(sg);
		}
	    }
	}
	return kBestSGSet;
    }

    @Override
    public String getName() {
	return NAME;
    }

    protected boolean isUseInternalDisjunctions() {
	return useInternalDisjunctions;
    }

    protected void setUseInternalDisjunctions(boolean useInternalDisjunctions) {
	this.useInternalDisjunctions = useInternalDisjunctions;
    }

    @Override
    public boolean isTreatMissingAsUndefinedSupported() {
	return true;
    }
}
