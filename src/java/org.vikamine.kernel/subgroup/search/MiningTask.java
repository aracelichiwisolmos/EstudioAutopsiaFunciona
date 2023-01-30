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

package org.vikamine.kernel.subgroup.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGFilters;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.analysis.WeightedCoveringAnalyzer;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * @author lemmerich, atzmueller
 */

public class MiningTask extends AbstractMiningTask implements Cloneable {

    protected int maxSGDSize = Integer.MAX_VALUE;
    private SDMethod method;

    /**
     * creates a new MiningTask (with empty search space, no ontology)
     */
    public MiningTask() {
	super();
    }

    public MiningTask(Ontology onto) {
	super();
	setOntology(onto);
    }

    public MiningTask(AbstractMiningTask oldTask) {
	this();
	setSearchSpace(new ArrayList<SGSelector>(oldTask.searchSpace));
	setTarget(oldTask.getTarget());
	setMethodType(oldTask.getMethodType());
	setQualityFunction(oldTask.getQualityFunction());
	setInitialSG(oldTask.getInitialSG());
	setOntology(oldTask.getOntology());
	setWeightedCovering(oldTask.isWeightedCovering());
	setMaxSGCount(oldTask.getMaxSGCount());
	setMinQualityLimit(oldTask.getMinQualityLimit());
	setMinSubgroupSize(oldTask.getMinSubgroupSize());
	setMinTPSupportAbsolute(oldTask.getMinTPSupportAbsolute());
	setMinTPSupportRelative(oldTask.getMinTPSupportRelative());
	setInvertQualityFunction(oldTask.isInvertQualityFunction());
	setSuppressStrictlyIrrelevantSubgroups(oldTask
		.isSuppressStrictlyIrrelevantSubgroups());
	setIgnoreDefaultValues(oldTask.isIgnoreDefaultValues());
	if (oldTask instanceof MiningTask) {
	    setMaxSGDSize(((MiningTask) oldTask).getMaxSGDSize());
	}
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
	MiningTask aTask = new MiningTask(this);

	// this has to be done separately, because it is not part of
	// AbstractMiningTask
	aTask.setMaxSGDSize(maxSGDSize);

	return aTask;
    }

    public int getMaxSGDSize() {
	return maxSGDSize;
    }

    public SGSet performSubgroupDiscovery() {
	return performSubgroupDiscovery(Collections.EMPTY_LIST);
    }

    public SGSet performSubgroupDiscovery(List<SGFilters> sgFilters) {

	// if no initialSG was set, we start with the standard SG.
	if (initialSG == null) {
	    initialSG = new SG(ontology.getDataView(), getTarget());
	}

	if (this.initialSG.getStatistics() == null) {
	    initialSG.createStatistics();
	}

	int realMaxSGCount = this.getMaxSGCount();
	if (isWeightedCovering()) {
	    this.setMaxSGCount((int) (Math.log(getMaxSGCount()) * getMaxSGCount()));
	}

	// creates the method, if necessary
	method = getMethod();
	try {
	    method.setTask((MiningTask) this.clone());
	} catch (CloneNotSupportedException e) {
	    // do nothing
	}

	SGSet result = method.perform(null);

	if ((sgFilters != null) && (!sgFilters.isEmpty())) {
	    for (SGFilters filter : sgFilters) {
		result = filter.filterSGs(result);
	    }
	}

	if (this.isWeightedCovering()) {
	    // reset if necessary
	    this.setMaxSGCount(realMaxSGCount);
	}

	if (this.weightedCovering && target.isBoolean() && (result != null)) {
	    WeightedCoveringAnalyzer w = new WeightedCoveringAnalyzer();
	    // reset quality after use, so it gets updated, if task changes
	    return w.getKBestCoveringSubgroups(this.maxSGCount, result,
		    this.ontology.getDataView(), getQualityFunction());
	} else {
	    return result;
	}
    }

    public boolean fulfillsMinTPSupport(double tpCount) {
	return fulfillsMinTPSupport(tpCount, ontology.getDataView().size());
    }

    public boolean fulfillsMinTPSupport(double tpCount, double dataViewSize) {
	double minTPSupport = Math.max(minTPSupportAbsolute,
		minTPSupportRelative * dataViewSize);
	return (tpCount >= minTPSupport);
    }

    public void setMaxSGDSize(int maxSGDSize) {
	this.maxSGDSize = maxSGDSize;
    }

    /**
     * orders a running (performing) task to abort the subgroup discovery as
     * soon as possible. If task is not running yet, it does nothing
     */
    public void cancel() {
	if (this.method != null) {
	    this.method.cancel();
	}
    }

    public void setMethod(SDMethod method) {
	this.method = method;
	this.methodType = method.getClass();
    }

    /**
     * returns the SDMethod (if it has not yet been created, it is created
     * lazily
     * 
     * @param c
     * @return
     */
    public SDMethod getMethod() {
	if (method == null) {
	    createMethod(methodType);
	}
	return method;
    }

    public void recreateMethod() {
	createMethod(methodType);
    }

    public int generateID() {
	final int prime = 31;
	int result = 1;

	String datasetName = getOntology().getDatasetName();
	result = prime * result + datasetName.hashCode();

	String targetTypeStr = "unknown";
	if (getTarget().isNumeric()) {
	    targetTypeStr = "numeric";
	}
	if (getTarget().isBoolean()) {
	    targetTypeStr = "boolean";
	}
	result = prime * result + targetTypeStr.hashCode();

	// targetAtt
	Collection<Attribute> targetAttributes = getTarget().getAttributes();
	String targetAttributesString = "";
	for (Attribute att : targetAttributes) {
	    targetAttributesString += att.getDescription();
	}
	result = prime * result + targetAttributesString.hashCode();

	// targetVal
	String targetSelector = "none";
	if (getTarget() instanceof SelectorTarget) {
	    SGSelector selector = ((SelectorTarget) getTarget()).getSelector();
	    targetSelector = selector.getDescription();
	}
	result = prime * result + targetSelector.hashCode();

	// Searchspace size
	int searchSpaceSize = getSearchSpace().size();
	result = prime * result + searchSpaceSize;

	// QualityFunction
	String qFunction = getQualityFunction().getName();
	result = prime * result + qFunction.hashCode();

	// method
	String method = getMethod().getName();
	result = prime * result + method.hashCode();

	// constraints
	result = prime * result + getMaxSGCount();
	result = prime * result
		+ Double.valueOf(getMinQualityLimit()).hashCode();
	result = prime * result + Double.valueOf(getMaxSGDSize()).hashCode();
	result = prime * result
		+ Double.valueOf(getMinSubgroupSize()).hashCode();
	result = prime * result
		+ Double.valueOf(getMinSubgroupSize()).hashCode();
	result = prime * result
		+ Double.valueOf(getMinTPSupportRelative()).hashCode();
	result = prime * result
		+ Double.valueOf(getMinTPSupportAbsolute()).hashCode();

	return result;
    }

    private void createMethod(Class c) {
	try {
	    method = (SDMethod) c.newInstance();
	} catch (InstantiationException e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException("Could not create SD-Algorithm!");
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException("Could not create SD-Algorithm!");
	}
    }

}
