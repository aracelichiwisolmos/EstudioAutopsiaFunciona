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
 * Created on 30.07.2004
 *
 */

package org.vikamine.kernel.subgroup.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.VKMUtil;

/**
 * @author atzmueller
 */
public class SGGeneralizer {

    private final SG initialSG;

    public SGGeneralizer(SG sg) {
	super();
	initialSG = sg;
    }

    private static SGNominalSelector copySelector(SGNominalSelector sel,
	    Set values) {
	if (sel instanceof DefaultSGSelector) {
	    return new DefaultSGSelector(sel.getAttribute(), values);
	} else {
	    throw new UnsupportedOperationException("Selector " + sel
		    + " cannot be copied!");
	}
    }

    private static List collectOneValuedSGSelectors(SGDescription sgd) {
	if (!sgd.isEmpty()) {
	    List oneValuedSGSelectors = new LinkedList();
	    for (Iterator iter = sgd.iterator(); iter.hasNext();) {
		SGNominalSelector sel = (SGNominalSelector) iter.next();
		// MissingValueSelectors have zero values!!
		if (sel.getValues().size() > 1) {
		    Collection values = sel.getValues();
		    for (Iterator iterator = values.iterator(); iterator
			    .hasNext();) {
			Value val = (Value) iterator.next();
			oneValuedSGSelectors.add(copySelector(sel,
				Collections.singleton(val)));
		    }
		} else {
		    oneValuedSGSelectors.add(sel);
		}
	    }
	    return oneValuedSGSelectors;
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    private static List convertOneValuedSGSelectorsToSGSelectorsSMCValues(
	    List selectors) {
	if (!selectors.isEmpty()) {
	    Map attributeSelectorsMap = new HashMap();
	    Map<Attribute, List> attributeValuesMap = new HashMap<Attribute, List>();
	    for (Iterator iter = selectors.iterator(); iter.hasNext();) {
		SGNominalSelector sel = (SGNominalSelector) iter.next();
		attributeSelectorsMap.put(sel.getAttribute(), sel);
		List values = attributeValuesMap.get(sel.getAttribute());
		if (values == null) {
		    values = new LinkedList();
		    attributeValuesMap.put(sel.getAttribute(), values);
		}
		values.addAll(sel.getValues());
	    }

	    List result = new LinkedList();
	    for (Entry<Attribute, List> entry : attributeValuesMap.entrySet()) {
		Attribute attr = entry.getKey();
		Set values = new HashSet(entry.getValue());
		SGNominalSelector newSel = copySelector(
			(SGNominalSelector) attributeSelectorsMap.get(attr),
			values);
		result.add(newSel);
	    }
	    return result;
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    public static List computeAllGeneralizationsOfSelectorSets(
	    Collection<? extends SGSelector> baseSelectors) {
	List result = new LinkedList();
	SGDescription baseSelectorsSGD = new SGDescription();
	baseSelectorsSGD.addAll(baseSelectors);
	List oneValuedSGSelectors = collectOneValuedSGSelectors(baseSelectorsSGD);
	List powerSetOfSelectors = VKMUtil
		.computePowerSet(oneValuedSGSelectors);
	for (Iterator iter = powerSetOfSelectors.iterator(); iter.hasNext();) {
	    List oneValuedSelectorList = (List) iter.next();
	    List mcSelectorList = convertOneValuedSGSelectorsToSGSelectorsSMCValues(oneValuedSelectorList);
	    if ((!mcSelectorList.isEmpty())
		    && (!mcSelectorList.equals(baseSelectors))) {
		result.add(mcSelectorList);
	    }
	}
	return result;
    }

    public SGSet computeAllSGGeneralizations(double minSGQualityLimit,
	    IQualityFunction qualityFunction) {
	KBestSGSet result = SGSets.createKBestSGSet(
		KBestSGSet.DEFAULT_MAX_SG_COUNT, minSGQualityLimit);

	List sgSelectors = collectOneValuedSGSelectors(initialSG
		.getSGDescription());
	List powerSetOfSelectors = VKMUtil.computePowerSet(sgSelectors);
	for (Iterator iter = powerSetOfSelectors.iterator(); iter.hasNext();) {
	    List oneValuedSelectorList = (List) iter.next();
	    List selectorList = convertOneValuedSGSelectorsToSGSelectorsSMCValues(oneValuedSelectorList);
	    if ((!selectorList.isEmpty())
		    && (!selectorList.equals(sgSelectors))) {
		SG newSubgroup = (SG) initialSG.clone();
		// setup the new (generalized) SGDescription
		SGDescription newSGDescription = new SGDescription();
		newSGDescription.addAll(selectorList);
		newSubgroup.setSGDescription(newSGDescription);
		newSubgroup.createStatistics(newSubgroup.getStatistics()
			.getOptions());
		newSubgroup.updateQuality(qualityFunction);
		if (result.isInKBestQualityRange(newSubgroup.getQuality()))
		    result.addByReplacingWorstSG(newSubgroup);
	    }
	}
	return result;
    }

    public static void main(String[] args) {
	System.out.println(VKMUtil.computePowerSet(Arrays.asList(new Object[] {
		"a", "b", "c" })));
	System.out.println(VKMUtil.computePowerSet(Arrays.asList(new Object[] {
		"a", "b", "c", "d" })));
    }

}
