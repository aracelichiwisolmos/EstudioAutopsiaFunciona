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
package org.vikamine.kernel.data.discretization;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.vikamine.kernel.data.AttributeBuilder;
import org.vikamine.kernel.data.DerivedAttributeBuilder;
import org.vikamine.kernel.data.DerivedNominalAttribute;
import org.vikamine.kernel.data.DerivedNominalValue;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.FormulaNumber;
import org.vikamine.kernel.formula.constants.FormulaAttributePrimitive;
import org.vikamine.kernel.formula.operators.And;
import org.vikamine.kernel.formula.operators.Greater;
import org.vikamine.kernel.formula.operators.GreaterEquals;

/**
 * {@link DiscretizationAttributeBuilder} builds a nominal custom
 * {@link AttributeBuilder}.
 * 
 * @author lemmerich, atzmueller
 * @date 04/2009
 */
public class DiscretizationAttributeBuilder extends DerivedAttributeBuilder {

    private final List<Double> cutpoints;
    private final NumericAttribute na;

    private final DecimalFormat formatter = new DecimalFormat("#.#");

    public DiscretizationAttributeBuilder(List<Double> cutpoints,
	    NumericAttribute na) {
	super();
	this.cutpoints = cutpoints;
	this.na = na;
    }

    public void buildNominalCustomAttribute(String attributeID) {
	List<Value> values = buildValues();
	attribute = new DerivedNominalAttribute(attributeID, values);
	for (Value v : values) {
	    ((DerivedNominalValue) v).setAttribute(attribute);
	}
    }

    private List<Value> buildValues() {
	List<Value> result = new ArrayList<Value>();
	for (int i = 0; i <= cutpoints.size(); i++) {
	    double minOfInterval = (i == 0) ? Double.NEGATIVE_INFINITY
		    : cutpoints.get(i - 1);
	    double maxOfInterval = (i == cutpoints.size()) ? Double.POSITIVE_INFINITY
		    : cutpoints.get(i);
	    String description = "[" + formatter.format(minOfInterval) + "; "
		    + formatter.format(maxOfInterval) + "]";
	    DerivedNominalValue value = new DerivedNominalValue(description,
		    description);

	    GreaterEquals left = new GreaterEquals();
	    left.setArg1(new FormulaAttributePrimitive(na));
	    left.setArg2(new FormulaNumber(minOfInterval));

	    Greater right = new Greater();
	    right.setArg1(new FormulaNumber(maxOfInterval));
	    right.setArg2(new FormulaAttributePrimitive(na));

	    FormulaBooleanElement formula = new And(left, right);
	    value.setCondition(formula);
	    result.add(value);
	}
	return result;
    }
}
