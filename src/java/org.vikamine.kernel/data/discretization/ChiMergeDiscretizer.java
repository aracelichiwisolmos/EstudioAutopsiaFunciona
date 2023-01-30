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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * ChiMerge discretizer, see R. Kerber, ChiMerge: Discretization of numeric
 * attributes, AAAI-92, Proceedings Ninth National Conference on Artificial
 * Intelligence, pp104-110, AAAI Press/The MIT Press, 1992.
 * 
 * @author Hagen Schwa�
 * 
 */
public class ChiMergeDiscretizer extends AbstractSupervisedDiscretizationMethod {

    private static final String NAME = "ChiMerge Discretizer";

    private static final String MAX_SEGMENT = "maxseg";
    private static final String MIN_SEGMENT = "minseg";
    private static final String PERCENTAGE = "percentage";

    private static final double P90THRESHOLD = 2.71d;
    private static final double P95THRESHOLD = 3.84d;
    private static final double P99THRESHOLD = 6.63d;

    private static final int DEF_MINSEG = 5;
    private static final int DEF_MAXSEG = 15;

    /**
     * Interval as described by Kerber, ChiMerge.
     * 
     * @author Hagen Schwa�
     * 
     */
    private static final class Interval {

	private Interval(final double minValue) {
	    this.minValue = minValue;
	}

	private final double minValue;
	private double maxValue;

	private int positives;
	private int negatives;

	private int instances;

	private WeakReference<Adjacents> lowerAdjacents;
	private Adjacents upperAdjacents;

	private void updateInstances() {
	    instances = positives + negatives;
	}

	private double chiAddand(int instancesPositive, int instancesNegative,
		int totalAdjacent) {
	    final double expectedPositives = ((double) (instances * instancesPositive))
		    / (double) totalAdjacent;
	    final double difPositives = positives - expectedPositives;
	    final double expectedNegatives = ((double) (instances * instancesNegative))
		    / (double) totalAdjacent;
	    final double difNegatives = negatives - expectedNegatives;
	    return (difPositives * difPositives / Math.max(0.5,
		    expectedPositives))
		    + (difNegatives * difNegatives / Math.max(0.5,
			    expectedNegatives));
	}

    }

    private static final Comparator<Adjacents> AdjacentsComp = new Comparator<Adjacents>() {

	@Override
	public int compare(Adjacents o1, Adjacents o2) {
	    return o1.chiSquared < o2.chiSquared ? -1
		    : o1.chiSquared == o2.chiSquared ? 0 : 1;
	}

    };

    /**
     * Building the initial data model, which is assigning all records to their
     * initial interval, computing the x� value for each pair of adjacent
     * intervals (modeled by class Adjacents) and adding all Adjacents to the
     * priority queue.
     * 
     * @author Hagen Schwa�
     * 
     */
    private final class AdjacentsInitializer extends Initializer {

	private final Interval firstInterval;

	private Interval currentInterval;

	private PriorityQueue<Adjacents> queue;

	private AdjacentsInitializer(BooleanTarget target) {
	    super(target);
	    firstInterval = new Interval(sortedSample.get(0)
		    .getValue(attribute));
	    currentInterval = firstInterval;
	}

	@Override
	void finish() {
	    currentInterval.maxValue = getValue();
	    currentInterval.negatives = sumNegatives;
	    currentInterval.positives = sumPositives;
	    currentInterval.updateInstances();
	    queue = new PriorityQueue<Adjacents>(getCount(), AdjacentsComp);
	    for (Adjacents adjacents = firstInterval.upperAdjacents; adjacents != null; adjacents = adjacents.upperInterval.upperAdjacents) {
		adjacents.computeChiSquared();
		queue.add(adjacents);
	    }
	}

	@Override
	void createNewBlock() {
	    final Interval newInterval = new Interval(getValue());
	    currentInterval.positives = sumPositives;
	    currentInterval.negatives = sumNegatives;
	    currentInterval.updateInstances();
	    currentInterval.upperAdjacents = new Adjacents(currentInterval,
		    newInterval);
	    currentInterval.maxValue = getPrevValue();
	    newInterval.lowerAdjacents = new WeakReference<Adjacents>(
		    currentInterval.upperAdjacents);
	    currentInterval = newInterval;
	    resetSum();
	}

    }

