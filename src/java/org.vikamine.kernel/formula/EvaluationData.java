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
 * Created on 12.04.2004
 */
package org.vikamine.kernel.formula;

import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.SGStatistics;

/**
 * This interface will be given to a formula for evaluation.
 * 
 * @author Tobias Vogele
 */
public class EvaluationData {

    protected SGStatistics statistics;

    protected DataRecord instance;

    protected List instances;

    public DataRecord getInstance() {
	return instance;
    }

    public void setInstance(DataRecord instance) {
	this.instance = instance;
    }

    public EvaluationData() {
	super();
    }

    public EvaluationData(SGStatistics statistics, DataRecord instance) {
	this.statistics = statistics;
	this.instance = instance;
    }

    public SGStatistics getStatistics() {
	return statistics;
    }

    public void setStatistics(SGStatistics statistics) {
	this.statistics = statistics;
    }

    public List getInstances() {
	return instances;
    }

    public void setInstances(List instances) {
	this.instances = instances;
    }
}
