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

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Entropy based discretization method, that requires the segment count
 * parameter as a user input.
 * 
 * @author Hagen Schwaß, lemmerich
 * 
 */
public class EntropyDiscretizer extends SupervisedBinaryDiscretizer {

    private static final String NAME = "Entropy Discretizer";

    private static final String SEGCOUNT = "segcount";

    static final double LOG2 = Math.log(2);

    static double entropy(final int negatives, final int positives,
	    final int totalCount) {
	return (negatives > 0 ? -entropyAddand(negatives, totalCount) : 0)
		- (positives > 0 ? entropyAddand(positives, totalCount) : 0);
    }

    private static double entropyAddand(final int classCount,
	    final int totalCount) {
	final double frequency = ((double) classCount) / ((double) totalCount);
	return frequency * (Math.log(frequency) / LOG2);
    }

    static double cieAddand(final int sizeCut, final double entropy) {
	final double weight = sizeCut;
	return weight * entropy;
    }

    /**
     * Represents a binary partition that can insert another binary partition as
     * a sub-partition, where the cut point of the sub-partition minimizes the
     * global class information entropy.
     * 
     * @author Hagen Schwa�
     * 
     */
    private static final class EntropyPartition extends BinaryPartition {

	private EntropyPartition() {
	    isAccepted = true;
	}

	private WeakReference<EntropyPartition> parent;

	/**
	 * Sum of all the weighted entropies of the lower cut.
	 */
	private double ciePartsLower;
	/**
	 * Sum of all the weighted entropies of the upper cut.
	 */
	private double ciePartsUpper;

	/**
	 * Insert a binary partition to have one more cut point.
	 * 
	 * The partition to insert becomes a leaf, where it has a cut point that
	 * minimizes the global class information entropy. To achieve this, the
	 * method calls itself on its leaves, where it has leaves. Otherwise it
	 * tries to evaluate all possible cut points in the interval the
	 * partition would represent as that leaf. If there is a cut point, that
	 * minimizes the global class information entropy, the partition becomes
	 * a leaf there.
	 * 
	 * @param start
	 *            The boundary point where to start iterating through the
	 *            potential cut points. In regular case it is
	 *            lowerBoundary.nextCutPoint(). If the lower boundary is
	 *            null, it is firstBlock.upperBoundary.
	 * @param lowerBoundary
	 *            The lower boundary of the interval to partition. May be
	 *            null, if the interval starts at the beginning.
	 * @param upperBoundary
	 *            The upper boundary of the interval to partition. May be
	 *            null, if the interval ends at the global end.
	 * @param lastBlock
	 *            The very last block of the population.
	 * @param cieParts
	 *            The sum of the weighted entropies of all cuts that are are
	 *            not children of this partition.
	 * @param partition
	 *            The partition to insert.
	 */
	private void insertPartition(final BoundaryPoint start,
		final BoundaryPoint lowerBoundary,
		final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock, final double cieParts,
		final EntropyPartition partition) {
	    double ciePartsForCut = cieParts + ciePartsUpper;
	    if (lowerPartition == null) {
		if (partition.findBestCutpoint(start, lowerBoundary, cutPoint,
			lastBlock, ciePartsForCut)) {
		    lowerPartition = partition;
		    partition.switchParent(this);
		}
	    } else {
		((EntropyPartition) lowerPartition).insertPartition(start,
			lowerBoundary, cutPoint, lastBlock, ciePartsForCut,
			partition);
	    }
	    ciePartsForCut = cieParts + ciePartsLower;
	    if (upperPartition == null) {
		if (partition.findBestCutpoint(cutPoint.nextCutPoint(),
			cutPoint, upperBoundary, lastBlock, ciePartsForCut)) {
		    upperPartition = partition;
		    partition.switchParent(this);
		}
	    } else {
		((EntropyPartition) upperPartition).insertPartition(
			cutPoint.nextCutPoint(), cutPoint, upperBoundary,
			lastBlock, ciePartsForCut, partition);
	    }

	}

	private double classInformationEntropy = Double.POSITIVE_INFINITY;

