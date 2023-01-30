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
 * Created on 15.03.2004
 */
package org.vikamine.kernel.persistence;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.NumericSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.DOMAccessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Marshaller for {@link DefaultSGSelector}.
 * 
 * @author Tobias Vogele, atzmueller
 */
public class SGSelectorMarshaller implements SelectorMarshaller {

    @Override
    public void marshall(SGSelector selector, Element element, Document document) {
	Attribute att = selector.getAttribute();
	Element attNode = document.createElement("attribute");
	attNode.appendChild(document.createTextNode(att.getId()));
	element.appendChild(attNode);

	Element valueNode = document.createElement("values");
	attNode.appendChild(valueNode);
	if (selector instanceof SGNominalSelector) {
	    for (Iterator iter = ((SGNominalSelector) selector).getValues()
		    .iterator(); iter.hasNext();) {
		Value val = (Value) iter.next();
		new ValuesMarshaller()
			.createValueNode(document, valueNode, val);
	    }
	} else {
	    NumericSelector selectorNum = (NumericSelector) selector;
	    attNode.setAttribute("lowerBound", selectorNum.getLowerBound() + "");
	    attNode.setAttribute("upperBound", selectorNum.getUpperBound() + "");
	}
    }

    @Override
    public SGSelector unmarshall(Element element,
	    AttributeProvider attributeProvider, Ontology ontology)
	    throws ParseException {
	String id = DOMAccessor.getChildNodeText(element, "attribute");
	Attribute att = attributeProvider.getAttribute(id);
	if (att == null) {
	    throw new ParseException("Unknown attribute: " + id, -1);
	}
	if (att instanceof NominalAttribute) {
	    Set values = new HashSet();
	    NodeList valuesNode = element.getElementsByTagName("values");
	    if (valuesNode.getLength() != 1) {
		throw new ParseException("No values-node", -1);
	    }
	    NodeList valueNodes = valuesNode.item(0).getChildNodes();
	    for (int i = 0; i < valueNodes.getLength(); i++) {
		Node node = valueNodes.item(i);
		if (node instanceof Element) {
		    values.add(new ValuesMarshaller().parseValueNode(
			    attributeProvider, ontology, (Element) node));
		}
	    }
	    return new DefaultSGSelector(att, values);
	}
	if (att instanceof NumericAttribute) {
	    // TODO - handle when export is implemented
	}
	return null;
    }
}
