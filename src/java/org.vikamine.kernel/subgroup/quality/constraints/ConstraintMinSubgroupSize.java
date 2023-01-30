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
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatistics;


public class ConstraintMinSubgroupSize extends AbstractConstraintStatisticBased {

    double minSubgroupSize;

    boolean absolute;

    public ConstraintMinSubgroupSize(double minSubgroupSize, boolean absolute) {
	super();
	this.minSubgroupSize = minSubgroupSize;
	this.absolute = absolute;
    }

    public double getMinSubgroupSize() {
	return minSubgroupSize;
    }

    public boolean isAbsolute() {
	return absolute;
    }

    @Override
    public boolean isSatisfied(SGStatistics statistics) {
	if (absolute) {
	    return statistics.getSubgroupSize() >= minSubgroupSize;
	} else {
	    return statistics.getSubgroupSize()
		    / statistics.getDefinedPopulationCount() >= minSubgroupSize;
	}
    }

    public void setAbsolute(boolean absolute) {
	this.absolute = absolute;
    }

    public void setMinSubgroupSize(double minSubgroupSize) {
	this.minSubgroupSize = minSubgroupSize;
    }

    public double getMinSubgroupSizeAbsolute(SG initialSubgroup) {
	if (absolute) {
	    return minSubgroupSize;
	} else {
	    return minSubgroupSize
		    / initialSubgroup.getStatistics()
			    .getDefinedPopulationCount();
	}
    }
}
