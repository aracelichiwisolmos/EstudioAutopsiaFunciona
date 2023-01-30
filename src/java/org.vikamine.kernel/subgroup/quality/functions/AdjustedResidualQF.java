package org.vikamine.kernel.subgroup.quality.functions;


public class AdjustedResidualQF extends StandardQF {

    private static final String ID = "AdjustedResidualQF";
    private static final String NAME = "AdjustedResidual";

    public AdjustedResidualQF() {
	super(1);
    }

    @Override
    public String getID() {
	return ID;
    }

    @Override
    public String getName() {
	return NAME;
    }

    @Override
    public StandardQF clone() {
	return new AdjustedResidualQF();
    }

    @Override
    public double evaluateBin(double subgroupSize, double tp,
	    double definedPopulationCount, double populationPositives) {
	return getPenalty(subgroupSize, definedPopulationCount,
		populationPositives)
		* super.evaluateBin(subgroupSize, tp, definedPopulationCount,
			populationPositives);
    }

    @Override
    public double evaluateNum(double subgroupSize, double sgMean,
	    double definedPopulationCount, double populationMean) {
	return getPenalty(subgroupSize, definedPopulationCount, populationMean)
		* super.evaluateNum(subgroupSize, sgMean,
			definedPopulationCount, populationMean);
    }

    private double getPenalty(double subgroupSize,
	    double definedPopulationCount, double populationPositives) {
	double p0 = populationPositives / definedPopulationCount;
	double penalty = Math
		.sqrt(1 / (p0 * subgroupSize * (1 - p0) * (1 - subgroupSize
			/ definedPopulationCount)));
	return penalty;
    }

    @Override
    public double estimateQuality(double subgroupSize,
	    double subgroupPositives, double populationSize,
	    double populationPositives) {
	return super.estimateQuality(subgroupSize, subgroupPositives,
		populationSize, populationPositives)
		* getPenalty(subgroupPositives, populationSize,
			populationPositives);
    }
}
