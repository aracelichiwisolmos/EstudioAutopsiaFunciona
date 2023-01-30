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
package org.vikamine.kernel.util;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities for accessing XML DOMs.
 * 
 * @author Tobias Vogele
 */
public class DOMAccessor {

    /**
     * Returns the value of alle text-child-nodes. Example: if node is something
     * like:
     * 
     * <pre>
     * 
     *  &lt;foo&gt;some text bla bla&lt;/foo&gt;
     * 
     * </pre>
     * 
     * The returned String is "some text bla bla"
     * 
     * @param node
     * @return
     */
    public static String getNodeText(Node node) {
	StringBuffer buffy = new StringBuffer();
	NodeList children = node.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child.getNodeType() == Node.TEXT_NODE) {
		buffy.append(child.getNodeValue().trim());
	    }
	}
	return buffy.toString();
    }
    
    
    /**
     * Returns the content of the text-section of the given DOM-Node
     * 
     * @param node
     *            Node to grab the text-section from
     * @return the content of the text-section of the given DOM-Node
     */
    public static String getAllText(Node node) {
	Iterator iter = new ChildrenIterator(node);
	while (iter.hasNext()) {
	    Node child = (Node) iter.next();
	    if (child.getNodeType() == Node.CDATA_SECTION_NODE)
		return child.getNodeValue();
	}

	StringBuffer sb = new StringBuffer();
	iter = new ChildrenIterator(node);
	while (iter.hasNext()) {
	    Node child = (Node) iter.next();
	    if (child.getNodeType() == Node.TEXT_NODE)
		sb.append(child.getNodeValue());
	}
	return sb.toString().trim();
    }
    

    public static String getChildNodeText(Node node, String childElement) {
	StringBuffer buffy = new StringBuffer();
	if (!(node instanceof Element)) {
	    throw new IllegalArgumentException("node is no element " + node);
	}
	NodeList children = ((Element) node).getElementsByTagName(childElement);
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    buffy.append(getNodeText(child));
	}
	return buffy.toString();
    }

    /**
     * Tries to find a child-node of the given DOM-Node with the given name
     * 
     * @param node
     *            the Node whose children will be inspected
     * @param nodeName
     *            the name of the child-node to find
     * @return the childnode found by the specified parameters. <tt>null</tt>,
     *         if not found
     */
    public static Node getChildNode(Node node, String nodeName) {
	NodeList nl = node.getChildNodes();
	for (int i = 0; i < nl.getLength(); i++) {
	    if (nl.item(i).getNodeName().equals(nodeName)) {
		return nl.item(i);
	    }
	}
	return null;
    }
    
    public static String getNormalizedNodeValue(Node node) {
	String value = node.getNodeValue();
	return value.trim();
    }

    public static Element getFirstChildElement(Element elem, String tagName) {
	NodeList list = null;
	if (tagName == null)
	    list = elem.getChildNodes();
	else
	    list = elem.getElementsByTagName(tagName);

	if (list != null) {
	    for (int i = 0; i < list.getLength(); i++) {
		Node node = list.item(i);
		if (node instanceof Element)
		    return (Element) node;
	    }
	}
	return null;
    }

    public static Element getFirstChildElement(Element elem) {
	return getFirstChildElement(elem, null);
    }

}
