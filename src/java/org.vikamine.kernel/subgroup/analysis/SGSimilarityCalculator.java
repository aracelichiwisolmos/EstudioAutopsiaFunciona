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
 * Created on 18.06.2004
 */
package org.vikamine.kernel.subgroup.analysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.subgroup.SG;

/**
 * {@link SGSimilarityCalculator} calculates the similarity between subgroups by
 * their intersection divided by their union.
 * 
 * @author Tobias Vogele
 */
public class SGSimilarityCalculator {

    public static class SGInstanceData {

	private SG referenceSG;

	boolean[] contained;

	int[] instanceIndices;

	protected SGInstanceData() {
	    super();
	}

	public SGInstanceData(SG sg) {
	    referenceSG = sg;
	}

	public SG getSG() {
	    return referenceSG;
	}
    }

    private static boolean equalTargets(SG s1, SG s2) {
	return (((s1.getTarget() == null) && (s2.getTarget() == null)) || ((s1
		.getTarget() != null) && (s2.getTarget() != null) && (s1
		    .getTarget().equals(s2.getTarget()))));
    }

    /**
     * computes the ration of intersection of two subgroups. if one SG is null,
     * it returns NaN.
     */
    public static double calculateSimilarity(SG s1, SG s2) {
	if (s1 == null || s2 == null) {
	    return Double.NaN;
	}
	if (s1 == s2) {
	    return 1;
	}
	int intersection = 0;
	int union = 0;
	// we take only the population of the first SG, this should be ok
	// by convention: if two subgroups do have different targets, then
	// their similarity is 0!
	if (!equalTargets(s1, s2)) {
	    return 0;
	}

	for (Iterator<DataRecord> iter = s1.getPopulation().instanceIterator(); iter
		.hasNext();) {
	    DataRecord i = iter.next();
	    boolean b1 = s1.getSGDescription().isMatching(i);
	    boolean b2 = s2.getSGDescription().isMatching(i);
	    if (b1 || b2) {
		union++;
	    }
	    if (b1 && b2) {
		intersection++;
	    }

	}
	if (union == 0) {
	    return 0; // avoid division by zero
	}
	return (double) intersection / union;
    }

    /**
     * calculates the similarity between two subgroups by using their
     * SGInstanceData-Objects.
     */
    public static double calculateSimilarity(SGInstanceData data1,
	    SGInstanceData data2) {

	// by convention: if two subgroups do have different targets, then
	// their similarity is 0!
	if (!equalTargets(data1.getSG(), data2.getSG())) {
	    return 0;
	}

	int intersection = 0;
	boolean useFirst = data1.instanceIndices.length < data2.instanceIndices.length;
	int[] indices = useFirst ? data1.instanceIndices
		: data2.instanceIndices;
	boolean[] contained = useFirst ? data2.contained : data1.contained;
	for (int i = 0; i < indices.length; i++) {
	    boolean inOther = contained[indices[i]];
	    if (inOther) {
		intersection++;
	    }
	}
	int union = data1.instanceIndices.length + data2.instanceIndices.length
		- intersection;
	if (union == 0) {
	    return 0; // avoid division by zero
	}
	return (double) intersection / union;
    }

    public static SGInstanceData createSGInstanceData(SG sg, DataView popu) {
	SGInstanceData data = new SGInstanceData(sg);
	data.contained = new boolean[popu.size()];
	List sgInstIndices = new LinkedList();
	int instIndex = 0;
	for (Iterator<DataRecord> iter = popu.instanceIterator(); iter
		.hasNext(); instIndex++) {
	    DataRecord inst = iter.next();
	    boolean inSG = sg.getSGDescription().isMatching(inst);
	    data.contained[instIndex] = inSG;
	    if (inSG) {
		sgInstIndices.add(Integer.valueOf(instIndex));
	    }
	}
	data.instanceIndices = new int[sgInstIndices.size()];
	int i = 0;
	for (Iterator iterator = sgInstIndices.iterator(); iterator.hasNext(); i++) {
	    Integer instanceIndex = (Integer) iterator.next();
	    data.instanceIndices[i] = instanceIndex.intValue();
	}
	return data;
    }
}
