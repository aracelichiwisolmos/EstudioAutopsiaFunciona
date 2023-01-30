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

import java.util.Collection;

/**
 * {@link TripleStoreListener} is called when the {@link RDFTripleStore} is
 * modified.
 * 
 * @author lemmerich
 * 
 */

public interface TripleStoreListener {

    public static final String ANY = "***ANY***";

    public Collection<String> getSupportedTypes();

    public void statementsAdded(Collection<RDFStatement> statements);

    public void statementsRemoved(Collection<RDFStatement> statements);

    public void setOntology(Ontology onto);
}
