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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class CheckXMLValid {

    /**
     * checks for valid XML (according to schema), offers DC support
     * 
     * @param toCheck
     * @param xsdPath
     * @param dc
     *            indicates whether DC is used in this sheme or not
     * @return
     */
    public static boolean checkValid(Document doc, String xsdPath, Boolean dc) {
	SAXBuilder sax = new SAXBuilder(true);// true
	XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	String temp = outputter.outputString(doc);
	Reader reader = new StringReader(temp);
	sax.setFeature("http://apache.org/xml/features/validation/schema", true);
	if (!dc) {
	    sax.setProperty(
		    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
		    xsdPath);
	} else {
	    sax.setFeature("http://apache.org/xml/features/validation/schema",
		    true);

	    sax.setFeature("http://xml.org/sax/features/validation", true);
	    sax.setFeature("http://xml.org/sax/features/namespaces", true);
	    // sax.setFeature("http://apache.org/xml/features/validation/dynamic",true);
	    sax.setProperty(
		    "http://apache.org/xml/properties/schema/external-schemaLocation",
		    "c:\\SGPlugin\\XSD\\output.xsd");
	    // sax
	    // .setProperty(
	    // "http://apache.org/xml/properties/schema/external-schemaLocation",
	    // xsdPath+"http://purl.org/dc/elements/1.1/ file:///C:/SGPlugin/dc.xsd");
	    // + "c:\\SGPlugin\\XSD\\dcterms.xsd"+"c:\\SGPlugin\\XSD\\dc.xsd"
	}
	try {
	    sax.build(reader);
	    return true;
	} catch (JDOMException e) {
	    e.printStackTrace();
	    throw new XMLException("XML-Datei nicht valide");

	} catch (IOException e) {
	    System.out.println(e.getMessage());
	    return false;
	}

    }

    /**
     * checks for valid XML (according to schema)
     * 
     * @param toCheck
     * @param xsdPath
     * @return
     */
    public static boolean checkValid(Document doc, String xsdPath) {
	SAXBuilder sax = new SAXBuilder(false);// true
	XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	String temp = outputter.outputString(doc);
	Reader reader = new StringReader(temp);
	sax.setFeature("http://apache.org/xml/features/validation/schema",
		false);
	sax.setProperty(
		"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
		xsdPath);
	try {
	    sax.build(reader);
	    return true;
	} catch (JDOMException e) {
	    e.printStackTrace();
	    throw new XMLException("XML-Datei nicht valide");

	} catch (IOException e) {
	    System.out.println(e.getMessage());
	    return false;
	}

    }

    public static boolean checkValid(Document toCheck) {
	Properties properties = new Properties();
	FileInputStream stream;
	String schemaLocation;
	try {
	    try {
		stream = new FileInputStream(CheckXMLValid.class.getClass()
			.getResource("/resources/xml/xsd.properties").toURI()
			.getPath());
		properties.load(stream);
		stream.close();
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }

	} catch (FileNotFoundException e) {
	    System.out.println("Properties File not found");
	    System.out.println(e.getMessage());
	} catch (IOException e) {
	    System.out.println(e.getMessage());
	}
	schemaLocation = CheckXMLValid.class.getClass().getResource(
		"/resources/xml/")
		+ properties.getProperty("outputXsd");
	return CheckXMLValid.checkValid(toCheck, schemaLocation);
    }
}
