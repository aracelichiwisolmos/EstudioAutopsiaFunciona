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
 * Created on 30.09.2005
 *
 */
package org.vikamine.kernel.persistence;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.DerivedNominalValue;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.SingleValue;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.util.DOMAccessor;
import org.vikamine.kernel.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * (Un-)marshaller for all kinds of values.
 * 
 * @author atzmueller
 */
public class ValuesMarshaller {

    public ValuesMarshaller() {
	super();
    }

    public void createValueNode(Document document, Element parentNode, Value val) {
	if (val instanceof SingleValue) {
	    createSingleValueNode(document, parentNode, val);
	} else if (val instanceof Value.MissingValue) {
	    Value.MissingValue missing = (Value.MissingValue) val;
	    createMissingValueNode(document, parentNode, missing);
	} else if (val instanceof DerivedNominalValue) {
	    DerivedNominalValue customNominalValue = (DerivedNominalValue) val;
	    createCustomNominalValueNode(document, parentNode,
		    customNominalValue);
	} else if (val instanceof Value.CustomDiscretizedValue) {
	    Value.CustomDiscretizedValue v = (Value.CustomDiscretizedValue) val;
	    createCustomDiscretizedValueNode(v, parentNode, document);
	} else {
	    throw new IllegalArgumentException(
		    "selector-value has unknown type: " + val);
	}
    }

    private void createCustomNominalValueNode(Document document,
	    Element valueNode, DerivedNominalValue customNominalValue) {
	Element valNode = document.createElement("customNominalValue");
	valNode.setAttribute("attribute", customNominalValue.getAttribute()
		.getId());
	valNode.setAttribute("name", customNominalValue.getId());
	Element descriptionNode = document.createElement("description");
	descriptionNode.appendChild(document.createCDATASection(XMLUtils
		.prepareForCDATA(customNominalValue.getDescription())));
	valNode.appendChild(descriptionNode);
	Node formulaNode = customNominalValue.getCondition().createDOMNode(
		document);
	valNode.appendChild(formulaNode);
	valueNode.appendChild(valNode);
    }

    private void createMissingValueNode(Document document, Element valueNode,
	    Value.MissingValue val) {
	Element valNode = document.createElement("missingValue");
	valNode.setAttribute("attribute", val.getAttribute().getId());
	valueNode.appendChild(valNode);
    }

    private void createCustomDiscretizedValueNode(
	    Value.CustomDiscretizedValue value, Element parentNode, Document doc) {
	Element elem = doc.createElement("discretizedValue");
	elem.setAttribute("attribute", value.getAttribute().getId());
	elem.setAttribute("discretizedAttribute", value
		.getDiscretizedAttribute().getId());
	// elem.setAttribute("name", value.getName());
	// elem.setAttribute("description", value.getDescription());
	elem.setAttribute("leftOpen", String.valueOf(value.isLeftOpen()));
	elem.setAttribute("rightOpen", String.valueOf(value.isRightOpen()));
	Element child = doc.createElement("min");
	child.appendChild(doc.createTextNode(String.valueOf(value.getMin())));
	elem.appendChild(child);
	child = doc.createElement("max");
	child.appendChild(doc.createTextNode(String.valueOf(value.getMax())));
	elem.appendChild(child);
	parentNode.appendChild(elem);
    }

    private void createSingleValueNode(Document document, Element valueNode,
	    Value val) {
	Element valNode = document.createElement("value");
	valNode.setAttribute("attribute", val.getAttribute().getId());
	valNode.setAttribute("name", val.getId());
	valueNode.appendChild(valNode);
    }

    public Value parseValueNode(AttributeProvider attributeProvider,
	    Ontology ontology, Element valueElement) throws ParseException {
	return parseValueNode(attributeProvider, ontology, valueElement,
		new LinkedList());
    }

