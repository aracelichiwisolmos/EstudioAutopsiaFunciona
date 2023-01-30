/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2020 by Martin Atzmueller, Florian Lemmerich, and contributors.
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
 * Created on 16.04.2004
 */
package org.vikamine.kernel.subgroup.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.KBestSGSet;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGSets;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.quality.EstimatetableQFSimpleStatisticsBased;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.FastSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.util.VKMUtil;

/**
 * This abstract class is the parent of all subgroup discovery methods. Its most
 * important method is {@link SDMethod#perform(Options)} . This method is to be
 * called to perform the subgroup discovery.
 * <p/>
 * 
 * The methods {@link SDMethod#getName()} and {@link SDMethod#search(SG)} are to
 * be implemented by the inheriting classes. The {@link SDMethod} class provides
 * some basic functionality:
 * <p/>
 * 
 * The following parameters are stored:
 * <ul>
 * <li><b>{@link SDMethod#ontology}</b>: {@link Ontology}</li>
 * <li><b>{@link SDMethod#options}</b>: {@link Options}</li>
 * 
 * <li><b>{@link SDMethod#selectorsCache}</b>: {@link Collection} of
 * {@link SGSelector}s</li>
 * 
 * <li>{@link SDMethod#methodStat}s: {@link SDMethodStats}; default
 * <code>null</code></li>
 * </ul>
 * 
 * <p/>
 * 
 * {@link SDMethod#methodStats} is updated by
 * {@link SDMethod#perform(SGQualityFunction, SG, List, AttributeValuesMap, Ontology, SConstraints)}
 * if it is not <code>null</code>.
 * <p/>
 * 
 * </p>
 * 
 * @author atzmueller, lemmerich, becker, (comments, clean-up)
 * 
 */
public abstract class SDMethod {

    protected MiningTask task;

    private Ontology ontology;

    private Options options;

    protected List<SGSelector> selectorsCache;

    private SDMethodStats methodStats;

    protected boolean aborted;

    protected boolean verbose = false;

    public SDMethod() {
	this.aborted = false;
	setTask(new MiningTask());
    }

    public SDMethod(MiningTask task) {
	this.aborted = false;
	setTask(task);
    }

    /**
     * Adds the given subgroup to the given {@link KBestSGSet}, removing
     * strictly irrelevant subgroups also.
     * <p/>
     * 
     * <b>Note:</b>Not used by this class. Used by sub classes.
     * 
     * @param result
     *            result subgroup set
     * @param sg
     *            subgroup to add
     */
    protected void addSubgroupToSGSet(KBestSGSet result, SG sg) {
	result.addByReplacingWorstSG(sg);
	if (task.isSuppressStrictlyIrrelevantSubgroups()) {
	    // SGSets.removeIrrelevantSubgroupsFromSGSet(result);
	    SGSets.removeSubgroupsIrrelevantTo(sg, result);
	}
    }

    public void cancel() {
	this.aborted = true;
    }

    public SDMethodStats getMethodStats() {
	return methodStats;
    }

    public abstract String getName();

    public Ontology getOntology() {
	return ontology;
    }

    public DataView getPopulation() {
	return task.getOntology().getDataView();
    }

    public Options getOptions() {
	return options;
    }

    /**
     * during perform () a cache for the selectors is generated. this cache can
     * be used in discovery algorithms by using this method.
     * 
     * @param initialSubgroup
     * @param target
     * @return
     */
    protected List<SGSelector> getSelectorSet(SG initialSubgroup,
	    SGTarget target) {
	return selectorsCache;
    }

    /**
     * this converts the Default selectors in the task to FastSelectors and
     * saves them in the selectorCache
     */
    private void createSelectorCache() {
	selectorsCache = new ArrayList<SGSelector>();
	for (SGSelector sel : task.getSearchSpace()) {
	    if (sel instanceof DefaultSGSelector) {
		selectorsCache.add(new FastSelector(sel.getAttribute(),
			((DefaultSGSelector) sel).getValues()));
	    } else {
		selectorsCache.add(sel);
	    }
	}
    }

    public MiningTask getTask() {
	return task;
    }

    public boolean isVerbose() {
	return verbose;
    }

