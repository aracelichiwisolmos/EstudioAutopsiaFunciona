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
 * Created on 27.03.2007
 *
 */

package org.vikamine.kernel.subgroup.selectors;

import java.util.Collection;
import java.util.Set;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * {@link SGSelector} for boolean {@link SGTarget}s.
 * 
 * @author atzmueller
 */
public class BooleanTargetAdapterSelector implements SGNominalSelector,
	Cloneable {

    private final BooleanTarget target;

    public BooleanTargetAdapterSelector(BooleanTarget target) {
	super();
	this.target = target;
    }

    @Override
    public Object clone() {
	try {
	    // create the instance of the right class
	    SGNominalSelector sel = (SGNominalSelector) super.clone();
	    return sel;
	} catch (CloneNotSupportedException ex) {
	    // never possible
	    throw new Error("Assertion failure!");
	}
    }

    @Override
    public boolean addAll(Collection values) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean addValue(Value val) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Attribute getAttribute() {
	if (target instanceof SelectorTarget) {
	    return ((SelectorTarget) target).getSelector().getAttribute();
	} else {
	    return (Attribute) target.getAttributes().toArray()[0];
	}
    }

    public BooleanTarget getBooleanTarget() {
	return target;
    }

    @Override
    public Set<Value> getValues() {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean isContainedInInstance(DataRecord instance) {
	return target.isPositive(instance);
    }

    @Override
    public boolean isMaybeRedundant() {
	return false;
    }

    @Override
    public boolean removeAll(Collection values) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeValue(Value val) {
	throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public String getDescription(Describer d) {
	return d.createDescription(target);
    }

    @Override
    public String getId() {
	return target.getId();
    }

    @Override
    public boolean includes(SGSelector sel) {
	throw new UnsupportedOperationException();
    }
}
