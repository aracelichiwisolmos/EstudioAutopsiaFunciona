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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.data.discretization.DiscretizationMethod;

/**
 * {@link SGSelectorGenerator} generates a set of selectors for an attribute
 * according to different strategies, e.g., taking all combinations, all extreme
 * values, or all cutpoints (numeric attribute) into account.
 * 
 * @author lemmerich
 * 
 */
public interface SGSelectorGenerator {
    public Collection<SGSelector> getSelectors(Attribute att, DataView dataview);

    public static class EmptySelectorSetGenerator implements
	    SGSelectorGenerator {

	@Override
	public Collection<SGSelector> getSelectors(Attribute att,
		DataView dataview) {
	    return Collections.emptyList();
	}
    }

    public static class SplitSelectorGenerator implements SGSelectorGenerator {

	SGSelectorGenerator nominalSelectorGenerator;
	SGSelectorGenerator numericSelectorGenerator;

	public SplitSelectorGenerator(
		SGSelectorGenerator nominalSelectorGenerator,
		SGSelectorGenerator numericSelectorGenerator) {
	    super();
	    this.nominalSelectorGenerator = nominalSelectorGenerator;
	    this.numericSelectorGenerator = numericSelectorGenerator;
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute att,
		DataView dataview) {
	    if (att.isNominal()) {
		return nominalSelectorGenerator.getSelectors(att, dataview);
	    } else if (att.isNumeric()) {
		return numericSelectorGenerator.getSelectors(att, dataview);
	    }
	    return Collections.EMPTY_LIST;
	}
    }

    public static class SimpleValueSelectorGenerator implements
	    SGSelectorGenerator {

	int maxNumberOfValues = Integer.MAX_VALUE;

	public SimpleValueSelectorGenerator() {
	    super();
	}

	/**
	 * Selectors are only created for attributes, if the overall count of
	 * values fir this attribute is smaller than the given parameter.
	 * 
	 * @param maxNumberOfValues
	 */
	public SimpleValueSelectorGenerator(int maxNumberOfValues) {
	    this.maxNumberOfValues = maxNumberOfValues;
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute att,
		DataView dataview) {
	    List<SGSelector> result = new ArrayList<SGSelector>();

	    if (att.isNominal()
		    && ((NominalAttribute) att).getValuesCount() < maxNumberOfValues) {
		NominalAttribute nomAtt = (NominalAttribute) att;
		Iterator<Value> valIterator = nomAtt.allValuesIterator();
		while (valIterator.hasNext()) {
		    Value value = valIterator.next();
		    DefaultSGSelector sel = new DefaultSGSelector(att, value);
		    result.add(sel);
		}
	    }
	    return result;
	}
    }

    public static class SimpleValueSelectorGeneratorIgnoreDefaults implements
	    SGSelectorGenerator {

	int maxNumberOfValues = Integer.MAX_VALUE;
	
	public SimpleValueSelectorGeneratorIgnoreDefaults() {
	    super();
	}
	
	/**
	 * Selectors are only created for attributes, if the overall count of
	 * values for this attribute is smaller than the given parameter.
	 * 
	 * @param maxNumberOfValues
	 */
	public SimpleValueSelectorGeneratorIgnoreDefaults(int maxNumberOfValues) {
	    this.maxNumberOfValues = maxNumberOfValues;
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute att,
		DataView dataview) {
	    List<SGSelector> result = new ArrayList<SGSelector>();

	    if (att.isNominal()
		    && ((NominalAttribute) att).getValuesCount() < maxNumberOfValues) {
		NominalAttribute nomAtt = (NominalAttribute) att;
		Iterator<Value> valIterator = nomAtt.allValuesIterator();
		while (valIterator.hasNext()) {
		    Value value = valIterator.next();
		    if (!value.isDefaultValue()) {
			DefaultSGSelector sel = new DefaultSGSelector(att,
				value);
			result.add(sel);
		    }
		}
	    }
	    return result;
	}
    }

    public static abstract class AbstractNumericSelectorGenerator implements
	    SGSelectorGenerator {
	List<Double> cutpoints;
	DiscretizationMethod discMethod;

	public AbstractNumericSelectorGenerator(DiscretizationMethod discMethod) {
	    this.discMethod = discMethod;
	}

	protected void prepareDiscMethod(Attribute numAtt, DataView dataView) {
	    discMethod.setAttribute((NumericAttribute) numAtt);
	    discMethod.setPopulation(dataView);
	}

    }

