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

package org.vikamine.kernel.subgroup.quality.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vikamine.kernel.subgroup.quality.IQualityFunction;

/**
 * A provider for qualityFunctions, e.g., for reading task files
 * 
 * @author lemmerich
 * @date 11/2011
 */
public class QualityFunctionRegistry {

    public static QualityFunctionRegistry instance = null;

    public static IQualityFunction[] BASE_FUNCTIONS = { new AddedValueQF(),
	    new ChiSquareQF(), new LiftQF(), new PiatetskyShapiroQF(),
	    new BinomialQF(), new SimpleBinomialQF(), new WRAccQF(),
	    new InformationGainQF(), new FMeasureQF(), new PrecisionQF(),
	    new RecallQF(), new AdjustedResidualQF() };

    private List<IQualityFunction> qualityFunctions = null;

    public static QualityFunctionRegistry getInstance() {
	if (instance == null) {
	    instance = new QualityFunctionRegistry();
	}
	return instance;
    }

    public QualityFunctionRegistry() {
	qualityFunctions = new ArrayList<IQualityFunction>();
	qualityFunctions.addAll(Arrays.asList(BASE_FUNCTIONS));
    }

    public List<IQualityFunction> getQualityFunctions() {
	return qualityFunctions;
    }

    public void register(IQualityFunction qf) {
	qualityFunctions.add(qf);
    }

    public void register(int position, IQualityFunction qf) {
	qualityFunctions.add(position, qf);
    }
}
