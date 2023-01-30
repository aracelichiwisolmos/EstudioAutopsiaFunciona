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
 * Created on 17.03.2004
 * 
 */
package org.vikamine.kernel.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * Various small utility functions.
 * 
 * @author atzmueller
 */
public class VKMUtil {

    /** The small deviation allowed in double comparisons */
    public static final double SMALL = 1e-6;

    private static String lineSeparatorSaver = null;

    public final static String LINE_SEPARATOR_PROPERTY = "line.separator";

    public static void setNewLineAsLinefeedLineSeparator() {
	/***********************************************************************
	 * make cut-and-past to EXCEL This is essential!
	 */
	if (lineSeparatorSaver == null)
	    lineSeparatorSaver = System.getProperty(LINE_SEPARATOR_PROPERTY);
	System.setProperty(LINE_SEPARATOR_PROPERTY, "\n");
    }

    public static void restoreSavedLineSeparator() {
	// this will restore the property
	System.setProperty(LINE_SEPARATOR_PROPERTY, lineSeparatorSaver);
	lineSeparatorSaver = null;
    }

    public static String getFormattedDoubleStringDependingOnLocale(
	    Locale theLocale, double theDouble) {
	return getFormattedDoubleStringDependingOnLocale(theLocale, theDouble,
		3);
    }

    public static String getFormattedDoubleStringDependingOnLocale(
	    Locale theLocale, double theDouble, int fractionDigits) {
	if (Double.isNaN(theDouble)) {
	    return "-";
	}
	if (Double.isInfinite(theDouble)) {
	    return theDouble > 0 ? "Inf" : "- Inf";
	}
	BigDecimal scaledBigDecimal = new BigDecimal(theDouble);
	// heuristic: take one digit more into account for rounding (suitable
	// for percent display!)
	scaledBigDecimal = scaledBigDecimal.setScale(fractionDigits + 1,
		BigDecimal.ROUND_HALF_UP);
	scaledBigDecimal = scaledBigDecimal.setScale(fractionDigits,
		BigDecimal.ROUND_HALF_UP);
	double scaledDouble = scaledBigDecimal.doubleValue();

	NumberFormat formater = NumberFormat.getNumberInstance(theLocale);
	formater.setMinimumFractionDigits(fractionDigits);
	formater.setMaximumFractionDigits(fractionDigits);

	String result = formater.format(scaledDouble);
	return result;
    }

    public static String getFormattedDoubleString(double theDouble) {
	return getFormattedDoubleStringDependingOnLocale(Locale.getDefault(),
		theDouble);
    }

    public static String getFormattedDoubleString(double theDouble,
	    int fractionDigits) {
	return getFormattedDoubleStringDependingOnLocale(Locale.getDefault(),
		theDouble, fractionDigits);
    }

    public static String formatDoubleToMinDigitsOrExponentialString(double d,
	    int fractionDigits) {
	if (d > Math.pow(0.1, fractionDigits)) {
	    return getFormattedDoubleString(d, fractionDigits);
	} else {
	    NumberFormat format = NumberFormat.getNumberInstance(Locale
		    .getDefault());
	    if (format instanceof DecimalFormat) {
		DecimalFormat decFormat = (DecimalFormat) format;
		decFormat.applyPattern("0.0E0");
		return decFormat.format(d);
	    } else
		return getFormattedDoubleString(d, fractionDigits);
	}
    }

    public static List computePowerSet(List initialList) {
	// compute the powerset recursively:
	// first remove an element, then compute the powerset for the remaining
	// elements, then add new sets formed of all the sets of the powerset
	// plus the current (removed) element, so we have for a set of size n
	// |powerset(set)| = 2^n!

	List result = new LinkedList();
	if (initialList.isEmpty()) {
	    result.add(Collections.EMPTY_LIST);
	    return result;
	}

	Object currentElement = initialList.get(0);
	List tmp = new LinkedList(initialList);
	tmp.remove(currentElement);
	List powerSet = computePowerSet(tmp);
	for (Iterator iter = powerSet.iterator(); iter.hasNext();) {
	    List currentSet = (List) iter.next();
	    result.add(currentSet);
	    List currentSetPlusCurrentElement = new LinkedList(currentSet);
	    currentSetPlusCurrentElement.add(currentElement);
	    result.add(currentSetPlusCurrentElement);
	}

	return result;
    }

