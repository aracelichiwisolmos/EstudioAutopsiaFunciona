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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads in a group hierarchy.
 * 
 * @author lemmerich
 * 
 */

public class GroupReader {

    private static final int maxDepth = 10;

    private final List<Group<String>> allGroups;
    private final Reader dashTreeReader;
    private final List<Group<String>> rootGroups;

    public GroupReader(String dashTreeString) {
	this(new StringReader(dashTreeString));
    }

    public GroupReader(File f) throws FileNotFoundException {
	this(new FileReader(f));
    }

    public GroupReader(Reader dashTreeReader) {
	this.dashTreeReader = dashTreeReader;
	this.rootGroups = new ArrayList<Group<String>>();
	this.allGroups = new ArrayList<Group<String>>();
	init();
    }

    private boolean dashesEndWithPlus(String line) {
	int noDashes = numberOfLeadingDashes(line);
	if (line.length() <= noDashes) {
	    return false;
	}
	char c = line.charAt(noDashes);
	return c == '+';
    }

    public List<Group<String>> getAllGroups() {
	return allGroups;
    }

    public List<Group<String>> getRootGroups() {
	return rootGroups;
    }

    private int numberOfLeadingDashes(String line) {
	for (int i = 0; i < line.length(); i++) {
	    if (line.charAt(i) != '-') {
		return i;
	    }
	}
	return 0;
    }

    private void init() {
	Group<String>[] lastGroups = new Group[maxDepth];

	try {
	    BufferedReader br = new BufferedReader(dashTreeReader);
	    String line;
	    while ((line = br.readLine()) != null) {
		line = line.trim();
		// comments
		if (line.isEmpty() || line.startsWith("//")
			|| line.startsWith("#")) {
		    continue;
		}

		int noDashes = numberOfLeadingDashes(line);
		// group
		if (!dashesEndWithPlus(line)) {
		    Group<String> g = new Group(line.substring(noDashes).trim());
		    allGroups.add(g);
		    lastGroups[noDashes] = g;
		    if (noDashes == 0) {
			rootGroups.add(g);
		    } else {
			lastGroups[noDashes - 1].addSubset(g);
		    }
		}
		// element?
		else {
		    lastGroups[noDashes].addElement(line
			    .substring(noDashes + 1).trim());
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public Group<String> getGroup(String string) {
	for (Group<String> g : allGroups) {
	    if (g.getId().equals(string)) {
		return g;
	    }
	}
	return null;
    }
}
