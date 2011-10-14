package ish.oncourse.services.search;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);
	
	QueryResponse searchCourses(Map<SearchParam, String> params, int start, int rows);
	
	QueryResponse searchSuburbs(String term);
	
	QueryResponse searchSuburb(String location);
}
