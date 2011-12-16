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

package solr2155.solr.search.function.distance;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocSlice;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 **/
public class MultiDistanceFunctionTest extends SolrTestCaseJ4 {
  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig_multi.xml", "schema_multi.xml", "src/test/test-files/solr");
  }

  @Test
  public void testGeoMultiDist() throws Exception {
    clearIndex();
    assertU(adoc("id", "1", "store", "32.7693246, -79.9289094"));
    assertU(adoc("id", "2",
		              "store", "43.5614,-90.67341", 
			                        "store", "30.44614,-91.60341", 
							           "store", "35.0752,-97.202"));
    assertU(adoc("id", "3",
			      "store", "43.17614,-90.57341", 
				                 "store", "45.17614,-93.87341", 
								    "store", "35.0752,-97.102"));
    assertU(adoc("id", "4",
			      "store", "43.5614,-90.67341", 
					        "store", "30.44614,-91.70341", 
							           "store", "35.0752,-97.032"));
    assertU(adoc("id", "5", "store", "45.18014,-93.87741"));
    assertU(adoc("id", "6", "store", "37.7752,-100.0232"));
    assertU(commit());
    //center is at doc 3, distance is 0:
    assertQScore(req("fl", "*,score", "q", "{!func}geodist()", "fq", "{!geofilt}", "sfield", "store", "pt", "43.17614,-90.57341",
        "d", "100", "sfield", "store", "sort", "score asc", "fq", "id:3"), 0, 0f);
    
    assertQScore(req("fl", "*,score", "q", "{!func}geodist()", "fq", "{!geofilt}", "sfield", "store", "pt", "43.17614,-90.57341",
			"d", "100", "sfield", "store", "sort", "score asc", "fq", "id:2"), 0, 43.5949f);

    assertQ(req("fl", "*,score", "q", "{!func}geodist()", "fq", "{!geofilt}", "sfield", "store", "pt", "43.17614,-90.57341",
			"d", "1000", "sfield", "store", "sort", "score asc"),  "//*[@numFound='5']");  

    assertQScore(req("fl", "*,score", "q", "{!func}geodist()", "fq", "{!geofilt}", "sfield", "store", "pt", "43.17614,-90.57341",
			"d", "1000", "sfield", "store", "sort", "score asc"),  4, 998.7165f);

    assertQ(req("fl", "*,score", "q", "*:*", "fq", "{!geofilt}", "sfield", "store", "pt", "43.17614,-90.57341",
			"d", "1000", "sfield", "store", "sort", "geodist() asc"),  "//*[@numFound='5']");

    assertQ(req("fl", "id,store,score", "q", "*:*", "fq", "{!geofilt}", "sfield", "store", "pt", "35.0751,-97.0324",
			"d", "1000", "sfield", "store", "sort", "geodist() asc"),   "//doc[1]/str[@name='id']='4'");

    assertQ(req("fl", "id,store,score", "q", "*:*", "fq", "{!geofilt}", "sfield", "store", "pt", "30.44614,-91.6034",
			"d", "1000", "sfield", "store", "sort", "geodist() asc"),   "//doc[1]/str[@name='id']='2'");

  }

  /** TODO propose that this go into Solr's test harness. */
  private void assertQScore(SolrQueryRequest req, int docIdx, float targetScore) throws Exception {
    try {
      String handler = req.getParams().get(CommonParams.QT);
      SolrQueryResponse resp = h.queryAndResponse(handler, req);
//      ResultContext resCtx = (ResultContext) resp.getValues().get("response");
      final DocList docList = (DocList) resp.getValues().get("response");
      assertTrue("expected more docs", docList.size() >= docIdx+1);
      assertTrue("expected scores", docList.hasScores());
      DocIterator docIterator = docList.iterator();
      for(int i = -1; i < docIdx; i++) {//loops at least once
        docIterator.nextDoc();
      }
      float gotScore = docIterator.score();
      assertEquals(gotScore,targetScore, 0.0001);
    } finally {
      req.close();
    }
  }

}
