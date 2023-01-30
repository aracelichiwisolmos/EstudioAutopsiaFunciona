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
 * Created on 11.08.2004
 *
 */

package org.vikamine.kernel.subgroup.search;

/**
 * @author atzmueller
 */
public final class SDMethodFactory {

    private SDMethodFactory() {
	super();
    }

    public static SDBeamSearch createSDBeamSearch() {
	return new SDBeamSearch();
    }

    public static SDMethod createSDMap() {
	return new SDMap();
    }

    public static SDMethod createBSD() {
	return new BSD();
    }

    public static final SDMethod createSDMethod(CommonSDMethodType name) {
	switch (name) {
	case SDMAP:
	    return createSDMap();
	case BSD:
	    return createBSD();
	case BEAM_SEARCH:
	    return createSDBeamSearch();
	default:
	    throw new IllegalArgumentException(
		    "Impossible to reach: not a common SD_METHOD");
	}
    }

    public enum CommonSDMethodType {
	BEAM_SEARCH, SDMAP, BSD
    }
}
