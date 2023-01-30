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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.data.discretization.DiscretizationMethod;
import org.vikamine.kernel.data.discretization.EqualFreqDiscretizer;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGDescription;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGenerator;
import org.vikamine.kernel.subgroup.target.SGTarget;

/**
 * Utility class for creating subgroups and descriptions according to various
 * options.
 * 
 * @author lemmerich
 * 
 */
public class OntologyUtils {

    public static List<SG> buildSingleFactorSGsFromSelectors(
	    List<SGSelector> selectors, SGTarget target, DataView pop) {
	return buildSGsFromDescription(
		createSGDescriptionsFromSelectors(selectors), target, pop);
    }

    public static List<SG> buildSGsFromDescription(
	    Collection<SGDescription> descriptions, SGTarget target,
	    DataView pop) {
	List<SG> result = new ArrayList<SG>();

	for (SGDescription descr : descriptions) {
	    SG sg = new SG(pop, target, descr);
	    sg.createStatistics();
	    result.add(sg);
	}
	return result;
    }

    public static List<SGDescription> createSGDescriptionsFromSelectors(
	    List<SGSelector> selectors) {
	List<SGDescription> result = new ArrayList<SGDescription>();
	for (SGSelector descr : selectors) {
	    result.add(new SGDescription(Collections.singletonList(descr)));
	}
	return result;
    }

