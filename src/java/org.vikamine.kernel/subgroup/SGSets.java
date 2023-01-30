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
 * Created on 16.11.2004
 *
 */
package org.vikamine.kernel.subgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.NumericTarget;

/**
 * Container class for creating, querying and modifying {@link SGSet} objects.
 * 
 * @author atzmueller
 * 
 */
public final class SGSets {

    protected abstract static class AbstractSGSet implements SGSet {

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
		    + ((subgroups == null) ? 0 : subgroups.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    AbstractSGSet other = (AbstractSGSet) obj;
	    if (subgroups == null) {
		if (other.subgroups != null)
		    return false;
	    } else if (!subgroups.equals(other.subgroups))
		return false;
	    return true;
	}

	private static class SubgroupsIterator implements Iterator<SG> {

	    private final AbstractSGSet abstractSGSet;

	    private final Iterator<SG> innerIterator;

	    public SubgroupsIterator(SGSet sgSet) {
		this.abstractSGSet = (AbstractSGSet) sgSet;
		innerIterator = abstractSGSet.subgroups.iterator();
	    }

	    @Override
	    public boolean hasNext() {
		return innerIterator.hasNext();
	    }

	    @Override
	    public SG next() {
		return innerIterator.next();
	    }

	    @Override
	    public void remove() {
		innerIterator.remove();
		abstractSGSet.recalculateQualities();
	    }
	}

	protected List<SG> subgroups = new ArrayList();

	protected double maxSGQuality = Double.POSITIVE_INFINITY;

	protected double minSGQuality = Double.NEGATIVE_INFINITY;

	private String name;

	protected AbstractSGSet() {
	    super();
	}

	@Override
	public void add(SG subgroup) {
	    if (!contains(subgroup)) {
		addSGInternal(subgroup);
		recalculateQualities();
	    }
	}

	@Override
	public void addAll(Iterable<SG> subgroups) {
	    for (Object o : subgroups) {
		if (!(o instanceof SG)) {
		    continue;
		}
		SG sg = (SG) o;
		if (!contains(sg)) {
		    addSGInternal(sg);
		}
	    }
	    recalculateQualities();
	}

	protected void addSGInternal(SG subgroup) {
	    subgroups.add(subgroup);
	}

	@Override
	public boolean contains(SG otherSG) {
	    return (subgroups.contains(otherSG));
	}

	@Override
	public double getMaxSGQuality() {
	    return maxSGQuality;
	}

	@Override
	public double getMaxSubgroupSize() {
	    double maxSubgroupSize = 0;
	    for (SG sg : subgroups) {
		maxSubgroupSize = Math.max(
			sg.getStatistics().getSubgroupSize(), maxSubgroupSize);
	    }
	    return maxSubgroupSize;
	}

	@Override
	public double getMinSGQuality() {
	    return minSGQuality;
	}

	@Override
	public String getName() {
	    return name;
	}

	@Override
	public Iterator<SG> iterator() {
	    return new SubgroupsIterator(this);
	}

	protected void recalculateQualities() {
	    if ((subgroups == null) || (subgroups.isEmpty())) {
		maxSGQuality = Double.NEGATIVE_INFINITY;
		minSGQuality = Double.POSITIVE_INFINITY;
	    } else {
		double firstSGQuality = (subgroups.get(0)).getQuality();
		maxSGQuality = firstSGQuality;
		minSGQuality = firstSGQuality;
		Iterator sGIter = subgroups.iterator();
		while (sGIter.hasNext()) {
		    SG sg = (SG) sGIter.next();
		    if (sg.getQuality() > maxSGQuality)
			maxSGQuality = sg.getQuality();
		    if (sg.getQuality() < minSGQuality)
			minSGQuality = sg.getQuality();
		}
	    }
	}

	@Override
	public List<SG> toSortedList(boolean ascending) {
	    ArrayList<SG> result = new ArrayList<SG>(subgroups);
	    final boolean finalInvert = ascending;
	    Collections.sort(result, new Comparator<SG>() {
		@Override
		public int compare(SG sg1, SG sg2) {
		    int result = Double.compare(sg1.getQuality(),
			    sg2.getQuality());
		    return finalInvert ? result : -result;
		}
	    });
	    return result;
	}

