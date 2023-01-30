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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.vikamine.kernel.data.DataRecordIteration;
import org.vikamine.kernel.util.BitSetUtils;

public class KBestSGSetBitSetBased implements KBestSGSet {

    private int epsilon = 0;

    private double sgMinQualityLimit = DEFAULT_SG_MIN_QUALITY_LIMIT;

    private int maxSGCount = DEFAULT_MAX_SG_COUNT;

    private DataRecordIteration dataRecordIteration;

    private PriorityQueue<SG> sgList;

    private HashMap<SG, BitSet> negativesMap;

    private HashMap<SG, BitSet> positivesMap;

    private double maxQuality;

    private String name;

    public KBestSGSetBitSetBased(int maxSGCount, double minQualityLimit) {
	Comparator<SG> comparator = new Comparator<SG>() {
	    @Override
	    public int compare(SG o1, SG o2) {
		return Double.compare(o1.getQuality(), o2.getQuality());
	    }
	};
	this.maxQuality = 0;
	this.sgList = new PriorityQueue<SG>(maxSGCount + 1, comparator);
	this.negativesMap = new HashMap<SG, BitSet>();
	this.positivesMap = new HashMap<SG, BitSet>();
	this.maxSGCount = maxSGCount;
	this.sgMinQualityLimit = minQualityLimit;
	setEpsilon(0);
    }

    @Override
    public void add(SG subgroup) {
	if (isInKBestQualityRange(subgroup.getQuality())) {
	    addByReplacingWorstSG(subgroup);
	}
    }

    @Override
    public void addAll(Iterable<SG> subgroups) {
	for (SG sg : subgroups) {
	    add(sg);
	}
    }

    @Override
    public void addByReplacingWorstSG(SG betterSG) {
	if (dataRecordIteration == null) {
	    throw new IllegalArgumentException(
		    "not implemented yet. Must generate hashed BitSets here!");
	}
	BitSet allBitSet = BitSetUtils.generateBitSet(betterSG,
		dataRecordIteration);
	BitSet posBitSet = BitSetUtils.generatePositives(betterSG,
		dataRecordIteration);
	addByReplacingWorstSG(betterSG, allBitSet, posBitSet);
    }

    public void addByReplacingWorstSG(SG betterSG, BitSet allBitSet,
	    BitSet positivesBitSet) {
	sgList.offer(betterSG);
	positivesMap.put(betterSG, positivesBitSet);

	BitSet fpBitSet = (BitSet) allBitSet.clone();
	fpBitSet.andNot(positivesBitSet);
	negativesMap.put(betterSG, fpBitSet);

	maxQuality = Math.max(maxQuality, betterSG.getQuality());
	if (sgList.size() > maxSGCount) {
	    sgList.poll();
	}
    }

    @Override
    public boolean contains(SG otherSG) {
	return sgList.contains(otherSG);
    }

    public DataRecordIteration getDataRecordIteration() {
	return dataRecordIteration;
    }

    public int getEpsilon() {
	return epsilon;
    }

    @Override
    public int getMaxSGCount() {
	return maxSGCount;
    }

    @Override
    public double getMaxSGQuality() {
	return maxQuality;
    }

    @Override
    public double getMaxSubgroupSize() {
	double result = -1;
	for (SG sg : sgList) {
	    result = Math.max(result, sg.getStatistics().getSubgroupSize());
	}
	return result;
    }

