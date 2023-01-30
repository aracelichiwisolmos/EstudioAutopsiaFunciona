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

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.formula.EvaluationData;
import org.vikamine.kernel.formula.FormulaBooleanElement;


/**
 * A Value for a {@link DerivedNominalAttribute}.
 * 
 * @author atzmueller
 *
 */
public class DerivedNominalValue extends Value {

    private final String description;

    private FormulaBooleanElement condition;

    private Attribute attribute;

    public DerivedNominalValue(String id, String description) {
	super(id);
	this.description = description;
    }

    @Override
    public Attribute getAttribute() {
	return attribute;
    }

    public FormulaBooleanElement getCondition() {
	return condition;
    }

    @Override
    public String getDescription() {
	return description;
    }

    @Override
    public String getDescription(Describer d) {
	return d.createDescription(this);
    }

    @Override
    public boolean isMissingValue() {
	return false;
    }

    @Override
    public boolean isValueContainedInInstance(DataRecord instance) {
	EvaluationData data = new EvaluationData();
	data.setInstance(instance);
	return getCondition().eval(data).booleanValue();
    }

    public void setAttribute(Attribute att) {
	this.attribute = att;
    }

    public void setCondition(FormulaBooleanElement condition) {
	this.condition = condition;
    }

}
