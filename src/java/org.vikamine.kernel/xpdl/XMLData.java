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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * converts elements from XML to data structure
 * 
 * @author Manuel Lutz
 * 
 */
public class XMLData {

    private Document doc;

    public XMLData(Document doc) {
	this.doc = doc;
    }

    public XMLData(File file) throws IOException {
	if (!file.exists()) {
	    throw new FileNotFoundException(
		    "File for loading mining task not found: "
			    + file.getAbsolutePath());
	}
	doc = this.buildDoc(file);
    }

    public XMLData(InputStream stream) {
	doc = this.buildDoc(stream);
    }

    public String getMiningId() {
	return doc.getRootElement().getAttributeValue("id");
    }

    public MDataset getDataset() {
	Element elem = doc.getRootElement().getChild("dataset");
	MRestrictions res = new MRestrictions(this.getAttributes(elem
		.getChild("restrictions")));
	MDataset data = new MDataset(elem.getAttributeValue("name"), res);
	return data;
    }

    public MInitialSubgroup getInitialSubgroup() {
	Element elem = doc.getRootElement().getChild("initialSubgroup");
	if (elem != null) {
	    MOperator op = new MOperator("and", this.getAttributes(elem
		    .getChild("sgDescription").getChild("operator")));
	    return new MInitialSubgroup(op);
	}
	return null;
    }

    public MTarget getTarget() {
	Element target = doc.getRootElement().getChild("target");
	if (!(this.getAttributes(target).size() > 1)) {
	    MTarget data = new MTarget(target.getAttributeValue("type"), this
		    .getAttributes(target).get(0));
	    return data;
	} else
	    throw new XMLException("Target contains more than 1 Attribute");
    }

    public MSearchSpace getSearchSpace() {
	Element search = doc.getDocument().getRootElement()
		.getChild("searchSpace");

	// read "useAllNominal"
	boolean useAllNominal = false;
	Attribute useAllNominalString = search.getAttribute("useAllNominal");
	if (useAllNominalString != null) {
	    try {
		useAllNominal = useAllNominalString.getBooleanValue();
	    } catch (DataConversionException e) {
		Logger.getLogger(this.getClass().getName())
			.log(Level.WARNING,
				"could not convert Attribute value of limit Selectors to boolean, using default (false)");
	    }
	}

	// read "useAll"
	boolean useAll = false;
	Attribute useAllString = search.getAttribute("useAll");
	if (useAllString != null) {
	    try {
		useAll = useAllString.getBooleanValue();
	    } catch (DataConversionException e) {
		Logger.getLogger(this.getClass().getName())
			.log(Level.WARNING,
				"could not convert Attribute value of limit Selectors to boolean, using default (false)");
	    }
	}

	int limitSelectors;
	Attribute limitSelectorsAtt = search.getAttribute("limitSelectors");
	if (limitSelectorsAtt == null) {
	    limitSelectors = Integer.MAX_VALUE;
	} else {
	    try {
		limitSelectors = limitSelectorsAtt.getIntValue();
	    } catch (DataConversionException e) {
		// Logger
		// .getLogger(this.getClass().getName())
		// .log(Level.WARNING,
		// "could not convert Attribute value of limit Selectors to int, using default");
		limitSelectors = Integer.MAX_VALUE;
	    }
	}

	int[] metaLimitSelectors = null;
	if (limitSelectorsAtt == null) {
	    metaLimitSelectors = new int[] { Integer.MAX_VALUE };
	} else {
	    try {
		String metaLimitSelectorsString = limitSelectorsAtt.getValue();
		metaLimitSelectorsString = metaLimitSelectorsString.replaceAll(
			" ", "");
		String[] metaLimitsStrings = metaLimitSelectorsString
			.split(",");
		metaLimitSelectors = new int[metaLimitsStrings.length];
		int i = 0;
		for (String s : metaLimitsStrings) {
		    metaLimitSelectors[i++] = Integer.parseInt(s);
		}
	    } catch (NumberFormatException e) {
		Logger.getLogger(this.getClass().getName())
			.log(Level.WARNING,
				"could not convert Attribute value of limit Selectors to int [], using default");
		metaLimitSelectors = new int[] { Integer.MAX_VALUE };
	    }
	}

	return new MSearchSpace(this.getAttributes(search), useAll,
		useAllNominal, limitSelectors, metaLimitSelectors);
    }

