package ish.oncourse.services.search;

import ish.oncourse.model.Tag;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;
import java.util.Map;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);

	SearchResult searchCourses(SearchParams searchParams, int start, Integer rows);

    SolrDocumentList searchSuburbs(String term);
    
    SolrDocumentList searchSuburbs(String term, String state);
    
    SolrDocumentList searchSuburb(String location);

    Map<Long, Long> getCountersForTags(SearchParams searchParams, List<Tag> counts);

    Map<String, Count> getCountersForLocations(SearchParams params, List<Count> counts);
}
