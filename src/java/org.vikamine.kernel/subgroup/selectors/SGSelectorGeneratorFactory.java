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

import org.vikamine.kernel.data.discretization.EqualFreqDiscretizer;
import org.vikamine.kernel.data.discretization.EqualWidthDiscretizer;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGenerator.SimpleValueSelectorGenerator;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGenerator.SplitSelectorGenerator;

/**
 * 
 * @author atzmueller
 * 
 */
public final class SGSelectorGeneratorFactory {

    public static SGSelectorGenerator createSimpleValueGenerator() {
	return createStandardGenerator(false);
    }

    public static SGSelectorGenerator createSimpleValueGenerator(
	    boolean ignoreDefaults) {
	if (ignoreDefaults) {
	    return new SGSelectorGenerator.SimpleValueSelectorGeneratorIgnoreDefaults();
	} else {
	}
	return new SimpleValueSelectorGenerator();
    }

    public static SGSelectorGenerator createOnlyNumericGenerator() {
	return createOnlyNumericGenerator(5);
    }
    
    public static SGSelectorGenerator createOnlyNumericGenerator(int nbins) {
	return new SplitSelectorGenerator(
		new SGSelectorGenerator.EmptySelectorSetGenerator(),
		new SGSelectorGenerator.SimpleNumericSelectorGenerator(
			new EqualFreqDiscretizer(nbins)));
    }

    public static SGSelectorGenerator createStandardGenerator() {
	return createStandardGenerator(true);
    }

    public static SGSelectorGenerator createStandardGenerator(
	    boolean ignoreDefaults) {
	return new SplitSelectorGenerator(
		createSimpleValueGenerator(ignoreDefaults),
		new SGSelectorGenerator.SimpleNumericSelectorGenerator(
			new EqualFreqDiscretizer(5)));
    }

    public static SGSelectorGenerator createStandardGenerator(
	    int numberOfIntervals, boolean ignoreDefaults) {
	return new SplitSelectorGenerator(
		createSimpleValueGenerator(ignoreDefaults),
		new SGSelectorGenerator.SimpleNumericSelectorGenerator(
			new EqualFreqDiscretizer(numberOfIntervals)));
    }

    public static SGSelectorGenerator createIntervalGenerator(
	    boolean ignoreDefaults) {
	return new SplitSelectorGenerator(
		createSimpleValueGenerator(ignoreDefaults),
		new SGSelectorGenerator.AllExtremeValueBasedSelectorGenerator(
			new EqualFreqDiscretizer(5), false));
    }

    public static SGSelectorGenerator createIntervalGeneratorPlusSimpleIntervals(
	    boolean ignoreDefaults) {
	return new SplitSelectorGenerator(
		createSimpleValueGenerator(ignoreDefaults),
		new SGSelectorGenerator.AllExtremeValueBasedSelectorGenerator(
			new EqualFreqDiscretizer(5), true));
    }

    public static SGSelectorGenerator createIntervalGeneratorPlusSimpleIntervalsEqualWidth(
	    boolean ignoreDefaults) {
	return new SplitSelectorGenerator(
		createSimpleValueGenerator(ignoreDefaults),
		new SGSelectorGenerator.AllExtremeValueBasedSelectorGenerator(
			new EqualWidthDiscretizer(5), true));
    }
}
