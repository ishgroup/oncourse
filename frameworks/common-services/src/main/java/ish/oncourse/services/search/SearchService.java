package ish.oncourse.services.search;

import java.util.Map;

import ish.oncourse.model.College;
import ish.oncourse.services.property.IPropertyService;
import ish.oncourse.services.property.Property;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.ioc.annotations.Inject;

public class SearchService implements ISearchService {

	private static final Logger LOGGER = Logger.getLogger(SearchService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IPropertyService propertyService;

	private SolrServer solrServer;

	private SolrServer getSolrServer() {
		if (solrServer == null) {
			try {
				CommonsHttpSolrServer httpSolrServer = new CommonsHttpSolrServer(
						propertyService.string(Property.SolrUrl));
				solrServer = httpSolrServer;
			} catch (Exception e) {
				throw new RuntimeException("Unable to connect to solr server.",
						e);
			}
		}
		return solrServer;
	}

	public QueryResponse searchCourses(Map<String, String> params, int start, int rows) {
		try {
			SolrQuery q = new SolrQuery();

			q.addFilterQuery(String.format("(collegeId:%s)", webSiteService
					.getCurrentCollege().getId()));

			q.addFilterQuery("doctype:course");
			q.setQueryType("dismax");
			
			q.setStart(start);
			q.setRows(rows);
			q.setIncludeScore(true);

			q.setQuery(params.get(SearchParam.s.name()).toLowerCase());

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

			String query = String
					.format(
							"(course_name:%s && collegeId:%s) || ((location_suburb:%s location_postcode:%s) && (%s))",
							term, String.valueOf(college.getId()), term, term, buildStateQualifier(college));

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
			builder.append("location_state:").append(state).append(" ");
		}

		builder.append(")");

		return builder.toString();
	}
}
