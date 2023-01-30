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
 * Created on 05.12.2003
 * 
 */
package org.vikamine.kernel.data.creators;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.data.converters.KeelDataConverter;

/**
 * {@link KeelDataOntologyCreator} builds an {@link Ontology} given a
 * {@link DataRecordSet}, {@link String} containing the ARFF instances, or a
 * {@link File} in ARFF format.
 * 
 * @author Atzmueller
 */
public class KeelDataOntologyCreator extends AbstractOntologyCreator implements
	IFileSourceOntologyCreator {

    public static final String EXTENSION = "dat";

    private File file;

    private boolean isSparse;

    public KeelDataOntologyCreator() {
	super();
    }

    public KeelDataOntologyCreator(String keelString) throws IllegalStateException {
	super();
	dataset = importDataRecordFromKeelString(keelString);
    }

    public KeelDataOntologyCreator(DataRecordSet theInstances) {
	super();
	dataset = theInstances;
    }

    private DataRecordSet importDataRecordFromKeelString(String keelString) {
	KeelDataConverter keelReader = new KeelDataConverter(new StringReader(
		keelString));
	keelReader.setImportStringAttributes(false);
	this.isSparse = keelReader.isSparse();
	try {
	    return keelReader.createDataRecordSet();
	} catch (IOException e) {
	    Logger.getLogger(KeelDataOntologyCreator.class.getName()).throwing(
		    KeelDataOntologyCreator.class.getName(),
		    "Problem with the keel-dat format of" + keelString, e);
	    throw new IllegalStateException(e);
	}
    }

    private DataRecordSet importInstancesFromKeelFile(
	    boolean importStringAttributes) throws IOException {
	DataRecordSet inst = null;
	try {
	    Reader r = new BufferedReader(new FileReader(file));
	    int dotPos = file.getName().lastIndexOf(".");
	    if (dotPos != -1) {
		String extension = file.getName().substring(dotPos);
		if (extension.equals(".zip")) {
		    @SuppressWarnings("resource")
		    ZipFile zipFile = new ZipFile(file);
		    ZipEntry zEntry = zipFile.entries().nextElement();
		    BufferedInputStream bis = new BufferedInputStream(
			    zipFile.getInputStream(zEntry));
		    r = new InputStreamReader(bis);
		}
	    }
	    KeelDataConverter keelReader = new KeelDataConverter(r);
	    keelReader.setImportStringAttributes(importStringAttributes);
	    inst = keelReader.createDataRecordSet();

	    this.isSparse = keelReader.isSparse();

	    r.close();
	} catch (java.io.FileNotFoundException fnfex) {
	    Logger.getLogger(KeelDataOntologyCreator.class.getName()).throwing(
		    KeelDataOntologyCreator.class.getName(),
		    "File not found " + file, fnfex);
	    throw new IOException(fnfex);
	} catch (java.io.IOException ioex) {
	    Logger.getLogger(KeelDataOntologyCreator.class.getName()).throwing(
		    KeelDataOntologyCreator.class.getName(),
		    "Problem with the keel format of" + file, ioex);
	    throw new IOException(ioex);
	}
	return inst;
    }

    @Override
    public Ontology createOntology() {
	if (dataset != null) {
	    Ontology ontology = new Ontology(dataset);
	    createDatasetIDs();

	    if (file != null) {
		ontology.getTripleStore().addStatement(
			new RDFStatement(ontology,
				OntologyConstants.DATASET_FILETYPE_PROPERTY,
				".dat"));
		ontology.getTripleStore().addStatement(
			new RDFStatement(ontology,
				OntologyConstants.DATASET_FILE_NAME_PROPERTY,
				(file != null) ? file.getName() : "unknown"));
		RDFStatement absolutePathStatement = new RDFStatement(ontology,
			OntologyConstants.DATASET_FILE_ABSOLUTE_PATH_PROPERTY,
			file.getAbsolutePath());
		ontology.getTripleStore().addStatement(absolutePathStatement);
	    }

	    /*
	     * In case we have a sparse ARFF, an additional RDFStatement is
	     * added, stating that '0' is default.
	     */
	    if (this.isSparse) {
		ontology.getTripleStore().addStatement(
			new RDFStatement(ontology,
				OntologyConstants.DEFAULT_VALUE_PROPERTY, "0"));
	    }

	    copyAttributesToOntology(ontology);
	    ontology.getTripleStore().addStatement(
		    new RDFStatement(ontology,
			    OntologyConstants.DATASET_RELATION_NAME_PROPERTY,
			    dataset.getRelationName()));
	    return ontology;
	}
	return null;
    }

    @Override
    public Ontology createOntology(File file) throws IOException {
	return createOntology(file, true);
    }

    public Ontology createOntology(File file, boolean importStringAttributes)
	    throws IOException {
	this.file = file;
	dataset = importInstancesFromKeelFile(importStringAttributes);
	return createOntology();
    }
}
