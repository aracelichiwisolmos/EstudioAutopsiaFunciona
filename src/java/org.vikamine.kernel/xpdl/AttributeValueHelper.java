package org.vikamine.kernel.xpdl;

import org.vikamine.kernel.data.Attribute;
import org.vikamine.kernel.data.NominalAttribute;
import org.vikamine.kernel.data.NumericAttribute;
import org.vikamine.kernel.data.StringAttribute;
import org.vikamine.kernel.data.Value;

public class AttributeValueHelper {

    public static Value getAttributeValue(Attribute attribute,
	    String valueString) {
	if (attribute instanceof NominalAttribute) {
	    return ((NominalAttribute) attribute)
		    .getNominalValueFromID(valueString);
	} else if (attribute instanceof StringAttribute) {
	    return ((StringAttribute) attribute)
		    .getNominalValueFromID(valueString);
	} else if (attribute instanceof NumericAttribute) {
	    double d = Double.parseDouble(valueString);
	    return ((NumericAttribute) attribute).getNumericValue(d);
	} else {
	    throw new IllegalArgumentException("Invalid valuestring "
		    + valueString + " for attribute " + attribute);
	}
    }
}
