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

package org.vikamine.kernel.subgroup.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGFilters;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.quality.functions.AddedValueQF;
import org.vikamine.kernel.subgroup.quality.functions.AdjustedResidualQF;
import org.vikamine.kernel.subgroup.quality.functions.ChiSquareQF;
import org.vikamine.kernel.subgroup.quality.functions.LiftQF;
import org.vikamine.kernel.subgroup.quality.functions.PiatetskyShapiroQF;
import org.vikamine.kernel.subgroup.quality.functions.RelativeGainQF;
import org.vikamine.kernel.subgroup.quality.functions.StandardQF;
import org.vikamine.kernel.subgroup.quality.functions.WRAccQF;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGenerator;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGeneratorFactory;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * 
 * @author atzmueller
 * 
 */

public class SDSimpleTask extends MiningTask {

    private static final String QF_ADJUSTED_RESIDUAL = "ares";
    private static final String QF_BINOMIAL = "bin";
    private static final String QF_CHI2 = "chi2";
    private static final String QF_GAIN = "gain";
    private static final String QF_LIFT = "lift";
    private static final String QF_PIATETSKY_SHAPIRO = "ps";
    private static final String QF_RELATIVE_GAIN = "relgain";
    private static final String QF_WRACC = "wracc";

    private static final String SD_METHOD_BEAM = "beam";
    private static final String SD_METHOD_BSD = "bsd";
    private static final String SD_METHOD_SDMAP = "sdmap";
    private static final String SD_METHOD_SDMAP_DISJUNCTIVE = "sdmap-dis";

    private static final String POST_FILTER_MIN_IMPROVEMENT_GLOBAL = "min-improve-global";
    private static final String POST_FILTER_MIN_IMPROVEMENT_SET = "min-improve-set";
    private static final String POST_FILTER_RELEVANCY = "relevancy";
    private static final String POST_FILTER_SIG_IMPROVEMENT_GLOBAL = "sig-improve-global";
    private static final String POST_FILTER_SIG_IMPROVEMENT_SET = "sig-improve-set";
    private static final String POST_FILTER_WEIGHTED_COVERING = "weighted-covering";

    private final List<SGFilters> sgFilters = new ArrayList<SGFilters>();
    private double postFilterParameter;

    public static String[] getSimpleDescription(SGDescription sgDescription) {
	List<String> result = new ArrayList<String>();
	for (SGSelector sel : sgDescription.getSelectors()) {
	    result.add(sel.getDescription());
	}

	String[] strResult = new String[result.size()];
	strResult = result.toArray(strResult);
	return strResult;
    }

    public static String getAttributeIDOfSelector(SGSelector sel) {
	return sel.getAttribute().getId();
    }

    public static String getSingleValueIDOfSelector(SGSelector sel) {
	if (!(sel instanceof DefaultSGSelector)) {
	    if (sel instanceof NumericSelector) {
		NumericSelector numericSel = (NumericSelector) sel;
		return numericSel.getValueId();
	    } else {
	    throw new UnsupportedOperationException("Selector " + sel
		    + " is no DefaultSGSelector nor NumericSelector - this is currently not supported!");
	    }
	} else {
	    DefaultSGSelector defaultSGSelector = (DefaultSGSelector) sel;
	    Set<Value> values = defaultSGSelector.getValues();
	    if (values.size() > 1) {
		throw new UnsupportedOperationException("Selector " + sel
			+ " has more than one value: " + values
			+ " - this is currently not supported!");
	    } else {
		if (values.isEmpty()) {
		    return null;
		} else {
		    return values.iterator().next().getId();
		}
	    }
	}
    }

    public SDSimpleTask(Ontology ontology) {
	super();
	setOntology(ontology);
    }

    public SDSimpleTask(AbstractMiningTask oldTask) {
	super(oldTask);
    }

    public void setAttributes(String[] ids, boolean discretizeNumericAttributes, int nbins) {
	List<Attribute> attributes = new ArrayList<Attribute>(ids.length);
	for (String id : ids) {
	    if (checkAttribute(id)) {
		Attribute a = getOntology().getAttribute(id);
		attributes.add(a);
	    }
	}
	initializeSearchSpace(attributes, discretizeNumericAttributes, nbins);
    }

    private boolean checkAttribute(String attributeID) {
	if (getOntology().getAttribute(attributeID) == null) {
	    throw new IllegalArgumentException(
		    "Attribute " + attributeID + " not found!");
	} else if (target != null) {
	    Attribute attribute = ontology.getAttribute(attributeID);
	    if (target.getAttributes().contains(attribute)) {
		return false;
	    }
	}
	return true;
    }

    private void checkNominalAttributeValue(String attributeID,
	    String valueID) {
	checkAttribute(attributeID);
	Attribute a = getOntology().getAttribute(attributeID);
	if (!(a instanceof NominalAttribute)) {
	    throw new IllegalArgumentException(
		    "Attribute " + a + " is not nominal!");
	}
	NominalAttribute nom = (NominalAttribute) a;
	if (nom.getNominalValueFromID(valueID) == null)
	    throw new IllegalArgumentException("Value " + valueID
		    + " for attribute " + attributeID + " not found!");
    }

    public void setNominalTarget(String attributeID, String valueID) {
	checkAttribute(attributeID);
	checkNominalAttributeValue(attributeID, valueID);
	DefaultSGSelector selector = new DefaultSGSelector(getOntology(),
		attributeID, valueID);
	setTarget(new SelectorTarget(selector));
    }

    public void setNumericTarget(String attributeID) {
	checkAttribute(attributeID);
	Attribute a = getOntology().getAttribute(attributeID);
	if (a.isNumeric()) {
	    setTarget(new NumericTarget((NumericAttribute) a));
	} else {
	    throw new IllegalArgumentException(
		    attributeID + " is not a numeric attributed");
	}
    }

