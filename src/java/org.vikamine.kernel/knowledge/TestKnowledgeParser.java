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
import java.io.Reader;

import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.xpdl.DatasetProvider;
import org.vikamine.kernel.xpdl.DirectoryDatasetProvider;

/**
 * A simple test parser.
 * 
 * @author lemmerich
 *
 */
public class TestKnowledgeParser extends AbstractKnowledgeParser {

    private static final String NAME = "TestKnowledgeParser";

    public static void main(String[] args) {
	// This is basically the path to the used dataset
	DatasetProvider provider = new DirectoryDatasetProvider(
		"../VKM-UCI-Test/resources/datasets/");

	// Load the dataset
	Ontology onto = provider.getDataset("credit-g.arff");

	KnowledgeInputManager kim = new KnowledgeInputManager(onto);
	boolean success = kim.handleFile(new File("C:/test/test.txt"));
	System.out.println(success);
	for (RDFStatement statement : onto.getTripleStore().getStatements()) {
	    System.out.println(statement);
	}
    }

    @Override
    public void handleFile(File f, Reader fileContent, Ontology onto) {
	RDFStatement statement = new RDFStatement(onto, "hasParsed", f,
		f.getPath());
	onto.getTripleStore().addStatement(statement);
    }

    @Override
    public String getName() {
	return NAME;
    }

}
