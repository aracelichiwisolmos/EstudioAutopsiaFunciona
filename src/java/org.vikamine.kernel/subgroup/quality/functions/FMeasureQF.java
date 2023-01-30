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
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.quality.AbstractQFSimpleStatisticBased;
import org.vikamine.kernel.subgroup.quality.EstimatetableQFSimpleStatisticsBased;


public class FMeasureQF extends AbstractQFSimpleStatisticBased implements
	EstimatetableQFSimpleStatisticsBased {

    private static final String NAME = "F-Measure";

    double param;

    public FMeasureQF() {
	this(1.0);
    }

    public FMeasureQF(double param) {
	super();
	this.param = param;
    }

    public static final PrecisionQF PRECISION_QF = new PrecisionQF();
    public static final RecallQF RECALL_QF = new RecallQF();

    @Override
    public String getID() {
	return "FMeasureQF(" + param + ")";
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public double evaluateNum(double subgroupSize, double subgroupMean,
	    double definedPopulationCount, double populationMean) {
	return Double.NaN;
    }

    @Override
    public double evaluateBin(double subgroupSize, double subgroupPositives,
	    double definedPopulationCount, double populationPositives) {
	double precision = PRECISION_QF.evaluateBin(subgroupSize,
		subgroupPositives, definedPopulationCount, populationPositives);
	double recall = RECALL_QF.evaluateBin(subgroupSize, subgroupPositives,
		definedPopulationCount, populationPositives);
	return ((1 + param * param) * (precision * recall))
		/ (param * param * precision + recall);
    }

    @Override
    public AbstractQFSimpleStatisticBased clone() {
	return new FMeasureQF(param);
    }

    @Override
    public boolean isApplicable(SG subgroup) {
	return (subgroup.getTarget() != null)
		&& (subgroup.getTarget().isBoolean());
    }

    @Override
    public double estimateQuality(double subgroupSize,
	    double subgroupPositives, double populationSize,
	    double populationPositives) {

	// tp = subgroupPositives;
	// double fn = populationPositives - subgroupPositives;
	//
	// return ((1 + param * param) * subgroupPositives)
	// / ((1 + param * param) * subgroupPositives + param * param * fn);
	return 1;
    }

}
