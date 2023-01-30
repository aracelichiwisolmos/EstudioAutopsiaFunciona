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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.quality.IQualityFunction;
import org.vikamine.kernel.subgroup.quality.functions.QualityFunctionRegistry;
import org.vikamine.kernel.subgroup.search.AbstractMiningTask;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGenerator;
import org.vikamine.kernel.subgroup.selectors.SGSelectorGeneratorFactory;
import org.vikamine.kernel.subgroup.selectors.SelectorGeneratorUtils;
import org.vikamine.kernel.subgroup.target.NumericTarget;
import org.vikamine.kernel.subgroup.target.SelectorTarget;
import org.vikamine.kernel.util.FilterUtils;
import org.vikamine.kernel.util.Filters;

/**
 * Allows declarative specification and execution of a subgroup discovery task
 * 
 * @author Manuel, atzmueller
 * 
 */
public abstract class AbstractMiningTaskInterpreter {

    protected XMLData doc;
    protected AbstractMiningTask task;

    protected AbstractMiningTaskInterpreter(XMLData doc, AbstractMiningTask task) {
	super();
	this.doc = doc;
	this.task = task;
    }

    protected AbstractMiningTaskInterpreter(Document doc,
	    DatasetProvider provider, AbstractMiningTask task) {
	this(new XMLData(doc), provider, task);
    }

    protected AbstractMiningTaskInterpreter(File miningTask,
	    DatasetProvider provider, AbstractMiningTask task)
	    throws IOException {
	this(new XMLData(miningTask), provider, task);
    }

    protected AbstractMiningTaskInterpreter(String miningTask,
	    DatasetProvider provider, AbstractMiningTask task)
	    throws UnsupportedEncodingException {
	this(
		new XMLData(new ByteArrayInputStream(
			miningTask.getBytes("UTF-8"))), provider, task);
    }

    protected AbstractMiningTaskInterpreter(XMLData data,
	    DatasetProvider provider, AbstractMiningTask task) {
	this.doc = data;
	this.task = task;
	if (provider.providesDataset(this.doc.getDataset().getName())) {
	    task.setOntology(provider.getDataset(this.doc.getDataset()
		    .getName()));
	} else
	    throw new IllegalArgumentException("data file does not exist: "
		    + this.doc.getDataset().getName());
	this.init();

    }

    public AbstractMiningTask getTask() {
	return task;
    }

    protected void init() {
	this.initPopulation();
	this.initTarget();
	this.task.setMethodType(this.doc.getMethod().getName());
	this.initInitialSubgroup();
	this.initConstraints();
	this.initSearchSpace();
	this.initQualityFunction();
    }

    protected void initConstraints() {
	// Set default;
	task.setMaxSGCount(30);
	task.setMinSubgroupSize(0);
	for (IConstraint constr : doc.getMConstraints().getConstraints()) {
	    EConstraintTyp typ = constr.getName();
	    setConstraintForType(constr, typ);
	}

    }

    private void initInitialSubgroup() {
	DefaultSGSelector sel = null;
	Ontology ont = task.getOntology();
	SG sg = new SG(ont.getDataView(), task.getTarget());
	if (doc.getInitialSubgroup() != null) {
	    for (MAttribute a : doc.getInitialSubgroup().getOp()
		    .getAttributes()) {
		Set<Value> values = new HashSet<Value>();
		if (ont.getDataset().getAttribute(a.getName()) == null) {
		    throw new XMLException("Attribute " + a.getName()
			    + " does not exist");
		}
		for (MValue k : a.getValues()) {
		    if (k.getTyp().equals(ETyp.include)) {
			Value val = AttributeValueHelper.getAttributeValue(ont
				.getDataset().getAttribute(a.getName()), k
				.getValue());
			if (val.getDescription().equals(k.getValue())) {
			    values.add(val);
			}
		    }
		}
		sel = new DefaultSGSelector(ont.getDataset().getAttribute(
			a.getName()), values);
		sg.getSGDescription().add(sel);
	    }
	}
	task.setInitialSG(sg);
    }

    private void initPopulation() {
	DefaultSGSelector sel = null;
	List<SGSelector> selectors = new ArrayList<SGSelector>();
	for (MAttribute a : doc.getDataset().getRes().getAttributes()) {
	    Set<Value> values = new HashSet<Value>();
	    Ontology ont = task.getOntology();
	    if (ont.getDataset().getAttribute(a.getName()) == null) {
		throw new XMLException("Attribute " + a.getName()
			+ " doesn't exist");
	    }
	    for (MValue k : a.getValues()) {
		if (k.getTyp().equals(ETyp.include)) {
		    Value val = AttributeValueHelper.getAttributeValue(ont
			    .getDataset().getAttribute(a.getName()), k
			    .getValue());
		    if (val.getDescription().equals(k.getValue())) {
			values.add(val);
		    }
		}
	    }
	    sel = new DefaultSGSelector(ont.getDataset().getAttribute(
		    a.getName()), values);
	    selectors.add(sel);
	}
	task.getOntology().refinePopulation(selectors);
    }

