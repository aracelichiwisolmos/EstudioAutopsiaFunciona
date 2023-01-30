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

import java.text.DecimalFormat;

import org.vikamine.kernel.statistics.StatisticComponent;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;

/**
 * Get a String representation of a subgroup and some statistics in a String.
 * 
 * How to use it:
 * 
 * SG sgA = ... SG sgB = ...
 * 
 * int parameter = SGVerbalizer.SGQuality | SGVerbalizer.SGSize |
 * SGVerbalizer.SGTargetQuantity | SGVerbalizer.SGDeviation;
 * 
 * System.out.println("A: " + verbalizer.verbalize(sgA, parameter));
 * System.out.println("B: " + verbalizer.verbalize(sgB, parameter));
 * 
 * @author lemmerich, atzmueller
 * @date 2011/04
 */
public class SGVerbalizer {

    protected String delimeter = "\t";
    protected DecimalFormat formatter = new DecimalFormat("#.###");

    public static final int SGQuality = 1 << 0;
    public static final int SGSize = 1 << 1;
    public static final int SGTargetQuantity = 1 << 2;
    public static final int SGDeviation = 1 << 3;
    public static final int TP = 1 << 4;

    public static final int SIGNIFICANCE = 1 << 5;

    public SGVerbalizer() {
	super();
    }

    public SGVerbalizer(DecimalFormat decimalFormatter) {
	super();
	this.formatter = decimalFormatter;
    }

    public SGVerbalizer(String delimeter) {
	super();
	this.delimeter = delimeter;
    }

    public SGVerbalizer(String delimeter, DecimalFormat decimalFormatter) {
	super();
	this.delimeter = delimeter;
	this.formatter = decimalFormatter;
    }

    public String getDelimeter() {
	return delimeter;
    }

    public DecimalFormat getFormatter() {
	return formatter;
    }

    public void setDelimeter(String delimeter) {
	this.delimeter = delimeter;
    }

    public void setFormatter(DecimalFormat formatter) {
	this.formatter = formatter;
    }

    public String verbalize(SG sg, int parameter) {
	StringBuilder sb = new StringBuilder();
	String description = sg.getDescription().getDescription();
	sb.append(description.isEmpty() ? "Overall" : description);

	if ((parameter & SGQuality) > 0) {
	    sb.append(delimeter + formatter.format(sg.getQuality()));
	}

	if ((parameter & SGSize) > 0) {
	    sb.append(delimeter + sg.getStatistics().getSubgroupSize());
	}
	if ((parameter & SGTargetQuantity) > 0) {
	    if (sg.getStatistics() instanceof SGStatisticsBinary) {
		sb.append(delimeter
			+ formatter.format(((SGStatisticsBinary) sg
				.getStatistics()).getP()));
	    } else if (sg.getStatistics() instanceof SGStatisticsNumeric) {
		sb.append(delimeter
			+ formatter.format(((SGStatisticsNumeric) sg
				.getStatistics()).getSGMean()));
	    } else {
		sb.append(delimeter + "-");
	    }
	}
	if ((parameter & SGDeviation) > 0) {
	    sb.append(delimeter
		    + formatter.format(sg.getStatistics().getDeviation()));
	}
	if ((parameter & TP) > 0) {
	    if (sg.getTarget().isBoolean()) {
		sb.append(delimeter
			+ formatter.format(((SGStatisticsBinary) sg
				.getStatistics()).getTp()));
	    }
	}

	if ((parameter & SIGNIFICANCE) > 0) {
	    if (sg.getTarget().isBoolean()) {
		StatisticComponent signifComponent = StatisticComponent.SIGNIFICANCE;
		sb.append(delimeter
			+ signifComponent.getValue(sg.getStatistics()));
	    }
	}
	addAdditionalParameters(sg, sb);

	return sb.toString();
    }

    /**
     * This can be used too add more parameters that can used to express more
     * statistical information in sour sublcass of {@link SGVerbalizer}
     */
    @SuppressWarnings("unused")
    protected void addAdditionalParameters(SG sg, StringBuilder sb) {
	// do nothing. to be overridden by subclasses
    }

    public String verbalize(Iterable<SG> sgs, int parameter) {
	StringBuilder sb = new StringBuilder();

	SG firstSG = sgs.iterator().next();

	// create header
	sb.append("Description");

	if ((parameter & SGQuality) > 0) {
	    sb.append(delimeter + "Quality");
	}

	if ((parameter & SGSize) > 0) {
	    sb.append(delimeter + "Size");
	}
	if ((parameter & SGTargetQuantity) > 0) {
	    sb.append(delimeter + "Target");
	}
	if ((parameter & SGDeviation) > 0) {
	    sb.append(delimeter + "Deviation target");
	}
	if ((parameter & TP) > 0) {
	    if (firstSG.getTarget().isBoolean()) {
		sb.append(delimeter + "True Positives");
	    }
	}

	if ((parameter & SIGNIFICANCE) > 0) {
	    if (firstSG.getTarget().isBoolean()) {
		sb.append(delimeter + "Significance");
	    }
	}
	sb.append("\n");

	for (SG sg : sgs) {
	    sb.append(verbalize(sg, parameter) + "\n");
	}
	return sb.toString();
    }
}
