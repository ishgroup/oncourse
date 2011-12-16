package solr2155.solr.search;
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


import org.apache.lucene.spatial.DistanceUtils;
import solr2155.lucene.spatial.geohash.GeoHashUtils;
import solr2155.lucene.spatial.geohash.GridNode;
import solr2155.lucene.spatial.geometry.shape.Point2D;
import solr2155.lucene.spatial.geometry.shape.Rectangle;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.SolrInputDocument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class SpatialFilterTest extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig.xml", "schema.xml", "src/test/test-files/solr");
  }

  private void setupDocs(String fieldName) {
    clearIndex();
    assertU(adoc("id", "1", fieldName, "32.7693246, -79.9289094"));
    assertU(adoc("id", "2", fieldName, "33.7693246, -80.9289094"));
    assertU(adoc("id", "3", fieldName, "-32.7693246, 50.9289094"));
    assertU(adoc("id", "4", fieldName, "-50.7693246, 60.9289094"));
    assertU(adoc("id", "5", fieldName, "0,0"));
    assertU(adoc("id", "6", fieldName, "0.1,0.1"));
    assertU(adoc("id", "7", fieldName, "-0.1,-0.1"));
    assertU(adoc("id", "8", fieldName, "0,179.9"));
    assertU(adoc("id", "9", fieldName, "0,-179.9"));
    assertU(adoc("id", "10", fieldName, "89.9,50"));
    assertU(adoc("id", "11", fieldName, "89.9,-130"));
    assertU(adoc("id", "12", fieldName, "-89.9,50"));
    assertU(adoc("id", "13", fieldName, "-89.9,-130"));
    assertU(commit());
  }

  private void setupGridDocs(String fieldName) {
    clearIndex();
    //add a document for each point in a grid every ten degrees between lon -175 to +175, and lat -85 to 85.
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(18*2*9*2);
    for(int x = -175; x <= 180; x += 10) {//lon
      for(int y = -85; y <= 90; y += 10) {//lay
        assertU(adoc("id", ""+genGridDocId(y, x), fieldName, y + "," + x));
      }
    }
    assertU(commit());
  }

  private static int genGridDocId(int lat, int lon) {
    return lat * 1000 + lon;
  }

  @Test
  public void testPoints() throws Exception {
    String fieldName = "home";
    setupDocs(fieldName);
    //Try some edge cases
    checkHits(fieldName, "1,1", 100, 5, 3, 4, 5, 6, 7);
    checkHits(fieldName, "0,179.8", 200, 5, 3, 4, 8, 10, 12);
    checkHits(fieldName, "89.8, 50", 200, 9);
    //try some normal cases
    checkHits(fieldName, "33.0,-80.0", 300, 12);
    //large distance
    checkHits(fieldName, "33.0,-80.0", 5000, 13);
  }

  @Test
  public void testGeoHashMultiPoint() {
    String fieldName = "home_gh";

    clearIndex();
    assertU(adoc("id", "1",
            fieldName, "50, 10",
            fieldName, "20, 40"
            ));
    assertU(commit());

    //latitude of 2nd point with longitude of 2nd point should NOT match
    checkHits(fieldName, "20,10", 200, 0);
  }

  @Test
  public void testLatLonType() throws Exception {
    testSpatialType("home_ll");
  }

  @Test
  public void testGeoHashType() throws Exception {
    testSpatialType("home_gh");
  }

  public void testSpatialType(String fieldName) throws Exception {
    setupDocs(fieldName);
    //Try some edge cases
    checkHits(fieldName, "1,1", 175, 3, 5, 6, 7);
    checkHits(fieldName, "0,179.8", 200, 2, 8, 9);
    checkHits(fieldName, "89.8, 50", 200, 2, 10, 11);//this goes over the north pole
    checkHits(fieldName, "-89.8, 50", 200, 2, 12, 13);//this goes over the south pole
    //try some normal cases
    checkHits(fieldName, "33.0,-80.0", 300, 2);
    //large distance
    checkHits(fieldName, "1,1", 5000, 3, 5, 6, 7);
    //Because we are generating a box based on the west/east longitudes and the south/north latitudes, which then
    //translates to a range query, which is slightly more inclusive.  Thus, even though 0.0 is 15.725 kms away,
    //it will be included, b/c of the box calculation.
    checkHits(fieldName, false, "0.1,0.1", 15, 2, 5, 6);
   //try some more
    clearIndex();
    assertU(adoc("id", "14", fieldName, "0,5"));
    assertU(adoc("id", "15", fieldName, "0,15"));
    //3000KM from 0,0, see http://www.movable-type.co.uk/scripts/latlong.html
    assertU(adoc("id", "16", fieldName, "18.71111,19.79750"));
    assertU(adoc("id", "17", fieldName, "44.043900,-95.436643"));
    assertU(commit());

    checkHits(fieldName, "0,0", 1000, 1, 14);
    checkHits(fieldName, "0,0", 2000, 2, 14, 15);
    checkHits(fieldName, false, "0,0", 3000, 3, 14, 15, 16);
    checkHits(fieldName, "0,0", 3001, 3, 14, 15, 16);
    checkHits(fieldName, "0,0", 3000.1, 3, 14, 15, 16);

    //really fine grained distance and reflects some of the vagaries of how we are calculating the box
    checkHits(fieldName, "43.517030,-96.789603", 109, 0);

    // falls outside of the real distance, but inside the bounding box   
    checkHits(fieldName, true, "43.517030,-96.789603", 110, 0);
    checkHits(fieldName, false, "43.517030,-96.789603", 110, 1, 17);
  }

  private void checkHits(String fieldName, String pt, double distance, int count, int ... docIds) {
    checkHits(fieldName, true, pt, distance, count, docIds);
  }

  private void checkHits(String fieldName, boolean exact, String pt, double distance, int count, int ... docIds) {
    String[] tests = makeTestXPathsFromDocIds(count, docIds);

    String method = exact ? "geofilt" : "bbox";

    assertQ(req("fl", "id", "q","*:*", "rows", "1000", "fq", "{!"+method+" sfield=" +fieldName +"}",
              "pt", pt, "d", String.valueOf(distance)),
              tests);
  }

  private static String[] makeTestXPathsFromDocIds(int count, int[] docIds) {
    String [] tests = new String[docIds != null && docIds.length > 0 ? docIds.length + 1 : 1];
    tests[0] = "*[count(//doc)=" + count + "]";
    if (docIds != null && docIds.length > 0) {
      int i = 1;
      for (int docId : docIds) {
        tests[i++] = "//result/doc/int[@name='id'][.='" + docId + "']";
      }
    }
    return tests;
  }

  //Needs to be <= what's used in the test schema.xml; best to use the same
  final int PRECISION = GridNode.GridReferenceSystem.getMaxPrecision();
  final double RADIUS = DistanceUtils.EARTH_MEAN_RADIUS_KM;

  @Test
  public void randomTest() {
    final String fieldName = "home_gh";

    //1. Iterate test with the cluster at some worldly point of interest
    Point2D[] clusterCenters = new Point2D[]{new Point2D(0,0), new Point2D(0,90),new Point2D(0,-90)};
    for (Point2D clusterCenter : clusterCenters) {
      //2. Iterate on size of cluster (a really small one and a large one)
      String hashCenter = GeoHashUtils.encode(clusterCenter.getY(), clusterCenter.getX(),PRECISION);
      //calculate the number of degrees in the smallest grid box size (use for both lat & lon)
      String smallBox = hashCenter.substring(0,hashCenter.length()-1);//chop off leaf precision
      Rectangle clusterDims = GeoHashUtils.decodeBoundary(smallBox);
      double smallDegrees = Math.max(clusterDims.getMaxX()-clusterDims.getMinX(),clusterDims.getMaxY()-clusterDims.getMinY());
      assert smallDegrees < 1;
      double largeDegrees = 20d;//good large size; don't use >=45 for this test code to work
      double[] sideDegrees = {largeDegrees,smallDegrees};
      for (double sideDegree : sideDegrees) {
        //3. Index random points in this cluster box
        clearIndex();
        List<Point2D> points = new ArrayList<Point2D>();
        for(int i = 0; i < 20; i++) {
          double x = random.nextDouble()*sideDegree - sideDegree/2 + clusterCenter.getX();
          double y = random.nextDouble()*sideDegree - sideDegree/2 + clusterCenter.getY();
          final Point2D pt = normPoint(x, y);
          points.add(pt);
          assertU(adoc("id", ""+i, fieldName, pt.getY()+","+pt.getX()));
        }
        assertU(commit());

        //3. Use 4 query centers. Each is radially out from each corner of cluster box by twice distance to box edge.
        for(double qcXoff : new double[]{sideDegree,-sideDegree}) {//query-center X offset from cluster center
          for(double qcYoff : new double[]{sideDegree,-sideDegree}) {//query-center Y offset from cluster center
            Point2D queryCenter = normPoint(qcXoff+clusterCenter.getX(),
                   qcYoff+clusterCenter.getY());
            double[] distRange = calcDistRange(queryCenter,clusterCenter,sideDegree);
            //4.1 query a small box getting nothing
            final String queryCenterStr = queryCenter.getY() + "," + queryCenter.getX();
            checkHits(fieldName, queryCenterStr,distRange[0]*0.99,0);
            //4.2 Query a large box enclosing the cluster, getting everything
            checkHits(fieldName, queryCenterStr, distRange[1]*1.01, points.size());
            //4.3 Query a medium box getting some (calculate the correct solution and verify)
            double queryDist = distRange[0] + (distRange[1]-distRange[0])/2;//average
            
            //Find matching points.  Put into int[] of doc ids which is the same thing as the index into points list.
            int[] ids = new int[points.size()];
            int ids_sz = 0;
            for (int i = 0; i < points.size(); i++) {
              Point2D point = points.get(i);
              if (calcDist(queryCenter,point) <= queryDist)
                ids[ids_sz++] = i;
            }
            ids = Arrays.copyOf(ids,ids_sz);
            //assert ids_sz > 0 (can't because randomness keeps us from being able to)

            checkHits(fieldName, queryCenterStr, queryDist, ids.length, ids);
          }
        }

      }//for sideDegree

    }//for clusterCenter

  }

  private double[] calcDistRange(Point2D startPoint, Point2D targetCenter, double targetSideDegrees) {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for(double xLen : new double[]{targetSideDegrees,-targetSideDegrees}) {
      for(double yLen : new double[]{targetSideDegrees,-targetSideDegrees}) {
        Point2D p2 = normPoint(targetCenter.getX()+xLen/2,targetCenter.getY()+yLen/2);
        double d = calcDist(startPoint, p2);
        min = Math.min(min,d);
        max = Math.max(max,d);
      }
    }
    return new double[]{min,max};
  }

  private double calcDist(Point2D p1, Point2D p2) {
    return DistanceUtils.haversine(Math.toRadians(p1.getY()), Math.toRadians(p1.getX()),
            Math.toRadians(p2.getY()), Math.toRadians(p2.getX()), RADIUS);
  }

  /** Normalize x & y (put in lon-lat ranges) & ensure geohash round-trip for given precision. */
  private Point2D normPoint(double x, double y) {
    //put x,y as degrees into double[] as radians
    double[] latLon = {y*DistanceUtils.DEGREES_TO_RADIANS,x*DistanceUtils.DEGREES_TO_RADIANS};
    DistanceUtils.normLat(latLon);
    DistanceUtils.normLng(latLon);
    double x2 = latLon[1]*DistanceUtils.RADIANS_TO_DEGREES;
    double y2 = latLon[0]*DistanceUtils.RADIANS_TO_DEGREES;
    //overwrite latLon, units is now degrees
    latLon = GeoHashUtils.decode(GeoHashUtils.encode(y2, x2, PRECISION));
    return new Point2D(latLon[1], latLon[0]);
  }

}
 /*public void testSpatialQParser() throws Exception {
    ModifiableSolrParams local = new ModifiableSolrParams();
    local.add(CommonParams.FL, "home");
    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add(SpatialParams.POINT, "5.0,5.0");
    params.add(SpatialParams.DISTANCE, "3");
    SolrQueryRequest req = new LocalSolrQueryRequest(h.getCore(), "", "", 0, 10, new HashMap());
    SpatialFilterQParserPlugin parserPlugin;
    Query query;

    parserPlugin = new SpatialFilterQParserPlugin();
    QParser parser = parserPlugin.createParser("'foo'", local, params, req);
    query = parser.parse();
    assertNotNull("Query is null", query);
    assertTrue("query is not an instanceof "
            + BooleanQuery.class,
            query instanceof BooleanQuery);
    local = new ModifiableSolrParams();
    local.add(CommonParams.FL, "x");
    params = new ModifiableSolrParams();
    params.add(SpatialParams.POINT, "5.0");
    params.add(SpatialParams.DISTANCE, "3");
    req = new LocalSolrQueryRequest(h.getCore(), "", "", 0, 10, new HashMap());
    parser = parserPlugin.createParser("'foo'", local, params, req);
    query = parser.parse();
    assertNotNull("Query is null", query);
    assertTrue(query.getClass() + " is not an instanceof "
            + NumericRangeQuery.class,
            query instanceof NumericRangeQuery);
    req.close();
  }*/