	@Override
	public void remove(SG sg) {
	    subgroups.remove(sg);
	    recalculateQualities();
	}

	@Override
	public void removeAll(Iterable<SG> sgs) {
	    for (Iterator iter = sgs.iterator(); iter.hasNext();) {
		subgroups.remove(iter.next());
	    }
	    recalculateQualities();
	}

	@Override
	public void setName(String string) {
	    name = string;
	}

	@Override
	public int size() {
	    return subgroups.size();
	}

	@Override
	public String toString() {
	    return "SGSet (" + minSGQuality + ", " + maxSGQuality
		    + ") - subgroups: " + subgroups.toString();
	}

	@Override
	public SGSet sortSubgroupsByQualityDescending() {
	    AbstractSGSet sgSet = (AbstractSGSet) SGSets.copySGSet(this);
	    Collections.sort(sgSet.subgroups, new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
		    if ((o1 instanceof SG) && (o2 instanceof SG)) {
			double q1 = ((SG) o1).getQuality();
			double q2 = ((SG) o2).getQuality();

			return -1 * Double.compare(q1, q2);
		    } else {
			throw new RuntimeException("No SG element in SGSet!");
		    }
		}

		@Override
		public boolean equals(Object obj) {
		    return super.equals(obj);
		}

		@Override
		public int hashCode() {
		    assert false;
		    return 42;
		}
	    });
	    return sgSet;
	}
    }

    // TODO: refactor using PriorityQueue
    private static class KBestSGSetImpl extends AbstractSGSet implements
	    KBestSGSet {
	private double sgMinQualityLimit = DEFAULT_SG_MIN_QUALITY_LIMIT;

	private int maxSGCount = DEFAULT_MAX_SG_COUNT;

	public KBestSGSetImpl() {
	    super();
	}

	@Override
	public void addByReplacingWorstSG(SG betterSG) {
	    if (!contains(betterSG)) {
		if (subgroups.size() >= getMaxSGCount()) {
		    SG worstSG = betterSG;
		    double worstQuality = betterSG.getQuality();
		    Iterator sGIter = subgroups.iterator();
		    while (sGIter.hasNext()) {
			SG sg = (SG) sGIter.next();
			if (sg.getQuality() < worstQuality) {
			    worstSG = sg;
			    worstQuality = sg.getQuality();
			}
		    }
		    subgroups.remove(worstSG);
		}
		addSGInternal(betterSG);
		recalculateQualities();
	    }
	}

	/**
	 * @return Returns the maxNumberOfSubgroups.
	 */
	@Override
	public int getMaxSGCount() {
	    return maxSGCount;
	}

	@Override
	public double getQualitySum() {
	    double quality = 0;
	    for (SG sg : this) {
		quality += sg.getQuality();
	    }
	    return quality;
	}

	/**
	 * @return Returns the minSGQualityLimit.
	 */
	@Override
	public double getSGMinQualityLimit() {
	    return sgMinQualityLimit;
	}

	@Override
	public void initWith(SGSet sgSet) {
	    subgroups.addAll(sgSet.toSortedList(false));
	    this.maxSGQuality = sgSet.getMaxSGQuality();
	    this.minSGQuality = sgSet.getMinSGQuality();
	    if (sgSet instanceof KBestSGSet) {
		KBestSGSet kBestSGSet = (KBestSGSet) sgSet;
		setMaxSGCount(kBestSGSet.getMaxSGCount());
		setSGMinQualityLimit(kBestSGSet.getSGMinQualityLimit());
	    }
	}

	@Override
	public boolean isInKBestQualityRange(double quality) {
	    if (quality >= getSGMinQualityLimit()) {
		if (subgroups.size() < getMaxSGCount()) {
		    return true;
		} else {
		    return quality > getMinSGQuality();
		}
	    }
	    return false;
	}

	protected void setMaxSGCount(int maxNumberOfSubgroups) {
	    this.maxSGCount = maxNumberOfSubgroups;
	}

	/**
	 * @param minSGQualityLimit
	 *            The minSGQualityLimit to set.
	 */
	protected void setSGMinQualityLimit(double minSGQualityLimit) {
	    this.sgMinQualityLimit = minSGQualityLimit;
	}

	@Override
	public String toComprehensiveString() {
	    String result = "SGSet (" + minSGQuality + ", " + maxSGQuality
		    + ") - subgroups: \n";
	    for (SG sg : subgroups) {
		result += "q: (" + sg.getQuality() + ") \t"
			+ sg.getSGDescription() + "\n";
	    }
	    return result;
	}
    }

    private static class SGSetImpl extends AbstractSGSet {

	public SGSetImpl() {
	    super();
	}

	@Override
	public void initWith(SGSet sgSet) {
	    subgroups.addAll(sgSet.toSortedList(false));
	    this.maxSGQuality = sgSet.getMaxSGQuality();
	    this.minSGQuality = sgSet.getMinSGQuality();
	}
    }

    private static final double EPSILON = 0.05;

    public static SGSet copySGSet(SGSet sgSet) {
	SGSet newSGSet = null;
	if (sgSet instanceof SGSetImpl) {
	    newSGSet = new SGSetImpl();
	    ((SGSetImpl) newSGSet).initWith(sgSet);
	} else if (sgSet instanceof KBestSGSet) {
	    newSGSet = new KBestSGSetImpl();
	    ((KBestSGSetImpl) newSGSet).initWith(sgSet);
	}
	return newSGSet;
    }

    public static KBestSGSet createKBestSGSet(int maxSGCount,
	    double sgMinQualityLimit) {
	KBestSGSetImpl kBestSGSet = new KBestSGSetImpl();
	kBestSGSet.setMaxSGCount(maxSGCount);
	kBestSGSet.setSGMinQualityLimit(sgMinQualityLimit);
	return kBestSGSet;
    }

    public static SGSet createSGSet() {
	return new SGSetImpl();
    }

    private static List extractFP(SG sg) {
	List fps = new LinkedList();
	for (Iterator<DataRecord> iter = sg.subgroupInstanceIterator(); iter
		.hasNext();) {
	    DataRecord inst = iter.next();
	    if ((sg.getTarget() instanceof BooleanTarget)
		    && !((BooleanTarget) sg.getTarget()).isPositive(inst)) {
		fps.add(inst);
	    } else if ((sg.getTarget() instanceof NumericTarget)) {
		fps.add(inst);
	    }
	}
	return fps;
    }

    private static List extractTP(SG sg) {
	List tps = new LinkedList();
	for (Iterator<DataRecord> iter = sg.subgroupInstanceIterator(); iter
		.hasNext();) {
	    DataRecord inst = iter.next();
	    if ((sg.getTarget() instanceof BooleanTarget)
		    && ((BooleanTarget) sg.getTarget()).isPositive(inst)) {
		tps.add(inst);
	    } else if ((sg.getTarget() instanceof NumericTarget)) {
		tps.add(inst);
	    }
	}
	return tps;
    }

    /**
     * Returns true, if the true positives of the newSG are a strict subset of
     * the referenceSG, AND the false positives are a strict superset of the
     * referenceSG, and false otherwise.
     * 
     * @param newSG
     * @param referenceSG
     * @return
     */
    private static boolean isSGStrictlyIrrelevant(SG newSG, SG referenceSG) {
	if (newSG.getTarget().isNumeric()
		|| referenceSG.getTarget().isNumeric()) {
	    throw new IllegalArgumentException(
		    "Relevance check can only be performed for boolean targets");
	}

	double newSGTPCount = ((SGStatisticsBinary) newSG.getStatistics())
		.getTp();
	double newSGFPCount = ((SGStatisticsBinary) newSG.getStatistics())
		.getFp();
	double referenceSGTPCount = ((SGStatisticsBinary) referenceSG
		.getStatistics()).getTp();
	double referenceSGFPCount = ((SGStatisticsBinary) referenceSG
		.getStatistics()).getFp();

	// quick check - compare sizes
	if ((referenceSGFPCount != 0)
		&& ((newSGTPCount > referenceSGTPCount) || (newSGFPCount < referenceSGFPCount))) {
	    return false;
	} else {
	    List newSGTP = extractTP(newSG);
	    List newSGFP = extractFP(newSG);
	    List referenceSGTP = extractTP(referenceSG);
	    List referenceSGFP = extractFP(referenceSG);

	    if ((newSGFPCount == 0) && (referenceSGFPCount == 0)
		    && (referenceSGTP.containsAll(newSGTP))) {
		if (newSG.getSGDescription().size() < referenceSG
			.getSGDescription().size()) {
		    return false;
		} else if (newSG.getSGDescription().size() == referenceSG
			.getSGDescription().size()
			&& (newSG.getStatistics().getDefinedPopulationCount() > referenceSG
				.getStatistics().getDefinedPopulationCount())) {
		    return false;
		} else {
		    return true;
		}
	    } else if (referenceSGTP.containsAll(newSGTP)
		    && (newSGFP.containsAll(referenceSGFP))) {
		if ((newSGTPCount == referenceSGTPCount)
			&& (newSGFPCount == referenceSGFPCount)) {
		    if (newSG.getSGDescription().size() < referenceSG
			    .getSGDescription().size()) {
			return false;
		    } else if (newSG.getSGDescription().size() == referenceSG
			    .getSGDescription().size()
			    && (newSG.getStatistics()
				    .getDefinedPopulationCount() > referenceSG
				    .getStatistics()
				    .getDefinedPopulationCount())) {
			return false;

		    } else {
			return true;
		    }
		} else {
		    return true;
		}
	    } else {
		return false;
	    }
	}
    }

    private static boolean isSGStrictlyIrrelevant(SG newSG, SG referenceSG,
	    int epsilon) {
	if (newSG.getTarget().isNumeric()
		|| referenceSG.getTarget().isNumeric()) {
	    throw new IllegalArgumentException(
		    "Relevance check can only be performed for boolean targets");
	}

	double newSGTPCount = ((SGStatisticsBinary) newSG.getStatistics())
		.getTp();
	double newSGFPCount = ((SGStatisticsBinary) newSG.getStatistics())
		.getFp();
	double referenceSGTPCount = ((SGStatisticsBinary) referenceSG
		.getStatistics()).getTp();
	double referenceSGFPCount = ((SGStatisticsBinary) referenceSG
		.getStatistics()).getFp();

	// double atLeastNewTPs = newSGTPCount - referenceSGTPCount;
	// double atLeastRefFPCount = referenceSGFPCount - newSGFPCount;
	// if ((atLeastNewTPs + atLeastRefFPCount) > epsilon) {
	// return false;
	// }

	List newSGTP = extractTP(newSG);
	List newSGFP = extractFP(newSG);
	List referenceSGTP = extractTP(referenceSG);
	List referenceSGFP = extractFP(referenceSG);

	int tpExceptions = numberOfContainsAllExceptions(referenceSGTP, newSGTP);
	int fpExceptions = numberOfContainsAllExceptions(newSGFP, referenceSGFP);

	if (tpExceptions + fpExceptions <= epsilon) {
	    if ((newSGTPCount == referenceSGTPCount)
		    && (newSGFPCount == referenceSGFPCount)) {
		if (newSG.getSGDescription().size() < referenceSG
			.getSGDescription().size()) {
		    return false;
		} else if (newSG.getSGDescription().size() == referenceSG
			.getSGDescription().size()
			&& (newSG.getStatistics().getDefinedPopulationCount() > referenceSG
				.getStatistics().getDefinedPopulationCount())) {
		    return false;

		} else {
		    return true;
		}
	    } else {
		return true;
	    }
	}
	return false;
    }

    public static boolean isSGStrictlyIrrelevant(SG newSG, Iterable<SG> sgSet) {
	for (SG refSG : sgSet) {
	    if (!newSG.equals(refSG) && isSGStrictlyIrrelevant(newSG, refSG)) {
		return true;
	    }
	}
	return false;
    }

    public static SGSet mergeSGSetsToKBestSGSet(List<SGSet> multipleSets,
	    int maxSGCount, double minQualitylimit) {
	if (multipleSets == null) {
	    throw new IllegalArgumentException("try to merge null SGset");
	}
	KBestSGSet result = SGSets
		.createKBestSGSet(maxSGCount, minQualitylimit);
	for (SGSet s : multipleSets) {
	    for (SG sg : s) {
		if (result.isInKBestQualityRange(sg.getQuality())) {
		    result.addByReplacingWorstSG(sg);
		}
	    }
	}
	return result;
    }

    private static int numberOfContainsAllExceptions(List containingList,
	    List containedList) {
	int exceptionCounter = 0;
	for (Object o : containedList) {
	    if (!containingList.contains(o)) {
		exceptionCounter++;
	    }
	}
	return exceptionCounter;
    }

    public static void removeEmptySubgroupsFromSGSet(SGSet sgSet) {
	for (Iterator iter = sgSet.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    if (sg.getSGDescription().isEmpty()) {
		iter.remove();
	    }
	}
    }

    public static void removeIrrelevantSubgroupsFromSGSet(int epsilon,
	    SGSet sgSet) {
	List<SG> sgsToRemove = new LinkedList<SG>();
	int i = 0;
	for (Iterator iter = sgSet.iterator(); iter.hasNext();) {
	    SG sg = (SG) iter.next();
	    for (SG sgTest : sgSet) {
		if ((!sg.equals(sgTest)) && (!sgsToRemove.contains(sgTest))
			&& (!sgsToRemove.contains(sg))) {
		    if (isSGStrictlyIrrelevant(sgTest, sg, epsilon)) {
			sgsToRemove.add(sgTest);
		    }
		}
		// System.out.print(".");
	    }
	    System.out.print(";" + i);
	    i++;
	}
	sgSet.removeAll(sgsToRemove);
	System.out.println();
    }

    public static void removeIrrelevantSubgroupsFromSGSet(SGSet sgSet) {
	List<SG> sgsToRemove = new LinkedList<SG>();
	for (Iterator iter = sgSet.iterator(); iter.hasNext();) {

	    SG sg = (SG) iter.next();
	    for (SG sgTest : sgSet) {
		if ((!sg.equals(sgTest)) && (!sgsToRemove.contains(sgTest))
			&& (!sgsToRemove.contains(sg))) {
		    if (isSGStrictlyIrrelevant(sgTest, sg)) {
			sgsToRemove.add(sgTest);
		    }
		}
	    }
	}
	sgSet.removeAll(sgsToRemove);
    }

    public static void removeSubgroupsIrrelevantTo(SG sg, SGSet sgSet) {
	List<SG> sgsToRemove = new LinkedList<SG>();
	for (SG sgTest : sgSet) {
	    if ((!sg.equals(sgTest)) && (!sgsToRemove.contains(sgTest))) {
		if (isSGStrictlyIrrelevant(sgTest, sg)) {
		    sgsToRemove.add(sgTest);
		}
	    }
	}
	sgSet.removeAll(sgsToRemove);
    }

    public static double getQualitySum(SGSet set) {
	double result = 0;
	for (SG sg : set) {
	    result += sg.getQuality();
	}
	return result;
    }

    // is never used, since this class only contains static methods
    private SGSets() {
	super();
    }

    public static boolean haveEquivalentQualities(SGSet thisSet, SGSet otherSet) {
	return hasPairwiseBetterQuality(thisSet, otherSet)
		&& hasPairwiseBetterQuality(otherSet, thisSet);
    }

    public static boolean hasPairwiseBetterQuality(SGSet thisSet, SGSet otherSet) {
	List<SG> thisSorted = thisSet.toSortedList(false);
	List<SG> otherSorted = otherSet.toSortedList(false);

	if (otherSorted.size() > thisSorted.size()) {
	    return false;
	}

	for (int i = 0; i < thisSorted.size(); i++) {
	    if ((thisSorted.get(i).getQuality() - otherSorted.get(i)
		    .getQuality()) > EPSILON) {
		return false;
	    }
	}
	return true;
    }
}
