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

package org.vikamine.kernel.data;

/**
 * A {@link FullDataRecord} represents a fully instantiated data record, in
 * contrast to a sparse representation (SparseDataRecord).
 * 
 * @author atzmueller
 * 
 */
public class FullDataRecord extends AbstractDataRecord {

    protected double[] attributeValues;

    protected FullDataRecord() {
	super();
    }

    protected FullDataRecord(FullDataRecord instance) {
	this();
	attributeValues = instance.attributeValues;
	this.weight = instance.weight;
	recordSet = null;
    }

    public FullDataRecord(double weight, double[] attValues) {
	this();
	attributeValues = attValues;
	this.weight = weight;
	recordSet = null;
    }

    // public DataRecord(int numAttributes) {
    // this();
    // attributeValues = new double[numAttributes];
    // for (int i = 0; i < attributeValues.length; i++) {
    // attributeValues[i] = Value.MISSING_VALUE;
    // }
    // weight = 1;
    // recordSet = null;
    // }

    @Override
    public DataRecord copy() {
	FullDataRecord result = new FullDataRecord(this);
	result.recordSet = recordSet;
	result.setDataset(getDataset());
	return result;
    }

    @Override
    public void deleteAttributeAt(int position) {
	if (recordSet != null
		&& recordSet.getNumAttributes() >= attributeValues.length) {
	    throw new RuntimeException("Instance has access to a dataset!");
	}
	forceDeleteAttributeAt(position);
    }

    protected void forceDeleteAttributeAt(int position) {
	double[] newValues = new double[attributeValues.length - 1];

	System.arraycopy(attributeValues, 0, newValues, 0, position);
	if (position < attributeValues.length - 1) {
	    System.arraycopy(attributeValues, position + 1, newValues,
		    position, attributeValues.length - (position + 1));
	}
	attributeValues = newValues;
    }

    public void forceInsertAttributeAt(int position) {
	double[] newValues = new double[attributeValues.length + 1];
	System.arraycopy(attributeValues, 0, newValues, 0, position);
	newValues[position] = Value.MISSING_VALUE;
	System.arraycopy(attributeValues, position, newValues, position + 1,
		attributeValues.length - position);
	attributeValues = newValues;
    }

    @Override
    public int getNumAttributes() {
	return attributeValues.length;
    }

    @Override
    public final double getValue(int attIndex) {
	return attributeValues[attIndex];
    }

    @Override
    public void insertAttributeAt(int position) {
	// if (recordSet != null
	// && recordSet.getNumAttributes() <= attributeValues.length) {
	// throw new RuntimeException("Instance has access to a dataset!");
	// }
	// if ((position < 0) || (position > getNumAttributes())) {
	// throw new IllegalArgumentException(
	// "Can't insert attribute: index out " + "of range");
	// }
	forceInsertAttributeAt(position);
    }

    @Override
    public final void setValue(int attIndex, double value) {
	attributeValues[attIndex] = value;
    }

}
