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
 * Created on 27.10.2005
 *
 */
package org.vikamine.kernel.subgroup.analysis.introspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.FilteringDataRecordIterator;
import org.vikamine.kernel.data.IncludingDataRecordFilter;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.SupportingFactor;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.util.VKMUtil;

/**
 * {@link SGCBRIntrospector} is used for subgroup introspection, and creates a
 * {@link PrototypicalPatternCase} for a subgroup {@link SG}.
 * 
 * @author atzmueller
 */
public class SGCBRIntrospector {

    private class SimpleMatchingCBRComparator {
	public SimpleMatchingCBRComparator() {
	    super();
	}

	private double calculateSimilarityToRetrievedCase(
		List<SGNominalSelector> subgroupFactors,
		DataRecord retrievedCase) {
	    double similarity = 0.0;
	    for (SGNominalSelector sel : subgroupFactors) {
		if (sel.isContainedInInstance(retrievedCase)) {
		    similarity += 1.0;
		}
	    }
	    return similarity / subgroupFactors.size();
	}

	public CBRResultSet retrieveKMostSimilarCases(int k) {
	    CBRResultSet resultSet = new CBRResultSet(null);
	    List subgroupFactors = getSubgroupFactors(true);
	    Iterator<DataRecord> instanceIterator = new FilteringDataRecordIterator(
		    subgroup.subgroupInstanceIterator(),
		    new IncludingDataRecordFilter() {
			@Override
			public boolean isIncluded(DataRecord instance) {
			    if (subgroup.getTarget() instanceof BooleanTarget) {
				return ((BooleanTarget) subgroup.getTarget())
					.isPositive(instance);
			    } else {
				return true;
			    }
			}
		    });
	    while (instanceIterator.hasNext()) {
		DataRecord retrieveCase = instanceIterator.next();
		resultSet.addResult(new CBRResult(null, retrieveCase,
			calculateSimilarityToRetrievedCase(subgroupFactors,
				retrieveCase)));
	    }

	    return resultSet.getKBest(k);
	}
    }

    private final SG subgroup;

    private final List<Attribute> relevantAttributes;

    private final Ontology ontology;

    public SGCBRIntrospector(SG subgroup, List<Attribute> relevantAttributes,
	    Ontology ontology) {
	super();
	this.subgroup = subgroup;
	this.ontology = ontology;
	this.relevantAttributes = relevantAttributes;
    }

    private List<NominalAttribute> getRelevantNominalAttributesForCBR() {
	List<NominalAttribute> result = new LinkedList<NominalAttribute>();
	for (Iterator iter = ontology.getAttributes().iterator(); iter
		.hasNext();) {
	    Attribute att = (Attribute) iter.next();
	    if (att.isNominal())
		result.add((NominalAttribute) att);
	}
	return toDefaultNominalAttributes(result);
    }

    private List<NominalAttribute> toDefaultNominalAttributes(
	    List<? extends Attribute> attributes) {
	List<NominalAttribute> result = new LinkedList<NominalAttribute>();
	for (Attribute att : attributes) {
	    result.add((NominalAttribute) att);
	}
	return result;
    }

    public PrototypicalPatternCase getSubgroupFactorsAsPrototypicalPatternCase(
	    CBRResultSet resultSet) {
	List<SGSelector> principalFactors = VKMUtil.asList(subgroup
		.getSGDescription().iterator());
	List<SupportingFactor> supportingFactors = SGUtils
		.computeSupportingFactors(subgroup, relevantAttributes, false);
	List<SupportingFactor> filteredSupportingFactors = new LinkedList();
	for (SupportingFactor suppFactor : supportingFactors) {
	    if (suppFactor.isPositivelyCorrelated()) {
		filteredSupportingFactors.add(suppFactor);
	    }
	}
	return new PrototypicalPatternCase(principalFactors,
		filteredSupportingFactors, resultSet);
    }

