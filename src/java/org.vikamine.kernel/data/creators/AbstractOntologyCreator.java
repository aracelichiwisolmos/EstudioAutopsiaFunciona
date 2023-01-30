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

package org.vikamine.kernel.data.creators;

import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;

import org.vikamine.kernel.data.AbstractDataRecord;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecordSet;
import org.vikamine.kernel.data.Ontology;

/**
 * {@link AbstractOntologyCreator} is the base class of all
 * {@link OntologyCreator} classes that are used for factoring {@link Ontology}
 * instances.
 * 
 * @author atzmueller
 * 
 */
public abstract class AbstractOntologyCreator implements OntologyCreator {

    protected static class AttributeNameComparatorAscending implements
	    Comparator<Attribute> {

	private final Collator collator;

	public AttributeNameComparatorAscending() {
	    super();
	    this.collator = Collator.getInstance();
	}

	@Override
	public int compare(Attribute a1, Attribute a2) {
	    return collator.compare(a1.getId(), a2.getId());
	}
    }

    protected DataRecordSet dataset;

    protected void createDatasetIDs() {
	for (long i = 0; i < dataset.getNumInstances(); i++) {
	    AbstractDataRecord dataRecord = (AbstractDataRecord) dataset
		    .get((int) i);
	    dataRecord.setID(i);
	}
    }

    protected void copyAttributesToOntology(Ontology ontology) {
	for (Iterator<Attribute> iter = dataset.attributeIterator(); iter
		.hasNext();) {
	    Attribute att = iter.next();
	    if (att.getDescription() == null)
		att.setDescription(att.getId());
	    ontology.addAttribute(att);
	}
    }
}
