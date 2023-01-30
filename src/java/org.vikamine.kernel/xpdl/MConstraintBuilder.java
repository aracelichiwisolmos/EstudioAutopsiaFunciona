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

package org.vikamine.kernel.xpdl;

public abstract class MConstraintBuilder {

    public static IConstraint build(String value, String x) {
	EConstraintTyp typ = EConstraintTyp.valueOf(x);
	try {
	    if (typ.equals(EConstraintTyp.maxK)) {
		return new MConstraintNumeric(Double.parseDouble(value), typ);
	    } else if (typ.equals(EConstraintTyp.minQuality)) {
		return new MConstraintNumeric(Double.parseDouble(value), typ);
	    } else if (typ.equals(EConstraintTyp.maxSelectors)) {
		if (value.contains(",")) {
		    return new MConstraintIntEnumeration(
			    parseIntEnumeration(value), typ);
		} else {
		    return new MConstraintNumeric(Double.parseDouble(value),
			    typ);
		}
	    } else if (typ.equals(EConstraintTyp.minSubgroupSize)) {
		return new MConstraintNumeric(Double.parseDouble(value), typ);
	    } else if (typ.equals(EConstraintTyp.minTPSupportRelative)) {
		return new MConstraintNumeric(Double.parseDouble(value), typ);
	    } else if (typ.equals(EConstraintTyp.minTPSupportAbsolute)) {
		return new MConstraintNumeric(Double.parseDouble(value), typ);
	    } else if (typ.equals(EConstraintTyp.relevantSubgroupsOnly)) {
		return new MConstraintBoolean(Boolean.parseBoolean(value), typ);
	    } else if (typ.equals(EConstraintTyp.weightedCovering)) {
		return new MConstraintBoolean(Boolean.parseBoolean(value), typ);
	    } else if (typ.equals(EConstraintTyp.ignoreDefaultValues)) {
		return new MConstraintBoolean(Boolean.parseBoolean(value), typ);
	    } else {
		return null;
	    }
	} catch (NumberFormatException e) {
	    throw new XMLException("ungueltiger Constrainwert");
	}
    }

    private static int[] parseIntEnumeration(String value) {
	String[] ints = value.split(",");
	int[] result = new int[ints.length];
	for (int i = 0; i < result.length; i++) {
	    result[i] = Integer.parseInt(ints[i].trim());
	}
	return result;

    }
}
