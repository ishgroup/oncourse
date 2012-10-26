package ish.oncourse.services.search;

import ish.oncourse.model.Course;

import java.util.List;

import org.apache.solr.common.SolrDocumentList;

public interface ISearchService {
	SolrDocumentList autoSuggest(String term);

    SolrDocumentList searchCourses(SearchParams searchParams, int start, int rows);
    
    List<Course> getDirectCourseSearchResult(String term, Long collegeId);

    SolrDocumentList searchSuburbs(String term);

    SolrDocumentList searchSuburb(String location);
}
