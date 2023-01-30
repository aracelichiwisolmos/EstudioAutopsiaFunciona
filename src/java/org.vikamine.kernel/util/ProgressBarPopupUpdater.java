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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Updates the progress bar.
 * 
 * @author Alex Plischke
 * 
 */
public class ProgressBarPopupUpdater {

    private static int capacity = 1;
    private static int currentProgress = 0;
    private static int relativeProgress = 0;

    /**
     * Initialize the ProgressBarPopupUpdater with the max. progresses to be
     * counted.
     * 
     * @param capacity
     *            capacity of max progresses to be counted.
     */
    public static void init(int capacity) {
	ProgressBarPopupUpdater.capacity = capacity;
	currentProgress = 0;
	relativeProgress = 0;
    }

    /**
     * Initialize with max. capacity being the amount of attributes in the file.
     * This method is specifically made for dealing with ARFF files.
     * 
     * @param file
     * @throws IOException
     */
    public static void init(File file) throws IOException {
	if (!file.toString().endsWith(".arff")) {
	    throw new IOException(
		    "Please use this method only with ARFF files!");
	}
	BufferedReader buffy = new BufferedReader(new FileReader(file));
	String input = buffy.readLine();
	int attributeCount = 0;
	boolean foundOne = false;
	// check for attributes, if one is found and no more follow, then abort
	// the operation
	while (input != null) {
	    if (input.toLowerCase().startsWith("@attribute")) {
		foundOne = true;
		attributeCount++;
	    } else if (foundOne) {
		break;
	    }
	    input = buffy.readLine();
	}
	init(attributeCount);
	buffy.close();
    }

    /**
     * Update the progress (count plus 1 to the current progress) and set the
     * relative progress to be displayed as percent of the total progress.
     */
    public static void updateProgress() {
	currentProgress++;
	setRelativeProgress((currentProgress * 100) / capacity);
    }

    /**
     * Sets relative progress.
     * 
     * @param relativeProgress
     *            the relative progress
     */
    public static void setRelativeProgress(int relativeProgress) {
	ProgressBarPopupUpdater.relativeProgress = relativeProgress;
    }

    /**
     * Gets the relative progress.
     * 
     * @return The relative progress
     */
    public static int getRelativeProgress() {
	return relativeProgress;
    }

}
