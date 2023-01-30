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
 * Created on 28.11.2004
 *
 */
package org.vikamine.kernel.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.Options.Option;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanFormulaTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Base class for XML persistence (reader); is extended by applications.
 * 
 * @author atzmueller
 * 
 */
public abstract class XMLPersistenceReader {

    private Ontology ontology;

    public XMLPersistenceReader(Ontology onto) {
	super();
	ontology = onto;
    }

    public Document readXML(File theFile) throws IOException {
	InputStream in = new FileInputStream(theFile);
	try {
	    return readXML(in);
	} finally {
	    in.close();
	}
    }

    private Document parseXml(InputStream in) throws FactoryConfigurationError,
	    ParserConfigurationException, SAXException, IOException {
	DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = fac.newDocumentBuilder();
	return builder.parse(in);
    }

    public Document readXML(InputStream in) throws IOException {
	try {
	    Document doc = parseXml(in);
	    processDocument(doc);
	    return doc;
	} catch (FactoryConfigurationError e) {
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "readXML", ex);
	    throw ex;
	} catch (ParserConfigurationException e) {
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "readXML", ex);
	    throw ex;
	} catch (SAXException e) {
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "readXML", ex);
	    throw ex;
	} catch (IOException e) {
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "readXML", ex);
	    throw e;
	}
    }

    public abstract void processDocument(Document doc) throws IOException;

    protected List parseSelectorList(Node node,
	    AttributeProvider attributeProvider) {
	List selectors = new LinkedList();
	NodeList children = node.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child instanceof Element) {
		try {
		    SGSelector selector = new SelectorPersistenceManager()
			    .unmarshall((Element) child, attributeProvider,
				    ontology);
		    selectors.add(selector);
		} catch (ParseException e) {
		    Logger.getLogger(getClass().getName()).throwing(
			    getClass().getName(), "error at parsing selector",
			    e);
		}
	    }
	}
	return selectors;
    }

    protected List parseSubgroups(Node parent,
	    AttributeProvider attributeProvider) throws IOException {
	NodeList children = parent.getChildNodes();
	List subgroups = new LinkedList();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child instanceof Element) {
		if (!"sg".equals(child.getNodeName())) {
		    IOException e = new IOException("Wrong element: "
			    + child.getNodeName() + " (expected: 'sg')");
		    Logger.getLogger(getClass().getName()).throwing(
			    getClass().getName(), "parseSubgroups", e);
		    throw e;
		}
		SG sg = parseSGNode((Element) child, attributeProvider);
		subgroups.add(sg);
	    }
	}
	return subgroups;
    }

    protected SG parseSGNode(Element parent, AttributeProvider attributeProvider)
	    throws IOException {
	NodeList children = parent.getChildNodes();
	SGTarget target = null;
	SGDescription desc = new SGDescription();
	List populationSelectors = new LinkedList();

	DataView population = ontology.getDataView().createPopulation(
		populationSelectors);
	SG sg = new SG(population, target, desc);

	for (int i = 0; i < children.getLength(); i++) {
	    Node node = children.item(i);
	    if (node instanceof Element) {
		Element elem = (Element) node;
		if ("target".equals(node.getNodeName())) {
		    target = parseTarget(elem, attributeProvider);
		} else if ("description".equals(node.getNodeName())) {
		    for (Iterator iter = parseSelectorList(node,
			    attributeProvider).iterator(); iter.hasNext();) {
			SGNominalSelector select = (SGNominalSelector) iter
				.next();
			desc.add(select);
		    }
		} else if ("population".equals(node.getNodeName())) {
		    for (Iterator iter = parseSelectorList(node,
			    attributeProvider).iterator(); iter.hasNext();) {
			populationSelectors.add(iter.next());
		    }
		}
	    }
	}

	// update population (the subgroup was initialized with a dummy
	// population above!)
	population = ontology.getDataView().createPopulation(
		populationSelectors);
	sg.setPopulation(population);
	sg.setTarget(target);

	return sg;
    }

    protected SGTarget parseTarget(Element elem,
	    AttributeProvider attributeProvider) throws IOException {
	String type = elem.getAttribute("type");
	if (type.equals("emptyTarget")) {
	    return null;
	} else if ("selector".equals(type)) {
	    List selectorList = parseSelectorList(elem, attributeProvider);
	    if (selectorList.isEmpty()) {
		IOException e = new IOException("Missing target for Subgroup");
		Logger.getLogger(getClass().getName()).throwing(
			getClass().getName(), "parseTarget", e);
		throw e;
	    }
	    if (selectorList.size() != 1) {
		IOException e = new IOException(
			"wrong number of selectors in SelectorTarget: "
				+ selectorList.size());
		Logger.getLogger(getClass().getName()).throwing(
			getClass().getName(), "parseTarget", e);
		throw e;
	    }
	    return new SelectorTarget((SGNominalSelector) selectorList.get(0));
	} else if ("booleanFormula".equals(type)) {
	    NodeList formulaNodes = elem.getChildNodes();
	    for (int j = 0; j < formulaNodes.getLength(); j++) {
		Node child = formulaNodes.item(j);
		if (child instanceof Element
			&& "formula".equals(child.getNodeName())) {
		    FormulaElement formula = new FormulaUnmarshaller(ontology)
			    .unmarshal((Element) child, attributeProvider);
		    return new BooleanFormulaTarget(
			    (FormulaBooleanElement) formula);
		}
	    }
	    IOException e = new IOException("missing formula for FormulaTarget");
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "parseTarget", e);
	    throw e;
	} else if ("numeric".equals(type)) {
	    String attributeID = elem.getAttribute("variable");
	    NumericAttribute targetAttribute = (NumericAttribute) getOntology()
		    .getAttribute(attributeID);
	    if (targetAttribute == null) {
		IOException e = new IOException(
			"targetVariable for NumericTarget not found! ");
		Logger.getLogger(getClass().getName()).throwing(
			getClass().getName(), "parseTarget", e);
		throw e;
	    }
	    return new NumericTarget(targetAttribute);
	}

	else {
	    IOException e = new IOException("unknown target-type: " + type);
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "parseTarget", e);
	    throw e;
	}
    }

    protected Attribute getDMAttribute(String attributeString) {
	return ontology.getAttribute(attributeString);
    }

    private void setBooleanOption(Option option, Options options, Element el) {
	String attribtueString = el.getAttribute("attribute");
	Attribute attribute = getDMAttribute(attribtueString);
	if (attribute != null) {
	    Boolean bValue = Boolean.valueOf(el.getAttribute("value"));
	    options.setBooleanAttributeOption(attribute, option, bValue);
	}
    }

    protected Options parseOptions(Node node) throws IOException {
	Options options = new Options();
	NodeList children = node.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child instanceof Element) {
		if (!"option".equals(child.getNodeName())) {
		    IOException e = new IOException("Wrong element: "
			    + child.getNodeName() + " (expected: 'option')");
		    Logger.getLogger(getClass().getName()).throwing(
			    getClass().getName(), "parseOptions", e);
		    throw e;
		}
		Element el = (Element) child;
		String type = el.getAttribute("id");
		if (type.equals(Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE
			.getId())) {
		    setBooleanOption(
			    Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE,
			    options, el);
		}
	    }
	}
	return options;
    }

    public Ontology getOntology() {
	return ontology;
    }

    protected void setOntology(Ontology onto) {
	ontology = onto;
    }
}
