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
 * Created on 09.12.2004
 *
 */
package org.vikamine.kernel.data.creators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.converters.CSVDataConverter;
import org.vikamine.kernel.data.converters.CSVDataConverterConfig;

/**
 * 
 * 
 * @author atzmueller
 */
public class DataFactory {

    private static HashMap<String, IFileSourceOntologyCreator> EXTRA_FILE_EXTENSION_HANDLERS = new HashMap<String, IFileSourceOntologyCreator>();

    public static void addHandler(String extension,
	    IFileSourceOntologyCreator creator) {
	EXTRA_FILE_EXTENSION_HANDLERS.put(extension, creator);
    }

    public static boolean removeHandler(String extension) {
	return (EXTRA_FILE_EXTENSION_HANDLERS.remove(extension) != null);
    }

    protected DataFactory() {
	super();
    }

    public static Ontology createOntology(DataRecordSet theInstances) {
	return new ARFFDataOntologyCreator(theInstances).createOntology();
    }

    public static Ontology createOntologyFromString(String arffString) {
	return new ARFFDataOntologyCreator(arffString).createOntology();
    }

    public static Ontology createOntology(File file) throws IOException {
	int dotPos = file.getName().lastIndexOf(".");
	if (dotPos != -1) {
	    String extension = file.getName().substring(dotPos + 1);
	    if (extension.equals(CSVDataOntologyCreator.EXTENSION)) {
		return createOntologyFromCSVFile(file,
			CSVDataConverter.getStandardCsvConfiguration());
	    } else if (extension.equals(ARFFDataOntologyCreator.EXTENSION)) {
		return new ARFFDataOntologyCreator().createOntology(file);
	    } else if (extension.equals(KeelDataOntologyCreator.EXTENSION)) {
		return new KeelDataOntologyCreator().createOntology(file);
	    } else {
		for (Map.Entry<String, IFileSourceOntologyCreator> entry : EXTRA_FILE_EXTENSION_HANDLERS
			.entrySet()) {
		    if (entry.getKey().equals(extension)) {
			IFileSourceOntologyCreator creator = entry.getValue();
			return creator.createOntology(file);
		    }
		}
	    }
	}
	// fallback
	return new ARFFDataOntologyCreator().createOntology(file);
    }

    public static void saveRecordSetAsARFF(File file, IDataRecordSet instances,
	    Ontology om) {
	try {
	    FileOutputStream out = new FileOutputStream(file);
	    PrintStream ps = new PrintStream(out);
	    ps.print(instances.printInstances(om));
	    ps.close();
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(DataFactory.class.getName()).throwing(
		    DataFactory.class.getName(),
		    "appendConvertedToARFFBaseFile", ex);
	}
    }

    public static void saveOntologyAsARFF(File file, Ontology om) {
	try {
	    FileOutputStream out = new FileOutputStream(file);
	    PrintStream ps = new PrintStream(out);
	    ps.print(om.printAllAttributes());
	    ps.close();
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(DataFactory.class.getName()).throwing(
		    DataFactory.class.getName(),
		    "appendConvertedToARFFBaseFile", ex);
	}
    }

    public static Ontology createOntologyFromCSVFile(File csvFile,
	    CSVDataConverterConfig csvConfiguration) throws IOException {
	return new CSVDataOntologyCreator(csvFile, csvConfiguration)
		.createOntology();
    }

    public static Ontology createOntologyFromDTP(ResultSet data, String name)
	    throws SQLException {
	return new DTPDataOntologyCreator(data, name).createOntology();
    }

    public static Ontology createOntologyFromDBData(DBConnector db, String name)
	    throws IOException {
	return new DBDataOntologyCreator(db, name).createOntology();
    }

    public static Ontology createOntologyFromDBData(Connection con, String name)
	    throws IOException {
	return new DBDataOntologyCreator(new DBConnector(con), name).createOntology();
    }
    
    public static Ontology createOntologyFromDBData(ResultSet resultset,
	    ArrayList<String> list) {
	return new DBDataOntologyCreator(resultset, list).createOntology();
    }

    public static Ontology createEmptyOntology() {
	return Ontology.createEmptyOntology();
    }
}
