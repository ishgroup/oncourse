package ish.oncourse.services.search;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface ISearchService {
	QueryResponse autoSuggest(String term);

	QueryResponse searchCourses(Map<String, String> params, int start, int rows);
}
