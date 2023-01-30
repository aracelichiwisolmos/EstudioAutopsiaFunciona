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

/**
 * Builder for different {@link ParserElement}s.
 * 
 * @author Tobias Vogele
 */
public interface VariableParserElementBuilder extends ParserElementBuilder {

    /**
     * Returns true, if this text is valid for this Builder, which means, that
     * this builder can create a ParserElement from the given text.
     * 
     * @param text
     * @return
     */
    boolean isValid(String text);

    /**
     * Returns true, if any valid text of this builder begins with the given
     * substring. A text is valid for this builder, if it can create a
     * ParserElement from it.
     * <p>
     * This means, that if isValid() returns true, isValidStart() must also
     * return true.
     * 
     * @param text
     * @return
     */
    boolean isValidStart(String substring);

}
