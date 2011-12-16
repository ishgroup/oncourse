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

package solr2155.lucene.spatial.geohash;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import solr2155.lucene.spatial.geometry.shape.Point2D;
import solr2155.lucene.spatial.geometry.shape.Rectangle;

import java.util.*;

/**
 * A node in a geospatial grid hierarchy as specified by a {@link GridReferenceSystem}.
 */
public class GridNode {

  /**
   * An abstraction for encoding details of a hierarchical grid reference system.
   */
  public static class GridReferenceSystem {

    //TODO incorporate a user specifiable Projection (maps lon-lat to x-y and back)

    //TODO consider alternate more efficient implementation instead of GeoHash.

    final int maxLen;

    public GridReferenceSystem(int maxLen) {
      int MAXP = getMaxPrecision();
      if (maxLen <= 0 || maxLen > MAXP)
        throw new IllegalArgumentException("maxLen must be (0-"+MAXP+"] but got "+maxLen);
      this.maxLen = maxLen;
    }

    public static int getDefaultPrecision() { return 12; }
    public static int getMaxPrecision() { return 22;/*any more and the #s don't seem to change*/ }

    public int getPrecision() { return maxLen; }

    public int getGridSize() { return GeoHashUtils.BASE; }

    public List<GridNode> getSubNodes(Rectangle r) {
      double width = r.getMaxX() - r.getMinX();
      double height = r.getMaxY() - r.getMinY();
      int len = GeoHashUtils.lookupHashLenForWidthHeight(width,height);
      len = Math.min(len,maxLen-1);

      Set<String> cornerGeoHashes = new TreeSet<String>();
      cornerGeoHashes.add(encodeXY(r.getMinPoint(), len));
      cornerGeoHashes.add(encodeXY(r.getMaxPoint(), len));
      cornerGeoHashes.add(encodeXY(r.getMinXMaxYPoint(), len));
      cornerGeoHashes.add(encodeXY(r.getMaxXMinYPoint(), len));

      List<GridNode> nodes = new ArrayList<GridNode>(getGridSize()*cornerGeoHashes.size());
      for (String hash : cornerGeoHashes) {//happens in sorted order
        nodes.addAll(getSubNodes(hash));
      }
      return nodes;//should be sorted
    }

    /** Gets an ordered set of nodes directly contained by the given node.*/
    private List<GridNode> getSubNodes(String baseHash) {
      String[] hashes = GeoHashUtils.getSubGeoHashes(baseHash);
      ArrayList<GridNode> nodes = new ArrayList<GridNode>(hashes.length);
      for (String hash : hashes) {
        String termVal = hash;
        Rectangle rect = GeoHashUtils.decodeBoundary(hash);// min-max lat, min-max lon
        nodes.add(new GridNode(this, termVal, rect));
      }
      return nodes;
    }

    public List<GridNode> getSubNodes(GridNode node) {
      String baseHash = node == null ? "" : node.thisTerm;
      return getSubNodes(baseHash);
    }

    private String encodeXY(Point2D pt, int len) {
      return GeoHashUtils.encode(pt.y(), pt.x(), len);
    }

    public String encodeXY(double x, double y) {
      return GeoHashUtils.encode(y, x, maxLen);
    }

    //TODO return Point2D ?
    public double[] decodeXY(Fieldable f) {
      double[] latLon = GeoHashUtils.decode(f.stringValue());
      //flip to XY
      double y = latLon[0];
      latLon[0] = latLon[1];
      latLon[1] = y;
      return latLon;
    }

    public Point2D decodeXY(String term) {
      double[] latLon = GeoHashUtils.decode(term);
      return new Point2D(latLon[1],latLon[0]);
    }

  }

  private final GridReferenceSystem refSys;
  private final String thisTerm;
  private final Rectangle rect;

  private GridNode(GridReferenceSystem refSys, String value, Rectangle rect) {
    this.refSys = refSys;
    this.rect = rect;
    this.thisTerm = value;
    assert thisTerm.length() <= refSys.maxLen;
    //assert this.rect.getMinY() <= this.rect.getMaxY() && this.rect.getMinX() <= this.rect.getMaxX();
  }

  public String getTermVal() {
    return thisTerm;
  }

  public int length() {
    return thisTerm.length();
  }

  public boolean contains(String term) {
    return term.startsWith(thisTerm);
  }

  public Point2D getCentroid() {
    return rect.centroid();
  }

  public Collection<GridNode> getSubNodes() {
    return refSys.getSubNodes(this);
  }

  public Rectangle getRectangle() {
    return rect;
  }

  /**
   * Checks if the underlying term comes before the parameter (i.e. compareTo < 0).
   */
  public boolean before(String term) {
    return thisTerm.compareTo(term) < 0;
  }

  @Override
  public String toString() {
    return thisTerm+" "+rect;
  }

}
