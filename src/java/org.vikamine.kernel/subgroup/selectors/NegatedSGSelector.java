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
 * Created on 23.02.2004
 */

package org.vikamine.kernel.subgroup.selectors;

import java.util.Collection;
import java.util.Set;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.KernelResources;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * Implements a negated {@link SGNominalSelector}.
 * 
 * @author Tobias Vogele
 */
public class NegatedSGSelector implements SGNominalSelector, Cloneable {

    private static String NEGATION_STRING = KernelResources.getInstance()
	    .getI18N().getString("vkmKernel.NegatedSGSelector.NegationString");

    private SGNominalSelector positiveSelector;

    public NegatedSGSelector(SGSelector positiveSGSelector) {
	if (positiveSGSelector == null) {
	    throw new NullPointerException("Positive Selector must not be null");
	}
	if (!(positiveSGSelector instanceof SGNominalSelector)) {
	    throw new IllegalArgumentException(
		    "Negated selectors are only implemented for NominalSelectors");
	}
	setPositiveSelector((SGNominalSelector) positiveSGSelector);
    }

    public SGNominalSelector getPositiveSelector() {
	return positiveSelector;
    }

    public void setPositiveSelector(SGNominalSelector positiveSelector) {
	this.positiveSelector = positiveSelector;
    }

    @Override
    public boolean equals(Object object) {
	if (this == object)
	    return true;
	else if ((object == null) || (getClass() != object.getClass())) {
	    return false;
	} else {
	    NegatedSGSelector otherSGS = (NegatedSGSelector) object;
	    return getPositiveSelector().equals(otherSGS.getPositiveSelector());
	}
    }

    @Override
    public Attribute getAttribute() {
	return positiveSelector.getAttribute();
    }

    @Override
    public int hashCode() {
	return positiveSelector.hashCode();
    }

    /**
     * Returns the negated value of the positiveSelector;
     */
    @Override
    public boolean isContainedInInstance(DataRecord instance) {
	return !positiveSelector.isContainedInInstance(instance);
    }

    @Override
    public String toString() {
	return getId();
    }

    @Override
    public String getId() {
	return NEGATION_STRING + positiveSelector.getId();
    }

    @Override
    public String getDescription(Describer d) {
	return NEGATION_STRING + d.createDescription(positiveSelector);
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public boolean isMaybeRedundant() {
	return positiveSelector.isMaybeRedundant();
    }

    @Override
    public Set<Value> getValues() {
	return positiveSelector.getValues();
    }

    @Override
    public boolean addValue(Value val) {
	return positiveSelector.addValue(val);
    }

    @Override
    public boolean addAll(Collection values) {
	return positiveSelector.addAll(values);
    }

    @Override
    public boolean removeValue(Value val) {
	return positiveSelector.removeValue(val);
    }

    @Override
    public boolean removeAll(Collection values) {
	return positiveSelector.removeAll(values);
    }

    @Override
    public Object clone() {
	try {
	    // create the instance of the right class
	    NegatedSGSelector sel = (NegatedSGSelector) super.clone();
	    sel.positiveSelector = (SGNominalSelector) positiveSelector.clone();
	    return sel;
	} catch (CloneNotSupportedException ex) {
	    // never possible
	    throw new Error("Assertion failure!");
	}
    }

    @Override
    public boolean includes(SGSelector sel) {
	throw new UnsupportedOperationException();
    }
}