    public List<SGNominalSelector> getSubgroupFactors(
	    boolean doCombineSelectorForCommonAttribute) {
	List<SGSelector> principalFactors = VKMUtil.asList(subgroup
		.getSGDescription().iterator());
	List<SupportingFactor> supportingFactors = SGUtils
		.computeSupportingFactors(subgroup, relevantAttributes, false);
	List<SGNominalSelector> subgroupFactors = new LinkedList(
		principalFactors);
	for (SupportingFactor suppFactor : supportingFactors) {
	    if (suppFactor.isPositivelyCorrelated()) {
		subgroupFactors.add(suppFactor.getSelector());
	    }
	}
	if (doCombineSelectorForCommonAttribute) {
	    List<SGNominalSelector> combinedSubgroupFactors = combineSubgroupFactorSelectorsForCommonAttribute(subgroupFactors);
	    subgroupFactors = combinedSubgroupFactors;
	}
	return subgroupFactors;
    }

    /**
     * @param subgroupFactors
     * @return
     */
    private List<SGNominalSelector> combineSubgroupFactorSelectorsForCommonAttribute(
	    List<SGNominalSelector> subgroupFactors) {
	List<SGNominalSelector> combinedSubgroupFactors = new LinkedList<SGNominalSelector>();
	Map<Attribute, List<SGNominalSelector>> attributeToSelectorMap = new HashMap<Attribute, List<SGNominalSelector>>();
	for (SGNominalSelector sel : subgroupFactors) {
	    List<SGNominalSelector> selectorsForAttribute = attributeToSelectorMap
		    .get(sel.getAttribute());
	    if (selectorsForAttribute == null)
		selectorsForAttribute = new LinkedList<SGNominalSelector>();
	    selectorsForAttribute.add(sel);
	    attributeToSelectorMap.put(sel.getAttribute(),
		    selectorsForAttribute);
	}
	for (Entry<Attribute, List<SGNominalSelector>> entry : attributeToSelectorMap
		.entrySet()) {
	    Attribute attr = entry.getKey();
	    List<SGNominalSelector> selectorsForAttribute = entry.getValue();
	    if (selectorsForAttribute.size() > 1) {
		Set<Value> values = new HashSet<Value>();
		for (SGNominalSelector sel : selectorsForAttribute)
		    values.addAll(sel.getValues());
		SGNominalSelector factorSel = new DefaultSGSelector(attr,
			values);
		combinedSubgroupFactors.add(factorSel);
	    } else {
		combinedSubgroupFactors.add(selectorsForAttribute.get(0));
	    }
	}
	return combinedSubgroupFactors;
    }

    private List<Attribute> createAttributesForCBR() {
	List<Attribute> attributesForCBR = new LinkedList<Attribute>();
	for (Attribute attr : getRelevantNominalAttributesForCBR()) {
	    attributesForCBR.add(attr);
	}
	return attributesForCBR;
    }

    public CBRResultSet retrieveKMostSimilarCases(int k) {
	return new SimpleMatchingCBRComparator().retrieveKMostSimilarCases(k);
    }

    public void fillPrototypicalPatternCase(PrototypicalPatternCase expCase,
	    CBRResultSet resultset) {
	List<NominalAttribute> relevantNominalAttributes = getRelevantNominalAttributesForCBR();

	List<SGSelector> selectors = expCase
		.getUnionOfPrincipleAndSupportingFactors();
	for (SGSelector selector : selectors) {
	    if (relevantNominalAttributes.contains(selector.getAttribute()))
		relevantNominalAttributes.remove(selector.getAttribute());
	}

	List<SGNominalSelector> explicationCases = new LinkedList();
	for (NominalAttribute att : relevantNominalAttributes) {
	    Map<Value, Integer> countingMap = new HashMap();
	    for (CBRResult result : resultset.getResults()) {
		double value = result.getRetrievedCase().getValue(att);
		if (Value.isMissingValue(value)) {
		    continue;
		}
		Value val = att.getNominalValue((int) value);
		Integer counter = countingMap.get(val);
		if (counter == null) {
		    counter = Integer.valueOf(1);
		} else {
		    counter = Integer.valueOf(counter.intValue() + 1);
		}
		countingMap.put(val, counter);
	    }
	    Value mostFrequentValue = getMostFrequentValue(countingMap);
	    if (mostFrequentValue != null) {
		explicationCases.add(new DefaultSGSelector((att),
			mostFrequentValue));
	    }
	}
	expCase.setPrototypicalPatternCases(explicationCases);
    }

