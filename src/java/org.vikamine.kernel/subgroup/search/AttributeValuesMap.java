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
 * Created on 22.06.2005
 *
 */

package org.vikamine.kernel.subgroup.search;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.Value;


/**
 * Holds a {@link List} of {@link Value}s for each {@link Attribute}.
 * 
 * @author atzmueller
 */
public class AttributeValuesMap implements Cloneable {

    Map<Attribute, List<Value>> attributeValuesMap = new HashMap<Attribute, List<Value>>();

    public AttributeValuesMap() {
	super();
    }

    public void setValuesForAttribute(Attribute attribute, List<Value> values) {
	attributeValuesMap.put(attribute, values);
    }

    public List<Value> getValuesForAttribute(Attribute attribute) {
	return attributeValuesMap.get(attribute);
    }

    public List<Attribute> getAttributes() {
	return new LinkedList<Attribute>(attributeValuesMap.keySet());
    }

    @Override
    public AttributeValuesMap clone() {
	AttributeValuesMap c = new AttributeValuesMap();
	for (Attribute att : getAttributes()) {
	    c.setValuesForAttribute(att, getValuesForAttribute(att));
	}
	return c;
    }

}
