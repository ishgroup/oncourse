package solr2155.solr.search.function.distance;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.spatial.DistanceUtils;
import org.apache.solr.search.function.DocValues;
import org.apache.solr.search.function.ValueSource;
import solr2155.lucene.spatial.geometry.shape.Point2D;
import solr2155.solr.search.function.GeoHashValueSource;

import java.io.IOException;
import java.util.*;
import static java.util.Collections.*;

/**
 * Haversine function for use with {@link GeoHashValueSource} in which multiple points are supported.
 */
public class HaversineMultiConstFunction extends ValueSource {

  private final double latCenter;
  private final double lonCenter;
  private final GeoHashValueSource vs;
  private final boolean asc;

  public HaversineMultiConstFunction(double latCenter, double lonCenter, GeoHashValueSource vs, boolean asc) {
    this.latCenter = latCenter;
    this.lonCenter = lonCenter;
    this.vs = vs;
    this.asc = asc;
  }

  protected String name() {
    return "geodist";
  }

  @Override
  public DocValues getValues(Map context, IndexReader reader) throws IOException {
    final GeoHashValueSource.GeoHashDocValues ghDocVals = vs.getValues(context,reader);
    final double latCenterRad = this.latCenter * DistanceUtils.DEGREES_TO_RADIANS;
    final double lonCenterRad = this.lonCenter * DistanceUtils.DEGREES_TO_RADIANS;

    return new DocValues() {
      public float floatVal(int doc) {
        return (float) doubleVal(doc);
      }

      public int intVal(int doc) {
        return (int) doubleVal(doc);
      }

      public long longVal(int doc) {
        return (long) doubleVal(doc);
      }

      public double doubleVal(int doc) {
        List<Point2D> geoList = ghDocVals.point2Ds(doc);
        if (geoList == null) {
          if (asc == false) {
            return 0.0;
          } else {
            return DistanceUtils.EARTH_MEAN_RADIUS_KM;
          }
        }
        double distance = 0.0; // this will be overlaid
        boolean firstLap = true;
        for (Point2D point : geoList) {
          double distanceNew = distance(point);
          if ((firstLap) ||
              (asc == true && distanceNew < distance) ||
              (asc == false && distanceNew > distance)) {
            distance = distanceNew;
            firstLap = false;
          }
        }
        return distance;
      }

      private double distance(Point2D point) {
        double lat = point.getY();
        double lon = point.getX();
        double latRad = lat * DistanceUtils.DEGREES_TO_RADIANS;
        double lonRad = lon * DistanceUtils.DEGREES_TO_RADIANS;
        double distanceNew = DistanceUtils.haversine(latCenterRad, lonCenterRad, latRad, lonRad, DistanceUtils.EARTH_MEAN_RADIUS_KM);
        return distanceNew;
      }

      public String strVal(int doc) {
        return Double.toString(doubleVal(doc));
      }

      @Override
      public String toString(int doc) {
        int maxPoints = 3;
        List<Point2D> geoList = ghDocVals.point2Ds(doc);
        boolean truncated = false;

        TreeMap<Double, Point2D> heap = new TreeMap<Double, Point2D>(
            !asc ? reverseOrder() : reverseOrder(reverseOrder()));
        for (Point2D p : geoList == null ? Collections.<Point2D>emptyList() : geoList) {
          heap.put(distance(p), p);
          if (!heap.isEmpty() && heap.size() > maxPoints) {
            heap.remove(heap.lastKey());
            truncated = true;
          }
        }
        return name() + '(' + dump(heap.values()) +
            (truncated ? "..." : "") +
            ',' + latCenter + ',' + lonCenter + ')';
      }

      private StringBuilder dump(Collection<Point2D> points) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Point2D p : points) {
          if (!first) {
            sb.append(' ');
          }
          sb.append(p.getY());
          sb.append(',');
          sb.append(p.getX());

          first = false;
        }
        return sb;
      }
    };
  }

  @Override
  public void createWeight(Map context, Searcher searcher) throws IOException {
    vs.createWeight(context, searcher);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof HaversineMultiConstFunction)) return false;
    HaversineMultiConstFunction other = (HaversineMultiConstFunction) o;
    return this.latCenter == other.latCenter
        && this.lonCenter == other.lonCenter
        && this.asc == other.asc && this.vs.equals(other.vs);
  }

  @Override
  public int hashCode() {
    int result = vs.hashCode();
    long temp;
    temp = Double.doubleToRawLongBits(latCenter);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToRawLongBits(lonCenter);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public String description() {
    return name() + '(' + vs.toString() + ',' + latCenter + ',' + lonCenter + ')';
  }
}
