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
 * Created on 03.05.2004
 * 
 */
package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A {@link DataRecordSet} represents a dataset.
 * 
 * @author atzmueller
 */
public class DataRecordSet implements IDataRecordSet {

    protected String relationName;

    protected List<Attribute> attributes;

    private final Map<Attribute, Integer> attributeIndices = new HashMap<Attribute, Integer>();

    protected List<DataRecord> dataRecords;

    public DataRecordSet(DataRecordSet dataset, int capacity) {
	if (capacity < 0) {
	    capacity = 0;
	}
	relationName = dataset.relationName;
	attributes = new ArrayList<Attribute>(dataset.attributes);
	dataRecords = new ArrayList<DataRecord>(capacity);
    }

    public DataRecordSet(String name, List<Attribute> attributes) {
	relationName = name;
	this.attributes = new ArrayList<Attribute>(attributes);
	dataRecords = new ArrayList<DataRecord>();
    }

    public DataRecordSet(String name, List<Attribute> attributes, int capacity) {
	relationName = name;
	this.attributes = new ArrayList<Attribute>(attributes);
	dataRecords = new ArrayList<DataRecord>(capacity);
    }

    public DataRecordSet(String name, List<Attribute> attributes,
	    List<DataRecord> dataRecords) {
	relationName = name;
	this.attributes = new ArrayList<Attribute>(attributes);
	this.dataRecords = new ArrayList<DataRecord>(dataRecords);
	for (DataRecord d : this.dataRecords)
	    d.setDataset(this);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#add(org.vikamine.kernel.data.DataRecord)
     */
    @Override
    public DataRecord add(DataRecord instance) {
	DataRecord newInstance = instance.copy();
	newInstance.setDataset(this);
	dataRecords.add(newInstance);

	return newInstance;
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#attributeIterator()
     */
    @Override
    public Iterator<Attribute> attributeIterator() {
	return attributes.iterator();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#compactify()
     */
    @Override
    public void compactify() {
	if (dataRecords instanceof ArrayList<?>)
	    ((ArrayList<DataRecord>) dataRecords).trimToSize();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#delete()
     */
    @Override
    public void delete() {
	dataRecords.clear();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#delete(int)
     */
    @Override
    public void delete(int index) {
	dataRecords.remove(index);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#deleteAttributeAt(int)
     */
    @Override
    public void deleteAttributeAt(int position) {
	if ((position < 0) || (position >= attributes.size())) {
	    throw new IllegalArgumentException("Index out of range");
	}
	attributes.remove(position);
	for (DataRecord instance : dataRecords) {
	    instance.deleteAttributeAt(position);
	}
	attributeIndices.clear();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#get(int)
     */
    @Override
    public DataRecord get(int index) {
	return dataRecords.get(index);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getAttribute(int)
     */
    @Override
    public Attribute getAttribute(int index) {
	return attributes.get(index);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getAttribute(java.lang.String)
     */
    @Override
    public Attribute getAttribute(String name) {
	for (int i = 0; i < getNumAttributes(); i++) {
	    if (getAttribute(i).getId().equals(name)) {
		return getAttribute(i);
	    }
	}
	return null;
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getIndex(org.vikamine.kernel.data.Attribute)
     */
    @Override
    public int getIndex(Attribute attribute) {
	return getCachedIndex(attribute);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getNumAttributes()
     */
    @Override
    public int getNumAttributes() {
	return attributes.size();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getNumInstances()
     */
    @Override
    public int getNumInstances() {
	return dataRecords.size();
    }

    // @Deprecated
    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getRelationName()
     */
    @Override
    public String getRelationName() {
	return relationName;
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#insertAttributeAt(org.vikamine.kernel.data.Attribute, int)
     */
    @Override
    public void insertAttributeAt(Attribute att, int position) {
	if ((position < 0) || (position > attributes.size())) {
	    throw new IllegalArgumentException("Index out of range");
	}
	// Attribute attCopy = (Attribute) att.copy();
	attributes.add(position, att);
	for (int i = 0; i < getNumInstances(); i++) {
	    get(i).insertAttributeAt(position);
	}
	attributeIndices.clear();
    }

    @Override
    public Iterator<DataRecord> iterator() {
	return dataRecords.iterator();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#updateAttribute(org.vikamine.kernel.data.Attribute, int)
     */
    @Override
    public void updateAttribute(Attribute att, int position) {
	if ((position < 0) || (position > attributes.size())) {
	    throw new IllegalArgumentException("Index out of range");
	}
	if (!attributes.get(position).getId().equals(att.getId())) {
	    throw new IllegalArgumentException("Different attribute-ids");
	}
	attributes.set(position, att);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#printInstances(org.vikamine.kernel.data.Ontology)
     */
    @Override
    public String printInstances(Ontology om) {
	StringBuffer text = new StringBuffer();
	text.append("@relation").append(" ")
		.append(org.vikamine.kernel.util.VKMUtil.quote(relationName))
		.append("\n\n");
	for (int i = 0; i < getNumAttributes(); i++) {
	    text.append(getAttribute(i)).append("\n");
	}
	text.append("\n").append("@data").append("\n");
	if (om == null) {
	    for (int i = 0; i < getNumInstances(); i++) {
		text.append(get(i));
		if (i < getNumInstances() - 1) {
		    text.append('\n');
		}
	    }
	} else {
	    for (Iterator<DataRecord> iter = om.getDataView()
		    .instanceIterator(); iter.hasNext();) {
		text.append(iter.next());
		if (iter.hasNext()) {
		    text.append('\n');
		}
	    }
	}
	return text.toString();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#setRelationName(java.lang.String)
     */
    @Override
    public void setRelationName(String newName) {
	relationName = newName;
    }

    @Override
    public String toString() {
	return printInstances(null);
    }

    protected int getCachedIndex(Attribute attribute) {
	Integer index = attributeIndices.get(attribute);
	if (index == null) {
	    index = attributes.indexOf(attribute);
	    attributeIndices.put(attribute, index);
	}
	return index;
    }

    protected void clearAttributeIndexCache() {
	attributeIndices.clear();
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getAttributesUnmodifiable()
     */
    @Override
    public List<Attribute> getAttributesUnmodifiable() {
	return Collections.unmodifiableList(attributes);
    }

    /* (non-Javadoc)
     * @see org.vikamine.kernel.data.IDataRecordSet#getDataRecordsUnmodifiable()
     */
    @Override
    public List<DataRecord> getDataRecordsUnmodifiable() {
	return Collections.unmodifiableList(dataRecords);
    }
}
