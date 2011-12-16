/*
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

package solr2155.solr.search.function;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.solr.schema.FieldType;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.search.function.DocValues;
import org.apache.solr.search.function.MultiValueSource;
import org.apache.solr.search.function.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solr2155.lucene.TermsEnumCompatibility;
import solr2155.lucene.spatial.geohash.GridNode;
import solr2155.lucene.spatial.geometry.shape.Point2D;
import solr2155.solr.schema.GeoHashField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO consider moving this to lucene package and remove dependency on Solr.
 * TODO implement single-value data structure differently
 */
public class GeoHashValueSource extends MultiValueSource {

  private static final String CACHE_NAME = "fieldValueCache";//"geoHashValues";

  private static final int DEFAULT_ARRAY_CAPACITY = 5;
  private final String fieldName;

  /** Factory method invoked by {@link org.apache.solr.schema.GeoHashField#getValueSource(org.apache.solr.schema.SchemaField, org.apache.solr.search.QParser)}. */
  public static ValueSource getValueSource(String fieldName, FunctionQParser parser) {
    final SolrIndexSearcher searcher = parser.getReq().getSearcher();
    GeoHashValueSource valueSource = (GeoHashValueSource) searcher.cacheLookup(CACHE_NAME, fieldName);
    if (valueSource == null) {
      try {
        valueSource = new GeoHashValueSource(fieldName,searcher);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      searcher.cacheInsert(CACHE_NAME,fieldName,valueSource);
    }
    return valueSource;
  }

  private final Logger log = LoggerFactory.getLogger(getClass());

  //admittedly the List<Point2D> part isn't particularly memory efficient or kind to the GC.
  private List<Point2D>[] doc2PointsCache;//index by doc id, then list of points

  @SuppressWarnings({"unchecked"})
  GeoHashValueSource(String fieldName, SolrIndexSearcher searcher) throws IOException {
    log.info("Loading geohash field "+fieldName+" into memory.");
    this.fieldName = fieldName;

    //Get gridReferenceSystem
    final GridNode.GridReferenceSystem gridReferenceSystem;
    FieldType fieldType = searcher.getSchema().getField(fieldName).getType();
    if (fieldType instanceof GeoHashField) {
      gridReferenceSystem = ((GeoHashField) fieldType).getGridReferenceSystem();
    }
    else
      throw new RuntimeException("field "+fieldName+" should be a GeoHashField, not "+fieldType.getTypeName());

    //Traverse the index to load up doc2PointsCache
    IndexReader reader = searcher.getIndexReader();
    TermsEnumCompatibility termsEnum = new TermsEnumCompatibility(reader,fieldName);
    TermDocs termDocs = reader.termDocs(); //cached for termsEnum.docs() calls
    while(true) {
      final Term term = termsEnum.next();
      if (term == null)
        break;
      if (term.text().length() != gridReferenceSystem.getPrecision())
        continue;
      Point2D point = gridReferenceSystem.decodeXY(term.text());
      termDocs.seek(term);
      while(termDocs.next()) {
        final int docId = termDocs.doc();
        if (docId == DocIdSetIterator.NO_MORE_DOCS)
          break;
        if (doc2PointsCache == null)
          doc2PointsCache = (List<Point2D>[]) new List[reader.maxDoc()];//java generics hack
        List<Point2D> points = doc2PointsCache[docId];
        if (points == null) {
          points = new ArrayList<Point2D>(DEFAULT_ARRAY_CAPACITY);
          doc2PointsCache[docId] = points;
        }
        points.add(point);
      }
    }

    //Log statistics
    if (log.isInfoEnabled()) {
      int min = Integer.MAX_VALUE, sum = 0, max = 0;
      int dlen = 0;
      if (doc2PointsCache != null) {
        dlen = doc2PointsCache.length;
        for (List<Point2D> point2Ds : doc2PointsCache) {
          int plen = (point2Ds == null ? 0 : point2Ds.size());
          min = Math.min(min, plen);
          max = Math.max(max, plen);
          sum += plen;
        }
      }
      if (min == Integer.MAX_VALUE)
        min = 0;
      float avg = (float)sum/dlen;
      log.info("field '"+fieldName+"' in RAM: loaded min/avg/max per doc #: ("+min+","+avg+","+max+") #"+dlen);
    }
  }

  @Override
  public int dimension() {
    return 2;
  }

  /** This class is public so that {@link #point2Ds(int)} is exposed. */
  public class GeoHashDocValues extends DocValues {
    @Override
    public void doubleVal(int doc, double[] vals) {
      super.doubleVal(doc, vals);//TODO
    }

    /**
     * Do NOT modify the returned array!  May return null.
     */
    public List<Point2D> point2Ds(int doc) {
      final List<Point2D>[] cache = GeoHashValueSource.this.doc2PointsCache;
      if (cache == null)
        return null;
      return cache[doc];
    }

    @Override
    public String toString(int doc) {
      StringBuilder buf = new StringBuilder(100);
      buf.append("geohash(").append(fieldName).append(")x,y=");
      List<Point2D> points = point2Ds(doc);
      if (points != null) {
        for (Point2D point : points) {
          buf.append(point.getX()).append(',').append(point.getY());
          buf.append(' ');
        }
      }
      return buf.toString();
    }
  }

  private final GeoHashDocValues docValues = new GeoHashDocValues();

  @Override
  public GeoHashDocValues getValues(Map context, IndexReader reader) throws IOException {
    return docValues;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !getClass().equals(o.getClass()))
      return false;
    return fieldName.equals(((GeoHashValueSource)o).fieldName);
  }

  @Override
  public int hashCode() {
    return fieldName.hashCode();
  }

  @Override
  public String description() {
    return "Loads the geohash based field values into memory, in their lat-lon equivalent.";
  }

}
