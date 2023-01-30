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

package org.vikamine.kernel.subgroup.selectors;

import java.text.DecimalFormat;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * A selector for numeric attributes, with an interval matching condition
 * (lower, upper bound).
 * 
 * @author lemmerich
 * 
 */
public class NumericSelector implements SGSelector {

    private static final DecimalFormat format = new DecimalFormat("#.###");

    final NumericAttribute att;

    final boolean includeLowerBound;
    final boolean includeUpperBound;

    final double lowerBound;
    final double upperBound;

    public boolean isIncludeLowerBound() {
	return includeLowerBound;
    }

    public boolean isIncludeUpperBound() {
	return includeUpperBound;
    }

    public NumericSelector(NumericAttribute att, double lowerBound,
	    double upperBound) {
	this.att = att;
	this.lowerBound = lowerBound;
	this.upperBound = upperBound;
	this.includeLowerBound = false;
	this.includeUpperBound = false;
    }

    public NumericSelector(NumericAttribute att, double lowerBound,
	    double upperBound, boolean includeLowerBound,
	    boolean includeUpperBound) {
	this.att = att;
	this.lowerBound = lowerBound;
	this.upperBound = upperBound;
	this.includeLowerBound = includeLowerBound;
	this.includeUpperBound = includeUpperBound;
    }

    @Override
    public Object clone() {
	return new NumericSelector(att, lowerBound, upperBound,
		includeLowerBound, includeUpperBound);
    }

    @Override
    public Attribute getAttribute() {
	return att;
    }

    @Override
    public String getDescription() {
	return getId();
    }

    @Override
    public String getDescription(Describer dEr) {
	return getDescription();
    }

    @Override
    public String getId() {

	String result = att.getDescription();
	result += getValueId();

	return result;
    }

    public String getValueId() {
	String result = "";
	if (includeLowerBound && lowerBound != Double.NEGATIVE_INFINITY) {
	    result += "[";
	} else {
	    result += "]";
	}
	result += format.format(lowerBound) + ";" + format.format(upperBound);
	if (includeUpperBound && upperBound != Double.POSITIVE_INFINITY) {
	    result += "]";
	} else {
	    result += "[";
	}
	return result;
    }

    public double getLowerBound() {
	return lowerBound;
    }

    public double getUpperBound() {
	return upperBound;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((att == null) ? 0 : att.hashCode());
	result = prime * result + (includeLowerBound ? 1231 : 1237);
	result = prime * result + (includeUpperBound ? 1231 : 1237);
	long temp;
	temp = Double.doubleToLongBits(lowerBound);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	temp = Double.doubleToLongBits(upperBound);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	NumericSelector other = (NumericSelector) obj;
	if (att == null) {
	    if (other.att != null)
		return false;
	} else if (!att.equals(other.att))
	    return false;
	if (includeLowerBound != other.includeLowerBound)
	    return false;
	if (includeUpperBound != other.includeUpperBound)
	    return false;
	if (Double.doubleToLongBits(lowerBound) != Double
		.doubleToLongBits(other.lowerBound))
	    return false;
	if (Double.doubleToLongBits(upperBound) != Double
		.doubleToLongBits(other.upperBound))
	    return false;
	return true;
    }

    @Override
    public boolean isContainedInInstance(DataRecord instance) {
	double val = instance.getValue(att);
	if (Double.isNaN(val)) {
	    return false;
	}

	if (val < lowerBound || (!includeLowerBound && (val == lowerBound))) {
	    return false;
	}
	if (val > upperBound || (!includeUpperBound && (val == upperBound))) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return getDescription();
    }

    @Override
    public boolean includes(SGSelector otherSel) {
	if (!(otherSel instanceof NumericSelector)) {
	    return false;
	}
	if (getAttribute() != otherSel.getAttribute()) {
	    return false;
	}

	NumericSelector otherNumSel = (NumericSelector) otherSel;

	if (otherNumSel.lowerBound < lowerBound) {
	    return false;
	}
	if (otherNumSel.lowerBound == lowerBound) {
	    if (otherNumSel.includeLowerBound && (!includeLowerBound)) {
		return false;
	    }
	}
	if (otherNumSel.upperBound > upperBound) {
	    return false;
	}
	if (otherNumSel.upperBound == upperBound) {
	    if (otherNumSel.includeUpperBound && (!includeUpperBound)) {
		return false;
	    }
	}

	return true;
    }

    public NumericSelector join(NumericSelector sel) {
	double newLowerBound;
	boolean newIncludeLowerBound;

	if (lowerBound < sel.lowerBound) {
	    newLowerBound = sel.lowerBound;
	    newIncludeLowerBound = sel.includeLowerBound;
	} else if (lowerBound == sel.lowerBound) {
	    newLowerBound = sel.lowerBound;
	    newIncludeLowerBound = sel.includeLowerBound && includeLowerBound;
	} else {
	    newLowerBound = lowerBound;
	    newIncludeLowerBound = includeLowerBound;
	}

	double newupperBound;
	boolean newIncludeupperBound;

	if (upperBound > sel.upperBound) {
	    newupperBound = sel.upperBound;
	    newIncludeupperBound = sel.includeUpperBound;
	} else if (upperBound == sel.upperBound) {
	    newupperBound = sel.upperBound;
	    newIncludeupperBound = sel.includeUpperBound && includeUpperBound;
	} else {
	    newupperBound = upperBound;
	    newIncludeupperBound = includeUpperBound;
	}

	return new NumericSelector(att, newLowerBound, newupperBound,
		newIncludeLowerBound, newIncludeupperBound);

    }
}
