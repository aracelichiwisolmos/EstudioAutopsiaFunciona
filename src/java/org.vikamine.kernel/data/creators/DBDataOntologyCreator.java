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

package org.vikamine.kernel.data.creators;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.data.converters.CSVDataConverter;

/**
 * {@link DBDataOntologyCreator} creates a {@link DataRecordSet} given a
 * {@link DBConnector} or {@link ResultSet}.
 * 
 * @author lemmerich
 * 
 */
public class DBDataOntologyCreator extends AbstractOntologyCreator {
    public DBDataOntologyCreator(DBConnector dbo, String name)
	    throws IOException {
	dataset = importInstance(dbo, name);
    }

    public DBDataOntologyCreator(ResultSet resultset, ArrayList<String> list) {
	dataset = importInstance(resultset, list);
    }

    @Override
    public Ontology createOntology() {
	if (dataset != null) {
	    Ontology ontology = new Ontology(dataset);
	    ontology.getTripleStore().addStatement(
		    new RDFStatement(ontology,
			    OntologyConstants.DATASET_FILETYPE_PROPERTY,
			    "database"));
	    createDatasetIDs();
	    copyAttributesToOntology(ontology);
	    return ontology;
	}
	return null;

    }

    public DataRecordSet importInstance(DBConnector dbo, String name)
	    throws IOException {
	DataRecordSet inset = null;
	DBDataTableCreator dgb = new DBDataTableCreator(dbo, name);
	inset = CSVDataConverter.process(dgb.getTable(), CSVDataConverter
		.getStandardNumberFormat(), name);
	return inset;
    }

    public DataRecordSet importInstance(ResultSet resultset,
	    ArrayList<String> list) {
	DataRecordSet inset = null;
	DBDataTableCreator dgb = new DBDataTableCreator(resultset, list);
	try {
	    inset = CSVDataConverter.process(dgb.getTable(), CSVDataConverter
		    .getStandardNumberFormat(), null);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return inset;
    }
}
