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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * {@link CompressedSerializedObject} stores an arbitrary object in compact form.
 * 
 * @author atzmueller
 */
public class CompressedSerializedObject implements Serializable {

    private static final long serialVersionUID = -2749094857545759562L;

    private final byte[] storedObjectArray;

    public CompressedSerializedObject(Object toStore) throws Exception {

	ByteArrayOutputStream ostream = new ByteArrayOutputStream();
	OutputStream os = ostream;
	ObjectOutputStream p = new ObjectOutputStream(new BufferedOutputStream(
		new GZIPOutputStream(os)));
	p.writeObject(toStore);
	p.flush();
	p.close();
	storedObjectArray = ostream.toByteArray();
    }

    @Override
    public final boolean equals(Object compareTo) {
	if (compareTo == null)
	    return false;
	if (!compareTo.getClass().equals(this.getClass()))
	    return false;
	byte[] compareArray = ((CompressedSerializedObject) compareTo).storedObjectArray;
	if (compareArray.length != storedObjectArray.length)
	    return false;
	for (int i = 0; i < compareArray.length; i++) {
	    if (compareArray[i] != storedObjectArray[i])
		return false;
	}
	return true;
    }

    /**
     * Returns a serialized object.
     * 
     * @return the restored object, null if the object could not be restored
     */
    public Object getObject() {

	try {
	    ByteArrayInputStream istream = new ByteArrayInputStream(
		    storedObjectArray);
	    ObjectInputStream p = new ObjectInputStream(
		    new BufferedInputStream(new GZIPInputStream(istream)));
	    Object toReturn = p.readObject();
	    istream.close();
	    return toReturn;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Override
    public int hashCode() {
	return storedObjectArray.length;
    }
}
