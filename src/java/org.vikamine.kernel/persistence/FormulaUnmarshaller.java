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
 * Created on 01.07.2004
 */
package org.vikamine.kernel.persistence;

import java.text.ParseException;
import java.util.logging.Logger;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.AttributeProvider;
import org.vikamine.kernel.data.Ontology;
import org.vikamine.kernel.data.Value;
import org.vikamine.kernel.formula.FormulaBoolean;
import org.vikamine.kernel.formula.FormulaBooleanElement;
import org.vikamine.kernel.formula.FormulaElement;
import org.vikamine.kernel.formula.FormulaNumber;
import org.vikamine.kernel.formula.FormulaNumberElement;
import org.vikamine.kernel.formula.constants.Fn;
import org.vikamine.kernel.formula.constants.FormulaAttributePrimitive;
import org.vikamine.kernel.formula.constants.FormulaAttributeValuePrimitive;
import org.vikamine.kernel.formula.constants.Fp;
import org.vikamine.kernel.formula.constants.Negatives;
import org.vikamine.kernel.formula.constants.PopulationSize;
import org.vikamine.kernel.formula.constants.Positives;
import org.vikamine.kernel.formula.constants.SubgroupSize;
import org.vikamine.kernel.formula.constants.Tn;
import org.vikamine.kernel.formula.constants.Tp;
import org.vikamine.kernel.formula.operators.AbstractAttributeArgumentTerm;
import org.vikamine.kernel.formula.operators.AbstractBooleanArgumentTerm;
import org.vikamine.kernel.formula.operators.AbstractNumberArgumentTerm;
import org.vikamine.kernel.formula.operators.AbstractTwoBooleanArgumentsTerm;
import org.vikamine.kernel.formula.operators.AbstractTwoNumberArgumentsTerm;
import org.vikamine.kernel.formula.operators.Add;
import org.vikamine.kernel.formula.operators.And;
import org.vikamine.kernel.formula.operators.Average;
import org.vikamine.kernel.formula.operators.Div;
import org.vikamine.kernel.formula.operators.EqualsNumber;
import org.vikamine.kernel.formula.operators.Greater;
import org.vikamine.kernel.formula.operators.GreaterEquals;
import org.vikamine.kernel.formula.operators.Lower;
import org.vikamine.kernel.formula.operators.LowerEquals;
import org.vikamine.kernel.formula.operators.MatchingAttributeValue;
import org.vikamine.kernel.formula.operators.Max;
import org.vikamine.kernel.formula.operators.MaxValue;
import org.vikamine.kernel.formula.operators.Min;
import org.vikamine.kernel.formula.operators.MinValue;
import org.vikamine.kernel.formula.operators.Mult;
import org.vikamine.kernel.formula.operators.Not;
import org.vikamine.kernel.formula.operators.Or;
import org.vikamine.kernel.formula.operators.Pow;
import org.vikamine.kernel.formula.operators.Sqrt;
import org.vikamine.kernel.formula.operators.Sub;
import org.vikamine.kernel.formula.operators.UnaryMinus;
import org.vikamine.kernel.formula.operators.Xor;
import org.vikamine.kernel.util.DOMAccessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Tobias Vogele
 */
public class FormulaUnmarshaller {

    private Ontology ontology;

    public FormulaUnmarshaller(Ontology ontology) {
	super();
	this.ontology = ontology;
    }

    public FormulaElement unmarshal(Element elem,
	    AttributeProvider attributeProvider) {
	String symb = elem.getAttribute("symbol");
	if ("fn".equals(symb)) {
	    return new Fn();
	} else if ("fp".equals(symb)) {
	    return new Fp();
	} else if ("negatives".equals(symb)) {
	    return new Negatives();
	} else if ("populationSize".equals(symb)) {
	    return new PopulationSize();
	} else if ("positives".equals(symb)) {
	    return new Positives();
	} else if ("subgroupSize".equals(symb)) {
	    return new SubgroupSize();
	} else if ("tn".equals(symb)) {
	    return new Tn();
	} else if ("tp".equals(symb)) {
	    return new Tp();
	} else if ("attribute".equals(symb)) {
	    return createAttributePrimitive(elem, attributeProvider);
	} else if ("attributeValue".equals(symb)) {
	    return createAttributeValuePrimitive(elem, attributeProvider);
	} else if ("number".equals(symb)) {
	    return new FormulaNumber(new Double(elem.getAttribute("value")));
	} else if ("boolean".equals(symb)) {
	    boolean flag = Boolean.valueOf(elem.getAttribute("value"))
		    .booleanValue();
	    return flag ? FormulaBoolean.TRUE : FormulaBoolean.FALSE;
	} else {
	    FormulaElement op;
	    if ("!".equals(symb)) {
		op = new Not();
	    } else if ("&".equals(symb)) {
		op = new And();
	    } else if ("|".equals(symb)) {
		op = new Or();
	    } else if ("^".equals(symb)) {
		op = new Xor();
	    } else if ("sqrt".equals(symb)) {
		op = new Sqrt();
	    } else if ("unaryMinus".equals(symb)) {
		op = new UnaryMinus();
	    } else if ("+".equals(symb)) {
		op = new Add();
	    } else if ("/".equals(symb)) {
		op = new Div();
	    } else if ("=".equals(symb)) {
		if ("true".equals(elem.getAttribute("attributeValue"))) {
		    op = new MatchingAttributeValue();
		} else {
		    op = new EqualsNumber();
		}
	    } else if (">".equals(symb)) {
		op = new Greater();
	    } else if (">=".equals(symb)) {
		op = new GreaterEquals();
	    } else if ("<".equals(symb)) {
		op = new Lower();
	    } else if ("<=".equals(symb)) {
		op = new LowerEquals();
	    } else if ("max".equals(symb)) {
		op = new Max();
	    } else if ("min".equals(symb)) {
		op = new Min();
	    } else if ("*".equals(symb)) {
		op = new Mult();
	    } else if ("**".equals(symb)) {
		op = new Pow();
	    } else if ("-".equals(symb)) {
		op = new Sub();
	    } else if ("minValue".equals(symb)) {
		op = new MinValue();
	    } else if ("maxValue".equals(symb)) {
		op = new MaxValue();
	    } else if ("average".equals(symb)) {
		op = new Average();
	    } else {
		throw new IllegalArgumentException("unknown symbol: " + symb);
	    }
	    fillOperator(op, elem, attributeProvider);
	    return op;
	}
    }

