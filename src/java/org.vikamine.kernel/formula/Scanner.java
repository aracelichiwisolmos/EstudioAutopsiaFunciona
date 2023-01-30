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
 * Created on 18.03.2004
 */
package org.vikamine.kernel.formula;

import java.text.ParseException;
import java.util.List;

/**
 * Specification of a scanner, see {@link DefaultScanner}.
 * 
 * @author Tobias Vogele
 */
public interface Scanner {

    /**
     * Returns a List of ParserElements.
     * <p>
     * 
     * If a token can't be recognized, a ParseException is thrown.
     * 
     * @param text
     * @return
     */
    List scan(String text) throws ParseException;

    void registerConstantElement(ConstantParserElementBuilder builder);

    void registerVariableElement(VariableParserElementBuilder builder);

    boolean containsConstantElement(String constantString);

}
