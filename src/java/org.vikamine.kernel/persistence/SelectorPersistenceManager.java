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
import java.util.logging.Logger;

import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.DerivedNominalAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Basic manager for selector persistence.
 * 
 * @author Tobias Vogele
 */
public class SelectorPersistenceManager {

    public SelectorPersistenceManager() {
	super();
    }

    /**
     * Creates a DOM-Element from a Selector
     * 
     * @param selector
     * @param document
     */
    public Element marshall(SGSelector selector, Document document) {
	Element elem = document.createElement("selector");
	SelectorMarshaller marshaller = getMarshaller(selector);
	elem.setAttribute("type", marshaller.getClass().getName());
	marshaller.marshall(selector, elem, document);
	return elem;
    }

    public SGSelector unmarshall(Element element,
	    AttributeProvider attributeProvider, Ontology ontology)
	    throws ParseException {
	String type = element.getAttribute("type");
	SelectorMarshaller marshaller = createMarshaller(type);
	return marshaller.unmarshall(element, attributeProvider, ontology);
    }

    protected SelectorMarshaller getMarshaller(SGSelector sel) {
	if (sel instanceof NegatedSGSelector) {
	    return new NegatedSelectorMarshaller();
	} else if ((sel.getAttribute() instanceof DerivedNominalAttribute)
		&& (!((SGNominalSelector) sel).getValues().isEmpty())
		&& (((SGNominalSelector) sel).getValues().toArray()[0] instanceof Value.CustomDiscretizedValue)) {
	    return new DiscretizedSelectorMarshaller();
	} else {
	    return new SGSelectorMarshaller();
	}
    }

    protected SelectorMarshaller createMarshaller(String type)
	    throws ParseException {
	try {
	    Object obj = Class.forName(type).newInstance();
	    return (SelectorMarshaller) obj;
	} catch (Exception e) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "createMarshaller", e);
	    throw new ParseException("unknown selector-type: " + type, 0);
	}
    }

}
