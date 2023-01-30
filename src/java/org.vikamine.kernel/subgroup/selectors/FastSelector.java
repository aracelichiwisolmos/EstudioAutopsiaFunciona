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

package org.vikamine.kernel.subgroup.selectors;

import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SGDescription;

/**
 * This class is implements a selector to use for time critical operation, i.e.
 * for special subgroup discovery algorithms.
 * 
 * Beware: The equals method only tests on object identity, therefore, the
 * created {@link FastSelector}s need to be converted (e.g., to
 * {@link DefaultSGSelector}s) for later use.
 * 
 * @author atzmueller, lemmerich
 */
public class FastSelector extends DefaultSGSelector {

    private int cachedHashCode = 0;

    public FastSelector(Attribute a, Value v) {
	super(a, v);
    }

    public FastSelector(Attribute a, Set values) {
	super(a, values);
    }

    @Override
    public int hashCode() {
	if (cachedHashCode == 0) {
	    cachedHashCode = super.hashCode();
	}
	return cachedHashCode;
    }

    @Override
    public boolean equals(Object other) {
	return this == other;
    }

    /**
     * Returns a new selector, that is equivalent to this selector, but
     * implements a "real" equals function"
     * 
     * @return
     */
    public DefaultSGSelector toDefaultSGSelector() {
	return new DefaultSGSelector(getAttribute(), getValues());
    }

    public static void convertToDefaultSGSelectors(SGDescription sgd) {
	for (SGSelector sel : sgd.getSelectors()) {
	    if (sel instanceof FastSelector) {
		DefaultSGSelector newSel = ((FastSelector) sel)
			.toDefaultSGSelector();
		sgd.remove(sel);
		sgd.add(newSel);
	    }
	}
    }
}
