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
 * Created on 04.05.2004
 * 
 */
package org.vikamine.kernel.data.converters;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeBuilder;
import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.DateAttribute;
import org.vikamine.kernel.data.FullDataRecord;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.SparseDataRecord;
import org.vikamine.kernel.data.StringAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.util.ProgressBarPopupUpdater;

/**
 * {@link KeelDataConverter} creates a ({@link DataRecordSet} given a
 * {@link Reader}
 * 
 * @author atzmueller
 */
public class KeelDataConverter {

    private boolean importStringAttributes = true;

    private boolean isSparse = false;

    private final Collection<Integer> ignoredAttributeIndices = new LinkedList<Integer>();

    private int numAttributesInDataset = 0;

    private DataRecordSet dataset;

    private String datasetName;

    private final Reader reader;

    private final int initialNumInstancesCapacity;

    private final AttributeBuilder basicBuilder;

    private static final String KEEL_RELATION_START = "@relation";

    private static final String KEEL_DATA_START = "@data";

    private static final String KEEL_ATTRIBUTE_START = "@attribute";

    private static final String KEEL_ATTRIBUTE_TYPE_INTEGER = "integer";

    private static final String KEEL_ATTRIBUTE_TYPE_REAL = "real";

    private static final String KEEL_ATTRIBUTE_TYPE_NUMERIC = "numeric";

    private static final String KEEL_ATTRIBUTE_STRING = "string";

    private static final String KEEL_ATTRIBUTE_TYPE_DATE = "date";

    private KeelDataConverter(Reader reader, int initialNumInstancesCapacity) {
	super();
	this.reader = reader;
	this.initialNumInstancesCapacity = initialNumInstancesCapacity;
	this.basicBuilder = new AttributeBuilder();
    }

    public KeelDataConverter(Reader reader) {
	this(reader, 1000);
    }

    public DataRecordSet createDataRecordSet() throws IOException {
	StreamTokenizer tokenizer = new StreamTokenizer(reader);
	initTokenizer(tokenizer);

	List<Attribute> attributes = readKeelHeader(tokenizer);
	dataset = new DataRecordSet(datasetName, attributes,
		initialNumInstancesCapacity);

	while (readDataRecord(tokenizer)) {
	    // ...
	}
	dataset.compactify();
	return dataset;
    }

    public IDataRecordSet createSkeletonDataRecordSet() throws IOException {
	StreamTokenizer tokenizer = new StreamTokenizer(reader);
	initTokenizer(tokenizer);

	List<Attribute> attributes = readKeelHeader(tokenizer);
	dataset = new DataRecordSet(datasetName, attributes, 0);
	return dataset;
    }

    private int getNumberOfAttributes() {
	return dataset.getNumAttributes();
    }

    private Attribute getAttribute(int index) {
	return dataset.getAttribute(index);
    }

    private void initTokenizer(StreamTokenizer tokenizer) {

	tokenizer.resetSyntax();
	tokenizer.whitespaceChars(0, ' ');
	tokenizer.wordChars(' ' + 1, '\u00FF');
	tokenizer.whitespaceChars(',', ',');
	tokenizer.commentChar('%');
	tokenizer.quoteChar('"');
	tokenizer.quoteChar('\'');
	tokenizer.ordinaryChar('{');
	tokenizer.ordinaryChar('}');
	tokenizer.eolIsSignificant(true);
    }

