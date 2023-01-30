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

import java.util.BitSet;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;

/**
 * Utilities for generating bit sets for subgroup/selectors.
 * 
 * @author lemmerich
 *
 */

public class BitSetUtils {

    public static BitSet generateBitSet(SGSelector lws,
	    DataRecordIteration iteration) {
	BitSet selectorBitSet = new BitSet(iteration.size());
	int i = 0;
	for (DataRecord dr : iteration) {
	    if (lws.isContainedInInstance(dr)) {
		selectorBitSet.set(i);
	    }
	    i++;
	}
	return selectorBitSet;
    }

    public static BitSet generateBitSet(SGSelector lws,
	    List<DataRecord> iteration) {
	BitSet selectorBitSet = new BitSet(iteration.size());
	int i = 0;
	for (DataRecord dr : iteration) {
	    if (lws.isContainedInInstance(dr)) {
		selectorBitSet.set(i);
	    }
	    i++;
	}
	return selectorBitSet;
    }

    public static BitSet generateBitSet(SG sg, DataRecordIteration iteration) {
	BitSet selectorBitSet = new BitSet(iteration.size());
	int i = 0;
	for (DataRecord dr : iteration) {
	    if (sg.getSGDescription().isMatching(dr)) {
		selectorBitSet.set(i);
	    }
	    i++;
	}
	return selectorBitSet;
    }

    public static BitSet generatePositives(SG sg, DataRecordIteration iteration) {
	BooleanTarget target = (BooleanTarget) sg.getTarget();
	BitSet selectorBitSet = new BitSet(iteration.size());
	int i = 0;
	for (DataRecord dr : iteration) {
	    if (sg.getSGDescription().isMatching(dr) && target.isPositive(dr)) {
		selectorBitSet.set(i);
	    }
	    i++;
	}
	return selectorBitSet;
    }
}
