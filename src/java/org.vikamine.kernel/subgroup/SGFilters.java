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

package org.vikamine.kernel.subgroup;

import org.vikamine.kernel.util.SGImprovementCalculator;

/**
 * {@link SGFilters} specifies the interface for filtering an {@link Iterable}
 * of {@link SG}s, resulting in a filtered {@link SGSet}.
 * 
 * @author lemmerich
 */
public interface SGFilters {

    public static class MinImprovementFilterGlobal implements SGFilters {

	double minImprovement;

	public MinImprovementFilterGlobal(double minImprovement) {
	    super();
	    this.minImprovement = minImprovement;
	}

	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    SGImprovementCalculator calc = new SGImprovementCalculator();
	    for (SG sg : sgSet) {
		double improvement = calc.calculateMinImprovement(sg);
		if (improvement > minImprovement) {
		    result.add(sg);
		}
	    }
	    return result;
	}
    }

    public static class SizeFilter implements SGFilters {

	double minSize;

	public SizeFilter(double size) {
	    super();
	    this.minSize = size;
	}

	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    for (SG sg : sgSet) {
		double size = sg.getStatistics().getSubgroupSize();
		if (size > minSize) {
		    result.add(sg);
		}
	    }
	    return result;
	}
    }

    public static class MinImprovementFilterOnSGSet implements SGFilters {

	double minImprovement;

	public MinImprovementFilterOnSGSet(double minImprovement) {
	    super();
	    this.minImprovement = minImprovement;
	}

	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    for (SG sg : sgSet) {
		double improvement = Double.MAX_VALUE;
		for (SG sg2 : sgSet) {
		    // sg Description has "larger" description, i.e., is more
		    // specialized
		    if (!sg.equals(sg2)
			    && sg.getSGDescription().isSpecialization(
				    sg2.getSGDescription())) {
			improvement = Math.min(improvement, sg.getStatistics()
				.getDeviation()
				- sg2.getStatistics().getDeviation());
		    }
		}
		if (improvement > minImprovement) {
		    result.add(sg);
		}

	    }
	    return result;
	}
    }

    public static class RelevancyFilter implements SGFilters {
	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    for (SG sg : sgSet) {
		if (!SGSets.isSGStrictlyIrrelevant(sg, result)) {
		    result.add(sg);
		}
	    }
	    return result;
	}
    }

    public static class SignificantImprovementFilterGlobal implements SGFilters {

	double alpha;

	/**
	 * @param alpha
	 *            significance threshold
	 */
	public SignificantImprovementFilterGlobal(double alpha) {
	    super();
	    this.alpha = alpha;
	}

	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    SGImprovementCalculator calc = new SGImprovementCalculator();
	    for (SG sg : sgSet) {
		double maxPValue = calc.calculateMaxPValueToSubsets(sg);
		if (maxPValue < alpha) {
		    result.add(sg);
		}
	    }
	    return result;
	}
    }

    public static class SignificantImprovementFilterOnSet implements SGFilters {

	double alpha;

	/**
	 * @param alpha
	 *            significance threshold
	 */
	public SignificantImprovementFilterOnSet(double alpha) {
	    super();
	    this.alpha = alpha;
	}

	@Override
	public SGSet filterSGs(Iterable<SG> sgSet) {
	    SGSet result = SGSets.createSGSet();
	    for (SG sg : sgSet) {
		if (sg.getTarget().isNumeric())
		    throw new IllegalArgumentException(
			    "Not applicable for numeric target "
				    + sg.getTarget());

		boolean add = true;
		for (SG sg2 : sgSet) {
		    // sg Description has "larger" description, i.e., is more
		    // specialized
		    if (!sg.equals(sg2)
			    && sg.getSGDescription().isSpecialization(
				    sg2.getSGDescription())) {
			SGStatisticsBinary sgStats = (SGStatisticsBinary) sg
				.getStatistics();
			SGStatisticsBinary sg2Stats = (SGStatisticsBinary) sg2
				.getStatistics();
			double a = sgStats.getTp();
			double b = sg2Stats.getTp() - a;
			double c = sgStats.getFp();
			double d = sg2Stats.getFp() - c;
			double significanceOfDifference = SGUtils
				.getChi2SignificanceNiveau(a, b, c, d);
			if (significanceOfDifference > alpha) {
			    add = false;
			    break;
			}
		    }
		}
		if (add) {
		    result.add(sg);
		}

	    }
	    return result;
	}
    }

    public SGSet filterSGs(Iterable<SG> sgSet);

}