    private void readTillEOL(StreamTokenizer tokenizer) throws IOException {

	while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
	    // ...
	}
	tokenizer.pushBack();
    }

    private List<Attribute> readKeelHeader(StreamTokenizer tokenizer)
	    throws IOException {
	HashMap<String, Boolean> alreadyReadNamesHash = new HashMap<String, Boolean>();

	List<Attribute> attributes = new ArrayList<Attribute>();
	String attributeName;

	// read name of relation.
	ReaderUtils.readFirstToken(tokenizer);
	if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
	    ReaderUtils.throwError(tokenizer, "premature end of file");
	}
	if (KEEL_RELATION_START.equalsIgnoreCase(tokenizer.sval)) {
	    ReaderUtils.readNextToken(tokenizer, true);
	    datasetName = tokenizer.sval;
	    ReaderUtils.readLastToken(tokenizer, false);
	} else {
	    ReaderUtils.throwError(tokenizer, "keyword " + KEEL_RELATION_START
		    + " expected");
	}

	// read attribute declarations.
	ReaderUtils.readFirstToken(tokenizer);
	if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
	    ReaderUtils.throwError(tokenizer, "premature end of file");
	}

	while (KEEL_ATTRIBUTE_START.equalsIgnoreCase(tokenizer.sval)) {
	    ProgressBarPopupUpdater.updateProgress();
	    // read attribute name.
	    ReaderUtils.readNextToken(tokenizer, true);
	    attributeName = tokenizer.sval.trim();

	    if (alreadyReadNamesHash.get(attributeName) != null)
		ReaderUtils.throwError(tokenizer,
			"Duplicate attribute entry for " + attributeName);
	    alreadyReadNamesHash.put(attributeName, true);

	    ReaderUtils.readNextToken(tokenizer, true);

	    // test if attribute is nominal.
	    if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
		// Attribute is not nominal

		// Attribute is real, integer, or string.
		if (tokenizer.sval.startsWith(KEEL_ATTRIBUTE_TYPE_REAL)
			|| tokenizer.sval
				.startsWith(KEEL_ATTRIBUTE_TYPE_INTEGER)
			|| tokenizer.sval
				.startsWith(KEEL_ATTRIBUTE_TYPE_NUMERIC)) {
		    basicBuilder.buildNumericAttribute(attributeName);
		    attributes.add(basicBuilder.getAttribute());
		    numAttributesInDataset++;
		    readTillEOL(tokenizer);
		} else if (tokenizer.sval
			.equalsIgnoreCase(KEEL_ATTRIBUTE_STRING)) {
		    if (isImportStringAttributes()) {
			basicBuilder.buildStringAttribute(attributeName);
			attributes.add(basicBuilder.getAttribute());
		    } else {
			ignoredAttributeIndices.add(numAttributesInDataset);
		    }
		    numAttributesInDataset++;
		    readTillEOL(tokenizer);
		} else if (tokenizer.sval
			.equalsIgnoreCase(KEEL_ATTRIBUTE_TYPE_DATE)) {
		    String format = null;
		    if (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
			if ((tokenizer.ttype != StreamTokenizer.TT_WORD)
				&& (tokenizer.ttype != '\'')
				&& (tokenizer.ttype != '\"')) {
			    ReaderUtils.throwError(tokenizer,
				    "not a valid date format");
			}
			format = tokenizer.sval;
			readTillEOL(tokenizer);
		    } else {
			tokenizer.pushBack();
		    }
		    basicBuilder.buildDateAttribute(attributeName, format);
		    attributes.add(basicBuilder.getAttribute());
		    numAttributesInDataset++;

		} else {
		    ReaderUtils.throwError(tokenizer,
			    "no valid attribute type or invalid "
				    + "enumeration");
		}
	    } else {
		// attribute is nominal.
		List<String> attributeValues = new ArrayList<String>();
		tokenizer.pushBack();

		// values for nominal attribute.
		if (tokenizer.nextToken() != '{') {
		    ReaderUtils.throwError(tokenizer,
			    "{ expected at beginning of enumeration");
		}
		while (tokenizer.nextToken() != '}') {
		    if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
			ReaderUtils.throwError(tokenizer,
				"} expected at end of enumeration");
		    } else {
			attributeValues.add(tokenizer.sval.trim());
		    }
		}
		if (attributeValues.size() == 0) {
		    ReaderUtils
			    .throwError(tokenizer, "no nominal values found");
		}

		basicBuilder.buildNominalAttribute(attributeName,
			attributeValues);
		basicBuilder.buildNominalValues();
		attributes.add(basicBuilder.getAttribute());
		numAttributesInDataset++;
	    }
	    ReaderUtils.readLastToken(tokenizer, false);
	    ReaderUtils.readFirstToken(tokenizer);
	    if (tokenizer.ttype == StreamTokenizer.TT_EOF)
		ReaderUtils.throwError(tokenizer, "premature end of file");
	}

	while (!KEEL_DATA_START.equalsIgnoreCase(tokenizer.sval)) {
	    tokenizer.nextToken();
	}

	// test if data part follows. We can't easily check for EOL.
	if (!KEEL_DATA_START.equalsIgnoreCase(tokenizer.sval)) {
	    ReaderUtils.throwError(tokenizer, "keyword " + KEEL_DATA_START
		    + " expected");
	}

	// test if any attributes have been declared.
	if (attributes.size() == 0) {
	    ReaderUtils.throwError(tokenizer, "no attributes declared");
	}
	return attributes;
    }

    private double parseDateAttributeValue(DateAttribute dateAttribute,
	    String possibleDate) throws ParseException {
	double parsedDate = 0;
	try {
	    parsedDate = dateAttribute.parseDate(possibleDate);
	} catch (ParseException e) {
	    // try some more ...
	    ParseException lastParseException = null;
	    boolean dateParsingSucceeded = false;
	    String[] possibleDateFormats = new String[] { "dd/MM/yy",
		    "dd/MM/yyyy", "dd.MM.yy", "dd.MM.yyyy",
		    "yyyy-MM-dd'T'HH:mm:ss" };
	    for (int i = 0; i < possibleDateFormats.length; i++) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
			possibleDateFormats[i]);
		dateFormat.setLenient(true);
		try {
		    parsedDate = dateFormat.parse(possibleDate).getTime();
		    dateParsingSucceeded = true;
		} catch (ParseException ex) {
		    lastParseException = ex;
		}
		if (dateParsingSucceeded)
		    return parsedDate;
	    }
	    if (!dateParsingSucceeded) {
		if (lastParseException == null) {
		    lastParseException = new ParseException(
			    "Unknown data format " + possibleDate, 0);
		}
		Logger.getLogger(getClass().getName()).throwing(
			getClass().getName(), "parseDateAttributeValue",
			lastParseException);
		throw lastParseException;
	    }
	}
	return parsedDate;
    }

    private boolean readSparseDataRecord(StreamTokenizer tokenizer)
	    throws IOException {

	boolean checkForCR = true;
	SparseDataRecord sparse = new SparseDataRecord();

	ReaderUtils.readNextToken(tokenizer, true);
	while ((tokenizer.ttype != '}')
		&& (tokenizer.ttype != StreamTokenizer.TT_EOF)) {
	    int position = Integer.parseInt(tokenizer.sval);
	    ReaderUtils.readNextToken(tokenizer, true);

	    if (tokenizer.ttype == '?') {
		sparse.setMissing(position);
	    } else {

		// test if token is valid.
		if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
		    ReaderUtils.throwError(tokenizer, "not a valid value");
		}
		if (getAttribute(position).isNominal()) {
		    NominalAttribute baAttr = (NominalAttribute) getAttribute(position);
		    // test if value appears in header.
		    double val = baAttr.getIndexOfValue(tokenizer.sval);
		    if (val == -1) {
			ReaderUtils.throwError(tokenizer,
				"nominal value not declared in header for attribute: "
					+ getAttribute(position));
		    }
		    sparse.setValue(position, val);
		} else if (getAttribute(position).isNumeric()) {
		    // test if value is really a number.
		    try {
			double value = Double.parseDouble(tokenizer.sval);
			sparse.setValue(position, value);
		    } catch (NumberFormatException e) {
			ReaderUtils.throwError(tokenizer, "number expected");
		    }
		} else if (getAttribute(position).isString()) {
		    StringAttribute baAttr = (StringAttribute) getAttribute(position);
		    if (isImportStringAttributes()) {
			sparse.setValue(position,
				baAttr.addStringValue(tokenizer.sval));
		    }
		} else if (getAttribute(position).isDate()) {
		    // creating data attributes only works for the default
		    // date format, because it cannot be changed later
		    String possibleDate = tokenizer.sval;
		    try {
			DateAttribute dateAttr = (DateAttribute) getAttribute(position);
			sparse.setValue(position,
				parseDateAttributeValue(dateAttr, possibleDate));
		    } catch (ParseException e) {
			ReaderUtils.throwError(tokenizer, "unparseable date: "
				+ possibleDate);
		    }
		} else {
		    ReaderUtils.throwError(tokenizer,
			    "unknown attribute type in column " + position);
		}

	    }

	    ReaderUtils.readNextToken(tokenizer, true);
	}

	if (checkForCR) {
	    ReaderUtils.readLastToken(tokenizer, true);
	}

	dataset.add(sparse);
	return true;
    }

    private boolean readFullDataRecord(StreamTokenizer tokenizer)
	    throws IOException {
	boolean checkForCR = true;

	double[] instance = new double[getNumberOfAttributes()];
	int index;

	// read values for all attributes.
	int i = 0;
	for (int j = 0; j < numAttributesInDataset; j++) {
	    if (j > 0) {
		ReaderUtils.readNextToken(tokenizer, true);
	    }
	    if (ignoredAttributeIndices.contains(j)) {
		continue;
	    }

	    // test if value is missing.
	    if (tokenizer.ttype == '?') {
		instance[i] = Value.missingValue();
	    } else {

		// test if token is valid.
		if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
		    ReaderUtils.throwError(tokenizer, "not a valid value");
		}
		if (getAttribute(i).isNominal()) {
		    NominalAttribute baAttr = (NominalAttribute) getAttribute(i);
		    // test if value appears in header.
		    index = baAttr.getIndexOfValue(tokenizer.sval);
		    if (index == -1) {
			ReaderUtils.throwError(tokenizer,
				"nominal value not declared in header");
		    }
		    instance[i] = index;
		} else if (getAttribute(i).isNumeric()) {
		    // test if value is really a number.
		    try {
			instance[i] = Double.valueOf(tokenizer.sval)
				.doubleValue();
		    } catch (NumberFormatException e) {
			ReaderUtils.throwError(tokenizer, "number expected");
		    }
		} else if (getAttribute(i).isString()) {
		    StringAttribute baAttr = (StringAttribute) getAttribute(i);
		    if (isImportStringAttributes()) {
			instance[i] = baAttr.addStringValue(tokenizer.sval);
		    }
		} else if (getAttribute(i).isDate()) {
		    // creating data attributes only works for the default
		    // date format, because it cannot be changed later
		    String possibleDate = tokenizer.sval;
		    try {
			DateAttribute dateAttr = (DateAttribute) getAttribute(i);
			instance[i] = parseDateAttributeValue(dateAttr,
				possibleDate);
		    } catch (ParseException e) {
			ReaderUtils.throwError(tokenizer, "unparseable date: "
				+ possibleDate);
		    }
		} else {
		    ReaderUtils.throwError(tokenizer,
			    "unknown attribute type in column " + i);
		}
	    }
	    i++;
	}
	if (checkForCR) {
	    ReaderUtils.readLastToken(tokenizer, true);
	}

	dataset.add(new FullDataRecord(1, instance));
	return true;
    }

    private boolean readDataRecord(StreamTokenizer tokenizer)
	    throws IOException {
	ReaderUtils.readFirstToken(tokenizer);
	if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
	    return false;
	} else if (tokenizer.ttype == '{') {
	    this.isSparse = true;
	    return readSparseDataRecord(tokenizer);
	} else {
	    return readFullDataRecord(tokenizer);
	}
    }

    public boolean isSparse() {
	return this.isSparse;
    }

    public void setSparse(boolean value) {
	this.isSparse = value;
    }

    public boolean isImportStringAttributes() {
	return importStringAttributes;
    }

    public void setImportStringAttributes(boolean importStringAttributes) {
	this.importStringAttributes = importStringAttributes;
    }
}
