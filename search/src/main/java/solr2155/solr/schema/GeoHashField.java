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

package solr2155.solr.schema;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.spatial.DistanceUtils;
import org.apache.lucene.spatial.tier.InvalidGeoException;
import org.apache.solr.analysis.BaseTokenizerFactory;
import org.apache.solr.analysis.CharFilterFactory;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.common.SolrException;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.response.XMLWriter;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.SpatialQueryable;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrConstantScoreQuery;
import org.apache.solr.search.SpatialOptions;
import org.apache.solr.search.function.ValueSource;
import solr2155.lucene.spatial.geohash.GeoHashPrefixFilter;
import solr2155.lucene.spatial.geohash.GridNode;
import solr2155.lucene.spatial.geometry.shape.Geometry2D;
import solr2155.lucene.spatial.geometry.shape.MultiGeom;
import solr2155.lucene.spatial.geometry.shape.PointDistanceGeom;
import solr2155.solr.search.function.GeoHashValueSource;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

/**
 * This is a class that represents a <a
 * href="http://en.wikipedia.org/wiki/Geohash">Geohash</a> field. The field is
 * provided as a lat/lon pair and is internally represented as a string.
 * <p/>
 * The implementation is actually decoupled from GeoHashes, instead
 *  {@link solr2155.lucene.spatial.geohash.GridNode.GridReferenceSystem} is used to facilitate future changes.
 *
 * @see org.apache.lucene.spatial.DistanceUtils#parseLatitudeLongitude(double[], String)
 */
public class GeoHashField extends FieldType implements SpatialQueryable {

  public static final int DEFAULT_LENGTH = GridNode.GridReferenceSystem.getMaxPrecision();//~12
  private GridNode.GridReferenceSystem gridReferenceSystem;

  @Override
  protected void init(IndexSchema schema, Map<String, String> args) {
    String len = args.remove("length");
    gridReferenceSystem = new GridNode.GridReferenceSystem(len!=null?Integer.parseInt(len): DEFAULT_LENGTH);

    CharFilterFactory[] filterFactories = new CharFilterFactory[0];
    TokenFilterFactory[] tokenFilterFactories = new TokenFilterFactory[0];
    analyzer = new TokenizerChain(filterFactories, new BaseTokenizerFactory() {
      @Override
      public Tokenizer create(Reader input) {
        return new EdgeNGramTokenizer(input, EdgeNGramTokenizer.Side.FRONT, 1, Integer.MAX_VALUE);
      }
    }, tokenFilterFactories);
    //(leave default queryAnalyzer -- single token)
  }

  public GridNode.GridReferenceSystem getGridReferenceSystem() {
    return gridReferenceSystem;
  }

  @Override
  public SortField getSortField(SchemaField field, boolean top) {
    return getStringSort(field, top);
  }

  public Query createSpatialQuery(QParser parser, SpatialOptions options) {
    double [] point = new double[0];
    try {
      point = DistanceUtils.parsePointDouble(null, options.pointStr, 2);
    } catch (InvalidGeoException e) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, e);
    }
    PointDistanceGeom pDistGeo = new PointDistanceGeom(
            point[0],point[1],options.distance,options.radius);
    Geometry2D shape = pDistGeo;
    if (options.bbox) {
      shape = pDistGeo.getEnclosingBox1();
      Geometry2D shape2 = pDistGeo.getEnclosingBox2();
      if (shape2 != null)
        shape = new MultiGeom(Arrays.asList(shape,shape2));
    }
    return new SolrConstantScoreQuery(new GeoHashPrefixFilter(options.field.getName(),shape,gridReferenceSystem));
  }

  @Override
  public void write(TextResponseWriter writer, String name, Fieldable f)
          throws IOException {
    writer.writeStr(name, toExternal(f), false);
  }

  @Override
  public String toExternal(Fieldable f) {
    double[] xy = gridReferenceSystem.decodeXY(f);
    return xy[1] + "," + xy[0];
  }

  @Override
  public void write(XMLWriter xmlWriter, String name, Fieldable f) throws IOException {
    xmlWriter.writeStr(name, f.stringValue());
  }

  @Override
  public String toInternal(String val) {
    // validate that the string is of the form
    // latitude, longitude
    double[] latLon = new double[0];
    try {
      latLon = DistanceUtils.parseLatitudeLongitude(null, val);
    } catch (InvalidGeoException e) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, e);
    }
    return gridReferenceSystem.encodeXY(latLon[1], latLon[0]);
  }

//  //TODO Why does TrieField explicitly set the token stream on the field at this stage when it should happen later any way?
//  @Override
//  public Fieldable createField(SchemaField field, String externalVal, float boost) {
//    Field f = (Field) super.createField(field, externalVal, boost);
//    if (f != null) {
//      f.setTokenStream(analyzer.tokenStream(field.getName(), new StringReader(externalVal)));
//    }
//    return f;
//  }

  @Override
  public boolean isTokenized() {
    return true;
  }

  @Override
  public ValueSource getValueSource(SchemaField field, QParser parser) {
    return GeoHashValueSource.getValueSource(field.getName(), (FunctionQParser) parser);
  }

}
