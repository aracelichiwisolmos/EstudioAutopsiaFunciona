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
 * Created on 26.03.2007
 *
 */
package org.vikamine.kernel.subgroup.analysis.causality;

import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFStatement;
import org.vikamine.kernel.data.RDFTripleStore;
import org.vikamine.kernel.subgroup.SG;

/**
 * Factory for producing {@link CausalSGNode}s.
 * 
 * @author atzmueller
 */
public class CausalSGNodeFactory {

    private static CausalSGNodeFactory instance = new CausalSGNodeFactory();

    private CausalSGNodeFactory() {
	super();
    }

    public static CausalSGNodeFactory getInstance() {
	if (instance == null) {
	    throw new IllegalStateException(
		    "Instance should have been eagerly created!");
	}
	return instance;
    }

    public static boolean isACausalSubgroup(SG sg, Ontology onto) {
	if (onto == null) {
	    return false;
	}

	RDFTripleStore store = onto.getTripleStore();
	RDFStatement firstMatchingStatement = store.getFirstMatchingStatement(
		sg, OntologyConstants.ACAUSAL_SUBGROUP,
		RDFTripleStore.ANY_OBJECT);

	if (firstMatchingStatement == null) {
	    return false;
	} else {
	    return (Boolean) firstMatchingStatement.getObject();
	}
    }

    public CausalSGNode createCausalSGNode(SG sg, Ontology onto) {
	if (isACausalSubgroup(sg, onto)) {
	    return new CausalACausalSGNode(sg);
	} else {
	    return new CausalSGNode(sg);
	}
    }

    public CausalSGNode createTargetSGNode(SG targetSG) {
	return new TargetSGNode(targetSG);
    }

    public CausalSGNode createNonCausalSGNode(SG sg) {
	return new NonCausalSGNode(sg);
    }
}
