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
 * Created on 10.04.2004
 */
package org.vikamine.kernel.subgroup.target;

import java.util.Collection;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBooleanElement;

/**
 * {@link SGTarget} that can be described with a {@link FormulaBooleanElement}.
 * 
 * @author Tobias Vogele, atzmueller
 */
public class BooleanFormulaTarget implements BooleanTarget {

    protected FormulaBooleanElement formula;

    @Override
    public boolean isPositive(DataRecord instance) {
	return getFormula().eval(new EvaluationData(null, instance))
		.booleanValue();
    }

    public FormulaBooleanElement getFormula() {
	return formula;
    }

    // public void setFormula(FormulaBooleanElement formula) {
    // this.formula = formula;
    // }

    public BooleanFormulaTarget(FormulaBooleanElement formula) {
	this.formula = formula;
    }

    protected BooleanFormulaTarget() {
	super();
    }

    @Override
    public int hashCode() {
	return getFormula() == null ? 0 : getFormula().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	} else if (obj == null) {
	    return false;
	} else if (getClass() != obj.getClass()) {
	    return false;
	} else {
	    BooleanFormulaTarget t = (BooleanFormulaTarget) obj;
	    if (getFormula() == null) {
		return t.getFormula() == null;
	    } else {
		return getFormula().equals(t.getFormula());
	    }
	}
    }

    @Override
    public Collection getAttributes() {
	return getFormula().getAttributes();
    }

    @Override
    public String getId() {
	return "BooleanFormulaTarget " + getFormula().toString();
    }

    @Override
    public String toString() {
	return getId();
    }

    @Override
    public String getDescription() {
	return getFormula().toString();
    }

    public String getShortDescription() {
	return null;
    }

    @Override
    public String getDescription(Describer d) {
	return d.createDescription(this);
    }

    @Override
    public boolean isBoolean() {
	return true;
    }

    @Override
    public boolean isNumeric() {
	return false;
    }
}
