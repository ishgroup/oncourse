package ish.oncourse.services.search;

import org.apache.solr.common.SolrDocumentList;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);
	
	SolrDocumentList autoSuggest(String term, String withDirectSearch);

    SolrDocumentList searchCourses(SearchParams searchParams, int start, int rows);

    SolrDocumentList searchSuburbs(String term);

    SolrDocumentList searchSuburb(String location);
}
