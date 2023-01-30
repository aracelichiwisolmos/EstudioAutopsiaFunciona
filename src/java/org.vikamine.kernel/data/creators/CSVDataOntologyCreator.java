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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.data.converters.CSVDataConverter;
import org.vikamine.kernel.data.converters.CSVDataConverterConfig;
import org.vikamine.kernel.data.converters.GenericDataTable;
import org.vikamine.kernel.data.converters.GenericDataTableReader;

/**
 * {@link CSVDataConverter} builds an {@link Ontology} instance given a
 * {@link File} in CSV format.
 * 
 * @author atzmueller
 * 
 */
public class CSVDataOntologyCreator extends AbstractOntologyCreator {

    public final static String EXTENSION = "csv";

    private final File file;

    private final CSVDataConverterConfig csvConfig;

    protected CSVDataOntologyCreator(File inputFile) {
	super();
	this.file = inputFile;
	this.csvConfig = CSVDataConverter.getStandardCsvConfiguration();
    }

    public CSVDataOntologyCreator(File inputFile,
	    CSVDataConverterConfig csvConfiguration) throws IOException {
	super();
	this.file = inputFile;
	this.csvConfig = csvConfiguration;
	dataset = importInstancesFromCSVFile();
    }

    @Override
    public Ontology createOntology() {
	if (dataset == null) {
	    throw new IllegalStateException("Dataset is null!");
	}
	Ontology ontology = new Ontology(dataset);
	createDatasetIDs();
	copyAttributesToOntology(ontology);

	ontology.getTripleStore().addStatement(
		new RDFStatement(ontology,
			OntologyConstants.DATASET_FILETYPE_PROPERTY, ".csv"));
	ontology.getTripleStore().addStatement(
		new RDFStatement(ontology,
			OntologyConstants.DATASET_FILE_NAME_PROPERTY, file
				.getName()));
	RDFStatement absolutePathStatement = new RDFStatement(ontology,
		OntologyConstants.DATASET_FILE_ABSOLUTE_PATH_PROPERTY,
		file.getAbsolutePath());
	ontology.getTripleStore().addStatement(absolutePathStatement);

	return ontology;
    }

    private static GenericDataTable getCSVTable(File file, char separator,
	    char quote, char commentSymbol) throws IOException {
	return GenericDataTableReader.readTable(file, separator, quote,
		commentSymbol, true);
    }

    /**
     * @param f
     * @return
     */
    private DataRecordSet importInstancesFromCSVFile() throws IOException {
	DataRecordSet recordSet = null;
	try {
	    // FIXME
	    recordSet = CSVDataConverter.process(
		    getCSVTable(file, csvConfig.separator, csvConfig.quote,
			    csvConfig.comment), csvConfig.numberFormat, file
			    .getName());
	} catch (java.io.FileNotFoundException fnfex) {
	    Logger.getLogger(ARFFDataOntologyCreator.class.getName()).throwing(
		    ARFFDataOntologyCreator.class.getName(),
		    "File not found: " + file, fnfex);
	    throw new IOException(fnfex);
	} catch (java.io.IOException ioex) {
	    Logger.getLogger(ARFFDataOntologyCreator.class.getName()).throwing(
		    ARFFDataOntologyCreator.class.getName(),
		    "Problem with csv format of: " + file, ioex);
	    throw ioex;
	}
	return recordSet;
    }
}
