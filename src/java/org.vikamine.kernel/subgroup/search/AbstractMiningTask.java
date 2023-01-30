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
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.quality.constraints.IConstraint;
import org.vikamine.kernel.subgroup.quality.functions.InverseQF;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * 
 * @author lemmerich
 * 
 */

public abstract class AbstractMiningTask {

    protected List<SGSelector> searchSpace;

    public List<SGSelector> getSearchSpace() {
	return searchSpace;
    }

    public void setSearchSpace(List<SGSelector> searchSpace) {
	this.searchSpace = searchSpace;
    }

    protected SGTarget target;

    protected Class methodType;

    protected IQualityFunction qualityFunction;

    /**
     * constraints to apply in this task.
     */
    private List<IConstraint> constraints;

    /**
     * Return the list of constraints, NO CLONE, the actual list
     */
    // public List<IConstraint> getConstraints() {
    // return constraints;
    // }

    public void addConstraint(IConstraint constraint) {
	constraints.add(constraint);
    }

    public void setConstraints(List<IConstraint> constraints) {
	this.constraints = constraints;
    }

    protected SG initialSG;

    protected Ontology ontology;

    protected boolean weightedCovering;

    protected int maxSGCount = 10;

    protected double minQualityLimit = 0;

    protected double minSubgroupSize = 1;

    protected double minTPSupportAbsolute = 0;

    protected double minTPSupportRelative = 0;

    protected boolean invertQualityFunction = false;

    protected boolean suppressStrictlyIrrelevantSubgroups;

    protected boolean ignoreDefaultValues;

    public boolean isIgnoreDefaultValues() {
	return ignoreDefaultValues;
    }

    public void setIgnoreDefaultValues(boolean ignoreDefaultValues) {
	this.ignoreDefaultValues = ignoreDefaultValues;
    }

    public AbstractMiningTask() {
	super();
	this.constraints = new ArrayList<IConstraint>();
	this.searchSpace = new ArrayList<SGSelector>();
    }

    public AbstractMiningTask(List<SGSelector> searchSpace, SGTarget target,
	    Class methodtype, IQualityFunction function, SG initialSG,
	    Ontology ont, boolean wc, boolean ignoreDefaults) {
	super();
	this.searchSpace = searchSpace;
	this.target = target;
	this.methodType = methodtype;
	this.qualityFunction = function;
	this.initialSG = initialSG;
	this.ontology = ont;
	this.weightedCovering = wc;
	this.ignoreDefaultValues = ignoreDefaults;
	this.constraints = new ArrayList<IConstraint>();
    }

    /**
     * creates a list of all attributes used in the selectors of the search
     * space.
     * 
     * @return a new list of all attributes used in the selectors of the search
     *         space.
     */
    public List<Attribute> getAttributes() {
	List<Attribute> result = new ArrayList<Attribute>();

	for (SGSelector sel : searchSpace) {
	    Attribute att = sel.getAttribute();
	    if (!result.contains(att)) {
		result.add(att);
	    }
	}

	return result;
    }

    public IQualityFunction getQualityFunction() {
	if (isInvertQualityFunction())
	    return new InverseQF(qualityFunction);
	else
	    return qualityFunction;
    }

    public SG getInitialSG() {
	return initialSG;
    }

    public int getMaxSGCount() {
	return maxSGCount;
    }

    public Class getMethodType() {
	return methodType;
    }

    public double getMinQualityLimit() {
	return minQualityLimit;
    }

    public double getMinSubgroupSize() {
	return minSubgroupSize;
    }

    public double getMinTPSupportAbsolute() {
	return minTPSupportAbsolute;
    }

    public double getMinTPSupportRelative() {
	return minTPSupportRelative;
    }

    public Ontology getOntology() {
	return ontology;
    }

    public SGTarget getTarget() {
	return target;
    }

    public boolean isInvertQualityFunction() {
	return invertQualityFunction;
    }

    public boolean isSuppressStrictlyIrrelevantSubgroups() {
	return suppressStrictlyIrrelevantSubgroups;
    }

    public boolean isWeightedCovering() {
	return weightedCovering;
    }

    public void setQualityFunction(IQualityFunction function) {
	this.qualityFunction = function;
    }

    public void setInitialSG(SG initialSG) {
	this.initialSG = initialSG;
    }

    public void setInvertQualityFunction(boolean invertQualityFunction) {
	this.invertQualityFunction = invertQualityFunction;
    }

    public void setMaxSGCount(int maxSGCount) {
	this.maxSGCount = maxSGCount;
    }

    public void setMethodType(Class methodType) {
	this.methodType = methodType;
    }

    public void setMinQualityLimit(double minQualityLimit) {
	this.minQualityLimit = minQualityLimit;
    }

    public void setMinSubgroupSize(double minSGSize) {
	this.minSubgroupSize = minSGSize;
    }

    public void setMinTPSupportAbsolute(double minTPSupportAbsolute) {
	this.minTPSupportAbsolute = minTPSupportAbsolute;
    }

    public void setMinTPSupportRelative(double minTPSupportRelative) {
	this.minTPSupportRelative = minTPSupportRelative;
    }

    public void setOntology(Ontology ont) {
	this.ontology = ont;
    }

    public void setSuppressStrictlyIrrelevantSubgroups(
	    boolean suppressStrictlyIrrelevantSubgroups) {
	this.suppressStrictlyIrrelevantSubgroups = suppressStrictlyIrrelevantSubgroups;
    }

    public void setTarget(SGTarget target) {
	this.target = target;
    }

    public void setWeightedCovering(boolean wc) {
	this.weightedCovering = wc;
    }
}