    private Value getMostFrequentValue(Map<Value, Integer> countingMap) {
	int count = 0;
	Value result = null;
	for (Entry<Value, Integer> entry : countingMap.entrySet()) {
	    Value val = entry.getKey();
	    if (result == null) {
		result = val;
	    }
	    int countOfVal = entry.getValue().intValue();
	    if (countOfVal > count) {
		count = countOfVal;
		result = val;
	    }
	}
	return result;
    }

    public CBRResultSet retrieveKMostDiverseOf2KSimilarCases(int k) {
	CBRComparator comparator = createDefaultCBRComparator();
	CBRResultSet mostDiverseCases = new CBRResultSet(null);
	CBRResultSet mostSimilarCases = retrieveKMostSimilarCases(2 * k);
	CBRResult mostSimilarCase = mostSimilarCases.getResult(0);
	mostDiverseCases.addResult(mostSimilarCase);
	List<CBRResult> availableCBRResults = new ArrayList<CBRResult>();
	availableCBRResults.addAll(mostSimilarCases.getResults());
	availableCBRResults.remove(mostSimilarCase);
	for (int i = 1; i < k; i++) {
	    CBRResult mostDiverseCase = getMostDiverseCase(availableCBRResults,
		    mostDiverseCases.getResults(), comparator);
	    if (mostDiverseCase == null) {
		break;
	    }
	    mostDiverseCases.addResult(mostDiverseCase);
	    availableCBRResults.remove(mostDiverseCase);
	    if (availableCBRResults.isEmpty()) {
		break;
	    }
	}
	return mostDiverseCases;
    }

    private CBRComparator createDefaultCBRComparator() {
	CBRComparator comparator = new CBRComparator(ontology);
	comparator.setUseKnowledge(false);
	comparator.setCompareMode(CompareMode.RETRIEVE_CASE_FILL_UNKNOWN);
	comparator.setAttributesForCBR(createAttributesForCBR());
	return comparator;
    }

    private CBRResult getMostDiverseCase(List<CBRResult> mostSimilarCases,
	    List<CBRResult> alreadySelectedDiverseCases,
	    CBRComparator comparator) {
	CBRResult mostDiverseCase = null;
	double highestDiversity = 0;
	for (CBRResult eachSimilarCase : mostSimilarCases) {
	    if (mostDiverseCase == null) {
		mostDiverseCase = eachSimilarCase;
	    } else {
		double relativeDiversity = getRelativeDiversity(
			eachSimilarCase, alreadySelectedDiverseCases,
			comparator);
		relativeDiversity *= eachSimilarCase.getSimilarity();
		if (highestDiversity < relativeDiversity) {
		    mostDiverseCase = eachSimilarCase;
		    highestDiversity = relativeDiversity;
		}
	    }
	}
	return mostDiverseCase;
    }

    private double getRelativeDiversity(CBRResult theCase,
	    List<CBRResult> alreadySelectedCases, CBRComparator comparator) {
	double summedDiversity = 0;
	for (CBRResult eachSelectedCase : alreadySelectedCases) {
	    summedDiversity = 1 - comparator
		    .calculateSimilarityBetweenQueryCaseAndRetrievedCase(
			    theCase.getRetrievedCase(),
			    eachSelectedCase.getRetrievedCase());
	}
	return summedDiversity / alreadySelectedCases.size();
    }
}
