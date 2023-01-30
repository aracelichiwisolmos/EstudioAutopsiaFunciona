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

package org.vikamine.kernel.subgroup.quality.functions;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.subgroup.ISubgroup;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.target.NumericMultiAttributeTarget;
import org.vikamine.kernel.util.VKMUtil;


public class KLDirichletQF implements IQualityFunction {

    private static final String ID = "KLDirichletQF";

    private static final String NAME = "Kullback-Leibler Dirichlet QF";

    private double[] alphasInPopulation;

    private final double a;

    public KLDirichletQF(double a) {
	super();
	this.a = a;
    }

    @Override
    public IQualityFunction clone() {
	return new KLDirichletQF(a);
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
    public boolean isApplicable(ISubgroup<DataRecord> subgroup) {
	if (!(subgroup instanceof SG)) {
	    return false;
	} else {
	    SG sg = (SG) subgroup;
	    return sg.getTarget() != null && sg.getTarget().isNumeric();
	}
    }

    private List<Attribute> getAttributes(ISubgroup<DataRecord> subgroup) {
	return new ArrayList(
		((NumericMultiAttributeTarget) ((SG) subgroup).getTarget())
			.getAttributes());
    }

    private double[][] getNumericTargetAttributeValues(
	    ISubgroup<DataRecord> subgroup) {
	List<Attribute> attributes = getAttributes(subgroup);

	int subgroupSize = VKMUtil.asList(subgroup.iterator()).size();
	double[][] values = new double[subgroupSize][attributes.size()];
	int i = 0;
	for (DataRecord record : subgroup) {
	    int j = 0;
	    for (Attribute attribute : attributes) {
		double value = record.getValue(attribute);
		values[i][j] = value;
		j++;
	    }
	    i++;
	}
	return values;
    }

    public void cachePopulationTargetParameters(ISubgroup<DataRecord> subgroup) {
	List<Attribute> attributes = getAttributes(subgroup);
	DataView dataview = ((SG) subgroup).getPopulation();
	double[] numericTargetAttributeValuesMeansInPopulation = new double[attributes
		.size()];
	int pos = 0;
	for (Attribute attribute : attributes) {
	    int size = 0;
	    double mean = 0;
	    for (DataRecord record : dataview) {
		mean += record.getValue(attribute);
		size++;
	    }
	    mean = mean / size;
	    numericTargetAttributeValuesMeansInPopulation[pos] = mean;
	    pos++;
	}
    }

    @Override
    public double evaluate(ISubgroup<DataRecord> subgroup) {
	List<Attribute> attributes = getAttributes(subgroup);

	double[][] numericTargetAttributeValuesMatrixArray = getNumericTargetAttributeValues(subgroup);
	RealMatrix numericTargetAttributeValuesMatrix = new BlockRealMatrix(
		numericTargetAttributeValuesMatrixArray);
	double[] numericTargetAttributeValuesMeans = new double[attributes
		.size()];

	Mean mean = new Mean();
	for (int i = 0; i < attributes.size(); i++) {
	    double attributeMean = mean
		    .evaluate(numericTargetAttributeValuesMatrix.getColumn(i));
	    numericTargetAttributeValuesMeans[i] = attributeMean;
	}

	RealMatrix covarianceMatrix = new Covariance(
		numericTargetAttributeValuesMatrix).getCovarianceMatrix();

	double[] meanDiff = new double[attributes.size()];
	for (int i = 0; i < attributes.size(); i++) {
	    meanDiff[i] = numericTargetAttributeValuesMeans[i]
		    - alphasInPopulation[i];
	}

	RealVector meanDiffVector = new ArrayRealVector(meanDiff);
	try {
	    RealVector inverseCoverianceMatrix = MatrixUtils.inverse(
		    covarianceMatrix).operate(meanDiffVector);
	    RealMatrix meanDiffVectorTransposed = MatrixUtils
		    .createRowRealMatrix(meanDiff);
	    RealVector resultElement = meanDiffVectorTransposed
		    .operate(inverseCoverianceMatrix);
	    double result = resultElement.getEntry(0);
	    double subgroupSize = ((SG) subgroup).getStatistics()
		    .getSubgroupSize();
	    double numAttributes = attributes.size();
	    double sizeFactor = subgroupSize * (subgroupSize - numAttributes)
		    / (numAttributes * (subgroupSize - 1));
	    // double sizeFactor = subgroupSize;
	    sizeFactor = Math.pow(sizeFactor, a);
	    result = result * sizeFactor;
	    return result;
	} catch (SingularMatrixException e) {
	    return Double.NaN;
	}
    }
}
