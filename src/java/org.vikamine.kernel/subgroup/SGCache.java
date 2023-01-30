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

import java.util.HashMap;
import java.util.Map;

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * An {@link SGDescription} to {@link SG} mapping store.
 * 
 * @author lemmerich
 * @date 10/2010
 */
public class SGCache {

    private SGTarget target;

    private DataView population;

    private final Map<SGDescription, SG> selToSGMap;

    public SGCache(SGTarget target, DataView population) {
	super();
	this.target = target;
	this.population = population;
	this.selToSGMap = new HashMap<SGDescription, SG>();
    }

    public void clear() {
	this.selToSGMap.clear();
    }

    public DataView getPopulation() {
	return population;
    }

    public boolean containsSG(SGDescription descr) {
	return selToSGMap.containsKey(descr);
    }

    public SG getSG(SGDescription description, boolean createStatisticsonDemand) {
	if (population != null && target != null) {
	    SG sg = selToSGMap.get(description);
	    if (sg == null) {
		sg = new SG(population, target, description);
		if (createStatisticsonDemand) {
		    sg.createStatistics();
		}
		cache(description, sg);
	    }
	    return sg;
	}
	return null;
    }

    public void cache(SGDescription sgd, SG sg) {
	selToSGMap.put(sgd, sg);
    }

    public SGTarget getTarget() {
	return target;
    }

    public void setPopulation(DataView population) {
	this.population = population;
	clear();
    }

    public void setTarget(SGTarget target) {
	this.target = target;
	clear();
    }

    public int getSize() {
	return selToSGMap.size();
    }
}
