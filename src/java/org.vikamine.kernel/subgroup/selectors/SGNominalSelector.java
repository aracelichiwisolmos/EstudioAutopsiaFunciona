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
 * Created on 13.12.2004
 *
 */

package org.vikamine.kernel.subgroup.selectors;

import java.util.Collection;
import java.util.Set;

import org.vikamine.kernel.data.Value;

/**
 * Implements handling of nominal selectors, i.e., capturing a set of (nominal) values for
 * an attribute.
 * 
 * @author atzmueller
 */
public interface SGNominalSelector extends SGSelector {
    public boolean isMaybeRedundant();

    public Set<Value> getValues();

    public boolean addValue(Value val);

    public boolean addAll(Collection values);

    public boolean removeValue(Value val);

    public boolean removeAll(Collection values);

}
