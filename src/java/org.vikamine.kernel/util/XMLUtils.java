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

/**
 * Utility class for xml-text-conversion
 * 
 * @author Tobias Vogele, atzmueller
 */
public class XMLUtils {

    public static String prepareForCDATA(String text) {
	String res = text;
	res = res.replaceAll("\\u005b", "&#005b;"); // [
	res = res.replaceAll("\\u005d", "&#005d;"); // ]
	return res;
    }

    public static String prepareFromCDATA(String text) {
	text = text.replaceAll("&#005b;", "\u005b"); // [
	text = text.replaceAll("&#005d;", "\u005d"); // ]
	return text;
    }

    /**
     * Interfering xml-controlling characters (like "&lt;", "&gt;", """) will be
     * replaced by valid xml-strings.
     * 
     * @param s
     *            String that will be used e.g. as xml-attribute-value
     * @return String with xml-compliant text
     */
    public static String convertToXMLCompliantText(String s) {
	// right characters to metastrings
	s = s.replaceAll("&amp;", "~°°~1");
	s = s.replaceAll("&apos;", "~°°~2");
	s = s.replaceAll("&quot;", "~°°~3");
	s = s.replaceAll("&gt;", "~°°~4");
	s = s.replaceAll("&lt;", "~°°~5");

	// wrong characters to metastrings
	s = s.replaceAll("&", "~°°~1");
	s = s.replaceAll("'", "~°°~2");
	s = s.replaceAll("\"", "~°°~3");
	s = s.replaceAll(">", "~°°~4");
	s = s.replaceAll("<", "~°°~5");

	// metastrings to right characters
	s = s.replaceAll("~°°~1", "&amp;");
	s = s.replaceAll("~°°~2", "&apos;");
	s = s.replaceAll("~°°~3", "&quot;");
	s = s.replaceAll("~°°~4", "&gt;");
	s = s.replaceAll("~°°~5", "&lt;");

	return (s);
    }

    /**
     * Interfering xml-controlling characters (like "&lt;", "&gt;", """) and
     * special characters (like german umlauts) will be replaced by valid
     * html-strings, that can be interpreted by a browser without using the
     * "iso-8859-1"-standard.
     * 
     * @param s
     *            String that will be printed within a html-page
     * @return String with html-compliant text
     */
    public static String convertToHTMLCompliantText(String s) {
	// first, it has to be xml-compliant
	s = convertToXMLCompliantText(s);

	// then replace special characters
	s = s.replaceAll("ß", "&szlig;");
	s = s.replaceAll("ä", "&auml;");
	s = s.replaceAll("ü", "&uuml;");
	s = s.replaceAll("ö", "&ouml;");
	s = s.replaceAll("Ä", "&Auml;");
	s = s.replaceAll("Ü", "&Uuml;");
	s = s.replaceAll("Ö", "&Ouml;");
	s = s.replaceAll("°", "&deg;");
	s = s.replaceAll("µ", "&micro;");
	s = s.replaceAll("&apos;", "'");
	return (s);
    }

}