    /**
     * creates all SGDescriptionss to the current population of given ontology
     * and given SGTarget.
     * 
     * Nominal attributes with more than maxValueCount are skipped. Numeric
     * attributes are discretized
     * 
     * @param ontology
     * @param maxValueCount
     * @return
     */
    public static List<SG> createSingleFactorSGs(Ontology ontology,
	    SGTarget target, int maxValueCount, boolean includeNumeric) {
	if (ontology != null) {
	    return buildSGsFromDescription(
		    createSingleFactorSGDescriptions(ontology, maxValueCount,
			    includeNumeric), target, ontology.getDataView());
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    /**
     * creates all SGDescriptions to the current population of given ontology
     * and given SGTarget, but only the first maxSGCount. (the resulting list
     * has at Most maxSGCount entries.
     * 
     * Nominal attributes with more than maxValueCount are skipped. Numeric
     * attributes are discretized.
     * 
     * Attributes used in the target are ignored.
     * 
     * @param ontology
     * @param maxValueCount
     * @return
     */
    public static List<SG> createSingleFactorSGs(Ontology ontology,
	    SGTarget target, int maxValueCount, boolean includeNumeric,
	    int maxSGCount) {
	if (ontology != null) {
	    List<SG> result = new ArrayList<SG>();
	    List<SGDescription> descriptions = createSingleFactorSGDescriptions(
		    ontology, maxValueCount, includeNumeric);
	    for (SGDescription descr : descriptions) {
		if (target == null
			|| !target.getAttributes().contains(
				descr.getSelectors().get(0).getAttribute())) {
		    SG sg = new SG(ontology.getDataView(), target, descr);
		    sg.createStatistics();
		    result.add(sg);
		    if (result.size() >= maxSGCount) {
			break;
		    }
		}
	    }
	    return result;

	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    /**
     * creates all SGDescriptionss to the current population of given ontology
     * 
     * Nominal attributes with more than maxValueCount are skipped. Numeric
     * attributes are discretized
     * 
     * @param ontology
     * @param maxValueCount
     * @return
     */
    public static List<SGDescription> createSingleFactorSGDescriptions(
	    Ontology ontology, int maxValueCount, boolean includeNumeric) {
	List<SGDescription> result = new ArrayList<SGDescription>();
	if (ontology == null) {
	    return result;
	}

	Set<Attribute> allAttributesWithoutID = ontology.getAttributes();

	for (Attribute att : allAttributesWithoutID) {
	    if (att.isNominal()) {
		// skip nominalValues with more than 10 Attributes
		if (((NominalAttribute) att).getValuesCount() > maxValueCount) {
		    continue;
		}
		for (Value v : ((NominalAttribute) att)) {
		    SGDescription sgDescr = new SGDescription();
		    sgDescr.add(new DefaultSGSelector(att, v));
		    result.add(sgDescr);
		}
	    } else if (att.isNumeric()) {
		if (includeNumeric) {
		    NumericAttribute numAtt = (NumericAttribute) att;
		    DiscretizationMethod discMethod = new EqualFreqDiscretizer(
			    ontology.getDataView(), numAtt, 5);
		    Collection<SGSelector> selectors = new SGSelectorGenerator.SimpleNumericSelectorGenerator(
			    discMethod).getSelectors(numAtt,
			    ontology.getDataView());
		    for (SGSelector sel : selectors) {
			SGDescription sgDescr = new SGDescription();
			sgDescr.add(sel);
			result.add(sgDescr);
		    }
		}
	    }
	}
	return result;
    }

    /**
     * creates all SGDescriptionss to the current population of given ontology
     * and given SGTarget.
     * 
     * Nominal attributes with more than maxValueCount are skipped. Numeric
     * attributes are discretized
     * 
     * @param ontology
     * @param maxValueCount
     * @return
     */
    public static List<SG> createSingleFactorSGs(Ontology onto,
	    Iterable<Attribute> attributes, SGTarget target, int maxValueCount,
	    boolean includeNumeric) {
	if (onto != null) {
	    return buildSGsFromDescription(
		    createSingleFactorSGDescriptions(onto, attributes,
			    maxValueCount, includeNumeric), target,
		    onto.getDataView());
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    /**
     * creates all SGDescriptionss to the current population of given ontology
     * 
     * Nominal attributes with more than maxValueCount are skipped. Numeric
     * attributes are discretized
     * 
     * @param ontology
     * @param maxValueCount
     * @return
     */
    public static List<SGDescription> createSingleFactorSGDescriptions(
	    Ontology onto, Iterable<Attribute> attributes, int maxValueCount,
	    boolean includeNumeric) {
	if (onto == null) {
	    return Collections.EMPTY_LIST;
	}

	List<SGDescription> result = new ArrayList<SGDescription>();

	for (Attribute att : attributes) {
	    if (att.isNominal()) {
		// skip nominalValues with more than 10 Attributes
		if (((NominalAttribute) att).getValuesCount() > maxValueCount) {
		    continue;
		}
		for (Value v : ((NominalAttribute) att)) {
		    SGDescription sgDescr = new SGDescription();
		    sgDescr.add(new DefaultSGSelector(att, v));
		    result.add(sgDescr);
		}
	    } else if (att.isNumeric()) {
		if (includeNumeric) {
		    NumericAttribute numAtt = (NumericAttribute) att;
		    DiscretizationMethod discMethod = new EqualFreqDiscretizer(
			    onto.getDataView(), numAtt, 5);
		    Collection<SGSelector> selectors = new SGSelectorGenerator.SimpleNumericSelectorGenerator(
			    discMethod)
			    .getSelectors(numAtt, onto.getDataView());
		    for (SGSelector sel : selectors) {
			SGDescription sgDescr = new SGDescription();
			sgDescr.add(sel);
			result.add(sgDescr);
		    }
		}
	    }
	}
	return result;
    }

    /**
     * Creates a SGDescription for each Value of the attribute. for
     * NumericAttributes Equal Frequency discretization is used instead.
     */
    public static List<SGDescription> createSGDescriptionsForAttribute(
	    Ontology onto, Attribute att) {
	List<SGDescription> result = new ArrayList<SGDescription>();
	if (att.isNominal()) {
	    for (Value v : ((NominalAttribute) att)) {
		SGDescription sgDescr = new SGDescription();
		sgDescr.add(new DefaultSGSelector(att, v));
		result.add(sgDescr);
	    }
	} else if (att.isNumeric()) {
	    NumericAttribute numAtt = (NumericAttribute) att;
	    DiscretizationMethod discMethod = new EqualFreqDiscretizer(
		    onto.getDataView(), numAtt, 5);
	    Collection<SGSelector> selectors = new SGSelectorGenerator.SimpleNumericSelectorGenerator(
		    discMethod).getSelectors(numAtt, onto.getDataView());
	    for (SGSelector sel : selectors) {
		SGDescription sgDescr = new SGDescription();
		sgDescr.add(sel);
		result.add(sgDescr);
	    }
	}
	return result;
    }

}
