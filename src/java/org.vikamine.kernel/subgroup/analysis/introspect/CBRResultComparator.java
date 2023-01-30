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
 * Created on 26.02.2004
 *
 */
package org.vikamine.kernel.subgroup.analysis.introspect;

import java.util.Comparator;

/**
 * {@link CBRResultComparator} implements the comparison {@link Comparator}.
 * 
 * @author atzmueller
 */
public class CBRResultComparator implements Comparator {

    // eager creation, singleton
    private static CBRResultComparator instance = new CBRResultComparator();

    private CBRResultComparator() {
	super();
    }

    public static CBRResultComparator getInstance() {
	if (instance == null) {
	    throw new IllegalStateException(
		    "Instance should have been eagerly created!");
	}
	return instance;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Object arg0, Object arg1) {
	try {
	    CBRResult simRes0 = (CBRResult) arg0;
	    CBRResult simRes1 = (CBRResult) arg1;

	    if (simRes0.getSimilarity() > simRes1.getSimilarity()) {
		return -1;
	    } else if (simRes0.getSimilarity() < simRes1.getSimilarity()) {
		return 1;
	    } else
		return 0;

	} catch (Exception e) {
	    return 0;
	}
    }
}
