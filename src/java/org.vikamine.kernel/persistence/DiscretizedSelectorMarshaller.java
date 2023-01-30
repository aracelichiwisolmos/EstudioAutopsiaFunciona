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
import java.util.Collections;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.util.DOMAccessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Creates XML for discretized selectors, i.e., using {@link Value.CustomDiscretizedValue}
 * 
 * @author Tobias Vogele
 */
public class DiscretizedSelectorMarshaller implements SelectorMarshaller {

    @Override
    public void marshall(SGSelector selector, Element element, Document document) {
	Attribute att = selector.getAttribute();
	Element attNode = document.createElement("attribute");
	attNode.appendChild(document.createTextNode(att.getId()));
	element.appendChild(attNode);

	Value.CustomDiscretizedValue disc = (Value.CustomDiscretizedValue) ((DefaultSGSelector) selector)
		.getValues().iterator().next();
	new ValuesMarshaller().createValueNode(document, attNode, disc);
    }

    @Override
    public SGNominalSelector unmarshall(Element element,
	    AttributeProvider attributeProvider, Ontology ontomanager)
	    throws ParseException {
	String id = DOMAccessor.getChildNodeText(element, "attribute");
	Attribute att = attributeProvider.getAttribute(id);
	if (att == null) {
	    throw new ParseException("Unknown attribute: " + id, -1);
	}
	NodeList values = element.getElementsByTagName("discretizedValue");
	if (values.getLength() == 0) {
	    throw new ParseException("missing discretizedvalue", -1);
	}
	Element child = (Element) values.item(0);
	Value disc = new ValuesMarshaller().parseValueNode(attributeProvider,
		ontomanager, child);
	return new DefaultSGSelector(att, Collections.singleton(disc));
    }
}