    private void check() {
	if (getInitialSG() == null) {
	    initializeStartSubgroup();
	}
    }

    private void initializeSearchSpace(Collection<Attribute> attributes, boolean discretizeNumericAttributes, int nbins) {	
	List<SGSelector> newSearchSpace = new ArrayList<SGSelector>();
	for (Attribute att : attributes) {
	    if (att.isNominal()) {
		SGSelectorGenerator generator;
		if (isIgnoreDefaultValues())
		    generator = new SGSelectorGenerator.SimpleValueSelectorGeneratorIgnoreDefaults();
		else
		    generator = new SGSelectorGenerator.SimpleValueSelectorGenerator();
		
		newSearchSpace.addAll(generator.getSelectors(att,
			getOntology().getDataView()));
	    } else if (att.isNumeric() && discretizeNumericAttributes) {
		NumericAttribute numAtt = (NumericAttribute) att;

		int usedValuesCount = numAtt
			.getUsedValuesCount(getOntology().getDataset());
		if (usedValuesCount <= nbins) {
		    Set<Value> allValues = new HashSet<Value>();
		    for (Iterator<Value> valIter = numAtt.usedValuesIterator(getOntology().getDataset()); valIter.hasNext();) {
			Value value = valIter.next();
			allValues.add(value);
		    }
		    for (Value value : allValues) {
			SGSelector sel = new DefaultSGSelector(numAtt, value);
			newSearchSpace.add(sel);
		    }
		} else {
		    Collection<SGSelector> selectors = SGSelectorGeneratorFactory
				.createOnlyNumericGenerator(nbins).getSelectors(
					numAtt, getOntology().getDataView());
		    newSearchSpace.addAll(selectors);
		}
	    }
	}
	setSearchSpace(newSearchSpace);
    }

    public void initializeStartSubgroup() {
	SG initialSG = new SG(getOntology().getDataView(), getTarget());
	initialSG.createStatistics(null);
	setInitialSG(initialSG);
    }

    public void setQualityFunction(String qf) {
	if (qf.equals(QF_ADJUSTED_RESIDUAL)) {
	    setQualityFunction(new AdjustedResidualQF());
	} else if (qf.equals(QF_BINOMIAL))
	    setQualityFunction(new StandardQF(0.5));
	else if (qf.equals(QF_RELATIVE_GAIN))
	    setQualityFunction(new RelativeGainQF());
	else if (qf.equals(QF_GAIN))
	    setQualityFunction(new AddedValueQF());
	else if (qf.equals(QF_WRACC))
	    setQualityFunction(new WRAccQF());
	else if (qf.equals(QF_PIATETSKY_SHAPIRO))
	    setQualityFunction(new PiatetskyShapiroQF());
	else if (qf.equals(QF_LIFT))
	    setQualityFunction(new LiftQF());
	else if (qf.equals(QF_CHI2))
	    setQualityFunction(new ChiSquareQF());
	else
	    throw new IllegalArgumentException(
		    "Unknown quality function " + qf);
    }

    public void setSDMethod(String method) {
	if (method.equals(SD_METHOD_SDMAP))
	    setMethodType(SDMap.class);
	else if (method.equals(SD_METHOD_SDMAP_DISJUNCTIVE))
	    setMethodType(SDMapDisjunctive.class);
	else if (method.equals(SD_METHOD_BSD))
	    setMethodType(BSD.class);
	else if (method.equals(SD_METHOD_BEAM))
	    setMethodType(SDBeamSearch.class);
	else
	    throw new IllegalArgumentException("Unknown method " + method);
    }

    public void setPostFilter(String postFilter) {
	if (postFilter == null)
	    return;

	double postFilterParameterDefault = postFilterParameter;
	if (postFilterParameterDefault < 0) {
	    throw new IllegalStateException(
		    "Postfilter Parameter (parfilter) should be positive, but it is: "
			    + postFilterParameterDefault);
	}

	if (postFilter.equals(POST_FILTER_MIN_IMPROVEMENT_GLOBAL))
	    sgFilters.add(new SGFilters.MinImprovementFilterGlobal(
		    postFilterParameterDefault));
	else if (postFilter.equals(POST_FILTER_MIN_IMPROVEMENT_SET))
	    sgFilters.add(new SGFilters.MinImprovementFilterOnSGSet(
		    postFilterParameterDefault));
	else if (postFilter.equals(POST_FILTER_SIG_IMPROVEMENT_GLOBAL))
	    sgFilters.add(new SGFilters.SignificantImprovementFilterGlobal(
		    postFilterParameterDefault));
	else if (postFilter.equals(POST_FILTER_SIG_IMPROVEMENT_SET))
	    sgFilters.add(new SGFilters.SignificantImprovementFilterOnSet(
		    postFilterParameterDefault));
	else if (postFilter.equals(POST_FILTER_RELEVANCY))
	    sgFilters.add(new SGFilters.RelevancyFilter());
	else if (postFilter.equals(POST_FILTER_WEIGHTED_COVERING))
	    setWeightedCovering(true);
	else
	    throw new IllegalArgumentException(
		    "Unknown post-filter " + postFilter);
    }

    public void setPostFilterParameter(double param) {
	postFilterParameter = param;
    }

    @Override
    public SGSet performSubgroupDiscovery() {
	if (getMethodType().equals(BSD.class) && getTarget().isNumeric())
	    setMethodType(NumericBSD.class);

	check();
	SGSet result = super.performSubgroupDiscovery(sgFilters);

	return result;
    }
}
