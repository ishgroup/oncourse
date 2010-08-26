package ish.oncourse.services.search;

import java.util.Map;

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

	private static final int START_DEFAULT = 0;
	private static final int ROWS_DEFAULT = 20;

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

	public QueryResponse searchCourses(Map<String, String> params) {
		try {
			SolrQuery q = new SolrQuery();
			
			q.addFilterQuery(String.format("(collegeId:%s)", webSiteService
					.getCurrentCollege().getId()));
			
			q.addFilterQuery("(doctype:course)");

			q.setQuery(params.get(SearchParam.s.name()));
			q.setFields("name", "course_code", "class_code", "detail");

			int start = getIntParam(params.get(SearchParam.start.name()),
					START_DEFAULT);
			
			int rows = getIntParam(params.get(SearchParam.rows.name()),
					ROWS_DEFAULT);

			q.setStart(start);
			q.setRows(rows);

			return getSolrServer().query(q);
		} catch (Exception e) {
			LOGGER.error("Failed to search courses.", e);
			throw new SearchException("Unable to find courses.", e);
		}
	}

	private int getIntParam(String s, int def) {
		int start = def;
		try {
			start = (s != null) ? Integer.parseInt(s) : start;
		} catch (Exception e) {
			LOGGER.warn("Unparsable start param.", e);
		}

		return start;
	}
}
