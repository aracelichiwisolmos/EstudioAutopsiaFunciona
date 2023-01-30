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
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.quality.AbstractQFStatisticBased;

/**
 * 
 * @author atzmueller
 *
 */
public class InformationGainQF extends AbstractQFStatisticBased {

    private final static String ID = "InformationGainQF";
    private static final String NAME = "Information Gain";

    @Override
    public double evaluate(SGStatistics statistics) {
	if (statistics == null || !statistics.isBinary()) {
	    throw new IllegalArgumentException(
		    "Statistics for subgroup were computed before or are not binary!");
	}

	SGStatisticsBinary stats = (SGStatisticsBinary) statistics;

	double size = stats.getDefinedPopulationCount();
	double positives = stats.getPositives();
	double negatives = stats.getNegatives();
	double populationEntropy = entropy(size, positives, negatives);

	double tp = stats.getTp();
	double sgSize = stats.getSubgroupSize();
	double fp = sgSize - tp;
	double sgEntropy = entropy(sgSize, tp, fp);

	double nonSgSize = size - sgSize;
	double nonSgPositives = positives - tp; // this are the false
	// negatives
	double nonSgNegatives = negatives - fp; // this are the true
	// negatives
	double nonSgEntropy = entropy(nonSgSize, nonSgPositives, nonSgNegatives);

	double sgSizeRatio = sgSize / size;
	double nonSgSizeRatio = nonSgSize / size;

	double value = populationEntropy
		- ((sgSizeRatio * sgEntropy) + (nonSgSizeRatio * nonSgEntropy));
	if (Double.isNaN(value)) {
	    value = Double.NEGATIVE_INFINITY;
	}
	return value;
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
    public boolean isApplicable(SG subgroup) {
	return (subgroup.getTarget() != null)
		&& (subgroup.getTarget().isBoolean());
    }

    @Override
    public AbstractQFStatisticBased clone() {
	return new InformationGainQF();
    }

    protected double entropy(double size, double positive, double negative) {
	double posEnt = entropyPart(positive / size);
	double negEnt = entropyPart(negative / size);
	return posEnt + negEnt;
    }

    private double entropyPart(double p) {
	double logP = Math.log(p) / Math.log(2);
	return -p * logP;
    }

}
