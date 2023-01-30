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

package org.vikamine.kernel.data;

import org.vikamine.kernel.Describable;
import org.vikamine.kernel.Describer;

/**
 * Base class, providing ID, description, and cached hashcode (depending on ID)
 * 
 * @author atzmueller
 */
public abstract class DescribableDataObject implements Describable {

    protected boolean cachedHashCodeIsUpToDate;

    private volatile int cachedHashCode;

    final private String id;

    /** The description for this object */
    private String description;

    protected DescribableDataObject(String id) {
	super();
	this.id = id;
    }

    protected int computeCachedHashCode() {
	if (getId() != null) {
	    return getId().hashCode();
	} else {
	    return super.hashCode();
	}
    }

    /**
     * Returns a normal description of the object
     * 
     * @return
     */
    @Override
    public String getDescription() {
	return description;
    }

    /**
     * Returns a description determined by the describer
     * 
     * @param d
     * @return
     */
    @Override
    public String getDescription(Describer dEr) {
	return dEr.createDescription(this);
    }

    @Override
    public String getId() {
	return id;
    }

    @Override
    public final int hashCode() {
	if (!cachedHashCodeIsUpToDate) {
	    cachedHashCode = computeCachedHashCode();
	    cachedHashCodeIsUpToDate = true;
	}
	return cachedHashCode;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}