    private void initQualityFunction() {
	String name = this.doc.getQualityFunction().getName();
	for (IQualityFunction function : QualityFunctionRegistry.getInstance()
		.getQualityFunctions()) {
	    if (name.equals(function.getName())
		    || name.equals(function.getClass().getSimpleName())) {
		task.setQualityFunction(function.clone());
		break;
	    }
	}
	if (task.getQualityFunction() == null) {
	    throw new IllegalArgumentException("Quality Function not found: "
		    + name);
	}
	task.setInvertQualityFunction(this.doc.getQualityFunction().isInvert());
    }

    protected void initSearchSpace() {
	List<SGSelector> searchSpace = new ArrayList<SGSelector>();
	Element searchSpaceElem = doc.getDoc().getRootElement()
		.getChild("searchSpace");

	// get the generator
	String generatorAttributeStr = searchSpaceElem
		.getAttributeValue("generator");
	// default
	SGSelectorGenerator generator = SGSelectorGeneratorFactory
		.createSimpleValueGenerator(task.isIgnoreDefaultValues());
	if (generatorAttributeStr != null) {
	    generator = getGenerator(generatorAttributeStr);
	}

	// useAll
	if (doc.getSearchSpace().isUseAll()) {
	    searchSpace.addAll(SelectorGeneratorUtils.generateSelectors(
		    generator, task.getOntology().getAttributes(), task
			    .getOntology().getDataView()));

	    // useAllNominal
	} else if (doc.getSearchSpace().isUseAllNominal()) {
	    Set<Attribute> attributes = task.getOntology().getAttributes();
	    FilterUtils.applyFilter(Filters.NOMINAL_ATTRIBUTE_FILTER,
		    attributes);
	    searchSpace.addAll(SelectorGeneratorUtils.generateSelectors(
		    generator, attributes, task.getOntology().getDataView()));
	} else {
	    for (Element attrElem : (List<Element>) searchSpaceElem
		    .getChildren("attribute")) {
		String attName = attrElem.getAttributeValue("name");
		Attribute att = task.getOntology().getAttribute(attName);
		if (att == null
			|| task.getTarget().getAttributes().contains(att)) {
		    continue;
		}

		generatorAttributeStr = searchSpaceElem
			.getAttributeValue("generator");
		SGSelectorGenerator generatorForThisAttribute = generator;
		if (generatorAttributeStr != null) {
		    generatorForThisAttribute = getGenerator(generatorAttributeStr);
		}
		searchSpace.addAll(generatorForThisAttribute.getSelectors(att,
			task.getOntology().getDataView()));
	    }
	}

	task.setSearchSpace(searchSpace);
    }

    private SGSelectorGenerator getGenerator(String generatorAttributeStr) {
	if (generatorAttributeStr.equals("simplevaluegenerator")) {
	    return SGSelectorGeneratorFactory.createSimpleValueGenerator(task
		    .isIgnoreDefaultValues());
	}
	// TODO: add more generators here
	return null;
    }

    private void initTarget() {
	DefaultSGSelector sel = null;
	String targetType = doc.getTarget().getType();
	MAttribute mAttribute = doc.getTarget().getAttribute();
	IDataRecordSet drs = task.getOntology().getDataset();
	if (drs.getAttribute(mAttribute.getName()) == null) {
	    throw new XMLException("Attribute " + mAttribute.getName()
		    + " doesn't exist");
	}
	Attribute attribute = drs.getAttribute(mAttribute.getName());
	for (MValue k : mAttribute.getValues()) {
	    Value val = AttributeValueHelper.getAttributeValue(attribute,
		    k.getValue());
	    if (val.getDescription().equals(k.getValue())) {
		sel = new DefaultSGSelector(attribute, val);
	    }
	}
	if (targetType.equals("boolean"))
	    task.setTarget(new SelectorTarget(sel));
	else {
	    Attribute att = drs.getAttribute(doc.getTarget().getAttribute()
		    .getName());
	    if (att.isNumeric()) {
		NumericAttribute nAtt = (NumericAttribute) att;
		task.setTarget(new NumericTarget(nAtt));
	    } else {
		throw new IllegalStateException("not a numeric attribute!");
	    }
	}
    }

    protected abstract void setConstraintForMaxSelectors(IConstraint constr,
	    EConstraintTyp type);

    protected final void setConstraintForType(IConstraint constr,
	    EConstraintTyp type) {
	switch (type) {
	case maxK:
	    task.setMaxSGCount(((Double) constr.getValue()).intValue());
	    break;
	case relevantSubgroupsOnly:
	    task.setSuppressStrictlyIrrelevantSubgroups((Boolean) constr
		    .getValue());
	    break;
	case minTPSupportAbsolute:
	    task.setMinTPSupportAbsolute((Double) constr.getValue());
	    break;
	case minQuality:
	    task.setMinQualityLimit((Double) constr.getValue());
	    break;
	case minSubgroupSize:
	    task.setMinSubgroupSize((Double) constr.getValue());
	    break;
	case minTPSupportRelative:
	    task.setMinTPSupportRelative((Double) constr.getValue());
	    break;
	case weightedCovering:
	    task.setWeightedCovering((Boolean) constr.getValue());
	    break;
	case maxSelectors:
	    setConstraintForMaxSelectors(constr, type);
	    break;
	case ignoreDefaultValues:
	    task.setIgnoreDefaultValues((Boolean) constr.getValue());
	}
    }
}
