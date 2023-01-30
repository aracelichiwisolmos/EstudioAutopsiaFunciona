/*
t *  This file is part of the VKM-Kernel library.
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

package org.vikamine.kernel.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.data.AttributeBuilder;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Ontology;

/**
 * This class demonstrates, how you can create and use a sampling attribute to
 * randomly select a subset of your data (e.g., for increased performance)
 * 
 * @author lemmerich
 * @date 2012-10
 * 
 */
public class SampleAttributeCreator {
    public static final String VALUE_NAME_TRUE = "true";
    public static final String VALUE_NAME_FALSE = "false";

    public static NominalAttribute getBinaryAttribute(String name) {
	// the values of the new attribute, in this case we create a boolean
	// attribte
	// remember the index of your values!
	List<String> rawValuesList = new ArrayList<String>();
	rawValuesList.add(VALUE_NAME_FALSE);
	rawValuesList.add(VALUE_NAME_TRUE);

	// create the attribute
	AttributeBuilder attBuilder = new AttributeBuilder();
	attBuilder.buildNominalAttribute(name, rawValuesList);
	attBuilder.buildNominalValues();

	NominalAttribute binAttribute = (NominalAttribute) attBuilder
		.getAttribute();
	binAttribute.setDescription(name);
	return binAttribute;
    }

    public static NominalAttribute addSampleAttribute(Ontology onto,
	    String name, int sampleSize) {
	IDataRecordSet dataset = onto.getDataset();
	NominalAttribute sampleAtt = getBinaryAttribute(name);

	onto.addAttribute(sampleAtt);
	dataset.insertAttributeAt(sampleAtt, dataset.getNumAttributes());
	int indexOfSampleAttribute = dataset.getIndex(sampleAtt);

	// get the array of indices, for which the value should be true
	List<Integer> setIndices = getSetIndizes(sampleSize,
		dataset.getNumInstances());

	Iterator<Integer> iterSetIndices = setIndices.iterator();
	int nextSetIndex = iterSetIndices.next();
	int currentIndex = 0;
	for (DataRecord dr : dataset) {
	    if (currentIndex == nextSetIndex) {
		dr.setValue(indexOfSampleAttribute,
			SampleAttributeCreator.VALUE_NAME_TRUE);

		// update index, if no more index to set, we are ready
		if (iterSetIndices.hasNext()) {
		    nextSetIndex = iterSetIndices.next();
		} else {
		    break;
		}

		// index is not set ==> set false value
	    } else {
		dr.setValue(indexOfSampleAttribute,
			SampleAttributeCreator.VALUE_NAME_FALSE);
	    }

	    currentIndex++;
	}

	return sampleAtt;
    }

    private static List<Integer> getSetIndizes(int sampleSize, int numInstances) {
	List<Integer> listAll = new ArrayList<Integer>();
	for (int i = 0; i < numInstances; i++) {
	    listAll.add(i);
	}
	Collections.shuffle(listAll);

	List<Integer> result = new ArrayList<Integer>();
	for (int i = 0; i < sampleSize; i++) {
	    result.add(listAll.get(i));
	}
	Collections.sort(result);
	return result;
    }

}
