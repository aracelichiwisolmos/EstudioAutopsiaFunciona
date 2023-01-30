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

package org.vikamine.kernel.xpdl;

import java.io.File;
import java.io.IOException;

import org.vikamine.kernel.subgroup.search.MiningTaskFamily;

/**
 * 
 * @author lemmerich, atzmueller
 * 
 */

public class MiningTaskFamilyInterpreter extends AbstractMiningTaskInterpreter {

    public MiningTaskFamilyInterpreter(File miningTask,
	    DatasetProvider repository) throws IOException {
	super(miningTask, repository, new MiningTaskFamily());
    }

    @Override
    protected void setConstraintForMaxSelectors(IConstraint constr,
	    EConstraintTyp type) {
	if ((type == EConstraintTyp.maxSelectors)
		&& (constr instanceof MConstraintIntEnumeration)) {
	    ((MiningTaskFamily) task).setMetaMaxSGDSizes((int[]) constr
		    .getValue());
	} else if (constr instanceof MConstraintNumeric) {
	    int[] dummyArray = { (int) Math.round(((Double) constr.getValue())) };
	    ((MiningTaskFamily) task).setMetaMaxSGDSizes(dummyArray);
	} else {
	    throw new IllegalStateException("Cannot interpret maxSelectors");
	}
    }

    @Override
    public MiningTaskFamily getTask() {
	return (MiningTaskFamily) task;
    }

    @Override
    protected void initSearchSpace() {
	super.initSearchSpace();
    }
}
