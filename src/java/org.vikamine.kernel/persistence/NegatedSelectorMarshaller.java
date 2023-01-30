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

import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * (Un-)marshalling of negated selectors.
 * 
 * @author Tobias Vogele
 */
public class NegatedSelectorMarshaller implements SelectorMarshaller {

    @Override
    public void marshall(SGSelector selector, Element element, Document document) {
	NegatedSGSelector negSel = (NegatedSGSelector) selector;
	Element child = new SelectorPersistenceManager().marshall(
		negSel.getPositiveSelector(), document);
	element.appendChild(child);
    }

    @Override
    public SGSelector unmarshall(Element element,
	    AttributeProvider attributeProvider, Ontology ontology)
	    throws ParseException {
	NodeList children = element.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    if (children.item(i) instanceof Element) {
		SGSelector positive = new SelectorPersistenceManager()
			.unmarshall((Element) children.item(i),
				attributeProvider, ontology);
		return new NegatedSGSelector(positive);
	    }
	}
	throw new ParseException("Missing child-element for negated-selector",
		-1);
    }

}