    /**
     * @param Options
     *            : options for subgroup discovery, e.g., handling of missing
     *            values
     * 
     * @return set of found subgroups
     */
    public final SGSet perform(Options options) {

	setOptions(options);
	createSelectorCache();
	if (verbose) {
	    System.out.println(getName());
	    setMethodStats(new SDMethodStats());
	}
	long startTimeMillis = System.currentTimeMillis();
	long startCPUTime = VKMUtil.getCpuTime();

	SGSet result = search(task.initialSG);
	long endTimeMillis = System.currentTimeMillis();
	long endCPUTime = VKMUtil.getCpuTime();
	if (methodStats != null) {
	    methodStats.setStartTimeMillis(startTimeMillis);
	    methodStats.setEndTimeMillis(endTimeMillis);
	    methodStats.setStartCPUTime(startCPUTime);
	    methodStats.setEndCPUTime(endCPUTime);
	}
	
	convertFastSelectorsInResult(result);

	if (verbose) {
	    System.out.println("Time - took: "
		    + (endTimeMillis - startTimeMillis) + " ms. ("
		    + ((endCPUTime - startCPUTime) / 1000000)
		    + " ms. CPU time)");
	    if (methodStats != null) {
		System.out.println("Steps: " + methodStats.getSubgroupsComputedCounter());
	    }
	}
	if (aborted && verbose) {
	    System.out.println(">>aborted");
	}
	return result;
    }

    protected abstract SGSet search(SG initialSubgroup);

    public void createMethodStats() {
	this.methodStats = new SDMethodStats();
    }

    public void setMethodStats(SDMethodStats methodStats) {
	this.methodStats = methodStats;
    }

    protected void setOptions(Options options) {
	this.options = options;
    }

    public void setVerbose(boolean verbose) {
	this.verbose = verbose;
    }

    public void setTask(MiningTask task) {
	this.task = task;
    }

    protected boolean fullfillsMinSupportBooleanTarget(double tp, double n) {
	return task.fulfillsMinTPSupport(tp)
		&& (n >= task.getMinSubgroupSize());
    }

    protected boolean fullfillsMinSupportNumericTarget(double n) {
	return n >= task.getMinSubgroupSize();
    }

    /**
     * In the subgroup discovery algorithms FastSelectors are utilized, which
     * may be not compatible in other environments (equals-methods!) Thus, they
     * are converted here in default selectors
     */
    private void convertFastSelectorsInResult(SGSet result) {
	for (SG sg : result) {
	    FastSelector.convertToDefaultSGSelectors(sg.getSGDescription());
	}
    }

    public double getOptimisticEstimate(double subgroupSize,
	    double subgroupPositives, double totalPopulationSize,
	    double definedPositives) {
	double optEstimatedQuality = Double.MAX_VALUE;
	if (task.getQualityFunction() instanceof EstimatetableQFSimpleStatisticsBased) {
	    optEstimatedQuality = ((EstimatetableQFSimpleStatisticsBased) task
		    .getQualityFunction()).estimateQuality(subgroupSize,
		    subgroupPositives, totalPopulationSize, definedPositives);
	}
	return optEstimatedQuality;
    }

    public double getOptimisticEstimateNumeric(double subgroupSize,
	    double subgroupPositives, double totalPopulationSize,
	    double definedPositives) {
	double optEstimatedQuality = Double.MAX_VALUE;
	if (task.getQualityFunction() instanceof EstimatetableQFSimpleStatisticsBased) {
	    optEstimatedQuality = ((EstimatetableQFSimpleStatisticsBased) task
		    .getQualityFunction()).estimateQuality(subgroupSize,
		    subgroupPositives, totalPopulationSize, definedPositives);
	}
	return optEstimatedQuality;
    }

    public double getOptimisticEstimate(SGStatistics stats) {
	if (stats instanceof SGStatisticsBinary) {
	    SGStatisticsBinary statsBin = (SGStatisticsBinary) stats;
	    return getOptimisticEstimate(stats.getSubgroupSize(),
		    statsBin.getTp(), stats.getDefinedPopulationCount(),
		    statsBin.getPositives());
	} else {
	    SGStatisticsNumeric statsNum = (SGStatisticsNumeric) stats;
	    return getOptimisticEstimate(stats.getSubgroupSize(),
		    statsNum.getSGMean(), stats.getDefinedPopulationCount(),
		    statsNum.getPopulationMean());
	}
    }

    public abstract boolean isTreatMissingAsUndefinedSupported();

    public boolean isAborted() {
	return aborted;
    }

}
