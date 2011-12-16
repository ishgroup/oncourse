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

package solr2155.lucene.spatial.geohash;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;
import solr2155.lucene.TermsEnumCompatibility;
import solr2155.lucene.spatial.geometry.shape.Geometry2D;
import solr2155.lucene.spatial.geometry.shape.IntersectCase;
import solr2155.lucene.spatial.geometry.shape.Point2D;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Performs a spatial filter against a field indexed using Geohashes. Using the hierarchical grid nature of geohashes,
 * this filter recursively traverses each precision length and uses methods on {@link Geometry2D} to efficiently know
 * that all points at a geohash fit in the shape or not to either short-circuit unnecessary traversals or to efficiently
 * load all enclosed points.
 */
public class GeoHashPrefixFilter extends Filter {

  private static final int GRIDLEN_SCAN_THRESHOLD = 4;//>= 1
  private final String fieldName;//interned
  private final Geometry2D geoShape;
  private final GridNode.GridReferenceSystem gridReferenceSystem;

  public GeoHashPrefixFilter(String fieldName, Geometry2D geoShape, GridNode.GridReferenceSystem gridReferenceSystem) {
    this.fieldName = fieldName.intern();
    this.geoShape = geoShape;
    this.gridReferenceSystem = gridReferenceSystem;
  }

  @Override
  public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
    final OpenBitSet bits = new OpenBitSet(reader.maxDoc());
    final TermsEnumCompatibility termsEnum = new TermsEnumCompatibility(reader, fieldName);//Lucene 4 compatibility wrapper
    final TermDocs termDocs = reader.termDocs();
    Term term = termsEnum.term();//the most recent term examined via termsEnum.term()
    if (term == null)
      return bits;

    //TODO Add a precision short-circuit so that we are not accurate on the edge but we're faster.

    //TODO An array based nodes impl would be more efficient; or a stack of iterators.  LinkedList conveniently has bulk add to beginning.
    LinkedList<GridNode> nodes = new LinkedList<GridNode>(gridReferenceSystem.getSubNodes(geoShape.boundingRectangle()));
    while(!nodes.isEmpty() && term != null) {
      final GridNode node = nodes.removeFirst();
      assert node.length() > 0;
      if (!node.contains(term.text()) && node.before(term.text()))
        continue;//short circuit, moving >= the next indexed term
      IntersectCase intersection = geoShape.intersect(node.getRectangle());
      if (intersection == IntersectCase.OUTSIDE)
        continue;
      TermsEnumCompatibility.SeekStatus seekStat = termsEnum.seek(node.getTermVal());
      term = termsEnum.term();
      if (seekStat != TermsEnumCompatibility.SeekStatus.FOUND)
        continue;
      if (intersection == IntersectCase.CONTAINS) {
        termDocs.seek(term);
        addDocs(termDocs, bits);
        term = termsEnum.next();//move to next term
      } else {//any other intersection
        //TODO is it worth it to optimize the shape (e.g. potentially simpler polygon)?
        //GeoShape geoShape = this.geoShape.optimize(intersection);

        //We either scan through the leaf node(s), or if there are many points then we divide & conquer.
        boolean manyPoints = node.length() < gridReferenceSystem.maxLen - GRIDLEN_SCAN_THRESHOLD;

        //TODO Try variable depth strategy:
        //IF configured to do so, we could use term.freq() as an estimate on the number of places at this depth.  OR, perhaps
        //  make estimates based on the total known term count at this level?  Or don't worry about it--use fixed depth.
//        if (manyPoints) {
//          //Make some estimations on how many points there are at this level and how few there would need to be to set
//          // manyPoints to false.
//
//          long termsThreshold = (long) estimateNumberIndexedTerms(node.length(),geoShape.getDocFreqExpenseThreshold(node));
//
//          long thisOrd = termsEnum.ord();
//          manyPoints = (termsEnum.seek(thisOrd+termsThreshold+1) != TermsEnum.SeekStatus.END
//                  && node.contains(termsEnum.term()));
//          termsEnum.seek(thisOrd);//return to last position
//        }

        if (!manyPoints) {
          //traverse all leaf terms within this node to see if they are within the geoShape, one by one.
          for(; term != null && node.contains(term.text()); term = termsEnum.next()) {
            if (term.text().length() < gridReferenceSystem.maxLen)//not a leaf
              continue;
            final Point2D point = gridReferenceSystem.decodeXY(term.text());
            //Filter those out of the shape.
            if(!geoShape.contains(point))
                continue;

            //record
            termDocs.seek(term);
            addDocs(termDocs,bits);
          }
        } else {
          //divide & conquer
          nodes.addAll(0,node.getSubNodes());//add to beginning
        }
      }
    }//node loop

    return bits;
  }

//  double estimateNumberIndexedTerms(int nodeLen,double points) {
//    return 1000;
//    double levelProb = probabilityNumNodes[points];// [1,32)
//    if (nodeLen < geohashLength)
//      return levelProb + levelProb * estimateNumberIndexedTerms(nodeLen+1,points/levelProb);
//    return levelProb;
//  }

  private void addDocs(TermDocs termDocs, OpenBitSet bits) throws IOException {
    while(termDocs.next()) {
      bits.set(termDocs.doc());
    }
  }

  @Override
  public String toString() {
    return "GeoFilter{fieldName='" + fieldName + '\'' + ", shape=" + geoShape + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GeoHashPrefixFilter that = (GeoHashPrefixFilter) o;

    if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
    if (geoShape != null ? !geoShape.equals(that.geoShape) : that.geoShape != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = fieldName != null ? fieldName.hashCode() : 0;
    result = 31 * result + (geoShape != null ? geoShape.hashCode() : 0);
    return result;
  }

}