    private FormulaElement createAttributePrimitive(Element elem,
	    AttributeProvider attributeProvider) {
	String attName = elem.getAttribute("attribute");
	Attribute att = attributeProvider.getAttribute(attName);
	return new FormulaAttributePrimitive(att);
    }

    private FormulaElement createAttributeValuePrimitive(Element elem,
	    AttributeProvider attributeProvider) {
	Element valueElement = DOMAccessor.getFirstChildElement(elem,
		"attributeValue");
	Element valueContainingElement = DOMAccessor
		.getFirstChildElement(valueElement);

	try {
	    Value value = new ValuesMarshaller().parseValueNode(
		    attributeProvider, ontology, valueContainingElement);
	    return new FormulaAttributeValuePrimitive(value);
	} catch (ParseException ex) {
	    Logger.getLogger(getClass().getName()).throwing(
		    getClass().getName(), "createAttributeValuePrimitive", ex);
	    throw new IllegalStateException(
		    "Error in parsing the formula value ", ex);
	}
    }

    private void fillOperator(FormulaElement op, Element elem,
	    AttributeProvider attributeProvider) {
	if (op instanceof AbstractAttributeArgumentTerm) {
	    AbstractAttributeArgumentTerm term = (AbstractAttributeArgumentTerm) op;
	    term.setArg1((FormulaAttributePrimitive) getArgument("1", elem,
		    attributeProvider));
	}
	if (op instanceof AbstractBooleanArgumentTerm) {
	    AbstractBooleanArgumentTerm term = (AbstractBooleanArgumentTerm) op;
	    term.setArg1((FormulaBooleanElement) getArgument("1", elem,
		    attributeProvider));
	}
	if (op instanceof AbstractTwoBooleanArgumentsTerm) {
	    AbstractTwoBooleanArgumentsTerm term = (AbstractTwoBooleanArgumentsTerm) op;
	    term.setArg2((FormulaBooleanElement) getArgument("2", elem,
		    attributeProvider));
	}
	if (op instanceof AbstractNumberArgumentTerm) {
	    AbstractNumberArgumentTerm term = (AbstractNumberArgumentTerm) op;
	    term.setArg1((FormulaNumberElement) getArgument("1", elem,
		    attributeProvider));
	}
	if (op instanceof AbstractTwoNumberArgumentsTerm) {
	    AbstractTwoNumberArgumentsTerm term = (AbstractTwoNumberArgumentsTerm) op;
	    term.setArg2((FormulaNumberElement) getArgument("2", elem,
		    attributeProvider));
	}
	if (op instanceof MatchingAttributeValue) {
	    MatchingAttributeValue term = (MatchingAttributeValue) op;
	    term.setArg1((FormulaAttributePrimitive) getArgument("1", elem,
		    attributeProvider));
	    term.setArg2((FormulaAttributeValuePrimitive) getArgument("2",
		    elem, attributeProvider));
	}

    }

    private FormulaElement getArgument(String argIndex, Element elem,
	    AttributeProvider attributeProvider) {
	NodeList args = elem.getChildNodes();
	for (int i = 0; i < args.getLength(); i++) {
	    Node child = args.item(i);
	    if (child instanceof Element
		    && "formula".equals(child.getNodeName())) {
		Element childEl = (Element) child;
		String arg = childEl.getAttribute("argIndex");
		if (argIndex.equals(arg)) {
		    return unmarshal(childEl, attributeProvider);
		}
	    }
	}
	throw new RuntimeException("missing argument " + argIndex);
    }
}
