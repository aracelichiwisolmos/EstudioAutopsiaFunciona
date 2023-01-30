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

package org.vikamine.kernel.data.creators;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeBuilder;
import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.FullDataRecord;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.util.ProgressBarPopupUpdater;

/**
 * {@link DTPDataOntologyCreator} builds an {@link Ontology} given a
 * {@link ResultSet}.
 * 
 * @author lemmerich, atzmueller
 * 
 */
public class DTPDataOntologyCreator extends AbstractOntologyCreator {

    private static final String MISSING_VALUE_STRING = "?";

    public DTPDataOntologyCreator(ResultSet data, String name)
	    throws SQLException {
	if (data != null)
	    dataset = loadDataset(data, name);
	else
	    throw new IllegalArgumentException("data must not be null!");
    }

    private DataRecordSet loadDataset(ResultSet data, String dataSetName)
	    throws SQLException {
	// Header
	ArrayList<String> header = new ArrayList<String>();
	ResultSetMetaData metaData = data.getMetaData();
	for (int i = 1; i <= metaData.getColumnCount(); i++) {
	    header.add(metaData.getColumnName(i));
	}

	// determine attribute types
	boolean[] isAttributeNominal = new boolean[header.size()];
	int rowSize = 0;
	while (data.next()) {
	    ++rowSize;
	    for (int attributeIndex = 0; attributeIndex < header.size(); attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		if (!isAttributeNominal[attributeIndex]) {
		    Object attributeValue = data.getObject(attributeIndex + 1);

		    if (attributeValue instanceof String) {
			String castedValue = (String) attributeValue;

			if (!castedValue.equals(MISSING_VALUE_STRING)) {
			    isAttributeNominal[attributeIndex] = true;
			}
		    }
		}
	    }
	}
	data.beforeFirst();

	// determine domain of nominal values
	// (kept separate from the code above to ensure readability)
	List<List<String>> domains = new ArrayList<List<String>>();
	for (int i = 0; i < header.size(); i++) {
	    domains.add(new ArrayList<String>());
	}

	while (data.next()) {
	    for (int attributeIndex = 0; attributeIndex < header.size(); attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		if (isAttributeNominal[attributeIndex]) {
		    Object attributeValue = data.getObject(attributeIndex + 1);

		    String castedValue;
		    if (attributeValue instanceof String) {
			castedValue = (String) attributeValue;
		    } else {
			castedValue = attributeValue.toString();
		    }

		    if (!castedValue.equals(MISSING_VALUE_STRING)) {
			List<String> tempDomain = domains.get(attributeIndex);
			if (!tempDomain.contains(castedValue)) {
			    tempDomain.add(castedValue);
			}
		    }
		}
	    }
	}
	// build data record set
	// - build attributes
	List<Attribute> attributes = new ArrayList<Attribute>(header.size());
	AttributeBuilder attBuilder = new AttributeBuilder();

	for (int attributeIndex = 0; attributeIndex < header.size(); attributeIndex++) {
	    ProgressBarPopupUpdater.updateProgress();
	    String attributeName = header.get(attributeIndex);

	    if (isAttributeNominal[attributeIndex]) {
		attBuilder.buildNominalAttribute(attributeName, domains
			.get(attributeIndex));
		attBuilder.buildNominalValues();
	    } else {
		attBuilder.buildNumericAttribute(attributeName);
	    }

	    attributes.add(attBuilder.getAttribute());
	}

	// - create the data record set
	DataRecordSet dataSet = new DataRecordSet(dataSetName, attributes,
		rowSize);

	// - fill data record set
	data.beforeFirst();
	while (data.next()) {
	    // List parsedLine = parsedLines.get(lineIndex);
	    double[] values = new double[dataSet.getNumAttributes()];

	    for (int attributeIndex = 0; attributeIndex < header.size(); attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		Object value = data.getObject(attributeIndex + 1);

		if (isAttributeNominal[attributeIndex]) {
		    String castedValue;
		    if (value instanceof String)
			castedValue = (String) value;
		    else
			castedValue = value.toString();

		    if (castedValue.equals(MISSING_VALUE_STRING)) {
			values[attributeIndex] = Value.missingValue();
		    } else {
			List<String> domain = domains.get(attributeIndex);
			int index = domain.indexOf(castedValue);
			values[attributeIndex] = index;
		    }
		} else if (value instanceof Double) {
		    values[attributeIndex] = (Double) value;
		} else {
		    // does not happen due to previous processing
		}
	    }
	    dataSet.add(new FullDataRecord(1.0, values));
	}

	// done!
	return dataSet;
    }

    @Override
    public Ontology createOntology() {
	if (dataset == null) {
	    throw new IllegalStateException("Dataset is null!");
	}
	Ontology ontology = new Ontology(dataset);
	createDatasetIDs();
	copyAttributesToOntology(ontology);

	ontology.getTripleStore().addStatement(
		new RDFStatement(ontology,
			OntologyConstants.DATASET_FILETYPE_PROPERTY, "dtp"));

	return ontology;
    }

}
