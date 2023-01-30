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

import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.OntologyConstants;
import org.vikamine.kernel.data.RDFTripleStore;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.statistics.StatisticComponent.Coverage;
import org.vikamine.kernel.statistics.StatisticComponent.P;
import org.vikamine.kernel.statistics.StatisticComponent.P0;
import org.vikamine.kernel.statistics.StatisticComponent.Significance;
import org.vikamine.kernel.statistics.StatisticComponent.TPRate;
import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGStatistics;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.SGStatisticsNumeric;
import org.vikamine.kernel.subgroup.quality.functions.LiftQF;
import org.vikamine.kernel.subgroup.selectors.DefaultSGSelector;
import org.vikamine.kernel.subgroup.selectors.NegatedSGSelector;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

/**
 * converts SGSet to JDOM Document
 * 
 * @author Manuel
 * 
 */
public class XMLSGPatternExport {

    public static Document getXML(SGSet sgs, Ontology onto) {
	return getXML(sgs, onto, null, null);
    }

    public static Document getXML(SGSet sgs, Ontology onto, String title,
	    String creator) {
	return XMLSGPatternExport.getXML(
		sgs,
		onto,
		onto.getTripleStore()
			.getStatements(onto,
				OntologyConstants.DATASET_FILE_NAME_PROPERTY,
				RDFTripleStore.ANY_OBJECT).toString(), title,
		creator);
    }

    /**
     * writes given SGSet into a xml Document. Ontology is only needed, to
     * retrieve the dataset name, so if really necessary it /can/ be null
     * 
     * @param sgs
     * @param o
     * @return
     */
    public static Document getXML(SGSet sgs, Ontology onto, String datasetName,
	    String title, String creator) {
	if (sgs == null) {
	    throw new IllegalArgumentException("cannot write NULL SGSet!");
	}
	Element patternSet = new Element("patternSet");

	// Namespace n1 = Namespace.getNamespace("xsi",
	// "http://www.w3.org/2001/XMLSchema-instance");
	Namespace n2 = Namespace.getNamespace("dc",
		"http://purl.org/dc/elements/1.1/");

	patternSet.setNamespace(Namespace.NO_NAMESPACE);
	patternSet.addNamespaceDeclaration(n2);

	// Dublin Core
	Element dc;
	if (title != null && !title.equals("")) {
	    dc = new Element("title");
	    dc.setNamespace(n2);
	    dc.addContent(title);
	    patternSet.addContent(dc);
	}
	if (creator != null && creator.equals("")) {
	    dc = new Element("creator");
	    dc.setNamespace(n2);
	    dc.addContent(creator);
	    patternSet.addContent(dc);
	}
	dc = new Element("date");
	dc.setNamespace(n2);
	dc.addContent(GregorianCalendar.getInstance().getTime().toString());
	patternSet.addContent(dc);

	if (onto != null) {
	    patternSet.addContent(getDatasetElement(onto, datasetName));
	}

	patternSet.setAttribute("dataset", datasetName);
	for (Iterator<SG> iter = sgs.iterator(); iter.hasNext();) {
	    SG sg = iter.next();

	    Element subgroupPattern = new Element("subgroupPattern");
	    Element description = new Element("description");
	    Element operator = new Element("operator");
	    Element target = new Element("target");
	    operator.setAttribute("type", "and");
	    Element parameters = new Element("parameters");
	    // target
	    if (sg.getTarget() != null) {
		if (sg.getTarget().isBoolean())
		    target.setAttribute("type", "boolean");
		else
		    target.setAttribute("type", "numeric");
		for (Iterator<Attribute> it = sg.getTarget().getAttributes()
			.iterator(); it.hasNext();) {
		    Element att = new Element("attribute");
		    String name = (it.next()).getDescription();
		    try {
			Element include = new Element("includeValue");
			include.setText(sg.getTarget().getDescription()
				.split(name + "=")[1].replace(")", ""));
			att.addContent(include);
		    } catch (IndexOutOfBoundsException e) {
			// no includes
		    } finally {
			att.setAttribute("name", name);
			target.addContent(att);
		    }
		}
	    } else {
		target.setAttribute("type", "");
	    }
	    // Selectors
	    for (Iterator<SGSelector> it = sg.getSGDescription().iterator(); it
		    .hasNext();) {
		// TODO: handle non-nominal sgDescriptions
		SGNominalSelector sele = (SGNominalSelector) it.next();
		Element att = new Element("attribute");
		att.setAttribute("name", sele.getAttribute().getDescription());
		for (Value k : sele.getValues()) {
		    Element include = new Element("includeValue");
		    include.setText(k.getDescription());
		    att.addContent(include);
		}
		operator.addContent(att);
	    }

	    // Parameters
	    NumberFormat n = NumberFormat.getInstance();
	    n.setMaximumFractionDigits(2);
	    Element param = new Element("parameter");
	    param.setAttribute("name", "quality");
	    param.setAttribute("value",
		    String.valueOf(n.format(sg.getQuality())));
	    parameters.addContent(param);

	    param = new Element("parameter");
	    param.setAttribute("name", "populationSize");
	    param.setAttribute("value", String.valueOf((int) sg.getStatistics()
		    .getDefinedPopulationCount()));
	    parameters.addContent(param);

	    param = new Element("parameter");
	    param.setAttribute("name", "subgroupSize");
	    param.setAttribute("value",
		    String.valueOf((int) sg.getStatistics().getSubgroupSize()));
	    parameters.addContent(param);

	    if (sg.getTarget().isBoolean()) {
		param = new Element("parameter");
		param.setAttribute("name", "TP");
		param.setAttribute("value",
			String.valueOf((int) ((SGStatisticsBinary) sg
				.getStatistics()).getTp()));
		parameters.addContent(param);

		param = new Element("parameter");
		param.setAttribute("name", "FP");
		param.setAttribute("value",
			String.valueOf((int) ((SGStatisticsBinary) sg
				.getStatistics()).getFp()));
		parameters.addContent(param);

		P p = new P();
		param = new Element("parameter");
		param.setAttribute("name", "target/subgroup");
		param.setAttribute("value", p.getValue(sg.getStatistics())
			.toString());
		parameters.addContent(param);

		P0 po = new P0();
		param = new Element("parameter");
		param.setAttribute("name", "target/population");
		param.setAttribute("value", po.getValue(sg.getStatistics())
			.toString());
		parameters.addContent(param);

		TPRate t = new TPRate();
		param = new Element("parameter");
		param.setAttribute("name", "TP-Rate");
		param.setAttribute("value", t.getValue(sg.getStatistics())
			.toString());
		parameters.addContent(param);

		Coverage c = new Coverage();
		param = new Element("parameter");
		param.setAttribute("name", "coverage");
		param.setAttribute("value", c.getValue(sg.getStatistics()));
		parameters.addContent(param);

		Significance s = new Significance();
		param = new Element("parameter");
		param.setAttribute("name", "significance");
		param.setAttribute("value", s.getValue(sg.getStatistics())
			.toString());
		parameters.addContent(param);
	    }
	    n.setMaximumFractionDigits(3);
	    param = new Element("parameter");
	    param.setAttribute("name", "lift");
	    param.setAttribute("value",
		    String.valueOf(n.format(computeLift(sg.getStatistics()))));
	    parameters.addContent(param);

	    description.setContent(operator);
	    subgroupPattern.addContent(target);
	    subgroupPattern.addContent(description);
	    subgroupPattern.addContent(parameters);

	    patternSet.addContent(subgroupPattern);
	}
	Document doc = new Document(patternSet);
	return doc;
    }

