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
 * Created on 12.01.2005
 *
 */
package org.vikamine.kernel.subgroup.analysis.strata;

import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.subgroup.SG;

/**
 * Stratification of a subgroup according to a given stratification attribute,
 * i.e., a split of the subgroup (instances) according to the values of this
 * attribute. Holds total and stratified "holders" containing the respective
 * statistics.
 * 
 * @author atzmueller
 */
public class CFSGStratification {

    private NominalAttribute stratificationAttribute;

    private SG baseSG;

    private CFStratifiedSGHolder stratifiedSGHolderForTotalSG;

    private List strataSGHolders;

    public CFSGStratification(NominalAttribute stratificationAttribute, SG sg) {
	super();
	this.stratificationAttribute = stratificationAttribute;
	this.baseSG = sg;
	List temp = CFStratifiedStatisticsCreator.getInstance()
		.createStratifiedSGHolders(sg, stratificationAttribute);
	stratifiedSGHolderForTotalSG = (CFStratifiedSGHolder) temp.get(0);
	strataSGHolders = new LinkedList();
	for (int i = 1; i < temp.size(); i++) {
	    CFStratifiedSGHolder h = (CFStratifiedSGHolder) temp.get(i);
	    strataSGHolders.add(h);
	}
    }

    public SG getBaseSG() {
	return baseSG;
    }

    public List getStrataSGHolders() {
	return strataSGHolders;
    }

    public Attribute getStratificationAttribute() {
	return stratificationAttribute;
    }

    public CFStratifiedSGHolder getStratifiedSGHolderForTotalSG() {
	return stratifiedSGHolderForTotalSG;
    }

}
