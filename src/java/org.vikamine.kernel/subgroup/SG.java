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
 * Created on 07.11.2003
 * 
 */
package org.vikamine.kernel.subgroup;

import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.FilteringDataRecordIterator;
import org.vikamine.kernel.data.IncludingDataRecordFilter;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.util.VKMUtil;

/**
 * A subgroup ({@link SG}) represents a subgroup of {@link DataRecord} of a
 * {@link DataView} based on a {@link SGDescription}. A subgroup is usually
 * associated with a {@link SGTarget}. The subgroup's quality is based on the
 * {@link SGTarget}. This implementation requires a {@link SGTarget} to function
 * properly ({@link SG#hashCode()}, etc.).
 * 
 * @author atzmueller, becker (comments)
 */
public final class SG implements Cloneable, ISubgroup<DataRecord> {

    private DataView population;

    private SGTarget target;

    private SGDescription sGDescription;

    private double quality = 0;

    private SGStatistics statistics;

    private SG() {
	super();
    }

    public SG(DataView population, SGTarget target) {
	this();
	this.population = population;
	this.target = target;
	sGDescription = new SGDescription();
    }

    public SG(DataView population, SGTarget target, SGDescription sGDescription) {
	this();
	this.population = population;
	this.target = target;
	this.sGDescription = sGDescription;
    }

    /**
     * Clones the SG: clones the SGDescription, all other fields are
     * <b>shallow</b> copies (the clone method of the SGDescription also only
     * returns a <b>shallow</b> copy!)
     * 
     * @return new subgroup
     */
    @Override
    public Object clone() {
	try {
	    SG clonedSG = (SG) super.clone();
	    clonedSG.sGDescription = sGDescription.clone();
	    return clonedSG;
	} catch (CloneNotSupportedException ex) {
	    throw new Error("Assertion failure!"); // never possible
	}
    }

    /**
     * @return a <b>deep</b> copy of the subgroup
     */
    public SG deepCopy() {
	SG clonedSubgroup = (SG) clone();
	clonedSubgroup.setSGDescription(clonedSubgroup.getSGDescription()
		.deepCopy());
	if (getStatistics() != null) {
	    clonedSubgroup.createStatistics(getStatistics().getOptions());
	}
	return clonedSubgroup;
    }

