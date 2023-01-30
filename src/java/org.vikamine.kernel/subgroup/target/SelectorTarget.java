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
 * Created on 10.04.2004
 */
package org.vikamine.kernel.subgroup.target;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * {@link SelectorTarget} implements a {@link BooleanTarget} and captures an
 * {@link Attribute}, {@link Value} combination.
 * 
 * @author Tobias Vogele, atzmueller
 */
public class SelectorTarget implements BooleanTarget {

    protected SGSelector selector;

    // this is for caching:
    private List<Attribute> mAttributeAsList;

    protected SelectorTarget() {
	super();
    }

    public SelectorTarget(SGSelector selector) {
	this.selector = selector;
    }

    public SGSelector getSelector() {
	return selector;
    }

    @Override
    public boolean isPositive(DataRecord instance) {
	return getSelector().isContainedInInstance(instance);
    }

    @Override
    public Collection getAttributes() {
	if (mAttributeAsList == null) {
	    mAttributeAsList = Collections.singletonList(getSelector()
		    .getAttribute());
	}
	return mAttributeAsList;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	} else if (obj == null) {
	    return false;
	} else if (getClass() != obj.getClass()) {
	    return false;
	} else {
	    SelectorTarget t = (SelectorTarget) obj;
	    if (getSelector() == null) {
		return t.getSelector() == null;
	    } else {
		return getSelector().equals(t.getSelector());
	    }
	}
    }

    @Override
    public int hashCode() {
	return getSelector() == null ? 0 : getSelector().hashCode();
    }

    @Override
    public String toString() {
	return "T: " + getSelector().toString();
    }

    @Override
    public String getId() {
	return "SelectorTarget " + selector.getId();
    }

    @Override
    public String getDescription(Describer d) {
	return d.createDescription(selector);
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public boolean isBoolean() {
	return true;
    }

    @Override
    public boolean isNumeric() {
	return false;
    }
}
