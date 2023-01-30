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
 * Created on 11.01.2005
 *
 */
package org.vikamine.kernel.subgroup.analysis.strata;

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.selectors.SGNominalSelector;
import org.vikamine.kernel.subgroup.selectors.SGSelector;

/**
 * {@link CFStratifiedSGHolder} is used in {@link CFSGStratification} for
 * holding the parameters/statistics of a stratified subgroup {@link SG},
 * according to the stratifying (stratum) {@link SGSelector}.
 * 
 * @author atzmueller
 * 
 */
public class CFStratifiedSGHolder {
    private SG sg;
    private SGSelector stratumSelector;

    public CFStratifiedSGHolder(SG sg, SGSelector stratumSelector) {
	this.sg = sg;
	this.stratumSelector = stratumSelector;
    }

    public SG getSg() {
	return sg;
    }

    public void setSg(SG sg) {
	this.sg = sg;
    }

    public SGSelector getStratumSelector() {
	return stratumSelector;
    }

    public void setStratumSelector(SGNominalSelector stratumSelector) {
	this.stratumSelector = stratumSelector;
    }
}
