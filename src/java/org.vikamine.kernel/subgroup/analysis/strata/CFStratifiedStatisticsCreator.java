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
 * Created on 13.10.2004
 *
 */
package org.vikamine.kernel.subgroup.analysis.strata;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.Options;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.BooleanTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * {@link CFStratifiedStatisticsCreator} creates the k statistics/parameters for
 * a stratifying attribute with k values. These are enclosed in
 * {@link CFStratifiedSGHolder} objects, containing the respective stratified
 * subgroups.
 * 
 * @author atzmueller
 */
public class CFStratifiedStatisticsCreator {

    public static class CFStratifiedStatistics {
	private SG sg;

	private NominalAttribute stratifyingAttribute;

	private double[] strataValsForTVInSG;

	private double[] strataValsForTVInPop;

	private double[] strataValsForSGInPop;

	private double[] strataValsForTVInComplementaryPop;

	public CFStratifiedStatistics(SG sg,
		NominalAttribute stratifyingAttribute) {
	    this.sg = sg;
	    this.stratifyingAttribute = stratifyingAttribute;
	}

	public SG getSg() {
	    return sg;
	}

	public double[] getStrataValsForSGInPop() {
	    return strataValsForSGInPop;
	}

	public double[] getStrataValsForTVInPop() {
	    return strataValsForTVInPop;
	}

	public double[] getStrataValsForTVInSG() {
	    return strataValsForTVInSG;
	}

	public double[] getStrataValsForTVInComplementaryPop() {
	    return strataValsForTVInComplementaryPop;
	}

	public NominalAttribute getStratifyingAttribute() {
	    return stratifyingAttribute;
	}

	public String verbalize() {
	    StringBuffer buffy = new StringBuffer();
	    buffy.append("TV "
		    + ((sg.getTarget() != null) ? sg.getTarget()
			    .getDescription() : "None") + "\n");
	    buffy.append("SG " + SG.verbalizeSGAsPrologLikeRule(sg) + "\n");
	    buffy.append("StratVar " + stratifyingAttribute.getId() + "\n\n");
	    buffy.append("\tPPV(Pop->TV)%\tPPV(SG->TV)%\tPPV(Pop->SG)%\ttPPV(ComplementaryPop->TV)%\n");
	    int i = 0;
	    for (Iterator iter = stratifyingAttribute.allValuesIterator(); iter
		    .hasNext(); i++) {
		Value val = (Value) iter.next();
		buffy.append(val.getDescription() + "\t");
		buffy.append(strataValsForTVInPop[i] + "\t"
			+ strataValsForTVInSG[i] + "\t"
			+ strataValsForSGInPop[i] + "\t"
			+ strataValsForTVInComplementaryPop[i] + "\n");
	    }
	    return buffy.toString();
	}
    }

    private static CFStratifiedStatisticsCreator instance = new CFStratifiedStatisticsCreator();

    private CFStratifiedStatisticsCreator() {
	super();
    }

    public static CFStratifiedStatisticsCreator getInstance() {
	if (instance == null)
	    throw new IllegalStateException(
		    "Instance should have been eagerly created!");
	return instance;
    }

