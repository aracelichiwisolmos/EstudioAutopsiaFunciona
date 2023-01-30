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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.converters.GenericDataTable;

/**
 * {@link DBDataTableCreator} builds a {@link DataTable} given a
 * {@link DBConnector} or {@link ResultSet}.
 * 
 * @author lemmerich
 * 
 */
public class DBDataTableCreator {
    private DBConnector dbo;
    private String TableName;
    private int size;
    private ResultSet resultset;
    private GenericDataTable dt;
    private ArrayList<String> hList;

    public DBDataTableCreator(DBConnector dbo, String s) {
	this.dbo = dbo;
	this.TableName = s;
    }

    public DBDataTableCreator(ResultSet resultset, ArrayList<String> headlist) {
	this.resultset = resultset;
	hList = headlist;
    }

    private List<String> collectHeader() {
	if (hList != null) {
	    this.size = hList.size();
	    return hList;
	}
	ArrayList<String> headList = new ArrayList<String>();
	ResultSet rs = dbo.showColums(TableName);
	try {
	    while (rs.next()) {
		headList.add(rs.getString(1));
	    }
	} catch (Exception iea) {
	    iea.printStackTrace();
	}
	this.size = headList.size();
	return headList;
    }

    private void createGenericDataTable() {
	List<String> headers = collectHeader();
	if (dbo != null) {
	    resultset = dbo.getValues(TableName);
	}

	List<String[]> resultSetRows = new ArrayList<String[]>();
	int rowCount = 0;
	try {
	    while (resultset.next()) {
		String[] row = new String[size];
		int index = 1;
		while (index <= size) {
		    String s;
		    s = resultset.getString(index);
		    if (s == null || s.equals("null"))
			s = "?";
		    else if (s.contains("e+")) {
			s = Double.toString(Double.parseDouble(s));
		    }
		    row[index - 1] = s;
		    index++;
		}
		resultSetRows.add(row);
		rowCount++;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	int rowIndex = 0;
	dt = new GenericDataTable(rowCount, size, true);
	dt.setHeader(headers.toArray(new String[0]));
	for (String[] row : resultSetRows) {
	    dt.setRow(rowIndex, row);
	    rowIndex++;
	}
    }

    public GenericDataTable getTable() {
	createGenericDataTable();
	return dt;
    }

}
