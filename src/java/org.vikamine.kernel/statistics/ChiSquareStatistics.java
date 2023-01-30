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
 * Created on 08.07.2003
 * 
 */
package org.vikamine.kernel.statistics;

/**
 * Class for computing basic statistics based on the chi2-test.
 * 
 * @author atzmueller
 */
public class ChiSquareStatistics {

    private ChiSquareStatistics() {
	super();
    }

    private static final double CHI2_MINFREQ_RELIABILITY_THRESHOLD = 5;

    public static double computeFourFoldChiSqare(double freqXAndY,
	    double freqNotXAndY, double freqXAndNotY, double freqNotXAndNotY) {

	double freqX = freqXAndY + freqXAndNotY;
	double freqNotX = freqNotXAndY + freqNotXAndNotY;
	double freqY = freqXAndY + freqNotXAndY;
	double freqNotY = freqXAndNotY + freqNotXAndNotY;

	if ((freqX >= 6) && (freqNotX >= 6) && (freqY >= 6) && (freqNotY >= 6)) {
	    // could be done for freqY and freqNotY likewise.

	    // check for reliablity of values
	    if (checkForChi2Reliablity(freqXAndY, freqNotXAndY, freqXAndNotY,
		    freqNotXAndNotY)) {
		double chiSquare = Math.pow(freqXAndY * freqNotXAndNotY
			- freqXAndNotY * freqNotXAndY, 2)
			* (freqXAndY + freqXAndNotY + freqNotXAndY + freqNotXAndNotY)
			/ ((freqXAndY + freqXAndNotY)
				* (freqNotXAndY + freqNotXAndNotY)
				* (freqXAndNotY + freqNotXAndNotY) * (freqXAndY + freqNotXAndY));
		return chiSquare;
	    } else {
		// for expected counts <= CHI2_MINFREQ_RELIABLITITY_TRESHOLD,
		// i.e.,
		// counts <= 5, we take the yates correction:
		// Alternative methods:
		// 1) take (n-1) instead of n in the chi2-formula, c.f. Sachs,
		// p. 451
		// 2) apply the exact test of fisher (computationally more
		// expensive)
		// NOTE: CHI2 is an approximation ...

		double total = freqXAndY + freqXAndNotY + freqNotXAndY
			+ freqNotXAndNotY;
		double chiSquare = total
			* Math.pow((freqXAndY * freqNotXAndNotY - freqXAndNotY
				* freqNotXAndY)
				- (total / 2), 2)
			/ ((freqXAndY + freqXAndNotY)
				* (freqNotXAndY + freqNotXAndNotY)
				* (freqXAndNotY + freqNotXAndNotY) * (freqXAndY + freqNotXAndY));
		return chiSquare;
	    }
	} else {
	    return 0.0;
	}
    }

    private static boolean checkForChi2Reliablity(double freqXAndY,
	    double freqNotXAndY, double freqXAndNotY, double freqNotXAndNotY) {
	double total = freqXAndY + freqXAndNotY + freqNotXAndY
		+ freqNotXAndNotY;
	double freqX = freqXAndY + freqXAndNotY;
	double freqNX = freqNotXAndY + freqNotXAndNotY;
	double freqY = freqXAndY + freqNotXAndY;
	double freqNY = freqXAndNotY + freqNotXAndNotY;
	double expXY = freqX * freqY / total;
	double expNXY = freqNX * freqY / total;
	double expXNY = freqX * freqNY / total;
	double expNXNY = freqNX * freqNY / total;

	if ((expXY >= CHI2_MINFREQ_RELIABILITY_THRESHOLD)
		&& (expNXY >= CHI2_MINFREQ_RELIABILITY_THRESHOLD)
		&& (expXNY >= CHI2_MINFREQ_RELIABILITY_THRESHOLD)
		&& (expNXNY >= CHI2_MINFREQ_RELIABILITY_THRESHOLD)) {
	    return true;
	} else {
	    return false;
	}
    }

    public static double computePhiCoefficient(double freqXAndY,
	    double freqNotXAndY, double freqXAndNotY, double freqNotXAndNotY) {
	double freqX = freqXAndY + freqXAndNotY;
	double freqNotX = freqNotXAndY + freqNotXAndNotY;
	double freqY = freqXAndY + freqNotXAndY;
	double freqNotY = freqXAndNotY + freqNotXAndNotY;

	if ((freqX >= 6) && (freqNotX >= 6) && (freqY >= 6) && (freqNotY >= 6)) {
	    double epsilon = 0.05;
	    double correlationCoeff = (freqXAndY * freqNotXAndNotY - freqXAndNotY
		    * freqNotXAndY)
		    / Math.sqrt((freqXAndY + freqXAndNotY)
			    * (freqNotXAndY + freqNotXAndNotY)
			    * (freqXAndNotY + freqNotXAndNotY)
			    * (freqXAndY + freqNotXAndY));

	    if (correlationCoeff > 1.0 + epsilon) {
		throw new RuntimeException("CorrelationCoeff > 1!");
	    }

	    if (correlationCoeff == 0) {
		if ((freqXAndY > 0) && (freqNotXAndNotY == 0)) {
		    return 1.0;
		} else if ((freqNotXAndY > 0) && (freqXAndNotY == 0)) {
		    return -1.0;
		} else if ((freqXAndNotY > 0) && (freqNotXAndY == 0)) {
		    return -1.0;
		}
	    }
	    return correlationCoeff;
	} else {
	    return 0.0;
	}
    }
}
