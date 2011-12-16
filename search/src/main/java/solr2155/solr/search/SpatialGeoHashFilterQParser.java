/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solr2155.solr.search;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.spatial.DistanceUtils;
import org.apache.lucene.spatial.tier.InvalidGeoException;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.DefaultSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SolrConstantScoreQuery;
import solr2155.lucene.spatial.geohash.GeoHashPrefixFilter;
import solr2155.lucene.spatial.geometry.shape.Geometry2D;
import solr2155.lucene.spatial.geometry.shape.MultiGeom;
import solr2155.lucene.spatial.geometry.shape.PointDistanceGeom;
import solr2155.lucene.spatial.geometry.shape.Rectangle;
import solr2155.solr.schema.GeoHashField;

import java.util.Arrays;

/**
 * This query parser can parse geospatial queries based on certain arguments. The format and name of the arguments is
 * inspired from the <a href="http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2">
 * Geo extension to the OpenSearch spec</a> which in turn has some basis to <a
 * href="http://portal.opengeospatial.org/files/index.php?artifact_id=25355">OGC Simple Features Specification</a>.
 * The {@link GeoHashField} based field to index in the schema is referred to with the "sfield" parameter.  Depending on
 * the type of shape defining the search area, there are different parameters to use:
 * <ul>
 * <li>Point-radius (AKA distance) / circle: point, radius. point is "lat,lon" and radius is the distance in meters
 * forming the circle.</li>
 * <li>Bounding box: box.  "west,south,east,north" in degrees</li>
 * <li>Polygon: polygon. NOT IMPLEMENTED</li>
 * <li>WKT geometry: geometry. NOT IMPLEMENTED</li>
 * </ul>
 * NOTE: Polygon & WKT geometry require a separate module which in turn requires the LGPL licensed JTS library.
 */
public class SpatialGeoHashFilterQParser extends QParser {

  public SpatialGeoHashFilterQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    super(qstr, localParams, params, req);
  }

  //I hate having to make factory classes for such simple things like a query parser!
  public static class Plugin extends QParserPlugin {

    public static final String NAME = "geohashfilt";

    protected SolrParams defaultParams;

    public void init(NamedList args) {
      defaultParams = SolrParams.toSolrParams(args);
    }

    @Override
    public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
      localParams = new DefaultSolrParams(localParams, defaultParams);
      return new SpatialGeoHashFilterQParser(qstr, localParams, params, req);
    }
  }


  @Override
  public Query parse() throws ParseException {
    final String SFIELD = "sfield";
    String field = localParams.get(SFIELD);
    if (field == null)
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
              SFIELD + " is not properly specified"+" in local params");
    SchemaField schemaField = req.getSchema().getField(field);
    final FieldType fieldType = schemaField.getType();
    if (!(fieldType instanceof GeoHashField))
      throw new ParseException("Queried field "+field+" must be a GeoHashField but got "+fieldType);
    GeoHashField geoHashField = (GeoHashField) fieldType;

    final Geometry2D geoShape;

    String polygonArg = getParam("polygon");//ex: "5,2,33,55,22,3"
    String boxArg = getParam("box");
    String pointArg = getParam("point");
    String radiusArg = getParam("radius");//in some places we call this "distance"
    String geometryArg = getParam("geometry");

    int args = (polygonArg == null ? 0 : 1) + (boxArg == null ? 0 : 1) + (pointArg == null && radiusArg == null ? 0 : 1)
        + (geometryArg == null ? 0 : 1);
    if (args > 1)
      throw new ParseException("Conflicting geo params in "+params);
    if (polygonArg != null) {
      geoShape = parsePolygon(polygonArg);
    } else if (boxArg != null) {
      geoShape = parseBox(boxArg);
    } else if (pointArg != null) {
      geoShape = parsePointRadius(pointArg, radiusArg);
    } else if (geometryArg != null) {
      geoShape = parseGeometry(geometryArg);
    } else {
      throw new ParseException("Couldn't find a geo param in "+ params);
    }

    return new SolrConstantScoreQuery(new GeoHashPrefixFilter(field, geoShape, geoHashField.getGridReferenceSystem()));
  }

  protected Geometry2D parseGeometry(String geometryArg) throws ParseException {
    //return JtsGeom.parseGeometry(geometryArg);
    throw new UnsupportedOperationException();
  }

  protected Geometry2D parsePolygon(String polygonArg) throws ParseException {
    //return JtsGeom.parsePolygon(polygonArg);
    throw new UnsupportedOperationException();
  }

  /**
   * Follows the <a href="http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2#The_.22radius.22_parameters">
   * OpenSearch spec on polygon.</a>
   */
  protected Geometry2D parsePointRadius(String pointArg, String radiusArg) throws ParseException {
    if (pointArg == null || radiusArg == null)
      throw new ParseException("point or radius not specified");
    double[] point;
    try {
      point = DistanceUtils.parseLatitudeLongitude(pointArg);
    } catch (InvalidGeoException e) {
      throw new ParseException(e.toString());
    }
    double distanceKm = Double.parseDouble(radiusArg)/1000;//convert meters to km
    return new PointDistanceGeom(
        point[0],point[1],distanceKm,DistanceUtils.EARTH_MEAN_RADIUS_KM);
  }

  /**
   * Follows the <a href="http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2#The_.22box.22_parameter">
   * OpenSearch spec on box.</a>
   */
  protected Geometry2D parseBox(String boxArg) throws ParseException {
    String[] sides = boxArg.split(",");
    if (sides.length != 4)
      throw new ParseException("Expected box arg with west,south,east,north format.");
    double x1 = Double.parseDouble(sides[0]);
    double y1 = Double.parseDouble(sides[1]);
    double x2 = Double.parseDouble(sides[2]);
    double y2 = Double.parseDouble(sides[3]);

    if (x1 <= x2) {
      return new Rectangle( x1, y1, x2, y2 );
    } else {
      Geometry2D shape1 = new Rectangle(Math.max(x1,x2),y1,180,y2);
      Geometry2D shape2 = new Rectangle(-180,y1,Math.min(x1,x2),y2);
      return new MultiGeom(Arrays.asList(shape1, shape2));
    }
  }
}
