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

import java.util.Iterator;
import java.util.List;

public interface IDataRecordSet extends Iterable<DataRecord> {

    public abstract DataRecord add(DataRecord instance);

    public abstract Iterator<Attribute> attributeIterator();

    public abstract void compactify();

    public abstract void delete();

    public abstract void delete(int index);

    public abstract void deleteAttributeAt(int position);

    public abstract DataRecord get(int index);

    public abstract Attribute getAttribute(int index);

    public abstract Attribute getAttribute(String name);

    public abstract int getIndex(Attribute attribute);

    public abstract int getNumAttributes();

    public abstract int getNumInstances();

    // @Deprecated
    public abstract String getRelationName();

    public abstract void insertAttributeAt(Attribute att, int position);

    public abstract void updateAttribute(Attribute att, int position);

    public abstract String printInstances(Ontology om);

    public abstract void setRelationName(String newName);

    /**
     * Returns an unmodifiable List of the attributes saved in this
     * DataRecordSet. This contains no Derived Attributes. If you want a list of
     * all "current" attributes, then use Ontology.getAttributes();
     * 
     * @return
     */
    public abstract List<Attribute> getAttributesUnmodifiable();

    /**
     * Returns an unmodifiable List of the instances saved in this
     * DataRecordSet.
     */
    public abstract List<DataRecord> getDataRecordsUnmodifiable();

}