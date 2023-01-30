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
 * Created on 03.05.2004
 *
 */
package org.vikamine.kernel.data;

/**
 * {@link DataRecord} represents a record of a {@link DataRecordSet}
 * 
 * @author atzmueller
 */

public interface DataRecord {

    public IDataRecordSet getDataset();

    public void setDataset(IDataRecordSet instances);

    public int getNumAttributes();

    public double getWeight();

    public void setWeight(double weight);

    public long getID();

    public double getValue(int index);

    public double getValue(Attribute att);

    public void setValue(int attIndex, double value);

    public void setValue(Attribute att, double value);

    public void setValue(Attribute att, String value);

    public void setValue(int attIndex, String value);

    public void setValue(Value value);

    public String getStringValue(int attIndex);

    public String getStringValue(Attribute att);

    public boolean isMissing(Attribute att);

    public boolean isMissing(int attIndex);

    public void setMissing(Attribute att);

    public void setMissing(int attIndex);

    public DataRecord copy();

    public void deleteAttributeAt(int index);

    public void insertAttributeAt(int index);
}
