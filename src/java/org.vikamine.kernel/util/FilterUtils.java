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

package org.vikamine.kernel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for applying a single or a set of {@link Filter}s.
 * 
 * @author lemmerich
 *
 */

public class FilterUtils {
    public static <T> List<T> applyFilter(Filter<T> fil, Collection<T> source) {
	List<T> result = new ArrayList<T>();
	for (T elem : source) {
	    if (fil.applies(elem)) {
		result.add(elem);
	    }
	}
	return result;
    }

    public static <T> List<T> applyNegativeFilter(Filter<T> fil,
	    Collection<T> source) {
	List result = new ArrayList<T>();
	for (T elem : source) {
	    if (!fil.applies(elem)) {
		result.add(elem);
	    }
	}
	return result;
    }

    public static <T> List<T> applyFilters(List<Filter<T>> filters,
	    Collection<T> source) {
	List result = new ArrayList<T>();
	for (T elem : source) {
	    boolean add = true;
	    for (Filter<T> fil : filters) {
		if (!fil.applies(elem)) {
		    add = false;
		    break;
		}
	    }
	    if (add) {
		result.add(elem);
	    }
	}
	return result;
    }

    public static <T> List<T> applyNegativeFilters(List<Filter<T>> filters,
	    Collection<T> source) {
	List result = new ArrayList<T>();
	for (T elem : source) {
	    boolean add = true;
	    for (Filter<T> fil : filters) {
		if (fil.applies(elem)) {
		    add = false;
		    break;
		}
	    }
	    if (add) {
		result.add(elem);
	    }
	}
	return result;
    }
}
