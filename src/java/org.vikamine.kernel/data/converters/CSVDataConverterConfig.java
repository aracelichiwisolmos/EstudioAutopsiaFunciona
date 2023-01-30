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

package org.vikamine.kernel.data.converters;

import java.text.NumberFormat;

/**
 * 
 * {@link CSVDataConverterConfig} provides CSV options used for the conversion
 * in {@link CSVDataConverter}.
 * 
 * @author atzmueller, lemmerich
 * 
 */
public class CSVDataConverterConfig {
    public char separator;
    public char quote;
    public char comment;
    public NumberFormat numberFormat = CSVDataConverter
	    .getStandardNumberFormat();

    public CSVDataConverterConfig() {
    }

    public CSVDataConverterConfig(char separator, char quote,
	    NumberFormat numberFormat, char comment) {
	super();
	this.separator = separator;
	this.quote = quote;
	this.numberFormat = numberFormat;
	this.comment = comment;
    }

}
