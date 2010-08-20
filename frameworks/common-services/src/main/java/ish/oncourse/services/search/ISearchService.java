package ish.oncourse.services.search;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

public interface ISearchService {
	QueryResponse searchCourses(Map<String, String> params);
}
