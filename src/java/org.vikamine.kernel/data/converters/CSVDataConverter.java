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
 * Created on 30.06.2004
 *
 */
package org.vikamine.kernel.data.converters;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeBuilder;
import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.FullDataRecord;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.util.ProgressBarPopupUpdater;

/**
 * {@link CSVDataConverter} processes a {@link DataTable} creating a
 * {@link DataRecordSet} according to a given {@link CSVDataConverterConfig}
 * that provides options for the conversion.
 * 
 * @author Martin Becker, atzmueller
 */
public class CSVDataConverter {

    private static final int MAX_INTEGER_DIGITS = 16;
    private static final int MAX_FRACTION_DIGITS = 16;
    private static final Locale LOCALE_STANDARD = new Locale("en");
    private static final String MISSING_VALUE_STRING = "?";

    /**
     * Create the full data set.
     * 
     * @return the structure of the data set as an empty set of MInstances
     * @exception IOException
     *                if there is no source or parsing fails
     */
    public static DataRecordSet process(GenericDataTable csvTable,
	    NumberFormat numberFormat, String dataSetName) throws IOException {

	// headers
	String[] headers = csvTable.getHeader();
	// calculate the max capacity/iterations for the progressbar
	ProgressBarPopupUpdater.init(csvTable.size() + headers.length
		+ (csvTable.size() * headers.length) * 3);

	// parse lines
	List<List> parsedLines = new ArrayList<List>(csvTable.size());
	for (int i = 0; i < csvTable.size(); i++) {
	    ProgressBarPopupUpdater.updateProgress();
	    List tmpLine = new ArrayList(csvTable.getHeader().length);

	    for (String s : csvTable.getRow(i)) {
		s = s.trim();
		if (s.equals("")) {
		    tmpLine.add(MISSING_VALUE_STRING); // set as missing value
		} else {
		    ParsePosition parsePosition = new ParsePosition(0);
		    Number number = numberFormat.parse(s, parsePosition);
		    if (number != null
			    && s.length() == parsePosition.getIndex()) {
			// parsing successful
			tmpLine.add(number.doubleValue());
		    } else {
			Double test = null;
			try {
			    test = Double.valueOf(s);
			} catch (NumberFormatException e) {
			    // ignore
			}
			if (test != null) {
			    tmpLine.add(test.doubleValue());
			} else {
			    // parsing failed
			    tmpLine.add(prepareStringValue(s));
			}
		    }
		}
	    }
	    
	    parsedLines.add(tmpLine);
	}

	// determine attribute types
	boolean[] isAttributeNominal = new boolean[headers.length];
	for (int lineIndex = 0; lineIndex < parsedLines.size(); lineIndex++) {
	    for (int attributeIndex = 0; attributeIndex < headers.length; attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		if (!isAttributeNominal[attributeIndex]) {
		    Object attributeValue = parsedLines.get(lineIndex).get(
			    attributeIndex);

		    if (attributeValue instanceof String) {
			String castedValue = (String) attributeValue;

			if (!castedValue.equals(MISSING_VALUE_STRING)) {
			    isAttributeNominal[attributeIndex] = true;
			}
		    }
		}
	    }
	}

	// determine domain of nominal values
	// (kept separate from the code above to ensure readability)
	List<Set<String>> preDomains = new ArrayList<Set<String>>();
	for (int i = 0; i < headers.length; i++) {
	    preDomains.add(new LinkedHashSet<String>(10));
	}

	for (int lineIndex = 0; lineIndex < parsedLines.size(); lineIndex++) {
	    for (int attributeIndex = 0; attributeIndex < headers.length; attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		if (isAttributeNominal[attributeIndex]) {
		    Object attributeValue = parsedLines.get(lineIndex).get(
			    attributeIndex);

		    String castedValue;
		    if (attributeValue instanceof String) {
			castedValue = (String) attributeValue;
		    } else {
			castedValue = attributeValue.toString();
		    }

		    if (!castedValue.equals(MISSING_VALUE_STRING)) {
			Set<String> tempDomain = preDomains.get(attributeIndex);

			if (!tempDomain.contains(castedValue)) {
			    tempDomain.add(castedValue);
			}
		    }
		}
	    }
	}

	String[][] sortedDomains = new String[headers.length][];
	int i = 0;
	for (Set<String> preDomain : preDomains) {
	    String[] domain = new String[preDomain.size()];
	    int j = 0;
	    for (String val : preDomain) {
		domain[j] = val;
		j++;
	    }
	    Arrays.sort(domain);
	    sortedDomains[i] = domain;
	    i++;
	}

	// build data record set
	// - build attributes
	List<Attribute> attributes = new ArrayList<Attribute>(headers.length);
	AttributeBuilder attBuilder = new AttributeBuilder();

	for (int attributeIndex = 0; attributeIndex < headers.length; attributeIndex++) {
	    ProgressBarPopupUpdater.updateProgress();
	    String attributeName = headers[attributeIndex];

	    if (isAttributeNominal[attributeIndex]) {
		attBuilder.buildNominalAttribute(attributeName,
			Arrays.asList(sortedDomains[attributeIndex]));
		attBuilder.buildNominalValues();
	    } else {
		attBuilder.buildNumericAttribute(attributeName);
	    }

	    attributes.add(attBuilder.getAttribute());
	}

	// - create the data record set
	DataRecordSet dataSet = new DataRecordSet(dataSetName, attributes,
		parsedLines.size());

	// - fill data record set
	for (int lineIndex = 0; lineIndex < parsedLines.size(); lineIndex++) {

	    List parsedLine = parsedLines.get(lineIndex);
	    double[] values = new double[dataSet.getNumAttributes()];

	    for (int attributeIndex = 0; attributeIndex < headers.length; attributeIndex++) {
		ProgressBarPopupUpdater.updateProgress();
		Object value = parsedLine.get(attributeIndex);

		if (isAttributeNominal[attributeIndex]) {
		    String castedValue;
		    if (value instanceof String)
			castedValue = (String) value;
		    else
			castedValue = value.toString();

		    if (castedValue.equals(MISSING_VALUE_STRING)) {
			values[attributeIndex] = Value.missingValue();
		    } else {
			String[] domain = sortedDomains[attributeIndex];
			int index = Arrays.binarySearch(domain, castedValue);
			values[attributeIndex] = index;
		    }
		} else if (value instanceof Double) {
		    values[attributeIndex] = (Double) value;
		} else if (value.equals(MISSING_VALUE_STRING)) {
		    values[attributeIndex] = Value.missingValue();
		} else {
		    // does not happen due to previous processing
		}
	    }

	    dataSet.add(new FullDataRecord(1.0, values));
	}

	// done!
	return dataSet;
    }

