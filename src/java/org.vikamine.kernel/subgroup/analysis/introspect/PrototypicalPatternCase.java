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
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SupportingFactor;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

/**
 * 
 * {@link PrototypicalPatternCase} captures a set of cases, as a representative
 * of the contained cases, the principle and supporting factors, for
 * exemplifying a subgroup {@link SG}.
 * 
 * @author atzmueller
 * 
 */
public class PrototypicalPatternCase {

    private final List<SGSelector> principleFactors;

    private final List<SupportingFactor> supportingFactors;

    private List<SGNominalSelector> prototypicalPatternCases;

    private final CBRResultSet resultSet;

    public PrototypicalPatternCase(List<SGSelector> principleFactors,
	    List<SupportingFactor> supportingFactors, CBRResultSet resultSet) {
	this.principleFactors = principleFactors;
	this.supportingFactors = supportingFactors;
	this.resultSet = resultSet;
    }

    public List<SGNominalSelector> getPrototypicalPatternCases() {
	return prototypicalPatternCases;
    }

    public void setPrototypicalPatternCases(
	    List<SGNominalSelector> explicationCases) {
	this.prototypicalPatternCases = explicationCases;
    }

    public List<SGSelector> getPrincipalFactors() {
	return principleFactors;
    }

    public List<SupportingFactor> getSupportingFactors() {
	return supportingFactors;
    }

    public List<DataRecord> getCasesToSelector(SGNominalSelector selector,
	    Ontology ontology) {
	List records = new LinkedList();
	if (selector != null) {
	    for (Iterator<CBRResult> iter = resultSet.getResults().iterator(); iter
		    .hasNext();) {
		CBRResult result = iter.next();
		if (selector.isContainedInInstance(result.getRetrievedCase())) {
		    long theCaseID = result.getRetrievedCase().getID();
		    DataRecord theCase = searchForDataRecordWithID(theCaseID,
			    ontology);
		    records.add(theCase);
		}

	    }
	}
	return records;
    }

    private DataRecord searchForDataRecordWithID(long id, Ontology ontology) {
	for (DataRecord record : ontology.getDataset()) {
	    if (record.getID() == id)
		return record;
	}
	return null;
    }

    public List<SGSelector> getUnionOfPrincipleAndSupportingFactors() {
	List<SGSelector> union = new LinkedList<SGSelector>();
	union.addAll(principleFactors);
	for (SupportingFactor supportingFactor : supportingFactors) {
	    union.add(supportingFactor.getSelector());
	}
	return union;
    }

}
