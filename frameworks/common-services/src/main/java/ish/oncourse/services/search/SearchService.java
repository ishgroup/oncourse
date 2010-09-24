package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.log4j.Logger;
import org.apache.lucene.spatial.geohash.GeoHashUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.ioc.annotations.Inject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

public class SearchService implements ISearchService {

	private static final Logger logger = Logger.getLogger(SearchService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IPropertyService propertyService;

	private SolrServer solrServer;

	private SolrServer getSolrServer() {
		if (solrServer == null) {

			try {
				String solrURL = null;

				try {
					Context ctx = new InitialContext();
					solrURL = (String) ctx.lookup("java:comp/env/"
							+ Property.SolrServer.value());
					if (logger.isInfoEnabled()) {
						logger.info("SolrUrl configured through JNDI to: "
								+ solrURL);
					}
				} catch (NamingException ne) {
					logger.warn(
							"SolrUrl not defined by JNDI, falling to secondary config",
							ne);
				}

				solrURL = (solrURL == null) ? propertyService
						.string(Property.SolrServer) : solrURL;
				solrURL = (solrURL == null) ? System
						.getProperty(Property.SolrServer.value()) : solrURL;

				if (solrURL == null) {
					throw new IllegalStateException("Undefined property: "
							+ Property.SolrServer);
				}

				CommonsHttpSolrServer httpSolrServer = new CommonsHttpSolrServer(
						solrURL);

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

			String collegeId = String.valueOf(webSiteService
					.getCurrentCollege().getId());

			SolrQuery q = new SolrQuery();
			q.setParam("fl", "id, name");
			q.setStart(start);
			q.setRows(rows);
			q.setIncludeScore(true);

			q.setParam("fq",
					String.format("collegeId:%s doctype:course", collegeId));

			if (params.size() == 1 && params.get(SearchParam.s) != null) {
				q.setQueryType("dismax");
				q.setQuery(params.get(SearchParam.s).toLowerCase());
			} else {
				StringBuilder qString = new StringBuilder();
				if (params.containsKey(SearchParam.day)) {
					String day = params.get(SearchParam.day);
					qString.append(String.format("when:" + day)).append(" ");
				}

				if (params.containsKey(SearchParam.time)) {
					String time = params.get(SearchParam.time);
					qString.append("when:" + time).append(" ");
				}
				
				if(params.containsKey(SearchParam.subject)){
					String subject = params.get(SearchParam.subject);
					qString.append("tag:" + subject).append(" ");
				}

				if (params.containsKey(SearchParam.near)) {
					String near = params.get(SearchParam.near);
					// TODO remove this hack when the name of the location will
					// be searched in db
					try {
						double[] points = GeoHashUtils.decode(near);
						qString.append("{!sfilt fl=course_loc}");
						q.setParam("pt", String.valueOf(points[0]) + ","
								+ String.valueOf(points[1]));
						q.setParam("d", "10");
					} catch (NullPointerException e) {
						// TODO remove this hack when the name of the location
						// will be searched in db
					}
				}

				q.setQuery(qString.toString());
			}

			return getSolrServer().query(q);
		} catch (Exception e) {
			logger.error("Failed to search courses.", e);
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
				String t = terms[i].toLowerCase().trim() + "*";

				query.append(
						String.format("(name:%s && collegeId:%s)", t, collegeId))
						.append("||");

				query.append(String.format(
						"(doctype:place suburb:%s postcode:%s) ", t, t));

				if (i + 1 != terms.length) {
					query.append(" || ");
				}
			}

			q.setQuery(query.toString());

			return getSolrServer().query(q);
		} catch (Exception e) {
			logger.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}
}