    /**
     * Models a pair of two adjacent intervals to cache to the x�-values and for
     * finding the intervals in the priority queue.
     * 
     * Since PriorityQueue removes unused objects in linear time, Ajacents,
     * which x�-value has become updated, are'nt removed, but signed as
     * finalized, to be ignored when they get polled. Instead a new instance
     * with the updated x�-value is added to the queue.
     * 
     * @author Hagen Schwa�
     * 
     */
    private static final class Adjacents {

	private WeakReference<Interval> lowerInterval;
	private final Interval upperInterval;

	private double chiSquared;

	private boolean finalized;

	private Adjacents(Interval lowerInterval, Interval upperInterval) {
	    this.lowerInterval = new WeakReference<Interval>(lowerInterval);
	    this.upperInterval = upperInterval;
	}

	private Adjacents(Adjacents adjacents) {
	    this.lowerInterval = adjacents.lowerInterval;
	    this.upperInterval = adjacents.upperInterval;
	    adjacents.finalized = true;
	    computeChiSquared();
	}

	private void computeChiSquared() {
	    final int instancesPositive = lowerInterval.get().positives
		    + upperInterval.positives;
	    final int instancesNegative = lowerInterval.get().negatives
		    + upperInterval.negatives;
	    final int totalAdjacent = instancesNegative + instancesPositive;
	    chiSquared = lowerInterval.get().chiAddand(instancesPositive,
		    instancesNegative, totalAdjacent)
		    + upperInterval.chiAddand(instancesPositive,
			    instancesNegative, totalAdjacent);
	}

	/**
	 * Merge the lower interval and the upper interval into the lower
	 * intervals instance.
	 * 
	 * @return Merged lower interval instance.
	 */
	private Interval merge() {
	    Interval lowerInterval = this.lowerInterval.get();
	    lowerInterval.instances += upperInterval.instances;
	    lowerInterval.positives += upperInterval.positives;
	    lowerInterval.negatives += upperInterval.negatives;
	    lowerInterval.maxValue = upperInterval.maxValue;
	    lowerInterval.upperAdjacents = upperInterval.upperAdjacents;
	    if (lowerInterval.upperAdjacents == null)
		return lowerInterval;
	    lowerInterval.upperAdjacents.lowerInterval = new WeakReference<Interval>(
		    lowerInterval);
	    return lowerInterval;
	}

	private double toDouble() {
	    return (lowerInterval.get().maxValue + upperInterval.minValue) / 2;
	}

    }

    private int percentageLevel;

    private int maxSegments;
    private int minSegments;

    public ChiMergeDiscretizer(SGTarget target) {
	this();
	this.target = target;
    }

    public ChiMergeDiscretizer(DataView population, NumericAttribute na,
	    int segmentsCount, SGTarget target) {
	this();
	this.target = target;
	setPopulation(population);
	setAttribute(na);
	setSegmentsCount(segmentsCount);
    }

    public ChiMergeDiscretizer() {
	percentageLevel = 95;
	minSegments = DEF_MINSEG;
	maxSegments = DEF_MAXSEG;
    }

