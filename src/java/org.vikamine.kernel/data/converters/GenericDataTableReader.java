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
 * Created on 22.12.2003
 * 
 */
package org.vikamine.kernel.data.converters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class GenericDataTableReader {

    private GenericDataTableReader() {
	super();
    }

    /**
     * counts lines that are not comments
     */
    public static int countCSVLines(File f, char separator, char quote,
	    char comment) throws IOException {
	CSVReader csvr = new CSVReader(new BufferedReader(new FileReader(f)),
		separator, quote);

	int count = 1;
	String[] nextLine = csvr.readNext();
	while (true) {
	    nextLine = csvr.readNext();
	    if (nextLine == null) {
		break;
	    }
	    if (!nextLine[0].startsWith(String.valueOf(comment))) {
		count++;
	    }
	}
	csvr.close();
	return count;
    }

    /**
     * @param f
     * @param separator
     * @param quote
     * @param withHeader
     * @return
     * @throws IOException
     */
    public static GenericDataTable readTable(File f, char separator,
	    char quote, char commentSymbol, boolean withHeader)
	    throws IOException {

	CSVReader csvr = new CSVReader(new FileReader(f), separator, quote);
	String[] nextLine = csvr.readNext();

	GenericDataTable table = new GenericDataTable(
		GenericDataTableReader.countCSVLines(f, separator, quote,
			commentSymbol) - (withHeader ? 1 : 0), nextLine.length,
		withHeader);
	int lineNumber = 0;
	do {
	    if (nextLine[0].startsWith(String.valueOf(commentSymbol))) {
		continue;
	    }
	    if (withHeader) {
		if (lineNumber == 0) {
		    table.setHeader(nextLine);
		} else {
		    table.setRow(lineNumber - 1, nextLine);
		}
	    } else {
		table.setRow(lineNumber, nextLine);
	    }
	    lineNumber++;
	} while ((nextLine = csvr.readNext()) != null);
	csvr.close();
	return table;
    }
}
