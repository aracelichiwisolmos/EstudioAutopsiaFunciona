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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * {@link SoftMetaDiscretizer} implements soft meta discretization with given
 * deviations.
 * 
 * @author lemmerich, Hagen Schwa
 * 
 */
public class SoftMetaDiscretizer implements DiscretizationMethod {

    private static final String ALPHA = "alpha";
    private static final String TOLERANCE = "tolerance";
    private static final String INSTANCE = "instance";
    private static final String DISTANCE = "distance";

    private static final double ALPHA_DEF = .5d;
    private static final double ALPHA_TOLERANCE = 1d;
    private static final int TOLERANCE_DEF = 10;

    private static final MathContext DOUBLE_PREC = new MathContext(15);

    public static final int DISTANCE_TOLERANCE = 0;
    public static final int DISTANCE_INSTANCE_COUNT = 1;

    /**
     * Abstract class for computing distances.
     * 
     * @author Hagen Schwa
     * 
     */
    private static abstract class DistanceFunction {

	abstract void setCutpoint(final double cutpoint);

	abstract void setPrevCutpoint(final double cutpoint);

	abstract void setNextCutpoint(final double cutpoint);

	/**
	 * Get the distance from the current set cut point to the candidate,
	 * where the cut point is smaller or equal to the candidate.
	 * 
	 * @param candidate
	 *            The candidate to evaluate the distance
	 * @return The distance from the current set cut point to the candidate
	 */
	abstract int distanceUpper(final double candidate);

	/**
	 * Get the distance from the current set cut point to the candidate,
	 * where the cut point is bigger or equal to the candidate.
	 * 
	 * @param candidate
	 *            The candidate to evaluate the distance
	 * @return The distance from the current set cut point to the candidate
	 */
	abstract int distanceLower(final double candidate);
    }

    /**
     * Distance function counting instances from the cut point to the candidate.
     * 
     * @author Hagen Schwa
     * 
     */
    private static final class InstanceCountDistance extends DistanceFunction {

	List<Double> sortedSample;

	int index;

	private InstanceCountDistance(List<Double> sortedSample) {
	    this.sortedSample = sortedSample;
	    index = 0;
	}

	@Override
	void setCutpoint(final double cutpoint) {
	    while (sortedSample.get(index) < cutpoint)
		index++;
	}

	@Override
	int distanceUpper(final double candidate) {
	    int i = index;
	    while (i < sortedSample.size() && sortedSample.get(i) < candidate)
		i++;
	    return i - index;
	}

	@Override
	int distanceLower(final double candidate) {
	    int i = index;
	    while (i >= 0 && sortedSample.get(i) >= candidate)
		i--;
	    return index - i + 1;
	}

	@Override
	void setNextCutpoint(double cutpoint) {
	}

	@Override
	void setPrevCutpoint(double cutpoint) {
	}
    }

    /**
     * Distance function that allows candidates to deviate a specified
     * percentage level. The deviation is determined by the extend of the
     * interval the candidate is within.
     * 
     * @author Hagen Schwa
     * 
     */
    private static final class ToleranceDistance extends DistanceFunction {

	private double cutpoint;
	private double prevCutpoint;
	private double nextCutpoint;

	private final int tolerance;

	private ToleranceDistance(final int tolerance) {
	    this.tolerance = tolerance;
	}

	@Override
	void setCutpoint(final double cutpoint) {
	    this.cutpoint = cutpoint;
	}

	@Override
	int distanceLower(double candidate) {
	    final double tol = (cutpoint - prevCutpoint) * tolerance / 100d;
	    return candidate >= cutpoint - tol ? 0 : Integer.MAX_VALUE;
	}

	@Override
	int distanceUpper(double candidate) {
	    final double tol = (nextCutpoint - cutpoint) * tolerance / 100d;
	    return candidate <= cutpoint + tol ? 0 : Integer.MAX_VALUE;
	}

	@Override
	void setNextCutpoint(double cutpoint) {
	    this.nextCutpoint = cutpoint;
	}

	@Override
	void setPrevCutpoint(double cutpoint) {
	    this.prevCutpoint = cutpoint;
	}
    }

    private static int complexityOffset(double min, double max) {
	BigDecimal bd = new BigDecimal(Math.max(Math.abs(min), Math.abs(max)),
		DOUBLE_PREC);
	if (bd.scale() > 0)
	    return bd.precision() - bd.scale();
	return bd.precision();
    }

    private static double score(double complexity, double distance, double alpha) {
	double distanceFactor;
	if (distance == 0)
	    distanceFactor = Math.nextUp(1);
	else
	    distanceFactor = 1 / distance;
	return (1 / complexity) * Math.pow(distanceFactor, alpha);
    }

    private static double mean(final double d1, final double d2) {
	return (d1 + d2) / 2;
    }

