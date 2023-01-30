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
 * Created on 10.08.2004
 *
 */
package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math3.stat.Frequency;
import org.vikamine.kernel.statistics.DescriptiveStatisticsDummy;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

/**
 * A {@link DataView} is similar to a database view on a set of tables
 * concerning a dataset (DataRecordSet).
 * 
 * A {@link DataView} grants access to a {@link DataRecordSet} restricted by
 * filters specified using {@link SGNominalSelector}s. Those
 * {@link SGNominalSelector} make up a {@link SGDescription}. A
 * {@link DataRecord} will only be returned if it matches (
 * {@link SGDescription#isMatching(DataRecord)}) that {@link SGDescription}.
 * <p/>
 * 
 * Also provides some statistics keeping (
 * {@link DataView#getStatsForAttribute(NumericAttribute)},
 * {@link DataView#getFrequencyForAttribute(NominalAttribute)}).
 * 
 * @author atzmueller, becker (comments)
 */
public class DataView implements Iterable<DataRecord> {

    static private class DataViewRecordIterator implements Iterator<DataRecord> {

	private final IDataRecordSet instances;

	private int counter = 0;

	private int size = 0;

	private boolean applyFilter;

	private DataRecord nextInstance;

	private SGDescription populationRangesFilteringSGDescription;

	public DataViewRecordIterator(IDataRecordSet instances,
		List<SGSelector> populationRangesSelectors) {
	    this.instances = instances;
	    this.size = instances.getNumInstances();
	    if ((populationRangesSelectors == null)
		    || (populationRangesSelectors.isEmpty())) {
		applyFilter = false;
	    } else {
		applyFilter = true;
		populationRangesFilteringSGDescription = new SGDescription();
		for (Iterator<SGSelector> iter = populationRangesSelectors
			.iterator(); iter.hasNext();) {
		    SGSelector sel = iter.next();
		    populationRangesFilteringSGDescription.add(sel);
		}
	    }
	}

	@Override
	public boolean hasNext() {
	    for (; (nextInstance == null && counter < size); counter++) {
		DataRecord instance = instances.get(counter);
		if (!applyFilter
			|| populationRangesFilteringSGDescription
				.isMatching(instance)) {
		    nextInstance = instance;
		}
	    }
	    return nextInstance != null;
	}

	@Override
	public DataRecord next() {
	    if (hasNext()) {
		DataRecord instance = nextInstance;
		assert (instance != null);
		nextInstance = null;
		return instance;
	    } else {
		throw new NoSuchElementException();
	    }
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException(
		    "DataViewRecordIterator can not remove Elements!");
	}
    }

    private final IDataRecordSet drs;

    private final List<SGSelector> populationRangesSelectors;

    private final HashMap<Attribute, Frequency> frequencyMap;

    private final HashMap<Attribute, DescriptiveStatisticsDummy> numericStatsMap;

    private DataRecordIteration instanceIteration;

    public DataView(IDataRecordSet theInstances,
	    List<SGSelector> populationRangesSelectors) {
	super();
	this.drs = theInstances;
	this.populationRangesSelectors = populationRangesSelectors;
	this.frequencyMap = new HashMap<Attribute, Frequency>();
	this.numericStatsMap = new HashMap<Attribute, DescriptiveStatisticsDummy>();
	init();
    }

    /**
     * Creates a new {@link DataView} based on the internal
     * {@link DataRecordSet} given a {@link List} of {@link SGSelector}s calling
     * {@link DataView#Population(DataRecordSet, List)}.
     * 
     * @param rangeSelectors
     *            list of {@link SGNominalSelector}s
     * 
     * @return new {@link DataView}
     */
    public DataView createPopulation(List<SGSelector> rangeSelectors) {
	return new DataView(drs, rangeSelectors);
    }

    public IDataRecordSet dataset() {
	return drs;
    }

    private void init() {
	if ((populationRangesSelectors == null)
		|| (populationRangesSelectors.isEmpty())) {
	    this.instanceIteration = new DataRecordIteration(drs);
	} else {
	    Iterator<DataRecord> iter = new DataViewRecordIterator(drs,
		    populationRangesSelectors);
	    this.instanceIteration = new DataRecordIteration(iter);
	}
    }

    public Iterator<DataRecord> instanceIterator() {
	return instanceIteration.iterator();
    }

    /**
     * @return Returns a new copy of the populationRangesSelectors.
     */
    public List<SGSelector> populationRangesSelectors() {
	return new ArrayList<SGSelector>(populationRangesSelectors);
    }

    public int size() {
	return instanceIteration.size();
    }

    /**
     * @return an {@link Iterator} over all instances ({@link DataRecord}s) of
     *         this population
     */
    @Override
    public Iterator<DataRecord> iterator() {
	return instanceIterator();
    }

    /**
     * <b>Note:</b> If the {@link DescriptiveStatisticsDummy} for the given
     * {@link Attribute} has never been retrieved, this method will trigger an
     * iteration over the whole {@link DataRecordSet}.
     * 
     * @see SGUtils#createDescriptiveStats(Iterable, NumericAttribute)
     * 
     * @param att
     *            {@link Attribute} to retrieve
     *            {@link DescriptiveStatisticsDummy} for
     * 
     * @return {@link DescriptiveStatisticsDummy} for the given
     *         {@link Attribute}
     */
    public DescriptiveStatisticsDummy getStatsForAttribute(NumericAttribute att) {
	DescriptiveStatisticsDummy stat = numericStatsMap.get(att);
	if (stat == null) {
	    stat = new DescriptiveStatisticsDummy(
		    SGUtils.createDescriptiveStats(this, att));
	    numericStatsMap.put(att, stat);
	}
	return stat;
    }

    /**
     * <b>Note:</b> If the {@link Frequency} for the given {@link Attribute} has
     * never been retrieved, this method will trigger an iteration over the
     * whole {@link DataRecordSet}.
     * 
     * @see SGUtils#createFrequencyStats(Iterable, NominalAttribute)
     * 
     * @param att
     *            {@link Attribute} to retrieve {@link Frequency} for
     * 
     * @return {@link Frequency} for the given {@link Attribute}
     */
    public Frequency getFrequenciesForAttribute(Attribute att) {
	Frequency stat = frequencyMap.get(att);
	if (stat == null) {
	    stat = SGUtils.createFrequencyStats(this, att);
	    frequencyMap.put(att, stat);
	}
	return stat;
    }
}
