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

package org.vikamine.kernel.xpdl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.data.creators.DataFactory;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * @author Manuel class to get the SGSet from a given Patternset XML-File
 */
public class PatternsetToSGSet {

    private final Document doc;
    private Ontology ont;

    /**
     * Workaround for Restlet-Plugin, Placement of parameters changed
     * 
     * @param patternSet
     * @param datasetName
     * @param prov
     */
    public PatternsetToSGSet(DatasetProvider prov, File patternSet) {
	doc = this.buildDoc(patternSet);
	String datasetName = doc.getRootElement().getAttributeValue("dataset");
	datasetName = datasetName.substring(datasetName.indexOf("object=") + 7);
	datasetName = datasetName.replace("]", "");
	if (prov.providesDataset(datasetName)) {
	    this.ont = prov.getDataset(datasetName);
	} else
	    throw new XMLException("Dataset does not exist!");
    }

    /**
     * 
     * @param patternSet
     *            the xml-File that contains the patternset
     * @param repositoryLocation
     *            location of the dataSets
     * @throws IOException
     */
    public PatternsetToSGSet(File patternSet, String repositoryLocation)
	    throws IOException {
	doc = this.buildDoc(patternSet);
	String fileName = repositoryLocation + this.getDatasetName();
	File file = new File(fileName);
	if (!file.exists()) {
	    throw new IllegalArgumentException("data file does not exist!");
	}
	this.ont = DataFactory.createOntology(file);
	this.getSet();
    }

    public PatternsetToSGSet(File patternSet, DatasetProvider prov) {
	doc = this.buildDoc(patternSet);
	if (prov.providesDataset(this.getDatasetName())) {
	    this.ont = prov.getDataset(this.getDatasetName());
	} else
	    throw new XMLException("Dataset does not exist!");
    }

    private List<Element> getSGs() {
	return doc.getRootElement().getChildren("subgroupPattern");
    }

    private IDataRecordSet getDataset() {
	return ont.getDataView().dataset();
    }

    private SGTarget getTarget(Element sg) {
	Element target = sg.getChild("target");
	DefaultSGSelector sel = null;
	String targetType = target.getAttributeValue("type");
	Element EAttribute = this.getAttributes(target).get(0);
	if (this.getDataset()
		.getAttribute(EAttribute.getAttributeValue("name")) == null) {
	    throw new XMLException("Attribute "
		    + EAttribute.getAttributeValue("name") + " doesn't exist");
	}
	if (targetType.equals("boolean")) {
	    Attribute attribute = getDataset().getAttribute(
		    EAttribute.getAttributeValue("name"));
	    if (attribute instanceof NominalAttribute) {
		NominalAttribute nominalAttribute = (NominalAttribute) attribute;
		for (Iterator<Value> it = nominalAttribute.allValuesIterator(); it
			.hasNext();) {
		    Value val = it.next();
		    for (Element k : this.getValues(EAttribute)) {
			if (val.getDescription().equals(k.getValue())) {
			    sel = new DefaultSGSelector(attribute, val);
			}
		    }
		}
	    } else {
		throw new IllegalStateException("not a nominal attribute!");
	    }

	    return new SelectorTarget(sel);
	} else {
	    Attribute att = getDataset().getAttribute(
		    EAttribute.getAttributeValue("name"));
	    if (att.isNumeric()) {
		NumericAttribute nAtt = (NumericAttribute) att;
		return new NumericTarget(nAtt);
	    } else {
		throw new IllegalStateException("not a numeric attribute!");
	    }
	}
    }

    private String getDatasetName() {
	return doc.getRootElement().getAttributeValue("dataset");
    }

    private Element getRestrictions() {
	return doc.getRootElement().getChild("dataset")
		.getChild("restrictions");
    }

    private Iterator<Value> getValueIterator(Attribute attribute) {
	if (attribute instanceof NominalAttribute) {
	    return ((NominalAttribute) attribute).allValuesIterator();
	} else {
	    return attribute.usedValuesIterator(getDataset());
	}
    }

    private void setPopulation() {
	DefaultSGSelector sel = null;
	List<SGSelector> selectors = new ArrayList<SGSelector>();
	if (this.getRestrictions() != null) {
	    for (Element a : this.getAttributes(this.getRestrictions())) {
		Set<Value> values = new HashSet<Value>();
		if (getDataset().getAttribute(a.getAttributeValue("name")) == null) {
		    throw new XMLException("Attribute "
			    + a.getAttributeValue("name") + " does not exist");
		}
		for (Iterator<Value> it = getValueIterator(this.ont
			.getAttribute(a.getAttributeValue("name"))); it
			.hasNext();) {
		    Value val = it.next();
		    for (Element k : this.getValues(a)) {
			if (k.getName().equals("includeValue")) {
			    if (val.getDescription().equals(k.getValue())) {
				values.add(val);
			    }
			} else {
			    if (!val.getDescription().equals(k.getValue())) {
				values.add(val);
			    }
			}
		    }
		}
		sel = new DefaultSGSelector(getDataset().getAttribute(
			a.getAttributeValue("name")), values);
		selectors.add(sel);
	    }
	    this.ont.refinePopulation(selectors);
	}
    }

    private Element getDescription(Element sg) {
	return sg.getChild("description");
    }

    public SGSet getSet() {
	this.setPopulation();
	SGSet set = SGSets.createSGSet();
	for (Element e : this.getSGs()) {
	    SG sg = new SG(this.ont.getDataView(), this.getTarget(e));
	    DefaultSGSelector sel = null;
	    Element description = this.getDescription(e);
	    if (description != null) {
		for (Element a : this.getAttributes(description
			.getChild("operator"))) {
		    Set<Value> values = new HashSet<Value>();
		    if (getDataset().getAttribute(a.getAttributeValue("name")) == null) {
			throw new XMLException("Attribute "
				+ a.getAttributeValue("name")
				+ " doesn't exist");
		    }
		    for (Iterator<Value> it = getValueIterator(getDataset()
			    .getAttribute(a.getAttributeValue("name"))); it
			    .hasNext();) {
			Value val = it.next();
			for (Element k : this.getValues(a)) {
			    if (k.getName().equals("includeValue")) {
				if (val.getDescription().equals(k.getValue())) {
				    values.add(val);
				}
			    } else {
				if (!val.getDescription().equals(k.getValue())) {
				    values.add(val);
				}
			    }
			}
		    }
		    sel = new DefaultSGSelector(getDataset().getAttribute(
			    a.getAttributeValue("name")), values);

		    sg.getSGDescription().add(sel);
		}
		sg.createStatistics(null);
	    } else {
		sg.createStatistics(null);
	    }
	    set.add(sg);
	}
	return set;
    }

    @SuppressWarnings("unchecked")
    private List<Element> getAttributes(Element t) {
	return t.getChildren("attribute");
    }

    @SuppressWarnings("unchecked")
    private List<Element> getValues(Element attribute) {
	return attribute.getChildren();
    }

    private Document buildDoc(File toBuild) {
	SAXBuilder sax = new SAXBuilder();
	try {
	    return sax.build(toBuild);
	} catch (JDOMException e) {
	    throw new XMLException("Eingabe-datei nicht valide");
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	    return null;
	}
    }
}
