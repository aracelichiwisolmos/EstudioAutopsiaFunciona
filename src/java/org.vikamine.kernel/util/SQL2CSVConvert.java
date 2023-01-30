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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This is a Utils class that allows to generate a CSV file from a
 * java.sql.ResultSet
 * 
 * Thus, you can use this class to load a "dataset" that is created via an sql
 * select statement from a db.
 * 
 * null values in the db are treated as missing values.
 * 
 * @author lemmerich
 * 
 */
public class SQL2CSVConvert {

    public static void createCSVFile(ResultSet result, File file)
	    throws SQLException, IOException {
	ResultSetMetaData metaData = result.getMetaData();
	BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	int columnCount = metaData.getColumnCount();
	for (int i = 1; i <= columnCount; i++) {
	    writer.write(metaData.getColumnName(i));
	    if (i != columnCount) {
		writer.write(",");
	    }
	}
	writer.write("\n");

	while (result.next()) {
	    for (int i = 1; i <= columnCount; i++) {
		String value = result.getString(i);
		if (value == null) {
		    value = "?";
		}

		// remove possibly invalid values from the cell entries
		value = value.replaceAll("\r\n", "////");
		value = value.replaceAll("\r", "////");
		value = value.replaceAll("\n", "////");
		value = value.replaceAll("'", "");
		value = value.replaceAll("\"", "");
		value = value.replaceAll(";", "");
		value = value.replaceAll(",", "");
		writer.write("'" + value + "'");
		if (i != columnCount) {
		    writer.write(",");
		} else {
		    writer.write("\n");
		}
	    }
	    writer.flush();
	}
	writer.flush();
	writer.close();
    }
}
