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

package org.vikamine.kernel.subgroup.quality.functions;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.ISubgroup;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;

/**
 * This is a wrapper class for any function (primarily statistical based ones).
 * The quality is divided by the description length (number of used selectors in
 * the description), thus favoring short descriptions.
 * 
 * @author lemmerich
 * @date 11/2011
 */
public class SimplicityFavoringQF implements IQualityFunction {

    private static final String ID = "SimplicityFavoringQF";
    private final IQualityFunction wrappedQualityFunction;

    public SimplicityFavoringQF(IQualityFunction wrapped) {
	super();
	wrappedQualityFunction = wrapped;
    }

    @Override
    public String getID() {
        return ID + "(" + wrappedQualityFunction.getID() + ")";
    }
    
    @Override
    public String getName() {
	return wrappedQualityFunction.getName() + " (simplicity)";
    }

    @Override
    public boolean isApplicable(ISubgroup<DataRecord> subgroup) {
	return wrappedQualityFunction.isApplicable(subgroup);
    }

    @Override
    public double evaluate(ISubgroup<DataRecord> subgroup) {
	return wrappedQualityFunction.evaluate(subgroup)
		/ subgroup.getDescription().getSelectors().size();
    }

    @Override
    /**
     * returns a deep clone (i.e. the wrapped function is cloned too)
     */
    public SimplicityFavoringQF clone() {
	return new SimplicityFavoringQF(wrappedQualityFunction.clone());
    }

}
