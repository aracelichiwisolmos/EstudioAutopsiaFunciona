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
import java.util.List;

import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Abstract base class for supervised binary discretization.
 * 
 * @author Hagen Schwa
 * 
 */
public abstract class SupervisedBinaryDiscretizer extends
	AbstractSupervisedDiscretizationMethod {

    public SupervisedBinaryDiscretizer(SGTarget target) {
	super(target);
    }

    public SupervisedBinaryDiscretizer() {
    }

    private BlockGenerator generateBlocks(final BooleanTarget target) {
	final BlockGenerator generator = new BlockGenerator(target);
	generator.initialize();
	return generator;
    }

    /**
     * Counts all instances to instance blocks together, which are linked to
     * their upper boundary point. The boundary points are linked to their lower
     * and upper instance blocks. The block generator keeps track of the first
     * and the last instance block.
     * 
     * @author Hagen Schwa�
     * 
     */
    private final class BlockGenerator extends Initializer {

	private final InstanceBlock firstBlock;
	private InstanceBlock lastBlock;

	private InstanceBlock currentBlock;

	private BlockGenerator(final BooleanTarget target) {
	    super(target);
	    firstBlock = new InstanceBlock(getValue());
	    currentBlock = firstBlock;
	}

	@Override
	void finish() {
	    lastBlock = currentBlock;
	    lastBlock.maxValue = getValue();
	    lastBlock.cumsumNegatives = sumNegatives;
	    lastBlock.cumsumPositives = sumPositives;
	}

	@Override
	void createNewBlock() {
	    currentBlock.upperBoundary = new BoundaryPoint(currentBlock);
	    currentBlock.cumsumNegatives = sumNegatives;
	    currentBlock.cumsumPositives = sumPositives;
	    currentBlock.maxValue = getPrevValue();
	    final InstanceBlock upperBlock = new InstanceBlock(getValue());
	    currentBlock.upperBoundary.upperBlock = upperBlock;
	    currentBlock = upperBlock;
	}
    }

    /**
     * Represents a block of instances, which values of the attribute to
     * discretize are bigger than a lower boundary point (or are the smallest at
     * all), and that are smaller than the next bigger boundary point (or are
     * the biggest at all).
     * 
     * @author Hagen Schwa�
     * 
     */
    static final class InstanceBlock {

	private final double minValue;
	private double maxValue;

	BoundaryPoint upperBoundary;
	// private BoundaryPoint lowerBoundary;

	int cumsumNegatives;
	int cumsumPositives;

	private InstanceBlock(final double minValue) {
	    this.minValue = minValue;
	}

	int getSumNegativesLowerCut(final BoundaryPoint lowerBoundary) {
	    if (lowerBoundary == null)
		return cumsumNegatives;
	    return cumsumNegatives
		    - lowerBoundary.lowerBlock.get().cumsumNegatives;
	}

	int getSumPositivesLowerCut(final BoundaryPoint lowerBoundary) {
	    if (lowerBoundary == null)
		return cumsumPositives;
	    return cumsumPositives
		    - lowerBoundary.lowerBlock.get().cumsumPositives;
	}

	private int cumSize() {
	    return cumsumNegatives + cumsumPositives;
	}
    }

    /**
     * Represents a boundary point as defined in definition 2 by Fayyad and
     * Irani.
     * 
     * @author Hagen Schwa�
     * 
     */
    static final class BoundaryPoint {

	private final WeakReference<InstanceBlock> lowerBlock;
	private InstanceBlock upperBlock;

	private BoundaryPoint(final InstanceBlock lowerBlock) {
	    this.lowerBlock = new WeakReference<InstanceBlock>(lowerBlock);
	}

	int sumNegativesLowerCut(final BoundaryPoint lowerBoundary) {
	    return lowerBlock.get().getSumNegativesLowerCut(lowerBoundary);
	}

	int sumPositivesLowerCut(final BoundaryPoint lowerBoundary) {
	    return lowerBlock.get().getSumPositivesLowerCut(lowerBoundary);
	}

	int sumNegativesUpperCut(final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock) {
	    if (upperBoundary == null)
		return lastBlock.cumsumNegatives
			- lowerBlock.get().cumsumNegatives;
	    return upperBoundary.lowerBlock.get().cumsumNegatives
		    - lowerBlock.get().cumsumNegatives;
	}

	int sumPositivesUpperCut(final BoundaryPoint upperBoundary,
		final InstanceBlock lastBlock) {
	    if (upperBoundary == null)
		return lastBlock.cumsumPositives
			- lowerBlock.get().cumsumPositives;
	    return upperBoundary.lowerBlock.get().cumsumPositives
		    - lowerBlock.get().cumsumPositives;
	}

	BoundaryPoint nextCutPoint() {
	    return upperBlock.upperBoundary;
	}

	private double toDouble() {
	    return (lowerBlock.get().maxValue + upperBlock.minValue) / 2;
	}

    }

    /**
     * Abstract base class of a binary partition, where also the lower and the
     * upper subset is a binary partition.
     * 
     * @author Hagen Schwa�
     * 
     */
    abstract static class BinaryPartition {

	static int size(final InstanceBlock lastBlock) {
	    return lastBlock.cumsumNegatives + lastBlock.cumsumPositives;
	}

	static int size(BoundaryPoint lowerBoundary,
		BoundaryPoint upperBoundary, InstanceBlock lastBlock) {
	    if (upperBoundary == null) {
		if (lowerBoundary == null)
		    return lastBlock.cumSize();
		return lowerBoundary.sumNegativesUpperCut(upperBoundary,
			lastBlock)
			+ lowerBoundary.sumPositivesUpperCut(upperBoundary,
				lastBlock);
	    }
	    return upperBoundary.sumNegativesLowerCut(lowerBoundary)
		    + upperBoundary.sumPositivesLowerCut(lowerBoundary);
	}

	// the cut point which splits the records
	BoundaryPoint cutPoint;

	// the boundaries
	BinaryPartition lowerPartition;
	BinaryPartition upperPartition;

	boolean isAccepted;

	/**
	 * Recursively writing the double values of the cut points of the tree
	 * into a list.
	 * 
	 * @param list
	 *            The list to contain the cut points.
	 * @return The list to containing the cut points.
	 */
	private List<Double> toList(final List<Double> list) {
	    if (cutPoint == null || isAccepted == false)
		return list;
	    if (lowerPartition != null)
		lowerPartition.toList(list);
	    list.add(cutPoint.toDouble());
	    if (upperPartition != null)
		upperPartition.toList(list);
	    return list;
	}
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
	ArrayList<Double> result = new ArrayList<Double>();
	sortedSample = DiscretizationUtils.getSortedDataRecords(population,
		attribute, false, false);

	final BlockGenerator generator = generateBlocks((BooleanTarget) target);
	// result.add(generator.firstBlock.minValue);
	makeBinaryPartition(generator.firstBlock, generator.lastBlock).toList(
		result);
	// result.add(generator.lastBlock.maxValue);
	return result;
    }

    abstract BinaryPartition makeBinaryPartition(InstanceBlock firstBlock,
	    InstanceBlock lastBlock);

}
