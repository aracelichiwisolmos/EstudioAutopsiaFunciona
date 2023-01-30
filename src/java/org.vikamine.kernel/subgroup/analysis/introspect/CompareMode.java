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
 * Created on 22.01.2004
 *
 */
package org.vikamine.kernel.subgroup.analysis.introspect;

/**
 * {@link CompareMode} considers the handling of unknown values:
 * QUERY_CASE_FILL_UNKNOWN considers the case, for which only unknowns of the
 * query case are taken into account, RETRIEVED_CASE_FILL_UNKNOWN handles
 * unknowns only of the retrieved case, BOTH_FILL_UNKNOWN considers both, while
 * NO_FILL_UNKNOWN ignores (all) unknown values of both cases.
 * 
 * @author atzmueller
 * 
 */
public class CompareMode {

    private Object name = null;

    public CompareMode(Object name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return (String) this.name;
    }

    public static final CompareMode QUERY_CASE_FILL_UNKNOWN = new CompareMode(
	    "QueryCaseFillUnknown");

    public static final CompareMode RETRIEVE_CASE_FILL_UNKNOWN = new CompareMode(
	    "RetrieveCaseFillUnknown");

    public static final CompareMode BOTH_FILL_UNKNOWN = new CompareMode(
	    "BothFillUnknown");

    public static final CompareMode NO_FILL_UNKNOWN = new CompareMode(
	    "NoFillUnknown");
}