    private static Object computeLift(SGStatistics stats) {
	if (stats instanceof SGStatisticsBinary) {
	    SGStatisticsBinary binary = (SGStatisticsBinary) stats;
	    return LiftQF.computeLiftBinary(binary.getP(),
		    binary.getSubgroupSize(), binary.getP0(),
		    binary.getDefinedPopulationCount());
	} else if (stats instanceof SGStatisticsNumeric) {
	    SGStatisticsNumeric numeric = (SGStatisticsNumeric) stats;
	    return LiftQF.computeLiftNumeric(numeric.getSGMean(),
		    numeric.getPopulationMean());
	} else {
	    throw new IllegalStateException("Unknown SGStatistics!" + stats);
	}
    }

    private static Element getDatasetElement(Ontology onto, String datasetName) {
	Element dataset = new Element("dataset");
	dataset.setAttribute("name", datasetName);
	DataView pop = onto.getDataView();
	if (!pop.populationRangesSelectors().isEmpty()) {
	    Element restrictions = new Element("restrictions");
	    for (SGSelector selector : pop.populationRangesSelectors()) {
		if (selector instanceof DefaultSGSelector) {
		    Attribute att = selector.getAttribute();
		    Element attrElement = new Element("attribute");
		    attrElement.setAttribute("name", att.getId());
		    Set<Value> values = ((DefaultSGSelector) selector)
			    .getValues();
		    for (Value val : values) {
			Element inclElement = new Element("includeValue");
			inclElement.setText(val.getDescription());
			attrElement.addContent(inclElement);
		    }
		    restrictions.addContent(attrElement);
		} else if (selector instanceof NegatedSGSelector) {
		    SGNominalSelector posSelector = ((NegatedSGSelector) selector)
			    .getPositiveSelector();
		    if (!(posSelector instanceof DefaultSGSelector)) {
			throw new IllegalArgumentException(
				"Unsupported DataView restriction for XML-output!");
		    }
		    Attribute att = selector.getAttribute();
		    Element attrElement = new Element("attribute");
		    attrElement.setAttribute("name", att.getId());
		    Set<Value> values = posSelector.getValues();
		    for (Value val : values) {
			Element inclElement = new Element("excludeValue");
			inclElement.setText(val.getDescription());
			attrElement.addContent(inclElement);
		    }
		    restrictions.addContent(attrElement);
		} else {
		    throw new IllegalArgumentException(
			    "Unsupported DataView restriction for XML-output!");
		}
	    }
	    dataset.addContent(restrictions);
	}

	return dataset;
    }
    /*
     * public static Document getXML(List<SGSet> sgss) { Element pattern = new
     * Element("pattern");
     * 
     * for(SGSet sgs : sgss) { Element patternSet = new Element("patternSet");
     * 
     * for (Iterator<SG> iter = sgs.iterator(); iter.hasNext();) { SG sg =
     * iter.next(); Element subgroupPattern = new Element("subgroupPattern");
     * Element description = new Element("description"); Element operator = new
     * Element("operator"); Element target = new Element("target");
     * operator.setAttribute("type", "and"); Element parameters = new
     * Element("parameters"); // target if (sg.getTarget() != null) { if
     * (sg.getTarget().isBoolean()) target.setAttribute("type", "boolean"); else
     * target.setAttribute("type", "numeric"); for (Iterator<Attribute> it =
     * sg.getTarget().getAttributes() .iterator(); it.hasNext();) { Element att
     * = new Element("attribute"); Element include = new
     * Element("includeValue"); String name = (it.next()).getDescription();
     * include.setText(sg.getTarget().getDescription().split( name +
     * "=")[1].replace(")", "")); att.addContent(include);
     * att.setAttribute("name", name); target.addContent(att); } } else {
     * target.setAttribute("type", ""); } // Selectors for (Iterator<SGSelector>
     * it = sg.getSGDescription().iterator(); it .hasNext();) { SGSelector sele
     * = it.next(); Element att = new Element("attribute");
     * att.setAttribute("name", sele.getAttribute().getDescription()); for
     * (Value k : sele.getValues()) { Element include = new
     * Element("includeValue"); include.setText(k.getDescription());
     * att.addContent(include); } operator.addContent(att); } // Parameters
     * NumberFormat n = NumberFormat.getInstance();
     * n.setMaximumFractionDigits(2); Element param = new Element("parameter");
     * param.setAttribute("name", "quality"); param.setAttribute("value",
     * String.valueOf(n .format(sg.getQuality())));
     * parameters.addContent(param);
     * 
     * param = new Element("parameter"); param.setAttribute("name",
     * "populationSize"); param.setAttribute("value", String.valueOf((int)
     * sg.getStatistics() .getDefinedPopulationCount()));
     * parameters.addContent(param);
     * 
     * param = new Element("parameter"); param.setAttribute("name",
     * "subgroupSize"); param.setAttribute("value", String.valueOf((int)
     * sg.getStatistics() .getSubgroupSize())); parameters.addContent(param);
     * 
     * param = new Element("parameter"); param.setAttribute("name", "TP");
     * param.setAttribute("value", String.valueOf((int) sg.getStatistics()
     * .getTp())); parameters.addContent(param);
     * 
     * param = new Element("parameter"); param.setAttribute("name", "FP");
     * param.setAttribute("value", String.valueOf((int) sg.getStatistics()
     * .getFp())); parameters.addContent(param);
     * 
     * P p = new P(); param = new Element("parameter");
     * param.setAttribute("name", "target/subgroup");
     * param.setAttribute("value", p.getValue(sg.getStatistics()));
     * parameters.addContent(param);
     * 
     * P0 po = new P0(); param = new Element("parameter");
     * param.setAttribute("name", "target/population");
     * param.setAttribute("value", po.getValue(sg.getStatistics()));
     * parameters.addContent(param);
     * 
     * TPRate t = new TPRate(); param = new Element("parameter");
     * param.setAttribute("name", "TP-Rate"); param.setAttribute("value",
     * t.getValue(sg.getStatistics())); parameters.addContent(param);
     * 
     * Coverage c = new Coverage(); param = new Element("parameter");
     * param.setAttribute("name", "coverage"); param.setAttribute("value",
     * c.getValue(sg.getStatistics())); parameters.addContent(param);
     * 
     * n.setMaximumFractionDigits(3); param = new Element("parameter");
     * param.setAttribute("name", "lift"); param.setAttribute("value",
     * String.valueOf(n.format(LiftQF
     * .calculateSubgroupLift(sg.getStatistics()))));
     * parameters.addContent(param);
     * 
     * Significance s = new Significance(); param = new Element("parameter");
     * param.setAttribute("name", "significance"); param.setAttribute("value",
     * s.getValue(sg.getStatistics())); parameters.addContent(param);
     * 
     * description.setContent(operator); subgroupPattern.addContent(target);
     * subgroupPattern.addContent(description);
     * subgroupPattern.addContent(parameters);
     * 
     * patternSet.addContent(subgroupPattern); } pattern.addContent(patternSet);
     * } Document doc = new Document(pattern); //if
     * (CheckXMLValid.checkValid(doc)) return doc; //else //
     * System.out.println("Datei nicht valide"); //return null; }
     */

}
