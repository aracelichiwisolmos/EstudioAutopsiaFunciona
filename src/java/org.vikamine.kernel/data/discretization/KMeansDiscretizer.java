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

package org.vikamine.kernel.data.discretization;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import org.vikamine.kernel.data.DataRecord;
import org.vikamine.kernel.data.DataView;
import org.vikamine.kernel.data.IDataRecordSet;
import org.vikamine.kernel.data.NumericAttribute;

/**
 * Discretization method using the (1-dimensional) k-means clustering algorithm.
 * 
 * @author Hagen Schwa�
 * 
 */
public class KMeansDiscretizer extends AbstractDiscretizationMethod {

    private static final int DEF_TRIES = 50;
    private static final int DEF_SAMPLESIZE = 10000;

    private static final String NAME = "K-Means Discretizer";

    private static final String SEGCOUNT = "segcount";
    private static final String TRIES = "tries";
    private static final String SAMPLESIZE = "samplesize";

    private double[] minmax;

    private int tries;

    private int sampleSize;

    public KMeansDiscretizer() {
	tries = DEF_TRIES;
	sampleSize = DEF_SAMPLESIZE;
    }

    public KMeansDiscretizer(DataView population, NumericAttribute na,
	    int segCount) {
	tries = DEF_TRIES;
	sampleSize = DEF_SAMPLESIZE;
	setPopulation(population);
	setAttribute(na);
	setSegmentsCount(segCount);
    }

    /**
     * Constructs discretizer specifying segments count by String array.
     * 
     * Create String array by splitting following String with regex ";":
     * 
     * "k-means; segcount = 5 [; tries = 30][; samplesize = 10000]"
     * 
     * @param args
     *            String array as specified above.
     */
    public KMeansDiscretizer(String[] args) {
	this();
	for (int i = 1; i < args.length; i++) {
	    String[] arg = args[i].split("=");
	    if (arg[0].contains(SEGCOUNT)) {
		segmentsCount = Integer.parseInt(arg[1].trim());
	    } else if (arg[0].contains(TRIES)) {
		tries = Integer.parseInt(arg[1].trim());
	    } else if (arg[0].contains(SAMPLESIZE)) {
		sampleSize = Integer.parseInt(arg[1].trim());
	    }
	}
    }

    private static final class SampleList implements List<DataRecord> {

	private final DataRecord[] set;
	private final Set<DataRecord> sample;

	public SampleList(Set<DataRecord> resample) {
	    set = resample.toArray(new DataRecord[resample.size()]);
	    this.sample = resample;
	}

	@Override
	public boolean add(DataRecord arg0) {
	    return false;
	}

	@Override
	public void add(int arg0, DataRecord arg1) {
	}

