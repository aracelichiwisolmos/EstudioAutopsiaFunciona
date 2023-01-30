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
 * Created on 11.07.2005
 *
 */

package org.vikamine.kernel.subgroup.search;

/**
 * @author atzmueller
 */
public class SDMethodStats {

    private long startTimeMillis;
    private long endTimeMillis;

    private long startCPUTime;
    private long endCPUTime;

    private int subgroupsComputedCounter;

    public long getStartCPUTime() {
	return startCPUTime;
    }

    public void setStartCPUTime(long startCPUTime) {
	this.startCPUTime = startCPUTime;
    }

    public long getEndCPUTime() {
	return endCPUTime;
    }

    public void setEndCPUTime(long endCPUTime) {
	this.endCPUTime = endCPUTime;
    }

    public int getSubgroupsComputedCounter() {
	return subgroupsComputedCounter;
    }

    public void setSubgroupsComputedCounter(int nodeCounter) {
	this.subgroupsComputedCounter = nodeCounter;
    }

    public SDMethodStats() {
	super();
    }

    public long getEndTimeMillis() {
	return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
	this.endTimeMillis = endTimeMillis;
    }

    public long getStartTimeMillis() {
	return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
	this.startTimeMillis = startTimeMillis;
    }

    public long getRunTimeOverall() {
	return endTimeMillis - startTimeMillis;
    }

    public long getRunTimeCPUTime() {
	return endCPUTime - startCPUTime;
    }

    public void increaseNodeCounter() {
	subgroupsComputedCounter++;
    }

}
