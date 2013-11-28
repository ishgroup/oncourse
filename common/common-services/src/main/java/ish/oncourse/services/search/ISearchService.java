package ish.oncourse.services.search;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);

	QueryResponse searchCourses(SearchParams searchParams, int start, Integer rows);

    SolrDocumentList searchSuburbs(String term);

    SolrDocumentList searchSuburb(String location);
}
