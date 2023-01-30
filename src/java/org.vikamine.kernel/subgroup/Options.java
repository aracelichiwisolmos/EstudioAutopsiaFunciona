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
package org.vikamine.kernel.subgroup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.vikamine.kernel.data.Attribute;

/**
 * 
 * The {@link Options} class provides settable properties for objects that are
 * considered during subgroup discovery (methods) and for customizing the
 * behavior of subgroup objects. Missing value handling, for example, is
 * implemented by the Options.TREAT_MISSING_NOT_AS_DEFINED_VALUE property.
 * 
 * @author atzmueller
 * 
 */
public final class Options implements Serializable {

    public final static class Option {
	private String id;

	private Option(String id) {
	    super();
	    this.id = id;
	}

	public String getId() {
	    return id;
	}

	@Override
	public String toString() {
	    return getId();
	}
    }

    private static final long serialVersionUID = 1705383399258051185L;

    private Map<Object, Map<Option, Object>> optionsMap = new HashMap<Object, Map<Option, Object>>();

    public static final Option TREAT_MISSING_NOT_AS_DEFINED_VALUE = new Option(
	    "TREAT_MISSING_NOT_AS_DEFINED_VALUE");

    public Options() {
	super();
    }

    /**
     * Only makes a <b>shallow</b> shallow copy of the options!
     * 
     * @param sConstraints
     */
    public Options(Options other) {
	super();
	this.optionsMap = other.optionsMap;
    }

    public void setBooleanAttributeOption(Attribute attribute, Option option,
	    boolean bool) {
	Map<Option, Object> options = optionsMap.get(attribute);
	if (options == null) {
	    options = new HashMap();
	    optionsMap.put(attribute, options);
	}
	options.put(option, bool);
    }

    public boolean getBooleanAttributeOption(Attribute attribute, Option option) {
	Map<Option, Object> options = optionsMap.get(attribute);
	if (options == null)
	    return false;
	Object result = options.get(option);
	if (result == null)
	    return false;
	else
	    return (Boolean) result;
    }

    public Map<Option, Object> getAttributeOptions(Attribute attribute) {
	return optionsMap.get(attribute);
    }

    public void clear() {
	optionsMap = new HashMap<Object, Map<Option, Object>>();
    }

}
