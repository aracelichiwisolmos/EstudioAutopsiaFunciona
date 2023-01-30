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
import java.io.IOException;
import java.util.List;

import org.vikamine.kernel.subgroup.SG;
import org.vikamine.kernel.subgroup.SGFilters;
import org.vikamine.kernel.subgroup.SGSet;
import org.vikamine.kernel.subgroup.SGStatisticsBinary;
import org.vikamine.kernel.subgroup.search.MiningTask;
import org.vikamine.kernel.subgroup.search.MiningTaskFamily;
import org.vikamine.kernel.util.SGImprovementCalculator;

public class TaskRunner {

    public static void main(String[] args) throws IOException {
	File taskfile = new File(
		"../VKM-UCI-Test/resources/tasks/credit-g_complete_nominal.xml");
	DatasetProvider provider = new DirectoryDatasetProvider(
		"../VKM-UCI-Test/resources/datasets/");

	runTask(taskfile, provider);
    }

    private static final boolean VERBOSE = true;

    @SuppressWarnings("unchecked")
    public static void runTask(File taskfile, DatasetProvider datasetProvider)
	    throws IOException {
	MiningTaskFamily min = new MiningTaskFamilyInterpreter(taskfile,
		datasetProvider).getTask();
	List<MiningTask> tasksList = min.getTasks();

	for (MiningTask task : tasksList) {
	    System.out.println("********************");
	    System.out.println(taskfile.getName());
	    System.out.println("********************");

	    // perform discovery
	    SGSet result = task.performSubgroupDiscovery();

	    result = new SGFilters.SignificantImprovementFilterGlobal(0.05)
		    .filterSGs(result);
	    System.out.println(result.size());

	    if (VERBOSE) {
		for (SG sg : result.toSortedList(false)) {
		    SGStatisticsBinary statistics = (SGStatisticsBinary) sg
			    .getStatistics();

		    SGImprovementCalculator calc = new SGImprovementCalculator();
		    System.out.println(sg.getSGDescription() + " -> "
			    + statistics.getP() + "(" + "TP: "
			    + statistics.getTp() + "; FP: "
			    + statistics.getFp() + "/"
			    + statistics.getDeviation() + ")["
			    + calc.calculateMaxPValueToSubsets(sg) + "]");
		}
	    }
	}
    }
}
