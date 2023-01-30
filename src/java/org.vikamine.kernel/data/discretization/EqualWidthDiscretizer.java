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

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * {@link EqualWidthDiscretizer} implements equal width discretization, i.e., k
 * segments with an equal width on the value range (min, max) are created.
 * 
 * @author lemmerich
 * @date 04/2009
 */
public class EqualWidthDiscretizer extends AbstractDiscretizationMethod {

    private static final String NAME = "Equal Width Discretizer";

    public EqualWidthDiscretizer() {
	super();
    }

    public EqualWidthDiscretizer(int segmentsCount) {
	super();
	this.segmentsCount = segmentsCount;
    }

    public EqualWidthDiscretizer(DataView population, NumericAttribute na,
	    int segmentsCount) {
	this.population = population;
	this.attribute = na;
	this.segmentsCount = segmentsCount;
    }

    @Override
    public List<Double> getCutpoints() {
	if ((population == null) || (attribute == null)
		|| (population.dataset().getIndex(attribute) < 0)
		|| (segmentsCount < 2) || (population.size() < 2)) {
	    return new ArrayList<Double>();
	}

	List<Double> cutpoints = new ArrayList<Double>(segmentsCount + 1);

	double[] attributesMinAndMaxValue = DiscretizationUtils.getMinMaxValue(
		population, attribute);

	double segmentWidth = (attributesMinAndMaxValue[1] - attributesMinAndMaxValue[0])
		/ segmentsCount;

	for (int i = 1; i <= segmentsCount - 1; i++) {
	    cutpoints.add(attributesMinAndMaxValue[0] + segmentWidth * i);
	}

	return cutpoints;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public List<Double> getSortedSample() {
	// TODO Override this method or set the field
	// AbstractDiscretizationMethod.sortedSample to use this class with the
	// SoftMetaDiscretizer
	return null;
    }
}