	/**
	 * Find the best cut point within a given interval, evaluating the
	 * global class information entropy.
	 * 
	 * @param start
	 *            The first potential cut point to evaluate. In general it
	 *            it is, lowerBoundary.nextCutPoint(), if the lower boundary
	 *            is null, it is firstBlock.upperBoundary.
	 * @param lowerBoundary
	 *            The lower boundary of the interval that is to be split by
	 *            this cut point.
	 * @param upperBoundary
	 *            The upper boundary of the interval that is to be split by
	 *            this cut point.
	 * @param lastBlock
	 *            The very last InstanceBlock of this population.
	 * @param cieParts
	 *            The sum of the weighted entropies of all other cuts in the
	 *            resulting partition.
	 * @return True, if there was a cut point found, that minimizes the
	 *         class information entropy in a global manner. False
	 *         otherwise.
	 */
	private boolean findBestCutpoint(final BoundaryPoint start,
		final BoundaryPoint lowerBoundary,
		final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock, final double cieParts) {
	    boolean found = false;
	    for (BoundaryPoint candidate = start; candidate != upperBoundary; candidate = candidate
		    .nextCutPoint()) {

		final int negativesLower = candidate
			.sumNegativesLowerCut(lowerBoundary);
		final int positivesLower = candidate
			.sumPositivesLowerCut(lowerBoundary);
		final int negativesUpper = candidate.sumNegativesUpperCut(
			upperBoundary, lastBlock);
		final int positivesUpper = candidate.sumPositivesUpperCut(
			upperBoundary, lastBlock);

		final int sizeLowerCut = negativesLower + positivesLower;
		final int sizeUpperCut = negativesUpper + positivesUpper;

		final double entropyLower = entropy(negativesLower,
			positivesLower, sizeLowerCut);
		final double entropyUpper = entropy(negativesUpper,
			positivesUpper, sizeUpperCut);

		final double cieAddandLower = cieAddand(sizeLowerCut,
			entropyLower);
		final double cieAddandUpper = cieAddand(sizeUpperCut,
			entropyUpper);

		final double classInformationEntropy = cieParts
			+ cieAddandLower + cieAddandUpper;

		if (classInformationEntropy < this.classInformationEntropy) {
		    this.classInformationEntropy = classInformationEntropy;
		    cutPoint = candidate;
		    ciePartsLower = cieAddandLower;
		    ciePartsUpper = cieAddandUpper;
		    found = true;
		}
	    }

	    return found;
	}

	private void clearChild(final EntropyPartition child) {
	    if (lowerPartition == child)
		lowerPartition = null;
	    else
		upperPartition = null;
	}

	/**
	 * Switches the parent of this partition by setting the specified new
	 * parent. Brings the partition, that was this parent before, to release
	 * this child.
	 * 
	 * @param newParent
	 *            The new parent of this partition.
	 */
	private void switchParent(final EntropyPartition newParent) {
	    if (parent != null)
		parent.get().clearChild(this);
	    parent = new WeakReference<EntropyPartition>(newParent);
	}

	private double cieParts() {
	    return ciePartsLower + ciePartsUpper;
	}

	/**
	 * Assuming, that this partition has become a fix leaf now, there is a
	 * new valid cut point available, that modifies the sums of the weighted
	 * class information entropies, where this partition is direct or
	 * indirect child. Call this method to update these sums.
	 */
	private void updateCieParts() {
	    if (parent == null)
		return;
	    parent.get().updateCieParts(this);
	}

	private void updateCieParts(final EntropyPartition child) {
	    if (lowerPartition == child)
		ciePartsLower = child.cieParts();
	    else
		ciePartsUpper = child.cieParts();
	    if (parent == null)
		return;
	    parent.get().updateCieParts(this);
	}

    }

    public EntropyDiscretizer(SGTarget target) {
	super(target);
    }

    public EntropyDiscretizer(DataView population, NumericAttribute na,
	    int segmentsCount, SGTarget target) {
	super(target);
	setPopulation(population);
	setAttribute(na);
	setSegmentsCount(segmentsCount);
    }

    /**
     * Constructs discretizer specifying segments count by String array.
     * 
     * Create String array by splitting following String with regex ";":
     * 
     * "entropy; segcount = 5 "
     * 
     * @param args
     *            String array as specified above.
     */
    public EntropyDiscretizer(String[] args) {
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(SEGCOUNT)) {
		segmentsCount = Integer.parseInt(arg[1].trim());
	    }
	}
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

    @Override
    BinaryPartition makeBinaryPartition(InstanceBlock firstBlock,
	    InstanceBlock lastBlock) {
	final EntropyPartition ep = new EntropyPartition();
	if (firstBlock.upperBoundary == null)
	    return ep;
	ep.findBestCutpoint(firstBlock.upperBoundary, null, null, lastBlock, 0);
	for (int i = 2; i < segmentsCount; i++) {
	    final EntropyPartition x = new EntropyPartition();
	    ep.insertPartition(firstBlock.upperBoundary, null, null, lastBlock,
		    0, x);
	    x.updateCieParts();
	}
	return ep;
    }

}
