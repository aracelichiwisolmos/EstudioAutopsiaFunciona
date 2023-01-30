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
 * Created on 24.03.2004
 * 
 */
package org.vikamine.kernel.subgroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Abstract container class for bookkeeping on the statistics of a subgroup
 * {@link SG}.
 * 
 * @author atzmueller
 */
public abstract class SGStatistics {

    protected int descriptionLength = 0;

    protected final SG subgroup;

    protected Options options;

    // population
    protected double definedPopulationCount = 0;

    protected double undefinedPopulationCount = 0;

    protected double subgroupSize;

    protected abstract void calculateStatistics();

    protected SGStatistics(SG subgroup) {
	super();
	this.subgroup = subgroup;
	this.options = null;
	this.descriptionLength = subgroup.getSGDescription().size();
    }

    protected SGStatistics(SG subgroup, Options options) {
	super();
	this.subgroup = subgroup;
	this.options = options;
	this.descriptionLength = subgroup.getSGDescription().size();

	calculateStatistics();
    }

    protected boolean treatMissingAsDefinedValueForAttributes(
	    Collection dmAttributes) {
	if (options == null) {
	    return true;
	} else {
	    for (Iterator iter = dmAttributes.iterator(); iter.hasNext();) {
		Attribute att = (Attribute) iter.next();
		if (options.getBooleanAttributeOption(att,
			Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE))
		    return false;
	    }
	    return true;
	}
    }

    public boolean isTargetDefinedInInstance(SGTarget target,
	    DataRecord instance) {
	if (target.isBoolean()
		&& treatMissingAsDefinedValueForAttributes(target
			.getAttributes())) {
	    return true;
	} else {
	    return isTargetDefinedForTargetAttributes(target, instance);
	}
    }

    private boolean isTargetDefinedForTargetAttributes(SGTarget target,
	    DataRecord instance) {
	for (Attribute att : target.getAttributes()) {
	    if (att.isMissingValue(instance)) {
		return false;
	    }
	}
	return true;
    }

    public boolean isSGSelectorSetDefinedInInstance(DataRecord instance) {
	// this is not explicitly necessary, but is a considerable speedup!
	if (options == null) {
	    return true;
	}
	for (SGSelector sel : subgroup.getSGDescription()) {
	    if (!isAttributeDefinedInInstance(instance, sel.getAttribute())) {
		return false;
	    }
	}
	return true;
    }

    public boolean isAttributeDefinedInInstance(DataRecord instance,
	    Attribute attribute) {
	return (options == null || !instance.isMissing(attribute) || treatMissingAsDefinedValueForAttributes(Collections
		.singletonList(attribute)));
    }

    public abstract double getDeviation();

    public abstract double getTargetQuantitySG();
    public abstract double getTargetQuantityPopulation();

    public boolean isInstanceDefinedForSubgroupVars(DataRecord instance) {
	if ((subgroup.getTarget() != null)
		&& (!isTargetDefinedInInstance(subgroup.getTarget(), instance))) {
	    return false;
	} else if (!isSGSelectorSetDefinedInInstance(instance)) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the definedPopulationCount.
     */
    public double getDefinedPopulationCount() {
	return definedPopulationCount;
    }

    /**
     * Usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return the subgroup size
     */
    public double getSubgroupSize() {
	return subgroupSize;
    }

    public double getSubgroupShareOfPopulation() {
	return getSubgroupSize() / getDefinedPopulationCount();
    }

    /**
     * @return the relative subgroup size
     */
    public double getRelativeSubgroupSize() {
	return getSubgroupSize() / getDefinedPopulationCount();
    }

    /**
     * usually (using no covering or instance weights != 1.0) the value can just
     * be cast to int.
     * 
     * @return Returns the undefinedPopulationCount.
     */
    public double getUndefinedPopulationCount() {
	return undefinedPopulationCount;
    }

    /**
     * @return Returns the subgroup.
     */
    public SG getSubgroup() {
	return subgroup;
    }

    /**
     * @return Returns the sConstraints.
     */
    public Options getOptions() {
	return options;
    }

    @Override
    public String toString() {
	return "SGStatistics for ("
		+ SG.verbalizeSGAsPrologLikeRule(getSubgroup()) + ")\n"
		+ "DataView: " + definedPopulationCount + "("
		+ undefinedPopulationCount + ")\n" + "\n" + "SubgroupSize: "
		+ subgroupSize + "\n";
    }

    public int getDescriptionLength() {
	return descriptionLength;
    }

    public abstract boolean isBinary();

}
