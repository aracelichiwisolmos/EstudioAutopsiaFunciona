/*
 *  This file is part of the VKM-Kernel library.
 * 
 *  Copyright (C) 2003-2008 by Martin Atzmueller, and contributors.
 *  Copyright (C) 2008-2020 by Martin Atzmueller, Florian Lemmerich, and contributors.
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
 * Created on 10.11.2003
 * 
 */

package org.vikamine.kernel.subgroup.selectors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.Describer;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.util.IdentityLongDescriber;

/**
 * {@link DefaultSGSelector} represents a nominal {@link SGSelector}. 
 * 
 * @author atzmueller
 */
public class DefaultSGSelector extends AbstractSGSelector {

    private Set<Value> values;

    /**
     * Creates a DefaultSGSelector by specifying the name of the attribute and
     * the (one!) value for this selector.
     * 
     * @param onto
     *            The Ontology used for this selector
     * @param attributeID
     *            the id of the used attribute (must be nominal!)
     * @param valueID
     *            the id of the (one) value of the nominal attribute used for
     *            this selector
     */
    public DefaultSGSelector(Ontology onto, String attributeID, String valueID) {
	super(onto.getAttribute(attributeID));
	if (onto.getAttribute(attributeID) == null) {
	    throw new IllegalArgumentException("Attribute " + attributeID
		    + " does not exist!");
	} else if (!onto.getAttribute(attributeID).isNominal()) {
	    throw new IllegalArgumentException("Attribute " + attributeID
		    + " is not nominal!");
	}
	Value v = ((NominalAttribute) onto.getAttribute(attributeID))
		.getNominalValueFromID(valueID);
	this.values = Collections.singleton(v);
    }

    public DefaultSGSelector(Attribute a, Value value) {
	this(a, Collections.singleton(value));
	if (value == null) {
	    throw new IllegalArgumentException("Value is null");
	}
    }

    public DefaultSGSelector(Attribute a, Set<Value> values) {
	super(a);
	if (values == null) {
	    throw new IllegalArgumentException("Values is null");
	}
	this.values = new HashSet<Value>(values);
    }

    public DefaultSGSelector(DefaultSGSelector sel) {
	this(sel.getAttribute(), sel.getValues());
    }

    @Override
    public String toString() {
	return getId();
    }

    @Override
    public boolean isContainedInInstance(DataRecord instance) {
	for (Value value : values) {
	    if (value.isValueContainedInInstance(instance)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean equals(Object object) {
	if (this == object)
	    return true;
	else if ((object == null) || (getClass() != object.getClass())) {
	    return false;
	} else {
	    DefaultSGSelector otherSGS = (DefaultSGSelector) object;
	    return getAttribute().equals(otherSGS.getAttribute())
		    && (values.equals(otherSGS.values));
	}
    }

    @Override
    public int hashCode() {
	int hash = 17;
	hash = 37 * hash + getAttribute().hashCode();
	hash = 37 * hash + values.hashCode();
	return hash;
    }

    @Override
    public String getId() {
	StringBuffer buffy = new StringBuffer();
	List sortedValues = new LinkedList(values);
	if (sortedValues.size() > 1) {
	    buffy.append(getAttribute().getId() + "={");
	} else {
	    buffy.append(getAttribute().getId() + "=");
	}
	Collections.sort(sortedValues, new Value.ValueComparator());
	for (Iterator iter = sortedValues.iterator(); iter.hasNext();) {
	    Value element = (Value) iter.next();
	    buffy.append(element.getId());
	    if (iter.hasNext()) {
		buffy.append("; ");
	    }
	}
	if (sortedValues.size() > 1) {
	    buffy.append("}");
	}
	return buffy.toString();
    }

    @Override
    public String getDescription(Describer d) {
	StringBuffer buffy = new StringBuffer();
	buffy.append(d.createDescription(getAttribute()));
	if ((values.isEmpty()) || (values.size() > 1)) {
	    buffy.append("={");
	    List sortedValues = new LinkedList(values);
	    Collections.sort(sortedValues, new Value.ValueComparator());
	    for (Iterator iter = sortedValues.iterator(); iter.hasNext();) {
		Value element = (Value) iter.next();
		// buffy.append(element.getDescription(longFormat));
		buffy.append(d.createDescription(element));
		if (iter.hasNext()) {
		    buffy.append("; ");
		}
	    }
	    buffy.append("}");
	} else {
	    buffy.append("="
		    + d.createDescription(((Value) values.toArray()[0])));
	}
	return buffy.toString();
    }

    @Override
    public String getDescription() {
	return getDescription(new IdentityLongDescriber());
    }

    @Override
    public Set<Value> getValues() {
	return values;
    }

    @Override
    public boolean isMaybeRedundant() {
	if (!getAttribute().isNominal()) {
	    return false;
	} else {
	    List containedValues = new LinkedList();
	    for (Iterator iter = values.iterator(); iter.hasNext();) {
		Value val = (Value) iter.next();
		containedValues.add(val);
	    }
	    if (containedValues.isEmpty()) {
		return true;
	    }
	    NominalAttribute att = (NominalAttribute) getAttribute();
	    for (Iterator iter = att.allValuesIterator(); iter.hasNext();) {
		Value val = (Value) iter.next();
		if (!containedValues.contains(val)) {
		    return false;
		}
	    }
	    return true;
	}
    }

    @Override
    public Object clone() {
	// create the instance of the right class
	DefaultSGSelector sel = (DefaultSGSelector) super.clone();
	sel.values = new HashSet(sel.values);
	return sel;
    }

    @Override
    public boolean addValue(Value val) {
	return values.add(val);
    }

    @Override
    public boolean addAll(Collection c) {
	return values.addAll(c);
    }

    @Override
    public boolean removeValue(Value val) {
	return values.remove(val);
    }

    @Override
    public boolean removeAll(Collection c) {
	return values.removeAll(c);
    }

    /**
     * otherSel.isContainedInInstance ==> this.isContainedInInstance
     * (independent of record set)
     * 
     * @param otherSel
     * @return
     */
    @Override
    public boolean includes(SGSelector otherSel) {
	if (!(otherSel instanceof DefaultSGSelector)) {
	    return false;
	}
	if (getAttribute() != otherSel.getAttribute()) {
	    return false;
	}
	return values.containsAll(((DefaultSGSelector) otherSel).values);

    }
}