	@Override
	public boolean addAll(Collection<? extends DataRecord> arg0) {
	    return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends DataRecord> arg1) {
	    return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean contains(Object arg0) {
	    return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public DataRecord get(int arg0) {
	    return set[arg0];
	}

	@Override
	public int indexOf(Object arg0) {
	    return 0;
	}

	@Override
	public boolean isEmpty() {
	    return false;
	}

	@Override
	public Iterator<DataRecord> iterator() {
	    return sample.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
	    return 0;
	}

	@Override
	public ListIterator<DataRecord> listIterator() {
	    return null;
	}

	@Override
	public ListIterator<DataRecord> listIterator(int arg0) {
	    return null;
	}

	@Override
	public boolean remove(Object arg0) {
	    return false;
	}

	@Override
	public DataRecord remove(int arg0) {
	    return null;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public DataRecord set(int arg0, DataRecord arg1) {
	    return null;
	}

	@Override
	public int size() {
	    return sample.size();
	}

	@Override
	public List<DataRecord> subList(int arg0, int arg1) {
	    return null;
	}

	@Override
	public Object[] toArray() {
	    return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
	    return null;
	}

    }

    private final class PopulationList implements List<DataRecord> {

	IDataRecordSet set;

	private PopulationList() {
	    set = population.dataset();
	}

	@Override
	public boolean add(DataRecord arg0) {
	    return false;
	}

	@Override
	public void add(int arg0, DataRecord arg1) {
	}

	@Override
	public boolean addAll(Collection<? extends DataRecord> arg0) {
	    return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends DataRecord> arg1) {
	    return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean contains(Object arg0) {
	    return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public DataRecord get(int arg0) {
	    return set.get(arg0);
	}

	@Override
	public int indexOf(Object arg0) {
	    return 0;
	}

	@Override
	public boolean isEmpty() {
	    return false;
	}

	@Override
	public Iterator<DataRecord> iterator() {
	    return population.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
	    return 0;
	}

	@Override
	public ListIterator<DataRecord> listIterator() {
	    return null;
	}

	@Override
	public ListIterator<DataRecord> listIterator(int arg0) {
	    return null;
	}

	@Override
	public boolean remove(Object arg0) {
	    return false;
	}

	@Override
	public DataRecord remove(int arg0) {
	    return null;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
	    return false;
	}

	@Override
	public DataRecord set(int arg0, DataRecord arg1) {
	    return null;
	}

	@Override
	public int size() {
	    return population.size();
	}

	@Override
	public List<DataRecord> subList(int arg0, int arg1) {
	    return null;
	}

	@Override
	public Object[] toArray() {
	    return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
	    return null;
	}

    }

    /**
     * Represents a cluster.
     * 
     * @author Hagen Schwa�
     * 
     */
    @SuppressWarnings("serial")
    private static final class Cluster extends ArrayList<Double> implements
	    Comparable<Cluster> {

	private final double center;

	private double minVal = Double.MAX_VALUE;
	private double maxVal = Double.MIN_VALUE;

	/**
	 * Creates a cluster using the specified center.
	 * 
	 * @param center
	 *            The center value to use.
	 */
	private Cluster(double center) {
	    this.center = center;
	}

	/**
	 * Creates this cluster using the center computed by the specified
	 * cluster.
	 * 
	 * @param cluster
	 *            The cluster with the center to use.
	 */
	private Cluster(Cluster cluster) {
	    center = cluster.computeCenter();
	}

	private void add(double val) {
	    if (val < minVal)
		minVal = val;
	    if (val > maxVal)
		maxVal = val;
	    super.add(val);
	}

	private double computeCenter() {
	    if (size() == 0)
		return 0;
	    Iterator<Double> iterator = iterator();
	    BigDecimal center = new BigDecimal(0);
	    while (iterator.hasNext()) {
		center = center.add(new BigDecimal(iterator.next()));
	    }
	    return center
		    .divide(new BigDecimal(size()), MathContext.DECIMAL128)
		    .doubleValue();
	}

	private double distanceTo(double val) {
	    return Math.abs(center - val);
	}

	@Override
	public int compareTo(Cluster o) {
	    return maxVal < o.minVal ? -1 : minVal > o.maxVal ? 1 : 0;
	}

	private BigDecimal cubedDistances() {
	    BigDecimal result = BigDecimal.ZERO;
	    for (Iterator<Double> it = iterator(); it.hasNext();) {
		result = result.add(new BigDecimal(Math.pow(
			Math.abs(it.next() - center), 2)));
	    }
	    return result;
	}

	@Override
	public int hashCode() {
	    return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if (getClass() != obj.getClass())
		return false;
	    Cluster other = (Cluster) obj;
	    if (Double.doubleToLongBits(center) != Double
		    .doubleToLongBits(other.center))
		return false;
	    return true;
	}

    }

    /**
     * Represents a clustering.
     * 
     * @author Hagen Schwa�
     * 
     */
    private final class Clustering {

	private final Cluster[] cluster;

	/**
	 * Use this constructor for building the initial clustering. The initial
	 * centroids are chosen randomly.
	 * 
	 * @param recordIterator
	 *            Iterator through the population.
	 */
	private Clustering(List<DataRecord> records) {
	    cluster = new Cluster[segmentsCount];
	    final Random random = new Random();
	    final int size = records.size();
	    for (int i = 0; i < segmentsCount; i++) {
		cluster[i] = new Cluster(records.get(random.nextInt(size))
			.getValue(attribute));
	    }
	    populate(records.iterator());
	}

	/**
	 * Creates a new clustering by using the centroids computed out of the
	 * specified clustering.
	 * 
	 * @param oldClustering
	 *            The clustering to use its centroids.
	 */
	private Clustering(Clustering oldClustering, List<DataRecord> records) {
	    cluster = new Cluster[segmentsCount];
	    for (int i = 0; i < segmentsCount; i++) {
		cluster[i] = new Cluster(oldClustering.cluster[i]);
	    }
	    populate(records.iterator());
	}

	/**
	 * Assign each DataRecord to the cluster with minimal distance.
	 * 
	 * @param recordIterator
	 *            Iterator through the population.
	 */
	private void populate(Iterator<DataRecord> recordIterator) {
	    while (recordIterator.hasNext()) {
		double val = recordIterator.next().getValue(attribute);
		if (Double.isNaN(val) == false) {
		    Cluster closest = cluster[0];
		    double minDist = closest.distanceTo(val);
		    for (int i = 1; i < segmentsCount; i++) {
			final double dist = cluster[i].distanceTo(val);
			if (dist < minDist) {
			    minDist = dist;
			    closest = cluster[i];
			}
		    }
		    closest.add(val);
		}
	    }
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null)
		return false;
	    Clustering other = (Clustering) obj;
	    for (int i = 0; i < cluster.length; i++) {
		if (cluster[i].equals(other.cluster[i]) == false)
		    return false;
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    return super.hashCode();
	}

	private List<Double> toList() {
	    ArrayList<Double> list = new ArrayList<Double>();
	    Arrays.sort(cluster);
	    for (int i = 1; i < segmentsCount && cluster[i].size() > 0; i++) {
		list.add((cluster[i - 1].maxVal + cluster[i].minVal) / 2);
	    }
	    return list;
	}

	private BigDecimal cubedDistances() {
	    BigDecimal result = BigDecimal.ZERO;
	    for (int i = 0; i < segmentsCount; i++) {
		result = result.add(cluster[i].cubedDistances());
	    }
	    return result;
	}
    }

    private Clustering kMeansClustering(List<DataRecord> records) {

	Clustering clustering = new Clustering(records);
	Clustering oldClustering = null;

	while (clustering.equals(oldClustering) == false) {
	    oldClustering = clustering;
	    clustering = new Clustering(oldClustering, records);
	}

	return clustering;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getCutpoints
     * ()
     */
    @Override
    public List<Double> getCutpoints() {

	if ((population == null) || (attribute == null)
		|| (population.dataset().getIndex(attribute) < 0)
		|| (population.size() < segmentsCount)) {
	    return new ArrayList<Double>();
	}

	if (minmax == null)
	    minmax = DiscretizationUtils.getMinMaxValue(population, attribute);

	List<DataRecord> records;

	if (population.size() > sampleSize)
	    records = new SampleList(DiscretizationUtils.resample(
		    population.dataset(), population.size(), sampleSize));
	else
	    records = new PopulationList();

	sortedSample = DiscretizationUtils.getSortedDataRecords(records,
		attribute, false, false);

	Clustering clustering = kMeansClustering(records);
	BigDecimal smallestCubes = clustering.cubedDistances();
	for (int i = 1; i < tries; i++) {
	    final Clustering test = kMeansClustering(records);
	    final BigDecimal testCubes = test.cubedDistances();
	    if (testCubes.compareTo(smallestCubes) == -1) {
		clustering = test;
		smallestCubes = testCubes;
	    }

	}
	return clustering.toList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.vikamine.kernel.data.discretization.DiscretizationMethod#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

}
