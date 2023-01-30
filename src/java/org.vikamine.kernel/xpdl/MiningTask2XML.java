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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFTripleStore;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.search.MiningTask;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SGTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;

/**
 * Utils class that exports a MiningTask in an XML-File. BE CAREFUL! This file
 * may NOT be feature complete for all tasks!
 * 
 * Not supported at the moment: - DataViews - restrictions on attribute
 * selectors
 * 
 * @author lemmerich
 * @date 10/2011 lemmerich
 */
public class MiningTask2XML {

    /**
     * main class is for testing purposes only
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
	// This is basically the path to the used dataset
	DatasetProvider provider = new DirectoryDatasetProvider(
		"../VKM-UCI-Test/resources/datasets/");

	MiningTaskInterpreter interpreter = new MiningTaskInterpreter(new File(
		"../VKM-UCI-Test/resources/tests/simple/credit-g_nominal.xml"),
		provider);
	MiningTask task = interpreter.getTask();

	exportMiningTask(task, new File("C:/test/task.xml"));

    }

    /**
     * Exports the given mining task to the location exportFilename
     * 
     * Be careful, as this function may NOT be feature complete (restricted
     * dataViews)
     * 
     * QualityFunctionFactories are identified by their getID() function
     * SDMethods are identified by their class name.
     * 
     * @param task
     * @param exportFilename
     * @throws IOException
     */
    public static void exportMiningTask(MiningTask task, File exportFilename)
	    throws IOException {
	Element root = new Element("miningTask");

	root.addContent(getDatasetElement(task));
	root.addContent(getTargetElement(task.getTarget()));
	root.addContent(getSearchSpaceElement(task));
	root.addContent(getQualityFunctionElement(task));
	root.addContent(getMethodElement(task));
	root.addContent(getConstraintsElement(task));

	Document doc = new Document(root);
	new XMLOutputter(Format.getPrettyFormat()).output(doc, new FileWriter(
		exportFilename));
    }

    private static Element getConstraintsElement(MiningTask task) {
	Element result = new Element("constraints");

	Element cElem = new Element("constraint");
	cElem.setAttribute("name", "maxK");
	cElem.setAttribute("value", task.getMaxSGCount() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "minQuality");
	cElem.setAttribute("value", task.getMinQualityLimit() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "maxSelectors");
	cElem.setAttribute("value", task.getMaxSGDSize() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "minSubgroupSize");
	cElem.setAttribute("value", task.getMinSubgroupSize() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "minTPSupportRelative");
	cElem.setAttribute("value", task.getMinTPSupportRelative() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "minTPSupportAbsolute");
	cElem.setAttribute("value", task.getMinTPSupportAbsolute() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "relevantSubgroupsOnly");
	cElem.setAttribute("value",
		task.isSuppressStrictlyIrrelevantSubgroups() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "weightedCovering");
	cElem.setAttribute("value", task.isWeightedCovering() + "");
	result.addContent(cElem);

	cElem = new Element("constraint");
	cElem.setAttribute("name", "ignoreDefaultValues");
	cElem.setAttribute("value", task.isIgnoreDefaultValues() + "");
	result.addContent(cElem);

	return result;
    }

    private static Element getMethodElement(MiningTask task) {
	Element qElem = new Element("method");
	qElem.setAttribute("name", task.getMethodType().getSimpleName());
	return qElem;
    }

    /**
     * 
     * @param task
     * @return
     */
    private static Element getQualityFunctionElement(MiningTask task) {
	Element qElem = new Element("qualityFunction");
	qElem.setAttribute("name", task.getQualityFunction().getName());
	return qElem;
    }

    private static Element getSearchSpaceElement(MiningTask task) {
	Element targetElem = new Element("searchSpace");
	for (Attribute att : task.getAttributes()) {
	    Element element = new Element("attribute");
	    element.setAttribute("name", att.getDescription());
	    targetElem.addContent(element);
	}
	return targetElem;
    }

    private static Element getTargetElement(SGTarget target) {
	Element targetElem = new Element("target");
	if (target instanceof SelectorTarget) {
	    SelectorTarget selTarget = (SelectorTarget) target;
	    targetElem.setAttribute("type", "boolean");
	    targetElem.addContent(getSelectorElement(selTarget.getSelector()));
	}
	if (target instanceof NumericTarget) {
	    targetElem.setAttribute("type", "numeric");
	    Element attElement = new Element("attribute");
	    attElement.setAttribute("name", target.getAttributes().iterator()
		    .next().getDescription());
	    targetElem.addContent(attElement);
	}
	return targetElem;
    }

    private static Element getSelectorElement(SGSelector selector) {
	Element selElement = new Element("attribute");
	selElement.setAttribute("name", selector.getAttribute()
		.getDescription());
	if (selector instanceof SGNominalSelector) {
	    Set<Value> values = ((SGNominalSelector) selector).getValues();
	    for (Value val : values) {
		Element includeElement = new Element("includeValue");
		includeElement.addContent(val.getDescription());
		selElement.addContent(includeElement);
	    }
	}
	return selElement;
    }

    private static Element getDatasetElement(MiningTask task) {
	Element datasetElem = new Element("dataset");
	String datasetName = (String) task
		.getOntology()
		.getTripleStore()
		.getFirstMatchingStatement(RDFTripleStore.ANY_OBJECT,
			OntologyConstants.DATASET_FILE_NAME_PROPERTY,
			RDFTripleStore.ANY_OBJECT).getObject();
	datasetElem.setAttribute("name", datasetName);

	datasetElem.addContent(new Element("restrictions"));

	return datasetElem;
    }
}
