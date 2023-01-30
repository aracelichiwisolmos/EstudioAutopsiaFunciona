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
 * Created on 29.11.2004
 *
 */
package org.vikamine.kernel.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DerivedAttribute;
import org.vikamine.kernel.data.DerivedNominalAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.Options.Option;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.target.BooleanFormulaTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class for XML persistence (writer); is extended by applications.
 * 
 * @author atzmueller
 * 
 */
public abstract class XMLPersistenceWriter {

    public XMLPersistenceWriter() {
	super();
    }

    public Document writeXML(File file) throws IOException {
	OutputStream out = new FileOutputStream(file);
	try {
	    return writeXML(out);
	} finally {
	    out.close();
	}
    }

    public Document writeXML(OutputStream out) throws IOException {
	try {
	    Document document = buildDomTree();
	    write(out, document);
	    return document;
	} catch (ParserConfigurationException e) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", e);
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", ex);
	    throw ex;
	} catch (TransformerFactoryConfigurationError e) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", e);
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", ex);
	    throw ex;
	} catch (TransformerException e) {
	    IOException ex = new IOException(e.getMessage());
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", e);
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "writeXML", ex);
	    throw ex;
	}
    }

    protected abstract Document buildDomTree()
	    throws ParserConfigurationException;

    protected abstract void write(OutputStream out, Document doc)
	    throws TransformerFactoryConfigurationError, TransformerException;

    public Node createInterestingSGSetNode(Document document, List sgList) {
	Element groupsNode = document.createElement("interestingSGs");
	for (Iterator iter = sgList.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    groupsNode.appendChild(createSGNode(document, sg));
	}
	return groupsNode;
    }

    public Node createBackgroundKnowledgeSGNode(Document document, List sgList) {
	Element groupsNode = document.createElement("backgroundKnowledgeSGs");
	for (Iterator iter = sgList.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    groupsNode.appendChild(createSGNode(document, sg));
	}
	return groupsNode;
    }

    public Node createSGNode(Document doc, SG sg) {
	Element sgElem = doc.createElement("sg");
	// Target:
	SGTarget target = sg.getTarget();
	if (target == null) {
	    Element targetElem = doc.createElement("target");
	    targetElem.setAttribute("type", "emptyTarget");
	    sgElem.appendChild(targetElem);
	} else if (target instanceof SelectorTarget) {
	    Element targetElem = doc.createElement("target");
	    targetElem.setAttribute("type", "selector");
	    targetElem.appendChild(new SelectorPersistenceManager().marshall(
		    ((SelectorTarget) target).getSelector(), doc));
	    sgElem.appendChild(targetElem);
	} else if (target instanceof BooleanFormulaTarget) {
	    Element targetElem = doc.createElement("target");
	    targetElem.setAttribute("type", "booleanFormula");
	    targetElem.appendChild(((BooleanFormulaTarget) target).getFormula()
		    .createDOMNode(doc));
	    sgElem.appendChild(targetElem);
	} else if (target instanceof NumericTarget) {
	    Element targetElem = doc.createElement("target");
	    targetElem.setAttribute("type", "numeric");
	    targetElem.setAttribute("variable", ((NumericTarget) target)
		    .getNumericTargetAttribute().getId());
	    // targetElem.appendChild(new SelectorPersistenceManager().marshall(
	    // ((SelectorTarget) target).getSelector(), doc));
	    sgElem.appendChild(targetElem);
	} else {
	    throw new IllegalArgumentException("unknown target: " + target);
	}
	// SGDescription:
	Element descElem = doc.createElement("description");
	for (Iterator iter = sg.getSGDescription().iterator(); iter.hasNext();) {
	    SGNominalSelector sel = (SGNominalSelector) iter.next();
	    descElem.appendChild(new SelectorPersistenceManager().marshall(sel,
		    doc));
	}
	sgElem.appendChild(descElem);
	// DataView:
	Element popElem = doc.createElement("population");
	for (Iterator iter = sg.getPopulation().populationRangesSelectors()
		.iterator(); iter.hasNext();) {
	    SGNominalSelector sel = (SGNominalSelector) iter.next();
	    popElem.appendChild(new SelectorPersistenceManager().marshall(sel,
		    doc));
	}
	sgElem.appendChild(popElem);

	return sgElem;
    }

    public Node createCustomFeatureNode(Document document, DerivedAttribute att) {
	Element elem = document.createElement("attribute");
	elem.setAttribute("type", "customFeature");
	elem.setAttribute("id", att.getId());
	elem.setAttribute("description", att.getDescription());

	FormulaElement formula = att.getFormula();
	if (formula != null) {
	    elem.appendChild(formula.createDOMNode(document));
	} else if (att instanceof DerivedNominalAttribute) {
	    DerivedNominalAttribute dna = (DerivedNominalAttribute) att;
	    for (Iterator iter = dna.allValuesIterator(); iter.hasNext();) {
		Value alt = (Value) iter.next();
		new ValuesMarshaller().createValueNode(document, elem, alt);
	    }
	} else {
	    throw new IllegalStateException("Unsupported kind of attribute");
	}
	return elem;
    }

    public Element createZoomAttributeNode(Document document,
	    String attributeId, String parentAttributeId) {
	Element child = document.createElement("attribute");
	child.appendChild(document.createTextNode(attributeId));
	if (parentAttributeId != null)
	    child.setAttribute("treeParent", parentAttributeId);
	return child;
    }

    public Node createConstraintNodes(Document document, Options options,
	    Set<Attribute> dmAttributes) {
	Element constraintRoot = document.createElement("options");
	// save attribute constraints
	for (Iterator iter = dmAttributes.iterator(); iter.hasNext();) {
	    Attribute attribute = (Attribute) iter.next();
	    Map<Option, Object> optionsMap = options
		    .getAttributeOptions(attribute);
	    if ((optionsMap != null) && (!optionsMap.isEmpty())) {
		for (Entry<Option, Object> entry : optionsMap.entrySet()) {
		    Option option = entry.getKey();
		    if (option
			    .equals(Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE)) {
			createBooleanOptionNode(option,
				(Boolean) entry.getValue(), attribute,
				constraintRoot, document);
		    }
		}
	    }
	}
	return constraintRoot;
    }

    private void createBooleanOptionNode(Option currentOption, Boolean bValue,
	    Attribute attribute, Element constraintRoot, Document document) {
	if (bValue.booleanValue()) {
	    Element constraintNode = document.createElement("option");
	    constraintNode.setAttribute("id", currentOption.getId());
	    constraintNode.setAttribute("attribute", attribute.getId());
	    constraintNode.setAttribute("value", bValue.toString());
	    constraintRoot.appendChild(constraintNode);
	}
    }
}
