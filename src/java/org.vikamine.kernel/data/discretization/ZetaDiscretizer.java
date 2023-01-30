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
 * Zeta, see K.M. Ho and P.D. Scott, Zeta_ A Global Method for Discretization of
 * Continuos Variables, Kdd-97 Proceedings, AAAI, 1997
 * 
 * @author Hagen Schwaﬂ
 * 
 */
public class ZetaDiscretizer extends SupervisedBinaryDiscretizer {

    private final static String NAME = "Zeta Discretizer";

    private static final String REC_DEPTH = "rec-depth";
    private static final String THRESHOLD = "threshold";

    private static final int DEF_RECDEPTH = 3;

    private static final double DEF_THRESHOLD = .0d;

    private int recDepth;
    private double threshold;

    public ZetaDiscretizer() {
	recDepth = DEF_RECDEPTH;
	threshold = DEF_THRESHOLD;
    }

    public ZetaDiscretizer(SGTarget target) {
	super(target);
	recDepth = DEF_RECDEPTH;
	threshold = DEF_THRESHOLD;
    }

    public ZetaDiscretizer(DataView population, NumericAttribute na,
	    int segCount, SGTarget target) {
	super(target);
	recDepth = DEF_RECDEPTH;
	threshold = DEF_THRESHOLD;
	setPopulation(population);
	setAttribute(na);
	setSegmentsCount(segCount);
    }

    private static int zeta(final int negativesLower, final int positivesLower,
	    final int negativesUpper, final int positivesUpper) {
	return Math.max(negativesLower + positivesUpper, positivesLower
		+ negativesUpper);
    }

    /**
     * Creates the Zeta discretizer specifying the recursion depth and/or
     * threshold.
     * 
     * If no recursion depth is specified, the default recursion depth which is
     * 4 is used. If no threshold is specified, the default threshold .5d is
     * used.
     * 
     * Create String array by splitting following String with regex ";":
     * 
     * "zeta[; rec-depth = 2][; threshold = .5]"
     * 
     * @param args
     *            String array as specified above.
     */
    public ZetaDiscretizer(String[] args) {
	this();
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(REC_DEPTH)) {
		recDepth = Integer.parseInt(arg[1].trim());
	    } else if (arg[0].contains(THRESHOLD)) {
		threshold = Double.parseDouble(arg[1].trim());
	    }
	}
    }

    @Override
    public void setSegmentsCount(int segmentsCount) {
	recDepth = (int) (Math.ceil(Math.log(segmentsCount) / Math.log(2)));
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

    private final class ZetaPartition extends BinaryPartition {

	private ZetaPartition(InstanceBlock firstBlock,
		InstanceBlock lastBlock, int recursions) {
	    this(firstBlock, null, lastBlock, recursions);
	}

	private ZetaPartition(InstanceBlock firstBlock,
		BoundaryPoint upperBoundary, InstanceBlock lastBlock,
		int recursions) {
	    if (recursions-- == 0)
		return;
	    else
		isAccepted = true;
	    cutPoint = bestCutPoint(firstBlock.upperBoundary, null,
		    upperBoundary, lastBlock);
	    if (cutPoint == null)
		return;
	    lowerPartition = new ZetaPartition(firstBlock, cutPoint, lastBlock,
		    recursions);
	    upperPartition = new ZetaPartition(cutPoint, upperBoundary,
		    lastBlock, recursions);
	}

	private ZetaPartition(BoundaryPoint lowerBoundary,
		BoundaryPoint upperBoundary, InstanceBlock lastBlock,
		int recursions) {
	    if (recursions-- == 0)
		return;
	    else
		isAccepted = true;
	    cutPoint = bestCutPoint(lowerBoundary.nextCutPoint(),
		    lowerBoundary, upperBoundary, lastBlock);
	    if (cutPoint == null)
		return;
	    lowerPartition = new ZetaPartition(lowerBoundary, cutPoint,
		    lastBlock, recursions);
	    upperPartition = new ZetaPartition(cutPoint, upperBoundary,
		    lastBlock, recursions);
	}

	private BoundaryPoint bestCutPoint(final BoundaryPoint start,
		final BoundaryPoint lowerBoundary,
		final BoundaryPoint upperBoundary, final InstanceBlock lastBlock) {
	    int maxZeta = Integer.MIN_VALUE;
	    BoundaryPoint bestCutPoint = null;
	    for (BoundaryPoint cutPoint = start; cutPoint != upperBoundary; cutPoint = cutPoint
		    .nextCutPoint()) {
		final int zeta = zeta(
			cutPoint.sumNegativesLowerCut(lowerBoundary),
			cutPoint.sumPositivesLowerCut(lowerBoundary),
			cutPoint.sumNegativesUpperCut(upperBoundary, lastBlock),
			cutPoint.sumPositivesUpperCut(upperBoundary, lastBlock));
		if (zeta > maxZeta) {
		    maxZeta = zeta;
		    bestCutPoint = cutPoint;
		}
	    }
	    if ((double) maxZeta
		    / (double) size(lowerBoundary, upperBoundary, lastBlock) < threshold)
		return null;
	    return bestCutPoint;
	}
    }

    @Override
    BinaryPartition makeBinaryPartition(InstanceBlock firstBlock,
	    InstanceBlock lastBlock) {
	return new ZetaPartition(firstBlock, lastBlock, recDepth);
    }

}
