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
 * Created on 17.11.2004
 *
 */
package org.vikamine.kernel.subgroup;

import java.util.Set;

/**
 * A {@link Set} containing the k best subgroup patterns.
 * 
 * @author atzmueller
 * 
 */
public interface KBestSGSet extends SGSet {

    static final double DEFAULT_SG_MIN_QUALITY_LIMIT = Double.NEGATIVE_INFINITY;

    static final int DEFAULT_MAX_SG_COUNT = Integer.MAX_VALUE;

    boolean isInKBestQualityRange(double quality);

    /**
     * the subgroup is only added if it is not already contained (Set property)
     * 
     * @param betterSG
     */
    void addByReplacingWorstSG(SG betterSG);

    double getSGMinQualityLimit();

    int getMaxSGCount();

    double getQualitySum();

    public String toComprehensiveString();
}
