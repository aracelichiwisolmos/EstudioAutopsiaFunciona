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
 * Last Update: 05/11
 */
package org.vikamine.kernel.subgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.vikamine.kernel.Describable;
import org.vikamine.kernel.Describer;
import org.vikamine.kernel.KernelResources;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.BooleanTargetAdapterSelector;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * Describes a subgroup ({@link SG}) based on a set of {@link SGSelector}s.
 * 
 * @author Atzmueller, becker (comments)
 */
public class SGDescription implements Cloneable, Describable,
	Iterable<SGSelector> {

    private static class NegatedValueWrapper extends Value {

	private Value value;

	public NegatedValueWrapper(Value value) {
	    super("!" + value.getId());
	    this.value = value;
	}

	public Value getValue() {
	    return value;
	}

	@Override
	public Attribute getAttribute() {
	    return value.getAttribute();
	}

	@Override
	public String getDescription() {
	    // return "!" + value.getDescription();
	    return getDescription(new IdentityLongDescriber());
	}

	@Override
	public String getDescription(Describer d) {
	    return "!" + d.createDescription(value);
	    // return "!" + value.getDescription(longDescription);
	}

	@Override
	public String toString() {
	    return "!" + value.toString();
	}

	@Override
	public boolean isMissingValue() {
	    return value.isMissingValue();
	}

	@Override
	public boolean isValueContainedInInstance(DataRecord instance) {
	    return !value.isValueContainedInInstance(instance);
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other)
		return true;
	    else if ((other == null) || (getClass() != other.getClass())) {
		return false;
	    } else {
		return value.equals(((NegatedValueWrapper) other).value);
	    }
	}

	@Override
	public int computeCachedHashCode() {
	    return getValue().hashCode();
	}
    }

    private static class StrictlyConjunctiveSGSelector extends
	    DefaultSGSelector {
	private StrictlyConjunctiveSGSelector(Attribute a, Set<Value> values) {
	    super(a, values);
	}

	@Override
	public boolean isContainedInInstance(DataRecord instance) {
	    for (Iterator iter = getValues().iterator(); iter.hasNext();) {
		Value value = (Value) iter.next();
		if (!value.isValueContainedInInstance(instance)) {
		    return false;
		}
	    }
	    return true;
	}

	@Override
	public String getDescription(Describer d) {
	    if (d == null) {
		return getDescription();
	    }
	    StringBuffer buffy = new StringBuffer();
	    // if (longFormat) {
	    // buffy.append(getAttribute().getDescription());
	    // } else {
	    // buffy.append(getAttribute().getShortDescription());
	    // }
	    buffy.append(d.createDescription(getAttribute()));
	    if ((getValues().isEmpty()) || (getValues().size() > 1)) {
		buffy.append("={");
		List sortedValues = new LinkedList(getValues());
		Collections.sort(sortedValues, new Value.ValueComparator());
		for (Iterator iter = sortedValues.iterator(); iter.hasNext();) {
		    Value element = (Value) iter.next();
		    // buffy.append(element.getDescription(longFormat));
		    buffy.append(d.createDescription(element));
		    if (iter.hasNext()) {
			buffy.append(", ");
		    }
		}
		buffy.append("}");
	    } else {
		// buffy.append("="
		// + ((Value) getValues().toArray()[0])
		// .getDescription(longFormat));
		buffy.append("="
			+ d.createDescription((Value) getValues().toArray()[0]));
	    }
	    return buffy.toString();
	}

	@Override
	public String getDescription() {
	    // return getDescription(true);
	    return getDescription(new IdentityLongDescriber());
	}

	@Override
	public String getId() {
	    StringBuffer buffy = new StringBuffer();
	    buffy.append(getAttribute().getId() + "={");
	    List sortedValues = new LinkedList(getValues());
	    Collections.sort(sortedValues, new Value.ValueComparator());
	    for (Iterator iter = sortedValues.iterator(); iter.hasNext();) {
		Value element = (Value) iter.next();
		buffy.append(element.getId());
		if (iter.hasNext()) {
		    buffy.append(", ");
		}
	    }
	    buffy.append("}");
	    return buffy.toString();
	}

	@Override
	public String toString() {
	    return getId();
	}
    }

    private volatile int cachedHashCode;

    protected LinkedHashMap<Attribute, SGSelector> sgSelectors;

    private List cachedPlainSelectors = null;

    public SGDescription() {
	super();
	sgSelectors = new LinkedHashMap();
	initHashCode();
    }

    public SGDescription(SGSelector... selectors) {
	sgSelectors = new LinkedHashMap();
	addAll(Arrays.asList(selectors));
	initHashCode();
    }

    public SGDescription(Collection<? extends SGSelector> selectors) {
	this();
	addAll(selectors);
    }

    protected SGDescription(int initialCapacity, float loadFactor) {
	super();
	sgSelectors = new LinkedHashMap(initialCapacity, loadFactor);
	initHashCode();
    }

    protected void initHashCode() {
	this.cachedHashCode = 0;
	this.cachedPlainSelectors = null;
    }

    /**
     * @return a <b>shallow</b> copy of the subgroup description
     */
    @Override
    public SGDescription clone() {
	try {
	    SGDescription clonedSGD = (SGDescription) super.clone();
	    clonedSGD.sgSelectors = new LinkedHashMap(sgSelectors);
	    return clonedSGD;
	} catch (CloneNotSupportedException e) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "clone", e);
	    throw new Error("Assertion failure!"); // never possible
	}
    }

    /**
     * @return a <b>deep</b> copy
     */
    public SGDescription deepCopy() {
	SGDescription clonedSGD = clone();
	clonedSGD.sgSelectors = new LinkedHashMap(7);
	for (Iterator iter = iterator(); iter.hasNext();) {
	    SGSelector sgs = (SGSelector) iter.next();
	    clonedSGD.add((SGSelector) sgs.clone());
	}
	return clonedSGD;
    }

    @Override
    public boolean equals(Object other) {
	if (this == other)
	    return true;
	else if ((other == null) || (getClass() != other.getClass())) {
	    return false;
	} else {
	    SGDescription otherSGD = (SGDescription) other;
	    Map otherSGSelectorsMap = otherSGD.sgSelectors;

	    if ((sgSelectors != null) && (otherSGSelectorsMap != null)) {
		return (sgSelectors.equals(otherSGSelectorsMap));
	    } else {
		return super.equals(other);
	    }
	}
    }

    /**
     * @return the hashCode; this method ensures, that if two SGDescriptions are
     *         equal (and have the same set of SGSelectors), then the hashCodes
     *         are the same.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	if (cachedHashCode == 0) {
	    cachedHashCode = sgSelectors.hashCode();
	}
	return cachedHashCode;
    }

    /**
     * @param instanceToTest
     * 
     * @return <b>true</b>, if every {@link SGSelector} of this
     *         {@link SGDescription} is contained in the given instance<br/>
     *         <b>false</b>, else
     */
    public boolean isMatching(DataRecord instanceToTest) {
	if (instanceToTest != null) {
	    for (SGSelector select : sgSelectors.values()) {
		if (!select.isContainedInInstance(instanceToTest)) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }

    public void addAll(Collection<? extends SGSelector> selectors) {
	for (SGSelector sel : selectors) {
	    addInternal(sel);
	}
	initHashCode();
    }

    protected void addInternal(SGSelector sGSelector) {
	if (sGSelector instanceof SGNominalSelector) {
	    if (containsAttributeAsSelector(sGSelector.getAttribute())) {
		SGNominalSelector sel = (SGNominalSelector) sgSelectors
			.get(sGSelector.getAttribute());
		// do not modify the selector destructively ...
		// instead, recreate the conjunctive selector!
		// this should only happen for MC attributes!
		// if (!(sel instanceof StrictlyConjunctiveSGSelector)) {
		if (sel instanceof NegatedSGSelector) {
		    List negatedValues = createNegatedValues(sel);
		    sel = new StrictlyConjunctiveSGSelector(sel.getAttribute(),
			    new HashSet(negatedValues));
		} else if (sel instanceof BooleanTargetAdapterSelector) {
		    throw new UnsupportedOperationException();
		} else {
		    sel = new StrictlyConjunctiveSGSelector(sel.getAttribute(),
			    sel.getValues());
		}
		sgSelectors.put(sGSelector.getAttribute(), sel);
		if (sGSelector instanceof NegatedSGSelector) {
		    List negatedValues = createNegatedValues((SGNominalSelector) sGSelector);
		    sel.addAll(negatedValues);
		} else {
		    sel.addAll(((SGNominalSelector) sGSelector).getValues());
		}
	    } else {
		sgSelectors.put(sGSelector.getAttribute(), sGSelector);
	    }
	    // Numeric Selector
	} else if (sGSelector instanceof NumericSelector) {
	    if (sgSelectors.containsKey(sGSelector.getAttribute())) {
		NumericSelector oldSel = (NumericSelector) sgSelectors
			.get(sGSelector.getAttribute());
		sgSelectors.put(sGSelector.getAttribute(),
			((NumericSelector) sGSelector).join(oldSel));
	    } else {
		sgSelectors.put(sGSelector.getAttribute(), sGSelector);
	    }
	} else {
	    sgSelectors.put(null, sGSelector);
	}
    }

    /**
     * @param sGSelector
     * 
     * @return the negated versions of the given selector's values
     */
    private List createNegatedValues(SGNominalSelector sGSelector) {
	List negatedValues = new LinkedList();
	for (Iterator iter = sGSelector.getValues().iterator(); iter.hasNext();) {
	    Value val = (Value) iter.next();
	    NegatedValueWrapper negVal = new NegatedValueWrapper(val);
	    negatedValues.add(negVal);
	}
	return negatedValues;
    }

    public void add(SGSelector sGSelector) {
	addInternal(sGSelector);
	initHashCode();
    }

    public void remove(SGSelector sGSelector) {
	SGSelector sel = sgSelectors.get(sGSelector.getAttribute());
	if (sel != null && (sel instanceof SGNominalSelector)) {
	    SGNominalSelector nomSel = (SGNominalSelector) sel;
	    if (sel.equals(sGSelector)) {
		sgSelectors.remove(sel.getAttribute());
	    } else {
		if (sGSelector instanceof NegatedSGSelector) {
		    nomSel.removeAll(createNegatedValues((SGNominalSelector) sGSelector));
		} else {
		    nomSel.removeAll(((SGNominalSelector) sGSelector)
			    .getValues());
		}
		if (nomSel.getValues().isEmpty()) {
		    sgSelectors.remove(sel.getAttribute());
		} else if (nomSel.getValues().size() == 1) {
		    sel = new DefaultSGSelector(sel.getAttribute(),
			    nomSel.getValues());
		    sgSelectors.put(sel.getAttribute(), sel);
		}
	    }
	    initHashCode();
	}
    }

    public void removeAll(Collection<SGSelector> selectors) {
	for (SGSelector selector : selectors) {
	    remove(selector);
	}
    }

    public SGSelector removeSGSelectorForAttribute(Attribute attribute) {
	initHashCode();
	return sgSelectors.remove(attribute);
    }

    public boolean containsAttributeAsSelector(Attribute attribute) {
	return sgSelectors.containsKey(attribute);
    }

    @Override
    public String toString() {
	StringBuffer buffy = new StringBuffer();
	buffy.append("S{");
	for (Iterator iter = sgSelectors.values().iterator(); iter.hasNext();) {
	    SGSelector sel = (SGSelector) iter.next();
	    buffy.append(sel.toString());
	    if (iter.hasNext()) {
		buffy.append(", ");
	    }
	}
	buffy.append("}");
	return buffy.toString();
    }

    public boolean contains(SGSelector sel) {
	// return sgSelectors.containsValue(sel);
	if (containsAttributeAsSelector(sel.getAttribute())) {
	    if (sel instanceof SGNominalSelector) {
		SGNominalSelector contained = (SGNominalSelector) sgSelectors
			.get(sel.getAttribute());
		if (!(contained instanceof StrictlyConjunctiveSGSelector)) {
		    return contained.equals(sel);
		} else {
		    if (sel instanceof NegatedSGSelector) {
			List negatedValues = createNegatedValues((SGNominalSelector) sel);
			return contained.getValues().containsAll(negatedValues);
		    } else {
			return contained.getValues().containsAll(
				((SGNominalSelector) sel).getValues());
		    }
		}
		// numeric Selector
	    } else {
		return sgSelectors.get(sel.getAttribute()).equals(sel);
	    }
	} else {
	    return false;
	}
    }

    /**
     * Returns the number of selectors for this description
     * 
     * @return
     */
    public int size() {
	return sgSelectors.size();
    }

    public boolean isEmpty() {
	return sgSelectors.isEmpty();
    }

    /**
     * @return An Iterator over all selectors in the SGDescription. That
     *         implies, that for "internal" conjunctions, i.e., for an attribute
     *         with n conjunctive values, n Selectors are returned.
     */
    @Override
    public Iterator<SGSelector> iterator() {
	if (cachedPlainSelectors == null) {
	    List selectors = new ArrayList();
	    for (Iterator iter = sgSelectors.values().iterator(); iter
		    .hasNext();) {
		SGSelector sel = (SGSelector) iter.next();
		if (sel instanceof StrictlyConjunctiveSGSelector) {
		    collectSingleSelectors(selectors,
			    (StrictlyConjunctiveSGSelector) sel);
		} else {
		    selectors.add(sel);
		}
	    }
	    cachedPlainSelectors = selectors;
	}
	return cachedPlainSelectors.iterator();
    }

    protected void collectSingleSelectors(List selectors, SGNominalSelector sel) {
	for (Iterator iterator = sel.getValues().iterator(); iterator.hasNext();) {
	    Value val = (Value) iterator.next();
	    SGSelector newSel;
	    if (val instanceof NegatedValueWrapper) {
		newSel = new NegatedSGSelector(new DefaultSGSelector(
			sel.getAttribute(),
			((NegatedValueWrapper) val).getValue()));
	    } else {
		newSel = new DefaultSGSelector(sel.getAttribute(), val);
	    }
	    selectors.add(newSel);
	}
    }

    @Override
    public String getId() {
	StringBuffer buffer = new StringBuffer();
	Iterator sgsIter = iterator();
	while (sgsIter.hasNext()) {
	    SGNominalSelector sel = (SGNominalSelector) sgsIter.next();
	    buffer.append(sel.getId());
	    if (sgsIter.hasNext()) {
		buffer.append(" AND ");
	    }
	}
	return buffer.toString();
    }

    @Override
    public String getDescription(Describer d) {
	StringBuffer buffer = new StringBuffer();
	Iterator sgsIter = iterator();
	while (sgsIter.hasNext()) {
	    SGSelector sel = (SGSelector) sgsIter.next();
	    buffer.append(d.createDescription(sel));
	    if (sgsIter.hasNext()) {
		buffer.append(" "
			+ KernelResources.getInstance().getI18N()
				.getString("vkmKernel.generic.SGSelector.AND")
				.trim() + " ");
	    }
	}
	return buffer.toString();
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    public void sortSelectors(Comparator comparator) {
	List copy = new LinkedList(sgSelectors.values());
	Collections.sort(copy, comparator);
	sgSelectors.clear();
	addAll(copy);
    }

    /**
     * A SGDescription includes another, if it contains all selectors from that
     * description, that is, this subgroup is a specialization of the other
     * subgroup. A SGDescription is NOT a specialization of itself!
     * 
     * @param otherSGDescr
     * @return
     */
    public boolean isSpecialization(SGDescription otherSGDescr) {
	if (this.equals(otherSGDescr)) {
	    return false;
	}
	for (Entry<Attribute, SGSelector> entry : otherSGDescr.sgSelectors
		.entrySet()) {
	    Attribute att = entry.getKey();
	    SGSelector sel = entry.getValue();

	    if (!sel.equals(sgSelectors.get(att))) {
		return false;
	    }
	}
	return true;
    }

    public boolean isGeneralization(SGDescription otherSGDescription) {
	return otherSGDescription.isSpecialization(this);
    }

    /**
     * Returns a new List containing all selectors for this description.
     * 
     * @return
     */
    public List<SGSelector> getSelectors() {
	return new ArrayList<SGSelector>(sgSelectors.values());
    }
}