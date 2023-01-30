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
import java.io.UnsupportedEncodingException;

import org.jdom.Document;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.subgroup.search.MiningTask;

/**
 * Used to read a MiningTask form an XML-file.
 * 
 * @author lemmerich, atzmueller
 */

public class MiningTaskInterpreter extends AbstractMiningTaskInterpreter {

    public MiningTaskInterpreter(File miningTask, DatasetProvider provider)
	    throws IOException {
	super(miningTask, provider, new MiningTask());
    }

    public MiningTaskInterpreter(Ontology ontology, XMLData doc) {
	super(doc, new MiningTask());
	task.setOntology(ontology);
	init();
    }

    // needed for KnowWeplugin
    public MiningTaskInterpreter(Document doc, DatasetProvider provider) {
	super(doc, provider, new MiningTask());
    }

    public MiningTaskInterpreter(String miningTaskUTF8, DatasetProvider provider)
	    throws UnsupportedEncodingException {
	super(miningTaskUTF8, provider, new MiningTask());
    }

    // needed to execute a task
    public MiningTaskInterpreter(Ontology ontology, Document doc) {
	super(new XMLData(doc), new MiningTask());
	task.setOntology(ontology);
	init();
    }

    @Override
    protected void setConstraintForMaxSelectors(IConstraint constr,
	    EConstraintTyp type) {
	if (type == EConstraintTyp.maxSelectors) {

	    ((MiningTask) task).setMaxSGDSize(((Double) constr.getValue())
		    .intValue());

	} else {
	    throw new IllegalStateException("Cannot interpret maxSelectors");
	}
    }

    @Override
    public MiningTask getTask() {
	return (MiningTask) task;
    }
}
