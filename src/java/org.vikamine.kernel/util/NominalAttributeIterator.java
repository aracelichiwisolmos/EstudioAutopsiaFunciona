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
 * Created on 11.11.2004
 *
 */
package org.vikamine.kernel.util;

import java.util.Iterator;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * Iterates over an attribute and returns values (and their counts).
 * 
 * @author atzmueller
 */
public class NominalAttributeIterator implements AttributeValuesIterator {
    private SG sg;

    private Attribute dmAttribute;

    private int next = 0;

    private int valueCount;

    private Iterator iterator;

    private Value val;

    public NominalAttributeIterator(NominalAttribute att, SG sg) {
	super();
	this.sg = (SG) sg.clone();
	this.dmAttribute = att;
	this.valueCount = att.getValuesCount();
	iterator = att.allValuesIterator();
    }

    @Override
    public int getIterationValueCount() {
	return valueCount;
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException("Remove is not supported");
    }

    @Override
    public boolean hasNext() {
	return iterator.hasNext();
    }

    @Override
    public Object next() {
	next++;
	val = (Value) iterator.next();
	return val;
    }

    /**
     * Returns the number of instances, that have values between the previous
     * returned value and the one before.
     * 
     * @return
     */
    @Override
    public int getCurrentInstancesCount() {
	if (next == 0) {
	    throw new IllegalStateException(
		    "must call next() at least once before");
	}

	sg.setTarget(new SelectorTarget(new DefaultSGSelector(dmAttribute, val)));
	sg.createStatistics(sg.getStatistics().getOptions());
	return (int) ((SGStatisticsBinary) sg.getStatistics()).getTp();
    }

}
