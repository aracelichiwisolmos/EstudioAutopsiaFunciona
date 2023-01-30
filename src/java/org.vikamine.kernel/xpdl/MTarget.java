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

public class MTarget {

    private final MAttribute attribute;
    private final String type;

    public MTarget(String type, MAttribute attribute) {
	this.attribute = attribute;
	this.type = type;
	if (this.type.equals(ETyp.exclude))
	    throw new XMLException("excludevalue in target");
	else if (this.attribute.valuesCount() > 1)
	    throw new XMLException("More than 1 includevalue in target");
    }

    public String getType() {
	return type;
    }

    public MAttribute getAttribute() {
	return attribute;
    }

}
