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

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Entropy based discretizationmethod by Fayyad and Irani. See U. Fayyad, K.
 * Irani, "Multi-Interval Discretization of Continuos-Valued Attributes for
 * Classification Learning", XIII International Joint Conference on Artificial
 * Intelligence (IJCAI93), Chambéry (France 1993) 1022-1029.
 * 
 * @author Hagen Schwaß
 * 
 */
public class FayyadIraniDiscretizer extends SupervisedBinaryDiscretizer {

    private static final String NAME = "Fayyad Irani Discretizer";

    private static double cieAddand(final int sizeCut, final double size,
	    final double entropy) {
	final double weight = sizeCut / size;
	return weight * entropy;
    }

    private static double classInformationEntropy(final int sizeLowerCut,
	    final int sizeUpperCut, final double sizePartition,
	    final double entropyLower, final double entropyUpper) {
	return cieAddand(sizeLowerCut, sizePartition, entropyLower)
		+ cieAddand(sizeUpperCut, sizePartition, entropyUpper);
    }

    /**
     * Represents a binary partition of the data records above the lower
     * boundary and below the upper boundary, which are split by a cut point.
     * Each partition, if accepted by the MDLPC (see Fayyad and Irani) has a
     * binary partition of the lower subset and one of the upper subset.
     * 
     * @author Hagen Schwaß
     * 
     */
    private static final class FIDBinaryPartition extends BinaryPartition {

	private static int classCount(final int negatives, final int positives) {
	    int count = 0;
	    if (negatives > 0)
		count++;
	    if (positives > 0)
		count++;
	    return count;
	}

	private static int classCount(final InstanceBlock lastBlock) {
	    return classCount(lastBlock.cumsumNegatives,
		    lastBlock.cumsumPositives);
	}

	private static double entropy(final InstanceBlock lastBlock) {
	    return EntropyDiscretizer.entropy(lastBlock.cumsumNegatives,
		    lastBlock.cumsumPositives, size(lastBlock));
	}

	private int sizeLowerCut;
	private int sizeUpperCut;

	private double entropyLower;
	private double entropyUpper;

	private double classInformationEntropy;

	private final int classCount;

	private int classCountLower;
	private int classCountUpper;

	/**
	 * Constructor that should be called first for recursively building the
	 * tree of partitions.
	 * 
	 * @param firstBlock
	 *            InstanceBlock containing the instance with the lowest
	 *            value
	 * @param lastBlock
	 *            InstanceBlock containing the instance with the highest
	 *            value
	 */
	private FIDBinaryPartition(final InstanceBlock firstBlock,
		final InstanceBlock lastBlock) {
	    this(firstBlock, null, lastBlock, classCount(lastBlock),
		    size(lastBlock), entropy(lastBlock));
	}

	/**
	 * This constructor is called recursively to build the binary tree of
	 * partitions and should not be called for building the root partition.
	 * This constructor is for the cases that there is no lower boundary.
	 * 
	 * @param firstBlock
	 *            InstanceBlock containing the instance with the lowest
	 *            value
	 * @param upperBoundary
	 *            The upper boundary point of the subset to partition.
	 * @param lastBlock
	 *            The instance block with the highest value of the attribute
	 *            to discretize.
	 * @param classCount
	 *            The count of classes contained in the subset to partition.
	 * @param size
	 *            The size of the subset to partition. This is the number of
	 *            instances represented by all instance blocks between the
	 *            lower and the upper boundary point.
	 * @param entropy
	 *            The entropy of the subset to partition.
	 */
	private FIDBinaryPartition(final InstanceBlock firstBlock,
		final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock, final int classCount,
		final double size, final double entropy) {
	    this.cutPoint = firstBlock.upperBoundary;
	    this.classCount = classCount;
	    if (findBestCutPoint(null, upperBoundary, size, lastBlock)
		    && testAccepted(entropy, size)) {
		isAccepted = true;
		lowerPartition = new FIDBinaryPartition(firstBlock, cutPoint,
			lastBlock, classCountLower, sizeLowerCut, entropyLower);
		upperPartition = new FIDBinaryPartition(cutPoint,
			upperBoundary, lastBlock, classCountUpper,
			sizeUpperCut, entropyUpper);
	    }
	}

