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

import java.io.File;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.RDFTripleStore;
import org.vikamine.kernel.util.FileUtils;

/**
 * {@link KnowledgeInputManager} manages all knowledge parsers.
 * 
 * @author lemmerich
 * 
 */
public class KnowledgeInputManager {

    public static final String COMMENT_STRING = "#";

    final Ontology ontology;

    public Ontology getOntology() {
	return ontology;
    }

    Set<IKnowledgeParser> parsers;

    public KnowledgeInputManager(Ontology ontology) {
	super();
	this.ontology = ontology;
	parsers = new HashSet<IKnowledgeParser>();
    }

    /**
     * Uses All parsers to handle this file. Returns false, if the file did not
     * exist, could not be opened or an error/exception occurred.
     * 
     * @param f
     * @return
     */
    public boolean handleFile(File f) {
	String fileContent = FileUtils.readFileToString(f);

	if (fileContent == null) {
	    return false;
	}

	RDFTripleStore tripleStore = ontology.getTripleStore();
	tripleStore.removeStatements(tripleStore.getStatementsForFile(f
		.getPath()));
	for (IKnowledgeParser parser : parsers) {
	    parser.handleFile(f, new StringReader(fileContent), ontology);
	}

	return true;
    }

    public void addParser(IKnowledgeParser aParser) {
	parsers.add(aParser);
    }
}
