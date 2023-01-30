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

import java.util.ArrayList;

import org.vikamine.kernel.data.discretization.ChiMergeDiscretizer;
import org.vikamine.kernel.data.discretization.DiscretizationMethod;
import org.vikamine.kernel.data.discretization.EntropyDiscretizer;
import org.vikamine.kernel.data.discretization.FayyadIraniDiscretizer;
import org.vikamine.kernel.data.discretization.KMeansDiscretizer;
import org.vikamine.kernel.data.discretization.SoftMetaDiscretizer;
import org.vikamine.kernel.data.discretization.The345RuleDiscretizer;
import org.vikamine.kernel.data.discretization.ZetaDiscretizer;

public class MAttribute {

    private static final String ENTROPY = "entropy";
    private static final String FAYYAD = "fayyad";
    private static final String CHIMERGE = "chimerge";
    private static final String KMEANS = "k-means";
    private static final String THE345RULE = "3-4-5-rule";
    private static final String ZETA = "zeta";
    private static final String SOFTEN = "soften";

    private ArrayList<MValue> values;
    private final String name;
    private DiscretizationMethod discretizationMethod;

    public MAttribute(String name) {
	this.values = new ArrayList<MValue>();
	this.name = name;
    }

    public MAttribute(String name, String discretizationMethod) {
	this.values = new ArrayList<MValue>();
	this.name = name;
	String[] method = discretizationMethod.split(";");
	String[] type = method[0].split("=");
	final String method0 = type[0].trim();
	DiscretizationMethod disMethod = null;
	if (method0.equalsIgnoreCase(FAYYAD)) {
	    disMethod = new FayyadIraniDiscretizer();
	} else if (method0.equalsIgnoreCase(CHIMERGE)) {
	    disMethod = new ChiMergeDiscretizer(method);
	} else if (method0.equalsIgnoreCase(KMEANS)) {
	    disMethod = new KMeansDiscretizer(method);
	} else if (method0.equalsIgnoreCase(THE345RULE)) {
	    disMethod = new The345RuleDiscretizer(method);
	} else if (method0.equalsIgnoreCase(ZETA)) {
	    disMethod = new ZetaDiscretizer(method);
	} else if (method0.equalsIgnoreCase(ENTROPY)) {
	    disMethod = new EntropyDiscretizer(method);
	}
	if (disMethod == null)
	    return;
	if (type.length > 1 && type[1].contains(SOFTEN))
	    this.discretizationMethod = new SoftMetaDiscretizer(method,
		    disMethod);
	else
	    this.discretizationMethod = disMethod;
    }

    public void addValue(MValue value) {
	this.values.add(value);
    }

    public ArrayList<MValue> getValues() {
	return values;
    }

    public void setValues(ArrayList<MValue> values) {
	this.values = values;
    }

    public String getName() {
	return name;
    }

    public int valuesCount() {
	return this.values.size();
    }

    @Override
    public String toString() {
	return "Attribute: " + this.name + " ,Values: " + this.values;
    }

    public void setDiscretizationMethod(
	    DiscretizationMethod discretizationMethod) {
	this.discretizationMethod = discretizationMethod;
    }

    public boolean hasDiscretizationMethod() {
	return discretizationMethod != null;
    }

    public DiscretizationMethod getDiscretizationMethod() {
	return discretizationMethod;
    }

}