    /**
     * Escapes tabs and spaces in attribute names as "_".
     * 
     * @param s
     * @return
     */
    private static String prepareStringValue(String s) {
	String tmpString = s.replace(' ', '_');
	tmpString = tmpString.replace('\t', '_');
	return tmpString;
    }

    public static CSVDataConverterConfig getStandardCsvConfiguration() {
	CSVDataConverterConfig csvConfiguration = new CSVDataConverterConfig();
	csvConfiguration.separator = ',';
	csvConfiguration.quote = '\"';
	csvConfiguration.numberFormat = getStandardNumberFormat();
	csvConfiguration.comment = '#';
	return csvConfiguration;
    }

    public static CSVDataConverterConfig getGermanCsvConfiguration() {
	CSVDataConverterConfig csvConfiguration = new CSVDataConverterConfig();
	csvConfiguration.separator = ';';
	csvConfiguration.quote = '\"';
	csvConfiguration.numberFormat = getStandardNumberFormat(Locale.GERMAN);
	csvConfiguration.comment = '#';
	return csvConfiguration;
    }

    public static NumberFormat getStandardNumberFormat(Locale locale) {
	NumberFormat numberFormat = NumberFormat.getInstance(locale);
	numberFormat.setMaximumIntegerDigits(MAX_INTEGER_DIGITS);
	numberFormat.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
	return numberFormat;
    }

    public static NumberFormat getStandardNumberFormat() {
	return getStandardNumberFormat(LOCALE_STANDARD);
    }
}
