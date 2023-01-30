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

package org.vikamine.kernel.data.discretization;

import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * {@link AllValuesDiscretizer} discretizes (cutpoints) according to all used
 * values of the {@link NumericAttribute}.
 * 
 * @author lemmerich
 * @date 02/2010
 */
public class AllValuesDiscretizer extends AbstractDiscretizationMethod {

    private DataView population;
    private NumericAttribute attribute;

    private static final String NAME = "AllValuesDiscretizer";

    public AllValuesDiscretizer(DataView population, NumericAttribute na) {
	this.population = population;
	this.attribute = na;
    }

    @Override
    public List<Double> getCutpoints() {
	List<Double> cutpoints = new ArrayList<Double>();

	// This sorts in ascending order and excludes missing values.
	List<DataRecord> sortedDataRecords = DiscretizationUtils
		.getSortedDataRecords(population, attribute, false, false);

	double value = Double.NaN;
	double lastValue = Double.NaN;
	for (DataRecord dr : sortedDataRecords) {
	    lastValue = value;
	    value = dr.getValue(attribute);
	    if (!Double.isNaN(lastValue) && lastValue != value) {
		cutpoints.add((lastValue + value) / 2);
	    }
	}
	return cutpoints;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public NumericAttribute getAttribute() {
	return attribute;
    }

    @Override
    public DataView getPopulation() {
	return population;
    }

    @Override
    public void setAttribute(NumericAttribute attribute) {
	this.attribute = attribute;
    }

    @Override
    public void setPopulation(DataView population) {
	this.population = population;
    }

    @Override
    public void setSegmentsCount(int segmentsCount) {
	// do nothing
    }

    @Override
    public List<Double> getSortedSample() {
	// TODO Override this method or set the field
	// AbstractDiscretizationMethod.sortedSample to use this class with the
	// SoftMetaDiscretizer
	return null;
    }
}