    public Value parseValueNode(AttributeProvider attributeProvider,
	    Ontology ontology, Element valueElement,
	    List uncompleteNominalValues) throws ParseException {
	if (valueElement.getNodeName().equals("value")) {
	    Value val = parseSingleValue(valueElement, attributeProvider);
	    return val;
	} else if (valueElement.getNodeName().equals("missingValue")) {
	    Value val = parseMissingValue(attributeProvider, valueElement);
	    return val;
	} else if (valueElement.getNodeName().equals("customNominalValue")) {
	    DerivedNominalValue val = parseCustomNominalValue(
		    attributeProvider, ontology, valueElement,
		    uncompleteNominalValues);
	    return val;
	} else if (valueElement.getNodeName().equals("discretizedValue")) {
	    Value.CustomDiscretizedValue val = parseCustomDiscretizedValue(
		    valueElement, attributeProvider);
	    return val;
	} else {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "parseValueNode",
		    new IllegalStateException("No legal Value Element found!"));
	    return null;
	}
    }

    private Value.CustomDiscretizedValue parseCustomDiscretizedValue(
	    Element elem, AttributeProvider attributeProvider)
	    throws ParseException {
	String left = elem.getAttribute("leftOpen");
	boolean leftOpen = Boolean.valueOf(left).booleanValue();
	String right = elem.getAttribute("leftOpen");
	boolean rightOpen = Boolean.valueOf(right).booleanValue();
	double minValue, maxValue;
	try {
	    String min = DOMAccessor.getChildNodeText(elem, "min");
	    minValue = Double.parseDouble(min);
	    String max = DOMAccessor.getChildNodeText(elem, "max");
	    maxValue = Double.parseDouble(max);
	} catch (NumberFormatException e) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "unmarshall", e);
	    throw new ParseException("not a number", -1);
	}
	String attributeID = elem.getAttribute("attribute");
	NominalAttribute attr = (NominalAttribute) attributeProvider
		.getAttribute(attributeID);
	// String name = elem.getAttribute("name");
	// String description = elem.getAttribute("description");
	String discretizedAttributeID = elem
		.getAttribute("discretizedAttribute");
	Attribute discretizedAttribute = attributeProvider
		.getAttribute(discretizedAttributeID);
	Value.CustomDiscretizedValue disc = Value.createCustomDiscretizedValue(
		discretizedAttribute, minValue, maxValue);
	disc.setAttribute(attr);
	disc.setLeftOpen(leftOpen);
	disc.setRightOpen(rightOpen);
	return disc;
    }

    private DerivedNominalValue parseCustomNominalValue(
	    AttributeProvider attributeProvider, Ontology ontology,
	    Element elem, List uncompleteNominalValues) {
	String name = elem.getAttribute("name");
	String description = XMLUtils.prepareFromCDATA(DOMAccessor
		.getNodeText(DOMAccessor.getChildNode(elem, "description")));
	DerivedNominalValue val = new DerivedNominalValue(name, description);
	Attribute basicAttribute = attributeProvider.getAttribute(elem
		.getAttribute("attribute"));

	if (basicAttribute == null) {
	    // Logger.getLogger(getClass().getName()).throwing(
	    // getClass().getName(),
	    // "unmarshall",
	    // new IllegalStateException("BasicAttribute "
	    // + elem.getAttribute("attribute") + " not found!"));
	    Value.UncompleteNominalValue uncomplete = new Value.UncompleteNominalValue(
		    val, (Element) DOMAccessor.getChildNode(elem, "formula"));
	    uncompleteNominalValues.add(uncomplete);
	} else {
	    val.setAttribute(basicAttribute);

	    FormulaElement formula = new FormulaUnmarshaller(ontology)
		    .unmarshal(
			    (Element) DOMAccessor.getChildNode(elem, "formula"),
			    attributeProvider);
	    val.setCondition((FormulaBooleanElement) formula);
	}
	return val;
    }

    private Value parseMissingValue(AttributeProvider attributeProvider,
	    Element elem) {
	Attribute attr = attributeProvider.getAttribute(elem
		.getAttribute("attribute"));
	if (attr == null) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(),
		    "unmarshall",
		    new IllegalStateException("Attribute "
			    + elem.getAttribute("attribute") + " not found!"));
	}
	Value val = Value.missingValue(attr);
	return val;
    }

    private Value parseSingleValue(Node node,
	    AttributeProvider attributeProvider) {
	Element elem = (Element) node;
	String attr = elem.getAttribute("attribute");
	String name = elem.getAttribute("name");
	Attribute attrib = attributeProvider.getAttribute(attr);
	if (attrib instanceof NominalAttribute) {
	    NominalAttribute nom = (NominalAttribute) attrib;
	    for (Iterator<Value> iterator = nom.allValuesIterator(); iterator
		    .hasNext();) {
		Value val = iterator.next();
		if (name.equals(val.getId())) {
		    return val;
		}
	    }
	}
	throw new NoSuchElementException("Value not found " + name);

    }
}