    public MQualityFunction getQualityFunction() {
	Element func = doc.getRootElement().getChild("qualityFunction");
	try {
	    return new MQualityFunction(func.getAttributeValue("name"),
		    Boolean.parseBoolean(func.getAttributeValue("invert")));
	} catch (IllegalArgumentException e) {
	    throw new XMLException("Invalid quality function!");
	}
    }

    public MMethod getMethod() {
	return new MMethod(doc.getRootElement().getChild("method")
		.getAttributeValue("name"));
    }

    public MConstraints getMConstraints() {
	Element constraints = doc.getRootElement().getChild("constraints");
	return new MConstraints(this.getContraints(constraints));
    }

    @SuppressWarnings("unchecked")
    private ArrayList<IConstraint> getContraints(Element t) {
	ArrayList<IConstraint> temp = new ArrayList<IConstraint>();
	for (Element l : (List<Element>) t.getChildren()) {
	    IConstraint Constraint = MConstraintBuilder.build(
		    l.getAttributeValue("value"), l.getAttributeValue("name"));
	    temp.add(Constraint);
	}
	return temp;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<MAttribute> getAttributes(Element t) {
	ArrayList<MAttribute> attributes = new ArrayList<MAttribute>();
	for (Element e : (List<Element>) t.getChildren("attribute")) {
	    final Attribute discretizationMethod = e
		    .getAttribute("discretizationMethod");
	    MAttribute att;
	    if (discretizationMethod == null)
		att = new MAttribute(e.getAttribute("name").getValue());
	    else
		att = new MAttribute(e.getAttribute("name").getValue(),
			discretizationMethod.getValue());
	    List<Element> values = e.getChildren("includeValue");
	    for (Element f : values) {
		MValue value = new MValue(ETyp.include, f.getText());
		att.addValue(value);
	    }
	    values = e.getChildren("excludeValue");
	    for (Element f : values) {
		MValue value = new MValue(ETyp.exclude, f.getText());
		att.addValue(value);
	    }
	    attributes.add(att);
	}
	return attributes;
    }

    private SAXBuilder preBuild() {
	Properties properties = new Properties();
	InputStream stream;
	try {
	    // try {
	    stream = getClass().getResourceAsStream(
		    "/resources/xml/xsd.properties");
	    // stream = new FileInputStream(CheckXMLValid.class.getClass()
	    // .getResource("/resources/xml/xsd.properties").toURI()
	    // .getPath());
	    properties.load(stream);
	    stream.close();
	    // } catch (URISyntaxException e) {
	    // e.printStackTrace();
	    // }
	} catch (FileNotFoundException e) {
	    System.out.println("Properties File not found");
	    System.out.println(e.getMessage());
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	}
	// String schemaLocation = CheckXMLValid.class.getClass().getResource(
	// "/resources/xml/")
	// + properties.getProperty("inputXsd");
	// SAXBuilder sax = new SAXBuilder(true);
	// sax
	// .setFeature("http://apache.org/xml/features/validation/schema",
	// true);
	// sax
	// .setProperty(
	// "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
	// schemaLocation);
	SAXBuilder sax = new SAXBuilder();
	return sax;
    }

    private Document buildDoc(File toBuild) throws IOException {
	SAXBuilder sax = preBuild();
	try {
	    return sax.build(toBuild);
	} catch (JDOMException e) {
	    e.printStackTrace();
	    throw new XMLException("Eingabe-datei nicht valide");
	}
    }

    private Document buildDoc(InputStream stream) {
	SAXBuilder sax = preBuild();
	try {
	    return sax.build(stream);
	} catch (JDOMException e) {
	    e.printStackTrace();
	    throw new XMLException("Eingabe-datei nicht valide");
	} catch (IOException e) {
	    new XMLException("Fehler beim Lesen der Eingabedatei");
	    return null;
	}
    }

    public String getContent() {
	XMLOutputter xmlout = new XMLOutputter();
	String xml = xmlout.outputString(doc);
	return xml;
    }

    public void setDoc(Document doc) {
	this.doc = doc;
    }

    public Document getDoc() {
	return this.doc;
    }
}
