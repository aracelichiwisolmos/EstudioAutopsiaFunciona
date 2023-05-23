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
package org.vikamine.kernel.util;

import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;

/**
 * Exports the triples in the TripleStore of the current ontology as XML
 * 
 * @author lemmerich
 * 
 */
public class RDFExporter {
    /**
     * Creates an xml RDF File which describes all Statements in ArrayList @menge
     * 
     * @path path at file system
     * @ontology is not needed yet but probably will be in further
     *           implementations
     * */
    public static void writeTriples(String path, List<RDFStatement> menge) {
	String longPre = "org.vikamine.rdf";
	String shortPre = "vik";
	Namespace space = Namespace.getNamespace(shortPre, longPre);

	Document doc = new Document();

	ArrayList<Object> subjs = new ArrayList<Object>();
	Hashtable table = new Hashtable();

	/**
	 * First all subjects are collected, to get First level of xml element
	 */
	for (int i = 0; i < menge.size(); i++) {
	    if (!subjs.contains(menge.get(i).getSubject())) {
		subjs.add(menge.get(i).getSubject());

		ArrayList<RDFStatement> belongstoSubject = new ArrayList<RDFStatement>();
		belongstoSubject.add(menge.get(i));
		table.put(menge.get(i).getSubject(), belongstoSubject);

	    } else {
		ArrayList<RDFStatement> state = (ArrayList<RDFStatement>) (table
			.get(menge.get(i).getSubject()));
		state.add(menge.get(i));
	    }
	}

	Element elRoot = new Element("RDFStatements", shortPre, longPre);

	/** For each collected Subject write all triples with these subject */
	for (Object obj : subjs) {
	    Element elSubject = null;

	    /** Transform subject to a unique element */
	    if (obj instanceof Attribute) {
		elSubject = new Element("Attribute", shortPre, longPre);
		elSubject
			.setAttribute("id", ((Attribute) (obj)).getId(), space);
	    } else if (obj instanceof Ontology) {
		elSubject = new Element("Ontology", shortPre, longPre);
		// ID ist not necessary=> only one avaiable
	    } else if (obj instanceof RDFStatement) { // Should in general not
						      // be used.
		elSubject = new Element("RDFStatement", shortPre, longPre);
		elSubject.setAttribute("id", ((RDFStatement) (obj)).toString(),
			space);

		// }else if(obj instanceof
		// org.vikamine.kernel.knowledge.knowledgeParsing.AggregationObject){
		// elSubject = new
		// Element("AggregationObject",shortPre,longPre);
		// elSubject.setAttribute("id",obj.toString(),space);

	    } else if (obj instanceof DefaultSGSelector) {
		elSubject = new Element("DefaultSGSelector", shortPre, longPre);
		elSubject.setAttribute("id",
			((DefaultSGSelector) (obj)).getDescription(), space);

	    } else if (obj instanceof String) {
		elSubject = new Element("Parsable", shortPre, longPre);
		elSubject.setAttribute("id", obj.toString(), space);

	    } else if (obj instanceof String) {
		elSubject = new Element("Parsable", shortPre, longPre);
		elSubject.setAttribute("id", obj.toString(), space); // / Add
								     // some
								     // other
								     // objects
								     // type =>
								     // if
								     // better
								     // intepreted

	    } else {
		System.out.println("RDF Exporter: I can't handle this type: "
			+ obj);

	    }

	    /* Write Predicate + Object */
	    List<RDFStatement> statements = (List<RDFStatement>) (table
		    .get(obj));
	    for (RDFStatement stat : statements) {
		Element elState = new Element("Predicate", shortPre, longPre);
		elState.setAttribute("about",
			makePredicate(stat.getPredicate()), space);
		elState.addContent(new Text(getUnique(stat.getObject())
			.toString()));
		if (elSubject != null) {
		    elSubject.addContent(elState);
		}
	    }
	    if (elSubject != null) {
		elRoot.addContent(elSubject);
	    }
	}

	// Damit das XML-Dokument schoen formattiert wird holen wir uns ein
	// Format
	Format format = Format.getPrettyFormat();
	// und setzen das encoding, da in unseren Buechern auch Umlaute
	// vorkommen koennten.
	// Mit format kann man z.B. auch die Einrueckung beeinflussen
	format.setEncoding("iso-8859-1");
	doc.setContent(elRoot);
	// Erzeugung eines XMLOutputters dem wir gleich unser Format mitgeben
	try {
	    XMLOutputter xmlOut = new XMLOutputter(format);
	    // Schreiben der XML-Datei ins Filesystem
	    xmlOut.output(doc, new FileOutputStream(path));
	} catch (Exception e) {
	    // Wichtig noch throws hinzufgen
	}
    }

    /**
     * if predicate are not unique it can be replaced here
     * 
     * @return in standard it just returns the paramter predicate
     * */
    public static String makePredicate(String predicate) {
	return predicate;
    }

    /**
     * Get the unique part of an Object. Use the identify the object of a triple
     * */

    public static Object getUnique(Object toUnique) {
	if (toUnique instanceof Attribute) {
	    return ((Attribute) toUnique).getId();
	} else if (toUnique instanceof Ontology) {
	    return "Ontology";
	} else if (toUnique instanceof String) {
	    return toUnique;
	    // }else if(toUnique instanceof AggregationObject ) {
	    // return toUnique.toString();
	} else {
	    return toUnique;
	}

    }

}
