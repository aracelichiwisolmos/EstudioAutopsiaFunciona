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
 * Created on 10.03.2004
 */
package org.vikamine.kernel.formula.constants;

import org.vikamine.kernel.formula.EvaluationData;

/**
 * {@link SubgroupSize} considers the total number of data records contained in
 * the current subgroup.
 * 
 * @author Tobias Vogele
 */
public class SubgroupSize extends AbstractFormulaNumberPrimitive {

    public SubgroupSize() {
	super("subgroupSize");
    }

    @Override
    public synchronized Double eval(EvaluationData data) {
	return new Double(data.getStatistics().getSubgroupSize());
    }

    @Override
    public boolean isEqual(Object other) {

	if (this == other)
	    return true;
	else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public int computeHashCode() {
	return ((Object) this).hashCode();
    }
}
