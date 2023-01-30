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

package org.vikamine.kernel.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vikamine.kernel.formula.FormulaNumber;
import org.vikamine.kernel.formula.constants.FormulaAttributePrimitive;
import org.vikamine.kernel.formula.operators.EqualsNumber;

/**
 * A builder for creating nominal attributes.
 * 
 * @author Alex Plischke
 * @date 07/09
 */
public class DerivedNominalAttributeBuilder extends DerivedAttributeBuilder {

    private final NumericAttribute na;
    private final Ontology om;

    public DerivedNominalAttributeBuilder(NumericAttribute na, Ontology om) {
	super();
	this.na = na;
	this.om = om;
    }

    public void buildNominalCustomAttribute() {
	List<Value> values = buildValues();
	String name = na.getId() + "_nominalized";
	attribute = new DerivedNominalAttribute(name, values);
	attribute.setDescription(name);
	for (Value v : values) {
	    ((DerivedNominalValue) v).setAttribute(attribute);
	}
    }

    private List<Value> buildValues() {
	List<Value> result = new ArrayList<Value>();
	Iterator<Value> iter = na.usedValuesIterator(om.getDataset());
	int i = 0;
	while (iter.hasNext()) {
	    String num = iter.next().toString();
	    DerivedNominalValue value = new DerivedNominalValue("Value" + i,
		    num);
	    FormulaNumber formNum1 = new FormulaNumber(Double.parseDouble(num));
	    EqualsNumber eq = new EqualsNumber();
	    eq.setArg1(new FormulaAttributePrimitive(na));
	    eq.setArg2(formNum1);

	    value.setCondition(eq);
	    result.add(value);
	    i++;
	}
	return result;
    }
}
