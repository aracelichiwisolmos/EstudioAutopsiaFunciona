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
 * Created on 29.01.2006
 *
 */
package org.vikamine.kernel.subgroup.search;

/**
 * @author atzmueller, lemmerich
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.FastSelector;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * SDMAP - Subgroup Discovery Method using an Adapted Frequent Pattern Growth
 * Algorithm - Disjunctive Adaptation
 * 
 * @author atzmueller
 * 
 */
public class SDMapDisjunctive extends SDMap {

    private boolean excludeUnknown = true;

    private static class NegatedDefinedSGSelector extends NegatedSGSelector {

	public NegatedDefinedSGSelector(SGNominalSelector positive) {
	    super(positive);
	}

	@Override
	public boolean isContainedInInstance(DataRecord instance) {
	    if (instance.isMissing(this.getPositiveSelector().getAttribute()))
		return false;
	    return super.isContainedInInstance(instance);
	}
    }

    private static final String NAME = "SDMapDisjunctive";

    public SDMapDisjunctive() {
	super();
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    protected List<SGSelector> getSelectorSet(SG initialSubgroup,
	    SGTarget target) {
	List selectors = new ArrayList();
	for (SGSelector selSource : selectorsCache) {
	    if (!(selSource instanceof SGNominalSelector)) {
		throw new IllegalArgumentException(
			"SDMap disjunctive only be used with nominal selectors!");
	    }
	    SGNominalSelector sel = (SGNominalSelector) selSource;
	    selectors.add(new NegatedDefinedSGSelector(new FastSelector(sel
		    .getAttribute(), sel.getValues())));
	}
	return selectors;
    }

    private void convertToSGDescriptionWithoutNegation(SG sg) {
	SGDescription sgd = sg.getSGDescription();
	List selectors = new ArrayList();
	Map<Attribute, List> attributesToSelectors = new HashMap<Attribute, List>();
	for (Iterator iter = sgd.iterator(); iter.hasNext();) {
	    SGNominalSelector sel = (SGNominalSelector) iter.next();
	    if (!(sel instanceof NegatedSGSelector))
		selectors.add(new DefaultSGSelector((DefaultSGSelector) sel));
	    else {
		List selectorsForAttribute = attributesToSelectors.get(sel
			.getAttribute());
		if (selectorsForAttribute == null) {
		    selectorsForAttribute = new ArrayList();
		    attributesToSelectors.put(sel.getAttribute(),
			    selectorsForAttribute);
		}
		selectorsForAttribute.add(sel);
	    }
	}
	for (Iterator iter = attributesToSelectors.entrySet().iterator(); iter
		.hasNext();) {
	    Entry<NominalAttribute, List> entry = (Entry<NominalAttribute, List>) iter
		    .next();
	    NominalAttribute a = entry.getKey();
	    List negatedSelectorsForAttribute = entry.getValue();
	    Set realSelectorValues = new HashSet();
	    for (Iterator iterator = a.allValuesIterator(); iterator.hasNext();) {
		Value val = (Value) iterator.next();
		if (isExcludeUnknown()) {
		    if (val.getId().equals(Value.CONST_UNKNOWN))
			continue;
		}

		boolean contained = false;
		for (Iterator iterat = negatedSelectorsForAttribute.iterator(); iterat
			.hasNext();) {
		    NegatedSGSelector sel = (NegatedSGSelector) iterat.next();
		    // TODO: what about non-Nominal selectors
		    if (((SGNominalSelector) sel).getValues().contains(val)) {
			contained = true;
			break;
		    }
		}
		if (!contained) {
		    realSelectorValues.add(val);
		}
	    }
	    if (!realSelectorValues.isEmpty())
		selectors.add(new DefaultSGSelector(a, realSelectorValues));
	    else {
		throw new IllegalStateException("Converted value is missing!");
	    }
	}
	SGDescription converted = new SGDescription();
	converted.addAll(selectors);
	sg.setSGDescription(converted);
    }

    @Override
    protected void convertSGDescription(SG sg) {
	convertToSGDescriptionWithoutNegation(sg);
    }

    @Override
    protected SGSet search(final SG initialSubgroup) {
	return super.search(initialSubgroup);
    }

    public boolean isExcludeUnknown() {
	return excludeUnknown;
    }

    public void setExcludeUnknown(boolean isExcludeUnknown) {
	this.excludeUnknown = isExcludeUnknown;
    }
}