    public static <T> List<T> asList(Iterator<T> iterator) {
	List<T> result = new ArrayList();
	while (iterator.hasNext()) {
	    result.add(iterator.next());
	}
	return result;
    }

    public static double getDoubleWithMaxFractionDigits(double d, int digits) {
	if (Double.isInfinite(d) || Double.isNaN(d))
	    return d;

	double div = (int) Math.pow(10, digits);
	double newVal = (Math.round(d * div)) / div;
	return newVal;
    }

    public static boolean stringStartsWithIgnoreCase(String s, String q) {
	if (s.length() < q.length()) {
	    return false;
	}
	return s.substring(0, q.length()).equalsIgnoreCase(q);
    }

    /**
     * Rounds a double and converts it into String.
     * 
     * @param value
     *            the double value
     * @param afterDecimalPoint
     *            the (maximum) number of digits permitted after the decimal
     *            point
     * @return the double as a formatted string
     */
    public static/* @pure@ */String doubleToString(double value,
	    int afterDecimalPoint) {

	StringBuffer stringBuffer;
	double temp;
	int dotPosition;
	long precisionValue;

	temp = value * Math.pow(10.0, afterDecimalPoint);
	if (Math.abs(temp) < Long.MAX_VALUE) {
	    precisionValue = (temp > 0) ? (long) (temp + 0.5) : -(long) (Math
		    .abs(temp) + 0.5);
	    if (precisionValue == 0) {
		stringBuffer = new StringBuffer(String.valueOf(0));
	    } else {
		stringBuffer = new StringBuffer(String.valueOf(precisionValue));
	    }
	    if (afterDecimalPoint == 0) {
		return stringBuffer.toString();
	    }
	    dotPosition = stringBuffer.length() - afterDecimalPoint;
	    while (((precisionValue < 0) && (dotPosition < 1))
		    || (dotPosition < 0)) {
		if (precisionValue < 0) {
		    stringBuffer.insert(1, '0');
		} else {
		    stringBuffer.insert(0, '0');
		}
		dotPosition++;
	    }
	    stringBuffer.insert(dotPosition, '.');
	    if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
		stringBuffer.insert(1, '0');
	    } else if (stringBuffer.charAt(0) == '.') {
		stringBuffer.insert(0, '0');
	    }
	    int currentPos = stringBuffer.length() - 1;
	    while ((currentPos > dotPosition)
		    && (stringBuffer.charAt(currentPos) == '0')) {
		stringBuffer.setCharAt(currentPos--, ' ');
	    }
	    if (stringBuffer.charAt(currentPos) == '.') {
		stringBuffer.setCharAt(currentPos, ' ');
	    }

	    return stringBuffer.toString().trim();
	}
	return "" + value;
    }

    /**
     * Tests if a is equal to b.
     * 
     * @param a
     *            a double
     * @param b
     *            a double
     */
    public static/* @pure@ */boolean eq(double a, double b) {

	return (a - b < SMALL) && (b - a < SMALL);
    }

    /**
     * Quotes a string if it contains special characters.
     * 
     * The following rules are applied:
     * 
     * A character is backquoted version of it is one of
     * <tt>" ' % \ \n \r \t</tt>.
     * 
     * A string is enclosed within single quotes if a character has been
     * backquoted using the previous rule above or contains <tt>{ }</tt> or is
     * exactly equal to the strings <tt>, ? space or ""</tt> (empty string).
     * 
     * A quoted question mark distinguishes it from the missing value which is
     * represented as an unquoted question mark in arff files.
     * 
     * @param string
     *            the string to be quoted
     * @return the string (possibly quoted)
     */
    public static/* @pure@ */String quote(String string) {
	boolean quote = false;

	// backquote the following characters
	if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1)
		|| (string.indexOf('\'') != -1) || (string.indexOf('"') != -1)
		|| (string.indexOf('\\') != -1) || (string.indexOf('\t') != -1)
		|| (string.indexOf('%') != -1)) {
	    string = backQuoteChars(string);
	    quote = true;
	}

	// Enclose the string in 's if the string contains a recently added
	// backquote or contains one of the following characters.
	if ((quote == true) || (string.indexOf('{') != -1)
		|| (string.indexOf('}') != -1) || (string.indexOf(',') != -1)
		|| (string.equals("?")) || (string.indexOf(' ') != -1)
		|| (string.equals(""))) {
	    string = ("'".concat(string)).concat("'");
	}

	return string;
    }

    public static/* @pure@ */String backQuoteChars(String string) {

	int index;
	StringBuffer newStringBuffer;

	// replace each of the following characters with the backquoted version
	char charsFind[] = { '\\', '\'', '\t', '"', '%' };
	String charsReplace[] = { "\\\\", "\\'", "\\t", "\\\"", "\\%" };
	for (int i = 0; i < charsFind.length; i++) {
	    if (string.indexOf(charsFind[i]) != -1) {
		newStringBuffer = new StringBuffer();
		while ((index = string.indexOf(charsFind[i])) != -1) {
		    if (index > 0) {
			newStringBuffer.append(string.substring(0, index));
		    }
		    newStringBuffer.append(charsReplace[i]);
		    if ((index + 1) < string.length()) {
			string = string.substring(index + 1);
		    } else {
			string = "";
		    }
		}
		newStringBuffer.append(string);
		string = newStringBuffer.toString();
	    }
	}

	return VKMUtil.convertNewLines(string);
    }

    /**
     * Converts carriage returns and new lines in a string into \r and \n.
     * 
     * @param string
     *            the string
     * @return the converted string
     */
    public static/* @pure@ */String convertNewLines(String string) {

	int index;

	// Replace with \n
	StringBuffer newStringBuffer = new StringBuffer();
	while ((index = string.indexOf('\n')) != -1) {
	    if (index > 0) {
		newStringBuffer.append(string.substring(0, index));
	    }
	    newStringBuffer.append('\\');
	    newStringBuffer.append('n');
	    if ((index + 1) < string.length()) {
		string = string.substring(index + 1);
	    } else {
		string = "";
	    }
	}
	newStringBuffer.append(string);
	string = newStringBuffer.toString();

	// Replace with \r
	newStringBuffer = new StringBuffer();
	while ((index = string.indexOf('\r')) != -1) {
	    if (index > 0) {
		newStringBuffer.append(string.substring(0, index));
	    }
	    newStringBuffer.append('\\');
	    newStringBuffer.append('r');
	    if ((index + 1) < string.length()) {
		string = string.substring(index + 1);
	    } else {
		string = "";
	    }
	}
	newStringBuffer.append(string);
	return newStringBuffer.toString();
    }

    /**
     * @author lemmerich Returns a new List, that contains all elements of given
     *         Iterable aList, that are instances class filterClass
     */
    public static <T> List<T> filterListOnSubclass(Iterable<? super T> aList,
	    Class<T> filterClass) {
	List<T> result = new ArrayList<T>();
	for (Object o : aList) {
	    if (filterClass.isInstance(o)) {
		result.add((T) o);
	    }
	}
	return result;
    }

    /** Get CPU time in nanoseconds. */
    public static long getCpuTime() {
	ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	return bean.isCurrentThreadCpuTimeSupported() ? bean
		.getCurrentThreadCpuTime() : 0L;
    }

    /**
     * Returns the number of used values in the dataset for numeric attributes,
     * the overall number of values for nominal attirbutes. Returns -1 for
     * whatever strange other cases
     * 
     * @author lemmerich
     * @date 2012-10
     */
    public static int getValueCount(Attribute att, IDataRecordSet dataset) {
	if (att.isNumeric()) {
	    return ((NumericAttribute) att).getUsedValuesCount(dataset);
	} else if (att.isNominal()) {
	    return ((NominalAttribute) att).getValuesCount();
	}
	return -1;
    }
}