    public CFStratifiedStatistics createCFStratifiedStatistics(SG sg,
	    NominalAttribute stratifyingAttribute) {
	CFStratifiedStatistics stats = new CFStratifiedStatistics(sg,
		stratifyingAttribute);
	stats.strataValsForSGInPop = new double[(stratifyingAttribute)
		.getValuesCount()];
	stats.strataValsForTVInPop = new double[(stratifyingAttribute)
		.getValuesCount()];
	stats.strataValsForTVInSG = new double[(stratifyingAttribute)
		.getValuesCount()];
	stats.strataValsForTVInComplementaryPop = new double[stratifyingAttribute
		.getValuesCount()];

	int[] populationCountsInStrata = new int[(stratifyingAttribute)
		.getValuesCount()];
	int[] subgroupCountsInStrata = new int[(stratifyingAttribute)
		.getValuesCount()];
	int[] fnAndTn = new int[(stratifyingAttribute).getValuesCount()];

	SGTarget target = sg.getTarget();
	if ((target == null) || (target instanceof BooleanTarget)) {
	    BooleanTarget tVar = (BooleanTarget) target;
	    List populationRangesSelectors = sg.getPopulation()
		    .populationRangesSelectors();
	    if (sg.getStatistics()
		    .getOptions()
		    .getBooleanAttributeOption(stratifyingAttribute,
			    Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE)) {
		populationRangesSelectors.add(new NegatedSGSelector(
			new DefaultSGSelector((stratifyingAttribute), Value
				.missingValue(stratifyingAttribute))));
	    }
	    DataView strataPopulation = sg.getPopulation().createPopulation(
		    populationRangesSelectors);

	    for (Iterator<DataRecord> iter = strataPopulation
		    .instanceIterator(); iter.hasNext();) {
		DataRecord inst = iter.next();
		if (sg.getStatistics().isInstanceDefinedForSubgroupVars(inst)) {
		    if (!inst.isMissing(stratifyingAttribute)) {
			int stratValIndex = (int) inst
				.getValue(stratifyingAttribute);

			populationCountsInStrata[stratValIndex]++;
			if (sg.getSGDescription().isMatching(inst)) {
			    subgroupCountsInStrata[stratValIndex]++;
			    stats.strataValsForSGInPop[stratValIndex]++;
			    if ((tVar != null) && (tVar.isPositive(inst))) {
				stats.strataValsForTVInSG[stratValIndex]++;
				stats.strataValsForTVInPop[stratValIndex]++;
			    }
			} else {
			    if ((tVar != null) && (tVar.isPositive(inst))) {
				stats.strataValsForTVInPop[stratValIndex]++;
				stats.strataValsForTVInComplementaryPop[stratValIndex]++;
				fnAndTn[stratValIndex]++;
			    } else if ((tVar != null)
				    && (!tVar.isPositive(inst))) {
				fnAndTn[stratValIndex]++;
			    }
			}
		    }
		}
	    }
	    for (int i = 0; i < (stratifyingAttribute).getValuesCount(); i++) {
		stats.strataValsForTVInSG[i] /= subgroupCountsInStrata[i];
		stats.strataValsForSGInPop[i] /= populationCountsInStrata[i];
		stats.strataValsForTVInPop[i] /= populationCountsInStrata[i];
		stats.strataValsForTVInComplementaryPop[i] /= fnAndTn[i];
	    }

	} else {
	    throw new RuntimeException("Only boolean targets supported so far!");
	}
	return stats;
    }

    List createStratifiedSGHolders(SG sg, NominalAttribute strataAttribute) {
	List strataSubgroupHolders = new LinkedList();
	List basePopulationSelectors = new LinkedList(sg.getPopulation()
		.populationRangesSelectors());

	CFStratifiedSGHolder h = createStratifiedSGHolderForTotalSG(sg,
		strataAttribute, basePopulationSelectors);
	strataSubgroupHolders.add(h);
	if (strataAttribute.isNominal()) {
	    for (Iterator iter = strataAttribute.allValuesIterator(); iter
		    .hasNext();) {
		Value val = (Value) iter.next();
		SGNominalSelector sel = new DefaultSGSelector(strataAttribute,
			val);
		SG strataSubgroup = createStratumSubgroup(sg,
			basePopulationSelectors, sel);
		strataSubgroupHolders.add(new CFStratifiedSGHolder(
			strataSubgroup, sel));
	    }

	    // handle missing!
	    if (sg.getStatistics()
		    .getOptions()
		    .getBooleanAttributeOption(strataAttribute,
			    Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE)) {
		SGNominalSelector missingSel = new DefaultSGSelector(
			strataAttribute, Value.missingValue(strataAttribute));
		SG strataSubgroup = createStratumSubgroup(sg,
			basePopulationSelectors, missingSel);
		strataSubgroupHolders.add(new CFStratifiedSGHolder(
			strataSubgroup, missingSel));
	    }

	    return strataSubgroupHolders;
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    private SG createStratumSubgroup(SG sg, List basePopulationSelectors,
	    SGNominalSelector sel) {
	SG strataSubgroup = (SG) sg.clone();
	List newSelectors = new LinkedList(basePopulationSelectors);
	newSelectors.add(sel);
	DataView strataPopulation = sg.getPopulation().createPopulation(
		newSelectors);
	strataSubgroup.setPopulation(strataPopulation);
	strataSubgroup.createStatistics(sg.getStatistics().getOptions());
	return strataSubgroup;
    }

    private CFStratifiedSGHolder createStratifiedSGHolderForTotalSG(SG sg,
	    Attribute strataAttribute, List basePopulationSelectors) {
	SG clonedSubgroup = (SG) sg.clone();
	List selectors = new LinkedList(basePopulationSelectors);

	SGSelector totalSel = new NegatedSGSelector(new DefaultSGSelector(
		strataAttribute, Value.missingValue(strataAttribute)));	
	if (sg.getStatistics().getOptions().getBooleanAttributeOption(
		strataAttribute, Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE)) {
	    selectors.add(totalSel);
	}

	DataView population = sg.getPopulation().createPopulation(selectors);
	clonedSubgroup.setPopulation(population);
	clonedSubgroup.createStatistics(sg.getStatistics().getOptions());
	CFStratifiedSGHolder h = new CFStratifiedSGHolder(clonedSubgroup,
		totalSel);
	return h;
    }
}
