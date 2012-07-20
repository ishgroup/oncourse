package ish.oncourse.services.search;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);
	
	QueryResponse searchCourses(SearchParams searchParams, int start, int rows);
	
	QueryResponse searchSuburbs(String term);
	
	QueryResponse searchSuburb(String location);
}
