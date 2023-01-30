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

public class DirectoryDatasetProvider implements DatasetProvider {

    final String path;

    /**
     * path should end with a path separator! e.g. "C:\\test\\
     * 
     * @param path
     */
    public DirectoryDatasetProvider(String path) {
	super();
	this.path = path;
    }

    @Override
    public Ontology getDataset(String name) {
	try {
	    return DataFactory.createOntology(new File(path + name));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    @Override
    public boolean providesDataset(String name) {
	return new File(path + name).exists();
    }

}