    @Override
    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	} else if (other == null) {
	    return false;
	} else if (getClass() != other.getClass()) {
	    return false;
	} else {
	    SG otherSG = (SG) other;
	    return equalObjects(getTarget(), otherSG.getTarget())
		    && equalObjects(getSGDescription(),
			    otherSG.getSGDescription());
	}
    }

    @Override
    public int hashCode() {
	int hash = 17;
	if (sGDescription != null) {
	    hash = 37 * hash + sGDescription.hashCode();
	}
	if (target != null) {
	    hash = 37 * hash + target.hashCode();
	}
	return hash;
    }

    private boolean equalObjects(Object o1, Object o2) {
	if (o1 == null && o2 == null) {
	    return true;
	} else if (o1 != null) {
	    return o1.equals(o2);
	} else {
	    return false;
	}
    }

    /**
     * @return
     */
    public SGDescription getSGDescription() {
	return sGDescription;
    }

    /**
     * @param description
     */
    public void setSGDescription(SGDescription description) {
	sGDescription = description;
    }

    /**
     * @return
     */
    public SGTarget getTarget() {
	return target;
    }

    /**
     * @param object
     */
    public void setTarget(SGTarget object) {
	target = object;
    }

    /**
     * @return quality/"interestingness" of this subgroup.
     */
    @Override
    public double getQuality() {
	return quality;
    }

    /**
     * @param d
     */
    public void setQuality(double d) {
	quality = d;
    }

    /**
     * Updates this {@link SG}s quality based on the current internal
     * {@link SGStatistics} (can be <code>null</code>) by calling
     * {@link IQualityFunction#evaluate(ISubgroup).}.
     * 
     * @param qf
     *            {@link IQualityFunction} to evaluate quality by
     */
    public void updateQuality(IQualityFunction qf) {
	setQuality(qf.evaluate(this));
    }

    @Override
    public String toString() {
	return "SG(" + VKMUtil.getFormattedDoubleString(quality, 2) + ") "
		+ target + " <= " + sGDescription;
    }

    public Iterator<DataRecord> subgroupInstanceIterator() {
	// optimization if sgDescription is empty
	if (sGDescription.isEmpty()) {
	    return population.instanceIterator();
	} else {
	    return new FilteringDataRecordIterator(
		    population.instanceIterator(),
		    new IncludingDataRecordFilter() {
			@Override
			// the dataRecord should be included if:
			// 1. the description matches
			// 2. the target is defined for this sg or the target is
			// null
			public boolean isIncluded(DataRecord instance) {
			    if (!sGDescription.isMatching(instance)) {
				return false;
				// description is matching
			    } else {
				if (getTarget() == null) {
				    return true;
				}
				if (getStatistics() == null) {
				    return true;
				} else {
				    return getStatistics()
					    .isTargetDefinedInInstance(
						    getTarget(), instance);
				}
			    }
			}
		    });
	}
    }

    /**
     * @return Returns the subgroupStatistics.
     */
    public SGStatistics getStatistics() {
	return statistics;
    }

    /**
     * creates the statistics without constraints; convenience method for
     * "createStatistics(null)"
     */
    public void createStatistics() {
	createStatistics(null);
    }

    public void createStatistics(Options constraints) {
	setStatistics(SGStatisticsBuilder.buildSGStatisticsWithCalculation(
		this, constraints));
    }

    /**
     * Warning: public only for technical reasons. Do never use this, if you do
     * not EXACTLY know what
     * 
     * you are doing!
     * 
     * @param subgroupStatistics
     *            The subgroupStatistics to set.
     */
    public void setStatistics(SGStatistics subgroupStatistics) {
	statistics = subgroupStatistics;
    }

    /**
     * @return the population.
     */
    public DataView getPopulation() {
	return population;
    }

    /**
     * @param population
     *            The population to set.
     */
    public void setPopulation(DataView population) {
	this.population = population;
    }

    public static String verbalizeSGAsPrologLikeRule(SG sg) {
	StringBuffer buffy = new StringBuffer();
	if (sg.getTarget() != null) {
	    buffy.append(sg.getTarget().getDescription());
	} else {
	    buffy.append("<No TV>");
	}
	if ((sg.getSGDescription() != null)
		&& (!sg.getSGDescription().isEmpty())) {
	    buffy.append(" :- ");
	    buffy.append(verbalizeSGDescriptionAsPrologLikeRuleBody(sg));
	}
	return buffy.toString();
    }

    public static String verbalizeSGDescriptionAsPrologLikeRuleBody(SG sg) {
	StringBuffer buffy = new StringBuffer();	
	if ((sg.getSGDescription() != null)
		&& (!sg.getSGDescription().isEmpty())) {
	    buffy.append(sg.getSGDescription().getDescription());
	}
	return buffy.toString();
    }
    
    private static String listToCSVString(List<? extends Object> list) {
	StringBuffer result = new StringBuffer();
	for (Iterator<? extends Object> iter = list.iterator(); iter
		.hasNext();) {
	    result.append(iter.next().toString());
	    if (iter.hasNext()) {
		result.append(", ");
	    }
	}
	return result.toString();
    }
    
    public static String verbalizeSGDescriptionAsPrologLikeRuleBodyCSV(SG sg) {
	StringBuffer buffy = new StringBuffer();	
	if ((sg.getSGDescription() != null)
		&& (!sg.getSGDescription().isEmpty())) {
	    List<SGSelector> selectorList = sg.getSGDescription().getSelectors();
	    buffy.append(listToCSVString(selectorList));
	}
	return buffy.toString();
    }
    
    @Override
    public Iterator<DataRecord> iterator() {
	return subgroupInstanceIterator();
    }

    @Override
    public SGDescription getDescription() {
	return getSGDescription();
    }
}
