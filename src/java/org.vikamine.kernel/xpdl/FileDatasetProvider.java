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

import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.creators.DataFactory;

public class FileDatasetProvider implements DatasetProvider {

    private final String fileName;
    private final String name;

    public FileDatasetProvider(String fileName) {
	super();
	this.fileName = fileName;
	this.name = new File(fileName).getName();
    }

    public FileDatasetProvider(String fileName, String alias) {
	super();
	this.fileName = fileName;
	this.name = alias;
    }

    @Override
    public Ontology getDataset(String name) {
	try {
	    return DataFactory.createOntology(new File(fileName));
	} catch (IOException e) {
	    throw new IllegalStateException(e);
	}
    }

    @Override
    public boolean providesDataset(String name) {
	return this.name.equals(name);
    }

}