    /**
     * Constructs a discretizer by specifying the minimum and the maximum number
     * of segments and the threshold, see Kerber, ChiMerge.
     * 
     * Create the String array, by splitting the following String with regex
     * ";":
     * 
     * "ChiMerge; minseg = 30; maxseg = 50; percentage = [ 90 | 95 | 99 ]"
     * 
     * @param args
     *            String as specified.
     */
    public ChiMergeDiscretizer(String[] args) {
	this();
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(MAX_SEGMENT)) {
		maxSegments = Integer.parseInt(arg[1].trim());
	    } else if (arg[0].contains(PERCENTAGE)) {
		percentageLevel = Integer.parseInt(arg[1].trim());
	    } else if (arg[0].contains(MIN_SEGMENT)) {
		minSegments = Integer.parseInt(arg[1].trim());
	    }
	}
    }

    @Override
    public void setSegmentsCount(int segmentsCount) {
	super.setSegmentsCount(segmentsCount);
	maxSegments = segmentsCount;
	minSegments = segmentsCount;
    }

    public void setPercentageLevel(int percentage) {
	this.percentageLevel = percentage;
    }

    private double determineThreshold() {
	if (percentageLevel >= 99)
	    return P99THRESHOLD;
	if (percentageLevel >= 95)
	    return P95THRESHOLD;
	return P90THRESHOLD;
    }

    /**
     * This method is containing the bottom-up process of merging intervals.
     * 
     * The algorithm loops polling the Adjacents with the lowest x�-value, then
     * merging the adjacent intervals and updating and queuing the two new
     * resulting Adjacents.
     * 
     * The algorithm runs until the maximum number of segments is reached, then
     * stops when the threshold is exceeded, or the minimum number of segments
     * is reached.
     * 
     * @param queue
     *            The PriorityQueue containing the Adjacents ordered by their
     *            x�-value.
     * @param maxSegments
     *            Maximum number of segments to receive.
     * @param minSegments
     *            Minimum number of segments to receive.
     * @param threshold
     *            X�-threshold, see Kerber, ChiMerge.
     */
    private static void mergeAdjacents(PriorityQueue<Adjacents> queue,
	    final int maxSegments, final int minSegments, final double threshold) {
	int merges = 0;
	boolean merging = true;
	final int toMerge = queue.size() - maxSegments;
	final int toStop = queue.size() - minSegments;
	while (merging) {
	    Adjacents adjacents = queue.poll();
	    if (adjacents.finalized == false) {
		if ((adjacents.chiSquared <= threshold || merges <= toMerge)
			&& merges <= toStop) {
		    updateAdjacents(adjacents.merge(), queue);
		    merges++;
		} else {
		    merging = false;
		}
	    }
	}
    }

    /**
     * Update the lower and the upper (if exist) Adjacents of the merged
     * interval.
     * 
     * This is creating new Adjacents instances, computing their x�-value,
     * queuing them and updating all references. The existing Adjacents
     * instances become declared as finalized to be ignored in future.
     * 
     * @param interval
     *            The merged interval.
     * @param queue
     *            The PriorityQueue to add the updated Adjacents.
     */
    private static void updateAdjacents(Interval interval, PriorityQueue queue) {
	if (interval.lowerAdjacents != null) {
	    WeakReference<Adjacents> newLowerAdj = new WeakReference<Adjacents>(
		    new Adjacents(interval.lowerAdjacents.get()));
	    interval.lowerAdjacents = newLowerAdj;
	    newLowerAdj.get().lowerInterval.get().upperAdjacents = newLowerAdj
		    .get();
	    queue.add(newLowerAdj.get());
	}
	if (interval.upperAdjacents != null) {
	    Adjacents newUpperAdj = new Adjacents(interval.upperAdjacents);
	    interval.upperAdjacents = newUpperAdj;
	    newUpperAdj.upperInterval.lowerAdjacents = new WeakReference<Adjacents>(
		    newUpperAdj);
	    queue.add(newUpperAdj);
	}
    }

    /**
     * Getting the list of cut points, including the minimum and the maximum
     * value of the attribute to discretize.
     * 
     * @param firstAdjacents
     *            The first Adjacents.
     * @param cutPoints
     *            An empty ArrayList<Double> to add the cut points there
     * @return List of the cut points.
     */
    private static List<Double> Data2CutPoints(Adjacents firstAdjacents,
	    ArrayList<Double> cutPoints) {
	for (Adjacents adjacents = firstAdjacents; adjacents != null; adjacents = adjacents.upperInterval.upperAdjacents) {
	    if (!adjacents.finalized)
		cutPoints.add(adjacents.toDouble());
	}
	return cutPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getCutpoints
     * ()
     */
    @Override
    public List<Double> getCutpoints() {
	if (!check()) {
	    return Collections.EMPTY_LIST;
	}
	ArrayList<Double> list = new ArrayList<Double>();
	sortedSample = DiscretizationUtils.getSortedDataRecords(population,
		attribute, false, false);

	AdjacentsInitializer initializer = new AdjacentsInitializer(
		(BooleanTarget) target);

	initializer.initialize();

	mergeAdjacents(initializer.queue, maxSegments, minSegments,
		determineThreshold());

	List<Double> cutPoints = Data2CutPoints(
		initializer.firstInterval.upperAdjacents, list);

	return cutPoints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

}
