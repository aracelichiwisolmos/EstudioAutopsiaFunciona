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
 * Created on 22.01.2004
 */
package org.vikamine.kernel.util;

import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.Value;

/**
 * Finds distinct values.
 * <p>
 * Usage:
 * <p>
 * <code>
 * DistinctionFinder distinct = new DistinctionFinder(10);
 * for (double d = 0.0; d < 10.0; d += 0.5){
 *   int i = (int) d;
 *   distinct.addValue(i);
 *   if (distinct.isNewValue()){
 *     double value = distinct.getPreviousValue();
 *     double rate = distinct.getPreviousRate();
 *     // store this values
 *   }
 * }
 * </code>
 * 
 * @author Tobias Vogele
 */
public class DistinctionFinder {

    public static class DistinctValue {

	private double value = Double.NaN;

	private int number = 0;

	/**
	 * @return Returns the number.
	 */
	public int getNumber() {
	    return number;
	}

	/**
	 * @param number
	 *            The number to set.
	 */
	public void setNumber(int number) {
	    this.number = number;
	}

	/**
	 * @return Returns the value.
	 */
	public double getValue() {
	    return value;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(double value) {
	    this.value = value;
	}

    }

    private double currentValue = Double.NaN;

    private int currentValueNumber = 0;

    private DistinctValue currentStoredValue = null;

    private int addedNumber = 0;

    private double previousValue = Double.NaN;

    private int previousValueNumber = 0;

    private int totalNumber = -1;

    private boolean compareTolerant = true;

    private List values;

    private boolean storeValues = false;

    public DistinctionFinder() {
	super();
    }

    public DistinctionFinder(boolean storeAllValues) {
	setStoreValues(storeAllValues);
    }

    public DistinctionFinder(int totalNumber) {
	this.totalNumber = totalNumber;
    }

    public void addValue(double value) {
	previousValue = currentValue;
	previousValueNumber = currentValueNumber;
	currentValue = value;
	addedNumber++;
	if (Value.equalValues(previousValue, currentValue, isCompareTolerant())) {
	    currentValueNumber++;
	} else {
	    currentValueNumber = 1;
	}
	if (isStoreValues()) {
	    if (isNewValue() || currentStoredValue == null) {
		currentStoredValue = new DistinctValue();
		currentStoredValue.setNumber(1);
		currentStoredValue.setValue(currentValue);
		values.add(currentStoredValue);
	    } else {
		currentStoredValue.number++;
	    }
	}
    }

    /**
     * @return Returns the totalNumber.
     */
    public int getTotalNumber() {
	return totalNumber;
    }

    public boolean isNewValue() {
	return addedNumber > 1
		&& !Value.equalValues(previousValue, currentValue,
			isCompareTolerant());
    }

    /**
     * @param totalNumber
     *            The totalNumber to set.
     */
    public void setTotalNumber(int totalNumber) {
	this.totalNumber = totalNumber;
    }

    /**
     * @return Returns the current value.
     */
    public double getCurrentValue() {
	return currentValue;
    }

    /**
     * @return Returns the number of the last values with the same value.
     */
    public int getCurrentValueNumber() {
	return currentValueNumber;
    }

    public double getCurrentRate() {
	if (totalNumber < 0) {
	    throw new IllegalStateException("Total number is not set");
	}
	return (double) currentValueNumber / totalNumber;
    }

    public double getPreviousRate() {
	if (totalNumber < 0) {
	    throw new IllegalStateException("Total number is not set");
	}
	return (double) previousValueNumber / totalNumber;
    }

    /**
     * @return Returns the number of all added values.
     */
    public int getNumber() {
	return addedNumber;
    }

    /**
     * @return Returns the compareTolerant.
     */
    public boolean isCompareTolerant() {
	return compareTolerant;
    }

    /**
     * @param compareTolerant
     *            The compareTolerant to set.
     */
    public void setCompareTolerant(boolean compareTolerant) {
	this.compareTolerant = compareTolerant;
    }

    /**
     * @return Returns the previousValue.
     */
    public double getPreviousValue() {
	return previousValue;
    }

    /**
     * @return Returns the previousValueNumber.
     */
    public int getPreviousValueNumber() {
	return previousValueNumber;
    }

    /**
     * @return Returns the storeValues.
     */
    public boolean isStoreValues() {
	return storeValues;
    }

    /**
     * @param storeValues
     *            The storeValues to set.
     */
    public void setStoreValues(boolean storeValues) {
	this.storeValues = storeValues;
	if (storeValues && values == null) {
	    values = new ArrayList();
	}
    }

    /**
     * @return Returns the values.
     */
    public List getValues() {
	return values;
    }

    public double getRate(DistinctValue value) {
	return (double) value.getNumber() / getNumber();
    }

}
