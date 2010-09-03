package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.spatial.geohash.GeoHashUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.ioc.annotations.Inject;

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

			SolrQuery q = new SolrQuery();

			q.addFilterQuery(String.format("(" + Field.COLLEGE_ID + ":%s)",
					webSiteService.getCurrentCollege().getId()));
			
			q.addFilterQuery("doctype:course");
			
			//for text only searches use dismax parser
			if (params.size() == 1 && params.get(SearchParam.s) != null) {
				q.setQueryType("dismax");
				q.setQuery(params.get(SearchParam.s).toLowerCase());
			}
			else {
				StringBuilder qString = new StringBuilder();
				if (params.containsKey(SearchParam.day)) {
					String day = params.get(SearchParam.day);
					qString.append(String.format("(dayName:%s || dayType:%s)", day, day)).append(" ");
				}
				
				if (params.containsKey(SearchParam.time)) {
					String time = params.get(SearchParam.time);
					qString.append("time:" + time).append(" ");
				}
				
				if (params.containsKey(SearchParam.near)) {
					String near = params.get(SearchParam.near);
					double[] points = GeoHashUtils.decode(near);
					qString.append("{!sfilt fl=loc}");
					q.setParam("pt", String.valueOf(points[0]) + "," + String.valueOf(points[1]));
					q.setParam("d", "1");
				}
				
				q.setQuery(qString.toString());
			}
			
			q.setStart(start);
			q.setRows(rows);
			q.setIncludeScore(true);

			return getSolrServer().query(q);
		} catch (Exception e) {
			LOGGER.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	public QueryResponse autoSuggest(String term) {
		try {
			term = term.toLowerCase() + "*";

			College college = webSiteService.getCurrentCollege();

			SolrQuery q = new SolrQuery();
			q.setParam("q.op", "OR");
			q.setParam("wt", "javabin");
			
			/*(name:%s && collegeId:%s) || ((doctype:place suburb:%s postcode:%s) && (%s))
			String query = String
					.format(
							"(name:%s && collegeId:%s) || ((doctype:place suburb:%s postcode:%s) && (%s))",
							term, String.valueOf(college.getId()), term, term,
							buildStateQualifier(college));*/
			
			String query = String
			.format(
					"(name:%s && collegeId:%s) || ((doctype:place suburb:%s postcode:%s))",
					term, String.valueOf(college.getId()), term, term);

			q.setQuery(query);

			return getSolrServer().query(q);
		} catch (Exception e) {
			LOGGER.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	private static String buildStateQualifier(College college) {
		StringBuilder builder = new StringBuilder();

		builder.append("(");

		for (String state : college.getCollegeSiteStates()) {
			builder.append(Field.STATE).append(":").append(state).append(" ");
		}

		builder.append(")");

		return builder.toString();
	}
}