	/**
	 * This constructor is called recursively for building the binary tree
	 * and should not be called for instantiating the root partition. This
	 * constructor if for the cases that there is a lower boundary.
	 * 
	 * @param lowerBoundary
	 *            The lower boundary point of the subset to partition.
	 * @param upperBoundary
	 *            The upper boundary point of the subset to partition.
	 * @param lastBlock
	 *            The instance block with the highest value of the attribute
	 *            to discretization.
	 * @param classCount
	 *            The count of classes contained in the subset to partition.
	 * @param size
	 *            The size of the subset to partition. This is the number of
	 *            instances represented by all instance blocks between the
	 *            lower and the upper boundary point.
	 * @param entropy
	 *            The entropy of the subset to partition.
	 */
	private FIDBinaryPartition(final BoundaryPoint lowerBoundary,
		final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock, final int classCount,
		final double size, final double entropy) {
	    this.cutPoint = lowerBoundary.nextCutPoint();
	    this.classCount = classCount;
	    if (findBestCutPoint(lowerBoundary, upperBoundary, size, lastBlock)
		    && testAccepted(entropy, size)) {
		isAccepted = true;
		lowerPartition = new FIDBinaryPartition(lowerBoundary,
			cutPoint, lastBlock, classCountLower, sizeLowerCut,
			entropyLower);
		upperPartition = new FIDBinaryPartition(cutPoint,
			upperBoundary, lastBlock, classCountUpper,
			sizeUpperCut, entropyUpper);
	    }
	}

	/**
	 * Iterating through all potential cut points, and selecting the one
	 * with the minimal class information entropy.
	 * 
	 * @param lowerBoundary
	 *            The lower boundary point of the subset to evaluate. May be
	 *            null if starting at the beginning.
	 * @param upperBoundary
	 *            The upper boundary point of the subset to evaluate. May be
	 *            null if ending with the last instance block.
	 * @param size
	 *            The size of the subset to evaluate.
	 * @param lastBlock
	 *            The last instance block of the set of instances.
	 */
	private boolean findBestCutPoint(final BoundaryPoint lowerBoundary,
		final BoundaryPoint upperBoundary, final double size,
		final InstanceBlock lastBlock) {
	    boolean found = false;
	    classInformationEntropy = Double.MAX_VALUE;
	    for (BoundaryPoint cutPoint = this.cutPoint; cutPoint != upperBoundary; cutPoint = cutPoint
		    .nextCutPoint()) {

		final int negativesLower = cutPoint
			.sumNegativesLowerCut(lowerBoundary);
		final int positivesLower = cutPoint
			.sumPositivesLowerCut(lowerBoundary);
		final int negativesUpper = cutPoint.sumNegativesUpperCut(
			upperBoundary, lastBlock);
		final int positivesUpper = cutPoint.sumPositivesUpperCut(
			upperBoundary, lastBlock);
		final int sizeLowerCut = negativesLower + positivesLower;
		final int sizeUpperCut = negativesUpper + positivesUpper;
		final double entropyLower = EntropyDiscretizer.entropy(
			negativesLower, positivesLower, sizeLowerCut);
		final double entropyUpper = EntropyDiscretizer.entropy(
			negativesUpper, positivesUpper, sizeUpperCut);
		final double classInformationEntropy = classInformationEntropy(
			sizeLowerCut, sizeUpperCut, size, entropyLower,
			entropyUpper);

		if (classInformationEntropy < this.classInformationEntropy) {
		    this.cutPoint = cutPoint;
		    this.entropyLower = entropyLower;
		    this.entropyUpper = entropyUpper;
		    this.sizeLowerCut = sizeLowerCut;
		    this.sizeUpperCut = sizeUpperCut;
		    this.classCountLower = classCount(negativesLower,
			    positivesLower);
		    this.classCountUpper = classCount(negativesUpper,
			    positivesUpper);
		    this.classInformationEntropy = classInformationEntropy;
		    found = true;
		}
	    }
	    return found;
	}

	/**
	 * Testing the partition against the MDLPC.
	 * 
	 * @param entropy
	 *            The entropy of the subset to evaluate.
	 * @param size
	 *            The size of the subset to evaluate.
	 */
	private boolean testAccepted(final double entropy, final double size) {
	    final double gain = entropy - classInformationEntropy;
	    final double logN1 = Math.log(size - 1) / EntropyDiscretizer.LOG2;
	    final double delta = delta(entropy);
	    final double condition = (logN1 + delta) / size;
	    return gain > condition;
	}

	private double delta(final double entropy) {
	    return (Math.log(Math.pow(3, classCount) - 2) / EntropyDiscretizer.LOG2)
		    - ((classCount * entropy)
			    - (classCountLower * entropyLower) - (classCountUpper * entropyUpper));
	}

    }

    public FayyadIraniDiscretizer() {

    }

    public FayyadIraniDiscretizer(SGTarget target) {
	super(target);
    }

    public FayyadIraniDiscretizer(DataView population, NumericAttribute na,
	    SGTarget target) {
	super(target);
	setPopulation(population);
	setAttribute(na);
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
	return new FIDBinaryPartition(firstBlock, lastBlock);
    }

}
