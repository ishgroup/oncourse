package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.log4j.Logger;
import org.apache.lucene.spatial.geohash.GeoHashUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Map;

public class SearchService implements ISearchService {

    private static final Logger LOGGER = Logger.getLogger(SearchService.class);

    @Inject
    private IWebSiteService webSiteService;

    private SolrServer solrServer;

    private SolrServer getSolrServer() {
        if (solrServer == null) {
            try {
                CommonsHttpSolrServer httpSolrServer = new CommonsHttpSolrServer(
                        System.getProperty("solr.server"));
                solrServer = httpSolrServer;
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to solr server.",
                        e);
            }
        }
        return solrServer;
    }

    public QueryResponse searchCourses(Map<SearchParam, String> params,
                                       int start, int rows) {
        try {

            String collegeId = String.valueOf(webSiteService.getCurrentCollege().getId());

            SolrQuery q = new SolrQuery();
            q.setParam("fl", "id, name");
            q.setStart(start);
            q.setRows(rows);
            q.setIncludeScore(true);

            q.setParam("fq", String.format("collegeId:%s doctype:course", collegeId));


            if (params.size() == 1 && params.get(SearchParam.s) != null) {
                q.setQueryType("dismax");
                q.setQuery(params.get(SearchParam.s).toLowerCase());
            } else {
                StringBuilder qString = new StringBuilder();
                if (params.containsKey(SearchParam.day)) {
                    String day = params.get(SearchParam.day);
                    qString.append(
                            String.format("(dayName:%s || dayType:%s)", day,
                                    day)).append(" ");
                }

                if (params.containsKey(SearchParam.time)) {
                    String time = params.get(SearchParam.time);
                    qString.append("time:" + time).append(" ");
                }

                if (params.containsKey(SearchParam.near)) {
                    String near = params.get(SearchParam.near);
                    double[] points = GeoHashUtils.decode(near);
                    qString.append("{!sfilt fl=course_loc}");
                    q.setParam(
                            "pt",
                            String.valueOf(points[0]) + ","
                                    + String.valueOf(points[1]));
                    q.setParam("d", "10");
                }

                q.setQuery(qString.toString());
            }


            return getSolrServer().query(q);
        } catch (Exception e) {
            LOGGER.error("Failed to search courses.", e);
            throw new SearchException("Unable to find courses.", e);
        }
    }

    public QueryResponse autoSuggest(String term) {
        try {

            College college = webSiteService.getCurrentCollege();
            String collegeId = String.valueOf(college.getId());
            
            SolrQuery q = new SolrQuery();
            q.setParam("wt", "javabin");

            StringBuilder query = new StringBuilder();

            String[] terms = term.split("\\s");
            for (int i = 0; i < terms.length; i++) {
                String t = terms[i].toLowerCase() + "*";

                query.append(String
                        .format("(name:%s && collegeId:%s)", t, collegeId)).append("||");

                query.append(String.format("(doctype:place suburb:%s postcode:%s) ", t, t));
                
                if (i + 1 != terms.length) {
                     query.append(" || ");
                }
            }

            q.setQuery(query.toString());

            return getSolrServer().query(q);
        } catch (Exception e) {
            LOGGER.error("Failed to search courses.", e);
            throw new SearchException("Unable to find courses.", e);
        }
    }
}
