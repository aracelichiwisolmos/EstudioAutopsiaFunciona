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
 * Created on 30.09.2003
 * 
 */
package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.VKMUtil;

/**
 * {@link Ontology} is used for storing information about a dataset and for
 * managing its meta information.
 * 
 * @author Atzmueller, lemmerich
 */
public class Ontology implements AttributeProvider {

    private IDataRecordSet dataset = null;

    private DataView population;

    private LinkedHashMap<String, Attribute> attributes;

    // the user defined attributes are always a subset of the attributes list!
    private final List<Attribute> userDefinedAttributes;

    private final RDFTripleStore tripleStore;

    public static Ontology createEmptyOntology() {
	Ontology o = new Ontology();
	o.population = new DataView(new DataRecordSet("",
		Collections.EMPTY_LIST, 0), Collections.EMPTY_LIST);
	return o;
    }

    /**
     * Initializes some variables.
     */
    protected Ontology() {
	super();
	this.attributes = new LinkedHashMap<String, Attribute>();
	this.userDefinedAttributes = new LinkedList<Attribute>();
	this.tripleStore = new RDFTripleStore();
    }

    public Ontology(DataRecordSet dataset) {
	this();
	if (dataset == null) {
	    IllegalStateException ex = new IllegalStateException("The dataset "
		    + dataset + " is null!");
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "Ontology", ex);
	    throw ex;
	}
	this.dataset = dataset;
	population = new DataView(dataset, Collections.EMPTY_LIST);
    }

    /**
     * the user defined attributes are always a subset of the attributes list!
     * 
     * @param att
     */
    public void addUserDefinedAttribute(Attribute att) {
	if (addAttribute(att))
	    userDefinedAttributes.add(att);
    }

    public Attribute findUserDefinedAttribute(String id) {
	for (Attribute att : userDefinedAttributes) {
	    if (att.getId().equals(id)) {
		return att;
	    }
	}
	return null;
    }

    public boolean addAttribute(Attribute att) {
	if (!attributes.containsKey(att.getId())) {
	    attributes.put(att.getId(), att);
	    return true;
	}
	return false;
    }

    /**
     * get the Attribute corresponding to the id Note: this method takes both
     * the (ontological) DMAttributes and the user defined DMAttributes into
     * account!
     */
    @Override
    public Attribute getAttribute(String id) {
	Attribute attribute = attributes.get(id);
	if (attribute != null) {
	    return attribute;
	} else {
	    return findUserDefinedAttribute(id);
	}
    }

    /**
     * Returns A new sorted Set of all attributes contained in this ontology,
     * i.e., the order of the elements is retained.
     * 
     * @return A new sorted Set of all attributes contained in this ontology,
     *         i.e., the order of the elements is retained.
     */
    public LinkedHashSet<Attribute> getAttributes() {
	return new LinkedHashSet<Attribute>(attributes.values());
    }

    public Attribute getAttribute(int index) {
	int i = 0;
	for (Attribute att : attributes.values()) {
	    if (i == index)
		return att;
	    i++;
	}
	throw new ArrayIndexOutOfBoundsException();
    }

    public IDataRecordSet getDataset() {
	return dataset;
    }

    @Override
    public int getNumAttributes() {
	return getAttributes().size();
    }
    
    public int getNumInstances() {
	return getDataset().getNumInstances();
    }

    /**
     * @return Returns the population.
     */
    public DataView getDataView() {
	return population;
    }

    public List<Attribute> getUserDefinedAttributes() {
	return userDefinedAttributes;
    }

    public void refinePopulation(List<SGSelector> populationRangesSelectors) {
	population = new DataView(dataset, populationRangesSelectors);
    }

    public boolean removeUserDefinedAttribute(Attribute att) {
	return userDefinedAttributes.remove(att) & removeAttribute(att);
    }

    public boolean removeAttribute(Attribute att) {
	if (getUserDefinedAttributes().contains(att)) {
	    removeUserDefinedAttribute(att);
	}
	return attributes.remove(att.getId()) != null;
    }

    public void setAttributes(List<Attribute> dmAttributes) {
	attributes = new LinkedHashMap<String, Attribute>(dmAttributes.size());
	for (Attribute attribute : dmAttributes)
	    attributes.put(attribute.getId(), attribute);
    }

    public RDFTripleStore getTripleStore() {
	return tripleStore;
    }

    /**
     * Returns a string representation of the ontology in the ARFF format.
     * Attributes are sorted alphabetically. Use
     * {@link Ontology#printAttributes(Collection)} if you want to specify
     * Attributes you want to have exported.
     * 
     * @return String representation of the ontology in the ARFF format.
     */
    public String printAllAttributes() {
	Comparator<Attribute> attComp = new Comparator<Attribute>() {

	    @Override
	    public int compare(Attribute o1, Attribute o2) {
		return o1.getId().compareToIgnoreCase(o2.getId());
	    }
	};

	List<Attribute> attList = new ArrayList<Attribute>(getAttributes());
	Collections.sort(attList, attComp);

	return printAttributes(attList);
    }

    /**
     * Returns a string representation of the ontology in the ARFF format.
     * 
     * @param attList
     *            The attributes to be exported
     * 
     * @return String representation of the ontology in the ARFF format
     */
    public String printAttributes(Collection<Attribute> attList) {
	StringBuilder buddy = new StringBuilder();

	buddy.append("@relation ")
		.append(org.vikamine.kernel.util.VKMUtil.quote(dataset
			.getRelationName())).append("\n\n");

	for (Attribute att : attList) {
	    buddy.append(att).append("\n");
	}

	buddy.append("\n@data\n");

	for (int i = 0; i < dataset.getNumInstances(); i++) {
	    int counter = 0;

	    for (Attribute att : attList) {
		DataRecord record = dataset.get(i);

		if (counter != 0) {
		    buddy.append(",");
		}

		if (record.isMissing(att)) {
		    buddy.append('?');
		} else if (att.isNumeric()) {
		    buddy.append(record.getValue(att));
		} else {
		    buddy.append(VKMUtil.quote(record.getStringValue(att)));
		}

		counter++;
	    }
	    buddy.append("\n");
	}

	return buddy.toString();
    }

    public void setDataset(DataRecordSet dataset) {
	this.dataset = dataset;
    }

    public String getDatasetName() {
	RDFStatement datasetNameStatement = getTripleStore()
		.getFirstMatchingStatement(RDFTripleStore.ANY_OBJECT,
			OntologyConstants.DATASET_FILE_NAME_PROPERTY,
			RDFTripleStore.ANY_OBJECT);
	String datasetName = "unknown";
	if (datasetNameStatement != null) {
	    datasetName = (String) datasetNameStatement.getObject();
	}
	return datasetName;
    }

}
