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

import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * {@link AbstractDiscretizationMethod} provides basic methods for
 * discretization algorithms (see {@link DiscretizationMethod}).
 * 
 * @author lemmerich
 * @date 04/2009
 */
public abstract class AbstractDiscretizationMethod implements
	DiscretizationMethod {

    protected NumericAttribute attribute;

    protected DataView population;

    protected int segmentsCount;

    protected List<DataRecord> sortedSample;

    @Override
    public NumericAttribute getAttribute() {
	return attribute;
    }

    @Override
    public DataView getPopulation() {
	return population;
    }

    public int getSegmentsCount() {
	return segmentsCount;
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
	this.segmentsCount = segmentsCount;
    }

    @Override
    public void setTarget(SGTarget target) {

    }

    @Override
    public String toString() {
	return getName();
    }

    @Override
    public List<Double> getSortedSample() {
	return DiscretizationUtils.Records2Double(sortedSample, attribute);
    }
}
