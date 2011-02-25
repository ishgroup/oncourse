package ish.oncourse.services.search;

import ish.oncourse.model.College;
import ish.oncourse.services.jndi.ILookupService;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.tapestry5.ioc.annotations.Inject;

public class SearchService implements ISearchService {

	private static final Logger logger = Logger.getLogger(SearchService.class);

	private static final int MAX_DISTANCE = 3;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IPropertyService propertyService;
	
	@Inject
	private ILookupService lookupService;

	private SolrServer solrServer;

	private SolrServer getSolrServer() {
		if (solrServer == null) {

			try {
				String solrURL = (String) lookupService.lookup(Property.SolrServer.value());

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
			q.addFilterQuery(String.format("+collegeId:%s +doctype:course +end:[NOW TO *]",
					collegeId));

			if (params.size() == 1 && params.get(SearchParam.s) != null) {
				q.setQueryType("dismax");
				q.setQuery(params.get(SearchParam.s).toLowerCase());
			} else {
				StringBuilder qString = new StringBuilder();

				if (params.containsKey(SearchParam.s)) {
					String s = params.get(SearchParam.s);
					qString.append(s).append(" ");
					qString.append(
							String.format("detail: %s || tutor:%s || course_code:%s",
									s, s, s)).append(" ");
				}

				if (params.containsKey(SearchParam.price)) {
					String price = params.get(SearchParam.price);
					qString.append(String.format("price:[* TO %s]", price))
							.append(" ");
				}

				if (params.containsKey(SearchParam.day)) {
					String day = params.get(SearchParam.day);
					qString.append("when:" + day).append(" ");
				}

				if (params.containsKey(SearchParam.time)) {
					String time = params.get(SearchParam.time);
					qString.append("when:" + time).append(" ");
				}

				if (params.containsKey(SearchParam.subject)) {
					String subject = params.get(SearchParam.subject);
					qString.append("tag:" + subject).append(" ");
				}

				if (params.containsKey(SearchParam.near)) {
					String near = params.get(SearchParam.near);
					int separator = near.lastIndexOf(" ");

					if (separator > 0) {
						String[] suburbParams = { near.substring(0, separator),
								near.substring(separator + 1) };

						SolrDocumentList responseResults = searchSuburb(
								suburbParams[0], suburbParams[1]).getResults();

						if (!responseResults.isEmpty()) {
							SolrDocument doc = responseResults.get(0);
							String[] points = ((String) doc.get("loc"))
									.split(",");

							String distanceQuery = String
									.format("{!geofilt pt=%s,%s sfield=course_loc d=%s}",
											points[0], points[1], MAX_DISTANCE);

							if (params.size() > 1) {
								q.addFilterQuery(distanceQuery);
							} else {
								qString.append(distanceQuery);
							}
						}
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

			StringBuilder query = new StringBuilder();

			String[] terms = term.split("[\\s]+");
			for (int i = 0; i < terms.length; i++) {
				String t = terms[i].toLowerCase().trim() + "*";

				query.append(
						String.format("(name:%s && collegeId:%s)", t, collegeId))
						.append("||");

				query.append(
						String.format(
								"(course_code:%s && collegeId:%s)",
								t.indexOf("-") < 0 ? t : t.substring(0,
										t.indexOf("-")), collegeId)).append(
						"||");

				query.append(
						String.format(
								"(doctype:place && (suburb:%s || postcode:%s)) ",
								t, t)).append(" || ");

				query.append(String
						.format("(doctype:tag && collegeId:%s && tag:%s)",
								collegeId, t));

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

	public QueryResponse searchSuburbs(String term) {
		try {

			SolrQuery q = new SolrQuery();

			StringBuilder query = new StringBuilder();

			String[] terms = term.split("[\\s]+");
			for (int i = 0; i < terms.length; i++) {
				String t = terms[i].toLowerCase().trim() + "*";

				query.append(String.format(
						"(doctype:place && (suburb:%s || postcode:%s)) ", t, t));

				if (i + 1 != terms.length) {
					query.append(" || ");
				}
			}

			q.setQuery(query.toString());

			return getSolrServer().query(q);
		} catch (Exception e) {
			logger.error("Failed to search suburbs.", e);
			throw new SearchException("Unable to find suburbs.", e);
		}
	}

	public QueryResponse searchSuburb(String suburbName, String postcode) {
		try {

			SolrQuery q = new SolrQuery();

			StringBuilder query = new StringBuilder();

			query.append(String.format(
					"(doctype:place && suburb:%s && postcode:%s) ",
					suburbName.replaceAll("[\\s]+", "+"), postcode));

			q.setQuery(query.toString());

			return getSolrServer().query(q);
		} catch (Exception e) {
			logger.error("Failed to search suburb.", e);
			throw new SearchException("Unable to find suburb.", e);
		}
	}
}
