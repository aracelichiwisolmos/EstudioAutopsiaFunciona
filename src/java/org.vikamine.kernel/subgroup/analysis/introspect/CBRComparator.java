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

package org.vikamine.kernel.subgroup.analysis.introspect;

import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;

/**
 * Case-based reasoning like data record comparator. Implements D3-like case
 * (record) comparison, see Puppe, 1991.
 * 
 * @author atzmueller
 */
public class CBRComparator {

    private double unknownValueFactor = 0.1;

    private CompareMode compareMode;

    private boolean useKnowledge = true;

    private Ontology ontology;

    private List attributesForCBR;

    public CBRComparator(Ontology ontology) {
	this.ontology = ontology;
    }

    public CBRResultSet compareQueryCaseWithAllCases(DataRecord queryCase) {
	CBRResultSet resultSet = new CBRResultSet(queryCase);

	for (Iterator<DataRecord> iter = ontology.getDataView()
		.instanceIterator(); iter.hasNext();) {
	    DataRecord retrieveCase = iter.next();
	    if (!queryCase.equals(retrieveCase)) {
		resultSet.addResult(new CBRResult(queryCase, retrieveCase,
			calculateSimilarityBetweenQueryCaseAndRetrievedCase(
				queryCase, retrieveCase)));
	    }
	}
	return resultSet;
    }

    public CBRResultSet compareQueryCaseWithRetrievedCases(
	    DataRecord queryCase, List<DataRecord> retrievedCases) {
	CBRResultSet resultSet = new CBRResultSet(queryCase);
	for (Iterator<DataRecord> iter = retrievedCases.iterator(); iter
		.hasNext();) {
	    DataRecord retrievedCase = iter.next();
	    if (!queryCase.equals(retrievedCase)) {
		resultSet.addResult(new CBRResult(queryCase, retrievedCase,
			calculateSimilarityBetweenQueryCaseAndRetrievedCase(
				queryCase, retrievedCase)));
	    }
	}
	return resultSet;
    }

    public double calculateSimilarityBetweenQueryCaseAndRetrievedCase(
	    DataRecord queryCase, DataRecord retrievedCase) {
	double value = 0;
	double weightCount = 0;

	for (Iterator attributeIter = attributesForCBR.iterator(); attributeIter
		.hasNext();) {
	    Attribute currentAttribute = (Attribute) attributeIter.next();

	    Attribute evaluatableAtt = currentAttribute;
	    double queryCaseValue = queryCase.getValue(evaluatableAtt);
	    double retrievedCaseValue = retrievedCase.getValue(evaluatableAtt);

	    if ((isKnownValue(currentAttribute, queryCaseValue))
		    || (isKnownValue(currentAttribute, retrievedCaseValue))) {
		if (isKnownValue(currentAttribute, queryCaseValue)
			&& isKnownValue(currentAttribute, retrievedCaseValue)) {
		    // both answers defined (not missing and not MaU)
		    if (currentAttribute.isNumeric()) {

			value += questionComparatorNumDivision(queryCaseValue,
				retrievedCaseValue);

			weightCount++;
		    } else if (currentAttribute.isNominal()
			    || currentAttribute.isString()) {
			value += questionComparatorOCIndividualYN(
				queryCaseValue, retrievedCaseValue);
			weightCount++;
		    }
		} else {
		    if (((compareMode.equals(CompareMode.BOTH_FILL_UNKNOWN) || (compareMode
			    .equals(CompareMode.QUERY_CASE_FILL_UNKNOWN))) && (!isKnownValue(
			    currentAttribute, queryCaseValue)))
			    || ((compareMode
				    .equals(CompareMode.BOTH_FILL_UNKNOWN) || (compareMode
				    .equals(CompareMode.RETRIEVE_CASE_FILL_UNKNOWN))) && (!isKnownValue(
				    currentAttribute, retrievedCaseValue)))) {

			value += 0;
			weightCount += unknownValueFactor;
		    }
		}
	    }
	}

	return value / weightCount;
    }

    private double questionComparatorNumDivision(double queryCaseValue,
	    double retrievedCaseValue) {
	if (queryCaseValue > retrievedCaseValue)
	    return retrievedCaseValue / queryCaseValue;
	else
	    return (queryCaseValue / retrievedCaseValue);
    }

    private double questionComparatorOCIndividualYN(double queryCaseValue,
	    double retrievedCaseValue) {
	if (queryCaseValue == retrievedCaseValue) {
	    return 1;
	} else {
	    return 0;
	}
    }

    /**
     * @return Returns the compareMode.
     */
    public CompareMode getCompareMode() {
	return compareMode;
    }

    /**
     * @param compareMode
     *            The compareMode to set.
     */
    public void setCompareMode(CompareMode compareMode) {
	this.compareMode = compareMode;
    }

    /**
     * @return Returns the unknown_similarity.
     */
    public double getUnknownValueFactor() {
	return unknownValueFactor;
    }

    /**
     * @param unknown_similarity
     *            The unknown_similarity to set.
     */
    public void setUnknownValueFactor(double unknown_similarity) {
	this.unknownValueFactor = unknown_similarity;
    }

    /**
     * @return Returns the useKnowledge.
     */
    public boolean isUseKnowledge() {
	return useKnowledge;
    }

    /**
     * @param useKnowledge
     *            The useKnowledge to set.
     */
    public void setUseKnowledge(boolean useKnowledge) {
	this.useKnowledge = useKnowledge;
    }

    /**
     * @return Returns the ontology.
     */
    public Ontology getOntology() {
	return ontology;
    }

    /**
     * @return Returns the attributesForCBR.
     */
    public List getAttributesForCBR() {
	return attributesForCBR;
    }

    /**
     * @param attributesForCBR
     *            The attributesForCBR to set.
     */
    public void setAttributesForCBR(List attributesForCBR) {
	this.attributesForCBR = attributesForCBR;
    }

    public boolean isKnownValue(Attribute attribute, double value) {
	if (Value.isMissingValue(value))
	    return false;
	else if (attribute.isDate())
	    return true;
	else if (attribute.isNumeric()) {
	    return (value != Value.CONST_NUM_UNKNOWN);
	} else {
	    NominalAttribute att = (NominalAttribute) attribute;
	    return (!Value.isUnknownValue(att.getNominalValue((int) value)));
	}
    }
}
