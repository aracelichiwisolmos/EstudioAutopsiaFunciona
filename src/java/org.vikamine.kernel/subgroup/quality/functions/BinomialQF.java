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

public class BinomialQF extends StandardQF {

    private static final String ID = "BinomialQF";
    private static final String NAME = "Binomial";

    public BinomialQF() {
	super(0.5);
    }

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public StandardQF clone() {
	return new BinomialQF();
    }

    @Override
    public double evaluateBin(double subgroupSize, double tp,
	    double definedPopulationCount, double populationPositives) {
	return getPenalty(subgroupSize, definedPopulationCount)
		* super.evaluateBin(subgroupSize, tp, definedPopulationCount,
			populationPositives);
    }

    @Override
    public double evaluateNum(double subgroupSize, double sgMean,
	    double definedPopulationCount, double populationMean) {
	return getPenalty(subgroupSize, definedPopulationCount)
		* super.evaluateNum(subgroupSize, sgMean,
			definedPopulationCount, populationMean);
    }

    private double getPenalty(double subgroupSize, double definedPopulationCount) {
	return Math.sqrt(definedPopulationCount
		/ (definedPopulationCount - subgroupSize));
    }

    @Override
    public double estimateQuality(double subgroupSize,
	    double subgroupPositives, double populationSize,
	    double populationPositives) {
	return Math.pow(subgroupPositives, a)
		* (1 - populationPositives / populationSize)
		* getPenalty(subgroupSize, populationSize);
    }
}
