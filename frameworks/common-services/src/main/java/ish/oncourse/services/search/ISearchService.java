package ish.oncourse.services.search;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface ISearchService {
	QueryResponse autoSuggest(String term);

	QueryResponse searchCourses(Map<SearchParam, String> params, int start, int rows);
	
	QueryResponse searchSuburbs(String term);
}
