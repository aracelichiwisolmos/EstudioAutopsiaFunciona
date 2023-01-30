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

package org.vikamine.kernel.subgroup.target;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.util.IdentityLongDescriber;

public class NumericMultiAttributeTarget implements SGTarget {

    private final List<Attribute> attributes;

    public NumericMultiAttributeTarget(List<Attribute> attributes) {
	super();
	this.attributes = attributes;
    }

    @Override
    public String getId() {
	StringBuffer buffy = new StringBuffer();
	buffy.append("NumericMultiAttributeTarget ={");
	List sortedAttributes = new LinkedList(getAttributes());
	Collections.sort(sortedAttributes, new Comparator<Attribute>() {

	    @Override
	    public int compare(Attribute o1, Attribute o2) {
		return Collator.getInstance().compare(o1.getId(), o2.getId());
	    }
	});

	for (Iterator iter = sortedAttributes.iterator(); iter.hasNext();) {
	    Attribute a = (Attribute) iter.next();
	    buffy.append(a.getId());
	    if (iter.hasNext()) {
		buffy.append(", ");
	    }
	}
	buffy.append("}");
	return buffy.toString();
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public String getDescription(Describer d) {
	StringBuffer buffer = new StringBuffer();
	Iterator attributeIterator = getAttributes().iterator();
	while (attributeIterator.hasNext()) {
	    Attribute a = (Attribute) attributeIterator.next();
	    buffer.append(d.createDescription(a));
	    if (attributeIterator.hasNext()) {
		buffer.append(", ");
	    }
	}
	return buffer.toString();
    }

    @Override
    public Collection<Attribute> getAttributes() {
	return attributes;
    }

    @Override
    public boolean isBoolean() {
	return false;
    }

    @Override
    public boolean isNumeric() {
	return true;
    }

}
