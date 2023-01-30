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
 */package org.vikamine.kernel.data.converters;

public class GenericDataTable {

    protected int height;
    protected int width;
    protected String[] header;
    protected String[][] content;
    protected boolean withHeader = false;

    public GenericDataTable(int height, int width) {
	this(height, width, false);
    }

    public GenericDataTable(int height, int width, boolean withHeader) {
	super();
	this.width = width;
	this.height = height;
	this.withHeader = withHeader;
	if (this.withHeader) {
	    header = new String[width];
	}
	content = new String[height][width];
    }

    public String[][] getContent() {
	return content;
    }

    public String getContent(int row, int column) {
	return content[row][column];
    }

    public String[] getRow(int row) {
	return content[row];
    }

    public void setRow(int row, String[] rowContent) {
	for (int i = 0; i < width; i++) {
	    this.content[row][i] = rowContent[i];
	}
    }

    public String[] getHeader() {
	return header;
    }

    public void setHeader(String[] header) {
	this.header = header;
    }

    public void setContent(int row, int column, String value) {
	if (row >= height) {
	    throw new ArrayIndexOutOfBoundsException(" y-value too high: "
		    + row + ", maxHeight was: " + (height - 1));
	}
	if (column >= width) {
	    throw new ArrayIndexOutOfBoundsException(" x-value too high: "
		    + column + ", max-Width was: " + (width - 1));
	}
	content[row][column] = value;
    }

    protected void setContent(int height, int width, String[][] content) {
	this.height = height;
	this.width = width;
	this.content = content;
    }

    public boolean isWithHeader() {
	return withHeader;
    }

    public void setWithHeader(boolean noHeader) {
	this.withHeader = noHeader;
    }

    public int size() {
	return height;
    }
}