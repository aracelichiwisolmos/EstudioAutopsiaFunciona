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

package org.vikamine.kernel.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.vikamine.kernel.KernelResources;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.SGUtils;
import org.vikamine.kernel.subgroup.quality.functions.LiftQF;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.util.VKMUtil;

/**
 * Handles statistical subgroup parameters w.r.t. a target concept; checks, if
 * applicable.
 * 
 * @author lemmerich, atzmueller
 * 
 */
public abstract class StatisticComponent {

    DecimalFormat scientificFormat = new DecimalFormat("0.###E0");
    DecimalFormat simpleFormat = new DecimalFormat("#.###");

    public static class ChiSquared extends StatisticComponent {

	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.chi2");
	}

	@Override
	public Double getValue(SGStatistics stats) {

	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }
	    return SGUtils.calculateChi2OfSubgroup((SGStatisticsBinary) stats);
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class Coverage extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.subgroupShare/DataView");
	}

	@Override
	public String getValue(SGStatistics stats) {
	    return toPercentString(stats.getSubgroupShareOfPopulation());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class FP extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.fp");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    return ((SGStatisticsBinary) stats).getFp();
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class Lift extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.lift");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsBinary) {
		SGStatisticsBinary binary = (SGStatisticsBinary) stats;
		return binary.getP() / binary.getP0();
	    } else if (stats instanceof SGStatisticsNumeric) {
		SGStatisticsNumeric numeric = (SGStatisticsNumeric) stats;
		return LiftQF.computeLiftNumeric(numeric.getSGMean(),
			numeric.getPopulationMean());
	    } else {
		throw new IllegalStateException("Unknown SGStatistics!" + stats);
	    }
	}

	@Override
	public String getValueString(SGStatistics stats) {
	    double val = getValue(stats);
	    return VKMUtil.getFormattedDoubleString(val, 2);
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return true;
	}
    }

    public static class P extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.p");
	}

	@Override
	public Object getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }
	    return toPercentString(((SGStatisticsBinary) stats).getP());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class P0 extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.p0");
	}

	@Override
	public Object getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    return toPercentString(((SGStatisticsBinary) stats).getP0());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class SubgroupMedian extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.sgMedian");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getSGMedian();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class PopulationMedian extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.populationMedian");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getPopulationMedian();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class PopulationMean extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.populationMeanValue");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getPopulationMean();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class PopulationNormalizedMean extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.populationNormalizedMeanValue");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats)
			.getPopulationNormalizedMean();
	    }
	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class PopulationSize extends StatisticComponent {

	private PopulationSize() {
	}

	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.populationSize");
	}

	@Override
	public Integer getValue(SGStatistics stats) {
	    return (int) Math.round(stats.getDefinedPopulationCount());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    return true;
	}

	@Override
	protected boolean isAlwaysValid() {
	    return true;
	}
    }

    public static class PopulationVariance extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.populationVariance");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getPopulationVariance();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class RelativeGain extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.relativeGain");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsBinary) {
		double p = ((SGStatisticsBinary) stats).getP();
		double p0 = ((SGStatisticsBinary) stats).getP0();

		if (p0 == 1.0) {
		    return (p == 1.0) ? 0 : Double.NEGATIVE_INFINITY;
		}
		return (p - p0) / ((1 - p0) * p0);
	    }
	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    return target != null;
	}
    }

    public static class Sensitivity extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.sensitivity");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    return ((SGStatisticsBinary) stats).getTp()
		    / ((SGStatisticsBinary) stats).getPositives();
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class Significance extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.significance");
	}

	@Override
	public Object getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    double chi2Value = SGUtils
		    .calculateChi2OfSubgroup((SGStatisticsBinary) stats);
	    double significance = SGUtils.calculateChi2SignificanceNiveau(
		    chi2Value, 1);
	    // data.add(createVector(SIGNIFICANCE_TEXT, significance));
	    String formattedSignificance = "";
	    if (significance > 0) {
		formattedSignificance = SGUtils
			.toDiscretizedChi2SignificanceLevel(significance);
		if (significance != 1) {
		    formattedSignificance += " ("
			    + VKMUtil
				    .formatDoubleToMinDigitsOrExponentialString(
					    significance, 3) + ")";
		}
	    } else {
		formattedSignificance = SGUtils
			.toDiscretizedChi2SignificanceLevel(significance);
	    }

	    return formattedSignificance;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class SignificanceStrength extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.significanceStrength");
	}

	@Override
	public Object getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    double chi2Value = SGUtils
		    .calculateChi2OfSubgroup((SGStatisticsBinary) stats);
	    double significance = SGUtils.calculateChi2SignificanceNiveau(
		    chi2Value, 1);

	    double signifStrength = Math.log10(1 / significance);

	    return new DecimalFormat("#.#").format(signifStrength);
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class Specificity extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.specificity");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    return ((SGStatisticsBinary) stats).getTn()
		    / ((SGStatisticsBinary) stats).getNegatives();
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class StatisticComponentSubgroupComparator implements
	    Comparator<SG> {

	StatisticComponent compStatComponent;

	public StatisticComponentSubgroupComparator(
		StatisticComponent statComponent) {
	    compStatComponent = statComponent;
	}

	@Override
	public int compare(SG o1, SG o2) {
	    Object value1 = compStatComponent.getValue(o1.getStatistics());
	    Object value2 = compStatComponent.getValue(o2.getStatistics());
	    if (value1 instanceof Double && (value2 instanceof Double)) {
		return Double.compare((Double) value1, (Double) value2);
	    }

	    if (value1 instanceof Integer && (value2 instanceof Integer)) {
		return ((Integer) value1).compareTo((Integer) value2);
	    }

	    if (value1 instanceof String && (value2 instanceof String)) {
		return ((String) value1).compareTo((String) value2);
	    }

	    return 0;
	}
    }

    public static class SubgroupMean extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.sgMeanValue");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getSGMean();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class SubgroupNormalizedMean extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.sgNormalizedMeanValue");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getSGNormalizedMean();
	    }
	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class SubgroupSize extends StatisticComponent {

	private SubgroupSize() {
	    super();
	}

	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.subgroupSize");
	}

	@Override
	public Integer getValue(SGStatistics stats) {
	    return (int) Math.round(stats.getSubgroupSize());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    return true;
	}

	@Override
	protected boolean isAlwaysValid() {
	    return true;
	}
    }

    public static class SubgroupVariance extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.sgVariance");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (stats instanceof SGStatisticsNumeric) {
		return ((SGStatisticsNumeric) stats).getSGVariance();
	    }

	    return Double.NaN;
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isNumeric();
	}
    }

    public static class TP extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.tp");
	}

	@Override
	public Double getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }

	    return ((SGStatisticsBinary) stats).getTp();
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static class TPRate extends StatisticComponent {
	@Override
	public String getDescription() {
	    return KernelResources
		    .getInstance()
		    .getI18N()
		    .getString(
			    "vkmkernel.verbalization.subgroupStatInfoParameters.tpRate");
	}

	@Override
	public Object getValue(SGStatistics stats) {
	    if (!(stats instanceof SGStatisticsBinary)) {
		return Double.NaN;
	    }
	    return toPercentString(((SGStatisticsBinary) stats)
		    .getSubgroupShareOfPositives());
	}

	@Override
	public boolean isValid(SGTarget target) {
	    if (target == null) {
		return false;
	    }
	    return target.isBoolean();
	}
    }

    public static final PopulationVariance POPULATION_VARIANCE = new StatisticComponent.PopulationVariance();

    public static final SubgroupVariance SUBGROUP_VARIANCE = new StatisticComponent.SubgroupVariance();

    // public abstract boolean isEnabled(SGStatInfoSettings settings);

    public static final PopulationNormalizedMean POPULATION_NORMALIZED_MEAN = new StatisticComponent.PopulationNormalizedMean();

    public static final SubgroupNormalizedMean SUBGROUP_NORMALIZED_MEAN = new StatisticComponent.SubgroupNormalizedMean();

    public static final PopulationMean POPULATION_MEAN = new StatisticComponent.PopulationMean();
    public static final PopulationMedian POPULATION_MEDIAN = new StatisticComponent.PopulationMedian();
    public static final SubgroupMedian SUBGROUP_MEDIAN = new StatisticComponent.SubgroupMedian();

    public static final SubgroupMean SUBGROUP_MEAN = new StatisticComponent.SubgroupMean();

    public static final Specificity SPECIFICITY = new StatisticComponent.Specificity();

    public static final Significance SIGNIFICANCE = new StatisticComponent.Significance();

    public static final SignificanceStrength SIGNIFICANCE_STRENGTH = new StatisticComponent.SignificanceStrength();

    public static final Sensitivity SENSITIVITY = new StatisticComponent.Sensitivity();

    public static final ChiSquared CHI_SQUARED = new StatisticComponent.ChiSquared();

    public static final RelativeGain RELATIVE_GAIN = new StatisticComponent.RelativeGain();

    public static final Lift LIFT = new StatisticComponent.Lift();

    public static final Coverage COVERAGE = new StatisticComponent.Coverage();

    public static final TPRate TPRate = new StatisticComponent.TPRate();

    public static final P0 P_0 = new StatisticComponent.P0();

    public static final P P = new StatisticComponent.P();

    public static final FP FP = new StatisticComponent.FP();

    public static final TP TP = new StatisticComponent.TP();

    public static final SubgroupSize SUBGROUP_SIZE = new StatisticComponent.SubgroupSize();

    public static final PopulationSize POPULATION_SIZE = new StatisticComponent.PopulationSize();

    public static List<StatisticComponent> createAllStatisticComponents() {
	List<StatisticComponent> result = new ArrayList<StatisticComponent>();

	result.add(POPULATION_SIZE);
	result.add(SUBGROUP_SIZE);
	result.add(TP);
	result.add(FP);
	result.add(P);
	result.add(P_0);
	result.add(TPRate);
	result.add(COVERAGE);

	result.add(LIFT);
	result.add(RELATIVE_GAIN);
	result.add(CHI_SQUARED);
	result.add(SENSITIVITY);
	result.add(SIGNIFICANCE);
	result.add(SPECIFICITY);

	result.add(SUBGROUP_MEAN);
	result.add(POPULATION_MEAN);
	// result.add(SUBGROUP_NORMALIZED_MEAN);
	// result.add(POPULATION_NORMALIZED_MEAN);
	result.add(SUBGROUP_MEDIAN);
	result.add(POPULATION_MEDIAN);
	result.add(SUBGROUP_VARIANCE);
	result.add(POPULATION_VARIANCE);

	return result;

    }

    public static List<StatisticComponent> getAllComponentsValidForAtLeastOneTarget(
	    List<SGTarget> targets) {
	return removeAllInvalidComponentsForTargetList(
		createAllStatisticComponents(), targets);
    }

    public static List<StatisticComponent> getAllValidComponents(SGTarget target) {
	return removeAllInvalidComponentsForTargetList(
		createAllStatisticComponents(),
		Collections.singletonList(target));
    }

    public static List<StatisticComponent> getAllAlwaysValidComponents() {
	return removeAllInvalidComponentsForTargetList(
		createAllStatisticComponents(), Collections.EMPTY_LIST);
    }

    /**
     * Removes all StatatisticComponents from the list, which are not valid for
     * the given target.
     * 
     * @param statComponents
     * @param target
     * @return
     */
    public static List<StatisticComponent> removeAllInvalidComponentsForTargetList(
	    List<StatisticComponent> statComponents, List<SGTarget> targets) {
	Iterator<StatisticComponent> it = statComponents.iterator();
	while (it.hasNext()) {
	    StatisticComponent stc = it.next();
	    boolean isValidForAtLeastOneTarget = false;
	    if (stc.isAlwaysValid()) {
		isValidForAtLeastOneTarget = true;
	    } else {
		for (SGTarget target : targets) {
		    if (stc.isValid(target)) {
			isValidForAtLeastOneTarget = true;
			break;
		    }
		}
	    }
	    if (!isValidForAtLeastOneTarget) {
		it.remove();
	    }
	}
	return statComponents;
    }

    protected boolean isAlwaysValid() {
	return false;
    }

    private static String toPercentString(double d) {
	String percentage = VKMUtil.getFormattedDoubleStringDependingOnLocale(
		Locale.getDefault(), d * 100, 1) + "%";
	return (d < 0.1 && (!percentage.startsWith("0"))) ? "0" + percentage
		: percentage;
    }

    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	} else if (o == this) {
	    return true;
	} else {
	    return this.getClass() == o.getClass();
	}
    }

    public abstract String getDescription();

    public abstract Object getValue(SGStatistics stats);

    public String getValueString(SGStatistics stats) {
	Object statsValue = getValue(stats);
	if (statsValue instanceof Double && Double.isNaN((Double) statsValue)) {
	    statsValue = "-";
	}
	if (statsValue instanceof Double) {
	    double absValue = Math.abs((Double) statsValue);
	    if (absValue < 1E-5 || absValue > 1E5) {
		return scientificFormat.format(statsValue);
	    }
	    return simpleFormat.format(statsValue);
	}
	return String.valueOf(statsValue);
    }

    @Override
    public int hashCode() {
	assert false;
	return 42;
    }

    public abstract boolean isValid(SGTarget target);

    @Override
    public String toString() {
	return this.getDescription();
    }
}
