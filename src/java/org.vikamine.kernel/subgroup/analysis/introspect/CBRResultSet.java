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

package org.vikamine.kernel.subgroup.analysis.introspect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;

/**
 * {@link CBRResult} stores comparisons for a query case.
 * 
 * @author atzmueller
 * 
 */
public class CBRResultSet {

    private DataRecord queryCase;

    private List<CBRResult> results;

    private boolean isResultSetSorted = false;

    public CBRResultSet(DataRecord queryCase) {
	results = new LinkedList();
	this.queryCase = queryCase;
    }

    private void sortResultSetIfNeeded() {
	if (!isResultSetSorted) {
	    Collections.sort(results, CBRResultComparator.getInstance());
	    isResultSetSorted = true;
	}
    }

    public void addResult(CBRResult result) {
	this.results.add(result);
	isResultSetSorted = false;
    }

    public CBRResult getResult(int k) {
	sortResultSetIfNeeded();
	return this.results.get(k);
    }

    public CBRResultSet getKBest(int k) {
	sortResultSetIfNeeded();
	CBRResultSet kBestResultSet = new CBRResultSet(this.queryCase);
	if (!this.results.isEmpty()) {
	    for (int j = 0; j < (java.lang.Math.min(k, this.results.size())); j++) {
		kBestResultSet.addResult(this.results.get(j));
	    }
	}
	return kBestResultSet;
    }

    public List<CBRResult> getResults() {
	sortResultSetIfNeeded();
	return results;
    }
}
