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

package org.vikamine.kernel.subgroup;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * {@link Set} of {@link SG} objects with special bookkeeping, e.g., max and min
 * quality/size etc.
 * 
 * @author atzmueller
 * 
 */
public interface SGSet extends Iterable<SG> {

    /**
     * the subgroup is only added if it is not already contained (Set property)
     * 
     * @param subgroup
     */
    void add(SG subgroup);

    void addAll(Iterable<SG> subgroups);

    double getMaxSGQuality();

    double getMinSGQuality();

    boolean contains(SG otherSG);

    void remove(SG sg);

    void removeAll(Iterable<SG> sgs);

    double getMaxSubgroupSize();

    String getName();

    void setName(String string);

    SGSet sortSubgroupsByQualityDescending();

    /**
     * Returns an iterator, which _does not_ support the remove method.
     * 
     * @return an Iterator
     */
    @Override
    Iterator<SG> iterator();

    int size();

    /**
     * return a NEW List of sgs contained in this set, sorted by quality,
     * 
     * @return
     */
    public List<SG> toSortedList(boolean ascending);

    /**
     * copy sgSet to the given one
     * 
     * @param sgSet
     */
    void initWith(SGSet sgSet);
}
