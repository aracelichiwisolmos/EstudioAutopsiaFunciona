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

package org.vikamine.kernel.subgroup.target;

import org.vikamine.kernel.data.DataRecord;

/**
 * {@link InvertedNumericTarget} extends a {@link NumericTarget} in that way,
 * that the "direction" of the value is "inverted", i.e., multiplied by -1.
 * 
 * @author atzmueller
 * 
 */
public class InvertedNumericTarget extends NumericTarget {
    public InvertedNumericTarget(NumericTarget target) {
	super(target.getNumericTargetAttribute());
    }

    @Override
    public double getValue(DataRecord record) {
	return -1 * super.getValue(record);
    }

    @Override
    public String getDescription() {
        return "-" + super.getDescription();
    }
    
}
