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

import java.util.ArrayList;

public class MSearchSpace {

    private final ArrayList<MAttribute> att;
    private final int numberOfSelectorsLimit;
    private final boolean useAll;
    private final boolean useAllNominal;
    private int[] metaLimitSelectors;

    public int[] getMetaLimitSelectors() {
	return metaLimitSelectors;
    }

    public void setMetaLimitSelectors(int[] metaLimitSelectors) {
	this.metaLimitSelectors = metaLimitSelectors;
    }

    public MSearchSpace(ArrayList<MAttribute> att, boolean useAll,
	    boolean useAllNominal, int numberOfAttributesLimit,
	    int[] metaLimitSelectors) {
	this.att = att;
	this.useAllNominal = useAllNominal;
	this.useAll = useAll;
	this.numberOfSelectorsLimit = numberOfAttributesLimit;
	this.metaLimitSelectors = metaLimitSelectors;
    }

    public ArrayList<MAttribute> getAtt() {
	return att;
    }

    public int getNumberOfSelectorsLimit() {
	return numberOfSelectorsLimit;
    }

    public boolean isUseAll() {
	return useAll;
    }

    public boolean isUseAllNominal() {
	return useAllNominal;
    }

}
