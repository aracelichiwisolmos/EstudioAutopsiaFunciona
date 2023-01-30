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

package org.vikamine.kernel.subgroup.quality.constraints;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.ISubgroup;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatistics;


public class ConstraintMinSubgroupPositives extends AbstractConstraintStatisticBased {
    public double getMinSubgroupPositives() {
	return minSubgroupPositives;
    }

    public void setMinSubgroupPositives(double minSubgroupPositives) {
	this.minSubgroupPositives = minSubgroupPositives;
    }

    double minSubgroupPositives;

    public ConstraintMinSubgroupPositives(double minSubgroupPositives) {
	super();
	this.minSubgroupPositives = minSubgroupPositives;
    }

    @Override
    public boolean isSatisfied(SGStatistics statistics) {
	return statistics.getSubgroupSize() >= minSubgroupPositives;
    }

    @Override
    public boolean isApplicable(ISubgroup<DataRecord> subgroup) {
	return super.isApplicable(subgroup)
		&& (((SG) subgroup).getTarget() != null)
		&& ((SG) subgroup).getTarget().isBoolean();
    }
}
