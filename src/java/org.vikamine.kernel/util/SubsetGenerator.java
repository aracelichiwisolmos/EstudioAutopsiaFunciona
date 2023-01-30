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

package org.vikamine.kernel.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Generate subsets efficiently.
 * 
 * @author lemmerich
 * 
 */

public class SubsetGenerator {

    // That is for testing purposes
    public static void main(String[] args) {
	List<Integer> base = new ArrayList<Integer>();
	for (int size = 0; size < 3; size++) {
	    base.add(size);
	}
	List<List<Integer>> subsetList = generateSubsetHalf(base);
	System.out.println(subsetList);
	System.out.println(subsetList.size());
    }

    public static <T> List<List<T>> generateSubset(List<T> baseSet) {
	List<List<T>> result = new ArrayList<List<T>>();
	Iterator<List<T>> it = subsetIterator(baseSet);
	while (it.hasNext()) {
	    result.add(it.next());
	}
	return result;
    }

    public static <T> List<List<T>> generateSubsetHalf(List<T> baseSet) {
	List<List<T>> result = new ArrayList<List<T>>();
	Iterator<List<T>> it = subsetMaxLengthIterator(baseSet,
		baseSet.size() / 2, false);
	while (it.hasNext()) {
	    result.add(it.next());
	}
	return result;
    }

    public static <T> List<List<T>> generateMaxLengthSubset(List<T> baseSet,
	    int maxLength, boolean includeMaxLength) {
	List<List<T>> result = new ArrayList<List<T>>();
	Iterator<List<T>> it = subsetMaxLengthIterator(baseSet, maxLength,
		includeMaxLength);
	while (it.hasNext()) {
	    result.add(it.next());
	}
	return result;
    }

    public static <T> Iterator<List<T>> subsetIterator(final List<T> baseSet) {
	final int size = baseSet.size();
	if (size > 63) {
	    throw new IllegalArgumentException(
		    "baseSet is too large! Max allowed size is 63");
	}

	return new Iterator<List<T>>() {
	    long counter = (long) Math.pow(2, size);

	    @Override
	    public boolean hasNext() {
		return (this.counter > 0);
	    }

	    @Override
	    public List<T> next() {
		this.counter--;
		ArrayList<T> aSubset = new ArrayList<T>();
		for (int i = nextSetBit(this.counter, 0); i >= 0; i = nextSetBit(
			this.counter, i + 1)) {
		    aSubset.add(baseSet.get(i));
		}
		return aSubset;
	    }

	    @Override
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    /**
     * @param includeAllWithMaxLength
     *            should usually be true. If false, then for subsets with the
     *            maximum length only the ones not including the last element
     *            are used (see subsetHalf...)
     * @return
     */
    public static <T> Iterator<List<T>> subsetMaxLengthIterator(
	    final List<T> baseSet, final int maxLength,
	    final boolean includeAllWithMaxLength) {
	final int size = baseSet.size();
	if (size > 63) {
	    throw new IllegalArgumentException(
		    "baseSet is too large! Max allowed size is 63");
	}
	if (maxLength < 0) {
	    throw new IllegalArgumentException("maxLength is smaller than 0");
	}

	return new Iterator<List<T>>() {
	    long counter = (long) Math.pow(2, size);

	    @Override
	    public boolean hasNext() {
		return (this.counter > 0);
	    }

	    @Override
	    public List<T> next() {
		this.counter--;
		int bitCount = Long.bitCount(this.counter);
		if (bitCount > maxLength) {
		    return next();
		}
		if (bitCount == maxLength) {
		    if (!includeAllWithMaxLength) {
			if ((this.counter & 1) > 0) {
			    long counterInvert = counter & (~0L);
			    if (counterInvert > maxLength) {
				return next();
			    }
			}
		    }
		}
		ArrayList<T> aSubset = new ArrayList<T>(bitCount);
		for (int i = nextSetBit(this.counter, 0); i >= 0; i = nextSetBit(
			this.counter, i + 1)) {
		    aSubset.add(baseSet.get(i));
		}
		return aSubset;
	    }

	    @Override
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    // private static boolean isBitISet(long l, int i) {
    // long mask = 1 << i;
    // return (mask & l) != 0;
    // }

    private static int nextSetBit(long word, int fromIndex) {
	final long WORD_MASK = 0xffffffffffffffffL;
	word = word & (WORD_MASK << fromIndex);
	return (word == 0L) ? -1 : Long.numberOfTrailingZeros(word);
    }
}
