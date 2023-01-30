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
 * Created on 26.03.2007
 *
 */
package org.vikamine.kernel.subgroup.analysis.causality;

import org.vikamine.kernel.subgroup.SG;

/**
 * An acausal {@link CausalSGNode} represents a subgroup that has no causes (no
 * factors potentially causing the subgroup).
 * 
 * @author atzmueller
 */
public class CausalACausalSGNode extends CausalSGNode {

    protected CausalACausalSGNode(SG sg) {
	super(sg);
    }

}
