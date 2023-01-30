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

package org.vikamine.kernel.knowledge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.util.Group;
import org.vikamine.kernel.util.GroupReader;

/**
 * Parser for attribute groups (collection of attributes).
 * 
 * @author lemmerich, atzmueller
 * 
 */
public class AttributeGroupParser extends AbstractKnowledgeParser {

    private static final String NAME = "AttributeGroupParser";

    private static final String START_LINE = "Attribute Groups:";

    @Override
    public void handleFile(File f, Reader fileContent, Ontology onto) {
	BufferedReader bufReader = new BufferedReader(fileContent);
	boolean firstLine = true;

	String line;
	try {
	    while ((line = bufReader.readLine()) != null) {
		if (firstLine) {
		    if (!line.startsWith(KnowledgeInputManager.COMMENT_STRING)) {
			firstLine = false;
			if (!line.startsWith(START_LINE)) {
			    return;
			}
		    }
		}
		List<RDFStatement> newStatements = createStatements(onto,
			bufReader, f);
		onto.getTripleStore().addStatements(newStatements);

	    }
	} catch (IOException e) {
	    // ignore and return;
	}
    }

    private List<RDFStatement> createStatements(Ontology onto,
	    BufferedReader bufReader, File f) {
	List<RDFStatement> statements = new ArrayList<RDFStatement>();
	GroupReader gr = new GroupReader(bufReader);
	for (Group<String> rootGroup : gr.getRootGroups()) {
	    String rootGroupName = rootGroup.getId();
	    statements.add(new RDFStatement(onto,
		    OntologyConstants.HAS_ATTRIBUTE_GROUP, rootGroupName, f
			    .getPath()));
	    for (String s : rootGroup.getElements()) {
		Attribute att = onto.getAttribute(s);
		if (att == null) {
		    // ignore
		} else {
		    statements.add(new RDFStatement(att,
			    OntologyConstants.IN_ATTRIBUTE_GROUP,
			    rootGroupName, f.getPath()));
		}
	    }
	}
	return statements;
    }

    @Override
    public String getName() {
	return NAME;
    }
}
