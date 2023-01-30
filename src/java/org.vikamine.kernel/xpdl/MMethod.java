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

package org.vikamine.kernel.xpdl;

import java.util.HashMap;
import java.util.Map;

import org.vikamine.kernel.subgroup.search.BSD;
import org.vikamine.kernel.subgroup.search.NumericBSD;
import org.vikamine.kernel.subgroup.search.SDBeamSearch;
import org.vikamine.kernel.subgroup.search.SDMap;

public class MMethod {

    private final Class method;

    private static final Map<String, Class> additionalSDMethods = new HashMap<String, Class>();

    public static final void addSDAlgorithm(String s, Class c) {
	additionalSDMethods.put(s, c);
    }

    public MMethod(String name) {
	name = name.toLowerCase();
	if (name.equals("sd-map") || name.equals("sdmap")) {
	    this.method = SDMap.class;
	} else if (name.equals("beam_search") || name.equals("sdbeamsearch")) {
	    this.method = SDBeamSearch.class;
	} else if (name.equals("bsd") || name.equals("bitsetsd")) {
	    this.method = BSD.class;
	} else if (name.equals("numericbsd")) {
	    this.method = NumericBSD.class;
	} else if (additionalSDMethods.containsKey(name)) {
	    this.method = additionalSDMethods.get(name);
	} else
	    // this.name = EMethodType.NotValid;
	    throw new IllegalArgumentException(
		    "Not a valid common subgroup disovery method: " + name);
    }

    public Class getName() {
	return method;
    }

}
