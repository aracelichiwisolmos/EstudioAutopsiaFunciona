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
 * Created on 03.11.2005
 *
 */
package org.vikamine.kernel.subgroup;

import org.vikamine.kernel.Describable;
import org.vikamine.kernel.Describer;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.util.IdentityLongDescriber;
import org.vikamine.kernel.util.VKMUtil;

/**
 * A {@link SupportingFactor} is a selector that is correlated with the subgroup
 * compared to the set of all data records (i.e., the total population).
 * 
 * @author atzmueller
 */
public class SupportingFactor implements Describable {

    private static class ScoreStatistics {

	private ScoreStatistics() {
	    super();
	}

	private static double computeFAR(int fp, int tn) {
	    if ((fp == 0) && (tn == 0)) {
		return 1;
	    } else {
		return ((double) fp) / (fp + tn);
	    }
	}

	public static double computePositiveQPS(int tp, int fp, int tn) {
	    double precision = computePrecision(tp, fp);
	    double far = computeFAR(fp, tn);

	    return precision * (1 - far);
	}

	public static double computeNegativeQPS(int tp, int fp, int tn) {
	    double precision = computePrecision(tp, fp);
	    double far = computeFAR(fp, tn);

	    return precision * (1 - far);
	}

	public static double computeQPS(double correlationCoefficient, int tp,
		int fp, int fn, int tn) {
	    if (correlationCoefficient < 0)
		return -1 * computeNegativeQPS(fp, tp, fn);
	    else
		return computePositiveQPS(tp, fp, tn);
	}

	private static double computePrecision(int tp, int fp) {
	    return ((double) tp) / (tp + fp);
	}
    }

    final private SGNominalSelector selector;

    final private double shareInPositives;

    final private double negShareInPositives;

    final private double shareInNegatives;

    final private double negShareInNegatives;

    final private double correlationCoefficient;

    public SupportingFactor(SGNominalSelector selector,
	    double shareInPositives, double negShareInPositives,
	    double shareInNegatives, double negShareInNegatives,
	    double correlationCoefficient) {
	super();
	this.selector = selector;
	this.shareInPositives = shareInPositives;
	this.negShareInPositives = negShareInPositives;
	this.shareInNegatives = shareInNegatives;
	this.negShareInNegatives = negShareInNegatives;
	this.correlationCoefficient = correlationCoefficient;
    }

    public SGNominalSelector getSelector() {
	return selector;
    }

    public double getShareInNegatives() {
	return shareInNegatives;
    }

    public double getShareInPositives() {
	return shareInPositives;
    }

    public double getNegShareInNegatives() {
	return negShareInNegatives;
    }

    public double getNegShareInPositives() {
	return negShareInPositives;
    }

    public String getStatisticsString() {
	return shareInPositives + "/" + negShareInPositives + ", "
		+ shareInNegatives + "/" + negShareInNegatives;
    }

    public String getCorrelationString() {
	double qps = ScoreStatistics.computeQPS(correlationCoefficient,
		(int) getShareInPositives(), (int) getNegShareInPositives(),
		(int) getShareInNegatives(), (int) getNegShareInNegatives());
	String scoreVerbalization = VKMUtil.getFormattedDoubleString(qps);

	return " [" + scoreVerbalization + "]";
    }

    @Override
    public String toString() {
	return getId();
    }

    @Override
    public String getId() {
	return getSelector().getId() + " (" + getCorrelationString() + ") "
		+ getStatisticsString();
    }

    @Override
    public String getDescription() {
	// return getDescription(true);
	return getDescription(new IdentityLongDescriber());
    }

    public String getFactorDescription() {
	return getSelector().getDescription();
    }

    @Override
    public String getDescription(Describer d) {
	return d.createDescription(getSelector()) + " "
		+ getCorrelationString() + " " + getStatisticsString();
    }

    public boolean isPositivelyCorrelated() {
	return correlationCoefficient > 0.0;
    }
}