    public static class SimpleNumericSelectorGenerator extends
	    AbstractNumericSelectorGenerator {

	public SimpleNumericSelectorGenerator(DiscretizationMethod discMethod) {
	    super(discMethod);
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute numAtt,
		DataView dataview) {

	    if (!(numAtt instanceof NumericAttribute)) {
		throw new IllegalArgumentException(
			"This SelectorGenerator is only applicable for numeric attributes!");
	    }
	    prepareDiscMethod(numAtt, dataview);

	    cutpoints = discMethod.getCutpoints();
	    cutpoints.add(Double.NEGATIVE_INFINITY);
	    cutpoints.add(Double.POSITIVE_INFINITY);
	    Collections.sort(cutpoints);

	    List<SGSelector> result = new ArrayList<SGSelector>();
	    for (int i = 0; i < cutpoints.size() - 1; i++) {
		double lowerBound = cutpoints.get(i);
		double upperBound = cutpoints.get(i + 1);
		NumericSelector sel = new NumericSelector(
			(NumericAttribute) numAtt, lowerBound, upperBound,
			true, false);
		if (!result.contains(sel)) {
		    result.add(sel);
		}
	    }
	    return result;
	}
    }

    public static class AllCombinationsSelectorGenerator extends
	    AbstractNumericSelectorGenerator {

	public AllCombinationsSelectorGenerator(DiscretizationMethod discMethod) {
	    super(discMethod);
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute numAtt,
		DataView dataview) {
	    if (!(numAtt instanceof NumericAttribute)) {
		throw new IllegalArgumentException(
			"This SelectorGenerator is only applicable for numeric attributes!");
	    }

	    prepareDiscMethod(numAtt, dataview);

	    cutpoints = discMethod.getCutpoints();
	    cutpoints.add(Double.NEGATIVE_INFINITY);
	    cutpoints.add(Double.POSITIVE_INFINITY);
	    Collections.sort(cutpoints);

	    if (!(numAtt instanceof NumericAttribute)) {
		throw new IllegalArgumentException(
			"This SelectorGenerator is only applicable for numeric attributes!");
	    }
	    Collection<SGSelector> resultSet = new HashSet<SGSelector>();

	    for (int i = 0; i < cutpoints.size() - 1; i++) {
		for (int j = i + 1; j < cutpoints.size(); j++) {
		    double lowerBound = cutpoints.get(i);
		    double upperBound = cutpoints.get(j);
		    if ((i != 0) || (j != cutpoints.size() - 1)) {
			NumericSelector sel = new NumericSelector(
				(NumericAttribute) numAtt, lowerBound,
				upperBound, true, false);
			resultSet.add(sel);
		    }
		}
	    }
	    return resultSet;
	}
    }

    public static class AllExtremeValueBasedSelectorGenerator extends
	    AbstractNumericSelectorGenerator {
	boolean includeSimpleIntervals;

	public AllExtremeValueBasedSelectorGenerator(
		DiscretizationMethod discMethod, boolean simpleIntervals) {
	    super(discMethod);
	    this.includeSimpleIntervals = simpleIntervals;
	}

	@Override
	public Collection<SGSelector> getSelectors(Attribute numAtt,
		DataView dataview) {
	    if (!(numAtt instanceof NumericAttribute)) {
		throw new IllegalArgumentException(
			"This SelectorGenerator is only applicable for numeric attributes!");
	    }

	    prepareDiscMethod(numAtt, dataview);

	    cutpoints = discMethod.getCutpoints();
	    cutpoints.add(Double.NEGATIVE_INFINITY);
	    cutpoints.add(Double.POSITIVE_INFINITY);
	    Collections.sort(cutpoints);

	    List<SGSelector> result = new ArrayList<SGSelector>();

	    // "simple intervals"
	    if (includeSimpleIntervals) {
		for (int i = 0; i < cutpoints.size() - 1; i++) {
		    double lowerBound = cutpoints.get(i);
		    double upperBound = cutpoints.get(i + 1);
		    NumericSelector sel = new NumericSelector(
			    (NumericAttribute) numAtt, lowerBound, upperBound,
			    true, false);
		    if (!result.contains(sel)) {
			result.add(sel);
		    }
		}
	    }

	    // intervals starting from cutpoints[0];
	    for (int i = 1; i < cutpoints.size() - 1; i++) {
		double lowerBound = cutpoints.get(0);
		double upperBound = cutpoints.get(i);
		NumericSelector sel = new NumericSelector(
			(NumericAttribute) numAtt, lowerBound, upperBound,
			true, false);
		if (!result.contains(sel)) {
		    result.add(sel);
		}
	    }

	    // intervals ending at cutpoints[max];
	    for (int i = 1; i < cutpoints.size() - 1; i++) {
		double lowerBound = cutpoints.get(i);
		double upperBound = cutpoints.get(cutpoints.size() - 1);
		NumericSelector sel = new NumericSelector(
			(NumericAttribute) numAtt, lowerBound, upperBound,
			true, false);
		if (!result.contains(sel)) {
		    result.add(sel);
		}
	    }
	    return result;
	}
    }
}
