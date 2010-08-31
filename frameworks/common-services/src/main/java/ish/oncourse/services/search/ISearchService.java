package ish.oncourse.services.search;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface ISearchService {
	QueryResponse autoSuggest(String term);
	QueryResponse searchCourses(String query, int start, int rows);
}
