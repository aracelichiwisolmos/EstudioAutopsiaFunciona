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
 * Created on 10.07.2003
 *
 */

package org.vikamine.kernel.subgroup.selectors;
import org.vikamine.kernel.data.Attribute;


/**
 * Base class for nominal {@link SGSelector}s, {@link SGNominalSelector}.
 * 
 * @author atzmueller
 */
public abstract class AbstractSGSelector implements Cloneable,
	SGNominalSelector {

    private Attribute attribute;

    public AbstractSGSelector(Attribute attribute) {
	super();
	if (attribute != null) {
	    this.attribute = attribute;
	} else {
	    throw new IllegalArgumentException("Illegal attribute argument - Attribute is null!");
	}
    }

    @Override
    public Attribute getAttribute() {
	return attribute;
    }

    @Override
    public Object clone() {
	try {
	    // create the instance of the right class
	    AbstractSGSelector sel = (AbstractSGSelector) super.clone();
	    return sel;
	} catch (CloneNotSupportedException ex) {
	    // never possible
	    throw new Error("Assertion failure!");
	}
    }
}
