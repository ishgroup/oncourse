package ish.oncourse.services.search;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.Map;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);

	QueryResponse searchCourses(SearchParams searchParams, int start, Integer rows);

    SolrDocumentList searchSuburbs(String term);

    SolrDocumentList searchSuburb(String location);

    Map<Long, Long> getCountersForTags();
}