    @Override
    public double getMinSGQuality() {
	if (sgList.isEmpty()) {
	    return getSGMinQualityLimit();
	}
	return sgList.peek().getQuality();
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public double getQualitySum() {
	double quality = 0;
	for (SG sg : this) {
	    quality += sg.getQuality();
	}
	return quality;
    }

    @Override
    public double getSGMinQualityLimit() {
	return sgMinQualityLimit;
    }

    @Override
    public boolean isInKBestQualityRange(double quality) {
	if (quality >= getSGMinQualityLimit()) {
	    if (sgList.size() < getMaxSGCount()) {
		return true;
	    } else {
		return quality > getMinSGQuality();
	    }
	}
	return false;
    }

    public boolean isSGStrictlyIrrelevant(SG sg) {
	BitSet allBitSet = BitSetUtils.generateBitSet(sg, dataRecordIteration);
	BitSet positives = BitSetUtils.generatePositives(sg,
		dataRecordIteration);
	return isSGStrictlyIrrelevant(allBitSet, positives);
    }

    /**
     * returns true, if a subgroup with given instances and positives is
     * irrelevant w.r.t any sg in this set.
     * 
     * @param bitSetAll
     * @param bitSetPositives
     * @return
     */
    public boolean isSGStrictlyIrrelevant(BitSet bitSetAll,
	    BitSet bitSetPositives) {
	for (SG refSG : sgList) {
	    if (isSGStrictlyIrrelevant(bitSetAll, bitSetPositives, refSG)) {
		return true;
	    }
	}
	return false;
    }

    private boolean isSGStrictlyIrrelevant(BitSet bitSetAll,
	    BitSet bitSetPositives, SG referenceSG) {

	BitSet tpsOnlyInNew = (BitSet) bitSetPositives.clone();
	BitSet positivesReference = positivesMap.get(referenceSG);
	if (positivesReference == null) {
	    throw new IllegalArgumentException();
	}
	tpsOnlyInNew.andNot(positivesReference);

	if (tpsOnlyInNew.cardinality() > epsilon) {
	    return false;
	}

	BitSet referenceFP = negativesMap.get(referenceSG);
	if (referenceFP == null) {
	    throw new IllegalArgumentException();
	}

	BitSet newNegatives = (BitSet) bitSetAll.clone();
	newNegatives.andNot(bitSetPositives);
	BitSet fpsOnlyInReference = (BitSet) referenceFP.clone();
	fpsOnlyInReference.andNot(newNegatives);
	if (fpsOnlyInReference.cardinality() > epsilon) {
	    return false;
	}
	return true;
    }

    @Override
    public Iterator<SG> iterator() {
	return sgList.iterator();
    }

    @Override
    public void remove(SG sg) {
	sgList.remove(sg);
    }

    @Override
    public void removeAll(Iterable<SG> sgs) {
	for (SG sg : sgs) {
	    remove(sg);
	}
    }

    public void setDataRecordIteration(DataRecordIteration dataRecordIteration) {
	this.dataRecordIteration = dataRecordIteration;
    }

    public void setEpsilon(int epsilon) {
	this.epsilon = epsilon;
    }

    @Override
    public void setName(String string) {
	name = string;
    }

    @Override
    public int size() {
	return sgList.size();
    }

    @Override
    public List<SG> toSortedList(boolean ascending) {
	ArrayList<SG> result = new ArrayList<SG>(sgList);
	final boolean finalInvert = ascending;
	Collections.sort(result, new Comparator<SG>() {
	    @Override
	    public int compare(SG sg1, SG sg2) {
		int result = Double.compare(sg1.getQuality(), sg2.getQuality());
		return finalInvert ? result : -result;
	    }
	});
	return result;
    }

    public void testAndRemoveIrrelevantSubgroupsFromSGSet() {
	List<SG> sgsToRemove = new LinkedList<SG>();
	for (SG sgReference : sgList) {
	    for (SG sgTest : sgList) {
		if ((!sgReference.equals(sgTest))
			&& (!sgsToRemove.contains(sgTest))) {

		    if (sgReference.getQuality() < sgTest.getQuality()) {
			continue;
		    }
		    BitSet positives = positivesMap.get(sgTest);
		    BitSet allInstances = (BitSet) negativesMap.get(sgTest)
			    .clone();
		    allInstances.or(positives);

		    if (isSGStrictlyIrrelevant(allInstances, positives,
			    sgReference)) {
			sgsToRemove.add(sgTest);
		    }
		}
	    }
	}
	removeAll(sgsToRemove);
    }

    @Override
    public String toComprehensiveString() {
	String result = "SGSet (" + getMinSGQuality() + ", "
		+ getMaxSGQuality() + ") - subgroups: \n";
	ArrayList<SG> list = new ArrayList<SG>();
	list.addAll(sgList);
	Collections.sort(list, sgList.comparator());
	for (SG sg : list) {
	    result += "q: (" + sg.getQuality() + ") \t" + sg.getSGDescription()
		    + "\n";
	}
	return result;
    }

    @Override
    public String toString() {
	return toComprehensiveString();
    }

    @Override
    public void initWith(SGSet sgSet) {
	sgList.addAll(sgSet.toSortedList(false));
	this.maxQuality = sgSet.getMaxSGQuality();
	if (sgSet instanceof KBestSGSet) {
	    KBestSGSet kBestSGSet = (KBestSGSet) sgSet;
	    this.maxSGCount = kBestSGSet.getMaxSGCount();
	    this.sgMinQualityLimit = kBestSGSet.getSGMinQualityLimit();
	}
    }

    @Override
    public SGSet sortSubgroupsByQualityDescending() {
	List<SG> subgroups = Arrays.asList(sgList.toArray(new SG[0]));
	Collections.sort(subgroups, new Comparator() {

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
	SGSet result = SGSets.createSGSet();
	result.addAll(subgroups);
	return result;
    }
}