    private static List<Double> softenCutpoints(List<Double> cutpoints,
	    DistanceFunction distFunc, double alpha) {

	List<Double> result = new ArrayList<Double>();

	int complexityOffset = complexityOffset(cutpoints.get(0), cutpoints
		.get(cutpoints.size() - 1));

	final int size = cutpoints.size();

	for (int j = 1; j <= size; j++) {

	    final double d = cutpoints.get(j - 1);

	    distFunc.setCutpoint(d);

	    double max;

	    if (j == size) {
		max = Double.POSITIVE_INFINITY;
		distFunc.setNextCutpoint(Double.NaN);
	    } else {
		final double nextCutpoint = cutpoints.get(j);
		max = mean(nextCutpoint, d);
		distFunc.setNextCutpoint(nextCutpoint);
	    }

	    double min;

	    if (j == 1) {
		min = Double.NEGATIVE_INFINITY;
		distFunc.setPrevCutpoint(Double.NaN);
	    } else {
		final Double prevCutpoint = cutpoints.get(j - 2);
		min = mean(prevCutpoint, d);
		distFunc.setPrevCutpoint(prevCutpoint);
	    }

	    BigDecimal cutpoint = new BigDecimal(d, DOUBLE_PREC)
		    .stripTrailingZeros();

	    double maxScore = 0;
	    double bestSoftenedCutpoint = d;

	    for (int i = cutpoint.precision() - 1; i > 0; i--) {

		BigDecimal candidate;

		// evaluate the ceiling candidate with precision i
		candidate = cutpoint.round(new MathContext(i,
			RoundingMode.CEILING));
		final int complexity = (complexityOffset + candidate.scale()) * 2;
		double val = candidate.doubleValue();
		if (val < max) {
		    final int distance = distFunc.distanceUpper(candidate
			    .doubleValue());
		    final double score = score(complexity, distance, alpha);
		    if (score > maxScore) {
			maxScore = score;
			bestSoftenedCutpoint = candidate.doubleValue();
		    }
		}

		// evaluate the floor candidate with precision i
		candidate = cutpoint.round(new MathContext(i,
			RoundingMode.FLOOR));
		val = candidate.doubleValue();
		if (val >= min) {
		    final int distance = distFunc.distanceLower(candidate
			    .doubleValue());
		    final double score = score(complexity, distance, alpha);
		    if (score >= maxScore) {
			maxScore = score;
			bestSoftenedCutpoint = candidate.doubleValue();
		    }
		}

		// evaluate the candidate between floor and ceil with precision
		// i + 1
		candidate = candidate.add(new BigDecimal(5)
			.movePointLeft(candidate.scale() + 1));
		val = candidate.doubleValue();
		if (val < max && val >= min) {
		    final int distance = val < d ? distFunc.distanceLower(val)
			    : distFunc.distanceUpper(val);
		    final double score = score(complexity + 1, distance, alpha);
		    if (score >= maxScore) {
			maxScore = score;
			bestSoftenedCutpoint = candidate.doubleValue();
		    }
		}

	    }
	    result.add(bestSoftenedCutpoint);
	}
	return result;
    }

    DiscretizationMethod innerMethod;

    private double alpha;

    private int tolerance;

    private int distanceFunction;

    public DiscretizationMethod getInnerMethod() {
	return innerMethod;
    }

    public void setInnerMethod(DiscretizationMethod innerMethod) {
	this.innerMethod = innerMethod;
    }

    public SoftMetaDiscretizer(DiscretizationMethod innerMethod) {
	super();
	this.innerMethod = innerMethod;
	alpha = ALPHA_DEF;
	tolerance = TOLERANCE_DEF;
    }

    public SoftMetaDiscretizer(DiscretizationMethod innerMethod,
	    int distanceFunction) {
	this(innerMethod);
	this.distanceFunction = distanceFunction;
    }

    /**
     * Creates the soft meta discretizer additionally specifying alpha.
     * 
     * Create String array by splitting following String with regex ";":
     * 
     * "discr-method = soften; [param = value; ]*[distance = tolerance | instance; ][tolerance = 10; ]*[alpha = .5; ][param = value; ]*"
     * 
     * @param args
     *            String array as specified above.
     * @param innerMethod
     *            The inner method to soften.
     */
    public SoftMetaDiscretizer(String[] args, DiscretizationMethod innerMethod) {
	this(innerMethod);
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(ALPHA)) {
		alpha = Double.parseDouble(arg[1].trim());
	    } else if (arg[0].contains(DISTANCE)) {
		final String dis = arg[1].trim();
		if (dis.equalsIgnoreCase(TOLERANCE))
		    distanceFunction = DISTANCE_TOLERANCE;
		else if (dis.equalsIgnoreCase(INSTANCE))
		    distanceFunction = DISTANCE_INSTANCE_COUNT;
	    } else if (arg[0].contains(TOLERANCE)) {
		tolerance = Integer.parseInt(arg[1].trim());
	    }
	}
    }

    private DistanceFunction getDistanceFunction() {
	switch (distanceFunction) {
	case DISTANCE_TOLERANCE:
	    alpha = ALPHA_TOLERANCE;
	    return new ToleranceDistance(tolerance);
	case DISTANCE_INSTANCE_COUNT:
	default:
	    return new InstanceCountDistance(innerMethod.getSortedSample());
	}
    }

    @Override
    public NumericAttribute getAttribute() {
	return innerMethod.getAttribute();
    }

    @Override
    public List<Double> getCutpoints() {
	return softenCutpoints(innerMethod.getCutpoints(),
		getDistanceFunction(), alpha);
    }

    @Override
    public String getName() {
	return "MetaSoft(" + innerMethod.getName() + ")";
    }

    @Override
    public DataView getPopulation() {
	return innerMethod.getPopulation();
    }

    @Override
    public void setAttribute(NumericAttribute attribute) {
	innerMethod.setAttribute(attribute);
    }

    @Override
    public void setPopulation(DataView population) {
	innerMethod.setPopulation(population);
    }

    @Override
    public void setSegmentsCount(int segmentsCount) {
	innerMethod.setSegmentsCount(segmentsCount);
    }

    @Override
    public void setTarget(SGTarget target) {
	innerMethod.setTarget(target);
    }

    @Override
    public List<Double> getSortedSample() {
	return innerMethod.getSortedSample();
    }

}
