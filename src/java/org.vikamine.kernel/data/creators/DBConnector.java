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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * {@link DBConnector} provides common access functions to a database.
 * 
 * @author lemmerich, atzmueller
 *
 */
public class DBConnector {

    private Connection con = null;

    private boolean isConnected;

    public DBConnector(Connection con) {
	this.con = con;
    }

    public boolean isConnected() {
	return isConnected;
    }

    public DBConnector(String url, String dbUser, String dbPassword) {
	createTempConnection(url, dbUser, dbPassword);
    }

    public void createTables(String tname, ArrayList<String> fields,
	    ArrayList<String> types) {
	String query;
	Statement stmt;
	try {
	    query = "create Table " + tname + " ( ";
	    for (int i = 0; i < fields.size(); i++) {
		query = query + fields.get(i) + " " + types.get(i) + ",";
	    }
	    query = query + "Primary key (" + fields.get(0) + "));";

	    stmt = con.createStatement();
	    stmt.execute(query);
	    stmt.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void createTemporaryTable(String tname, String targetTable,
	    ArrayList<String> list) {
	String query;
	Statement stmt;
	try {
	    query = "create temporary Table " + tname + " as select ";
	    int index = 0;
	    while (index < list.size()) {
		query = query + list.get(index);
		if (index < list.size() - 1)
		    query = query + ", ";
		index++;
	    }
	    query = query + " from " + targetTable + ";";
	    stmt = con.createStatement();
	    stmt.execute(query);
	    // stmt.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public ResultSet getValue(String tname, ArrayList<String> fields) {
	String query;
	query = "select ";
	int index = 0;
	while (index < fields.size()) {
	    query = query + "`" + fields.get(index) + "`";
	    if (index < fields.size() - 1)
		query = query + ", ";
	    index++;
	}
	query = query + " from " + tname + ";";
	return query(query);

    }

    public ResultSet getValues(String str) {
	String query = "select * from " + str;
	return query(query);
    }

    public void insertValue(String table, String que) {
	String query = "Insert into " + table + " Values (" + que + ");";
	try {
	    Statement stmt = con.createStatement();
	    stmt.execute(query);
	} catch (SQLException e) {
	    throw new IllegalStateException(e);
	}
    }

    public ResultSet query(String query) {
	try {
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    // stmt.close();
	    return rs;
	} catch (SQLException e) {
	    throw new IllegalStateException(e);
	}
    }

    public ResultSet showColums(String str) {
	return query("show columns from " + str);
    }

    public ResultSet showDatabases() {
	String query = "show databases";
	return query(query);
    }

    public ResultSet showTables(String str) {
	String query = "show full Tables from " + str;
	return query(query);
    }

    private void createTempConnection(String jdbcURL, String dbUser,
	    String dbPassword) {
	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();
	    con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
	    isConnected = true;
	} catch (IllegalAccessException e) {
	    isConnected = false;
	    e.printStackTrace();
	} catch (InstantiationException e) {
	    isConnected = false;
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    isConnected = false;
	    e.printStackTrace();
	} catch (SQLException e) {
	    isConnected = false;
	    e.printStackTrace();
	}
    }
}
