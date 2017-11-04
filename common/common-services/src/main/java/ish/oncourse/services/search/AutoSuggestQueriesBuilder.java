package ish.oncourse.services.search;

import ish.oncourse.model.College;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.search.SearchService.*;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class AutoSuggestQueriesBuilder {
    private String collegeId;
    private String[] searchWords;
    private int index;
    private String searchWord;
    private String queryWord;
    private String stateQualifier;

    private List<String> courseQueries = new ArrayList<>();
    private List<String> tagQueries = new ArrayList<>();
    private List<String> suburbsQuery = new ArrayList<>();
    private List<String> postcodesQuery = new ArrayList<>();


    public AutoSuggestQueriesBuilder build() {
        for (index = 0; index < searchWords.length; index++) {
            searchWord = StringUtils.trimToNull(searchWords[index]);
            if (searchWord != null) {
                queryWord = searchWord + SOLR_ANYTHING_AFTER_CHARACTER;
                buildQueryForCourses();
                buildQueryForTags();
                buildQueryForSuburbs();
            }
        }
        return this;
    }

    public SolrQuery getCoursesQuery() {
        return courseQueries.size() > 0 ? new SolrQuery(StringUtils.join(courseQueries, TERMS_SEPARATOR_STRING)) : null;
    }

    public SolrQuery getTagsQuery() {
        return tagQueries.size() > 0 ? new SolrQuery(StringUtils.join(tagQueries, TERMS_SEPARATOR_STRING)) : null;
    }

    public SolrQuery getSuburbsQuery() {
        if (suburbsQuery.size() > 0) {
            String query = String.format("((%s) || (%s))",
                    StringUtils.join(suburbsQuery, " AND "), StringUtils.join(postcodesQuery, " AND "));
            return new SolrQuery(query);
        } else {
            return null;
        }
    }


    private void buildQueryForSuburbs() {
        //wildcard we should add only for last word of the search string.
        queryWord = index < (searchWords.length - 1) ? searchWord : queryWord;
        if (stateQualifier != null) {
            suburbsQuery.add(String.format("(suburb:%s AND state:%s)", queryWord, stateQualifier));
            postcodesQuery.add(String.format("(postcode:%s AND state:%s)", queryWord, stateQualifier));
        } else {
            suburbsQuery.add(String.format("suburb:%s", queryWord));
            postcodesQuery.add(String.format("postcode:%s", queryWord));
        }
    }

    private void buildQueryForTags() {
        tagQueries.add(String.format("(collegeId:%s AND name:%s)", collegeId, queryWord));
    }

    private void buildQueryForCourses() {
        courseQueries.add(String.format("(name:%s AND collegeId:%s)", queryWord, collegeId));
        courseQueries.add(String.format("(course_code:%s AND collegeId:%s)",
                !queryWord.contains("\\-") ? queryWord : queryWord.substring(0, queryWord.indexOf("\\-")), collegeId));
    }
    
    public static AutoSuggestQueriesBuilder valueOf(String searchString, College college) {
       return valueOf(searchString, college, null);
    }
    public static AutoSuggestQueriesBuilder valueOf(String searchString, College college, String state) {
        AutoSuggestQueriesBuilder builder = new AutoSuggestQueriesBuilder();
        builder.collegeId = String.valueOf(college.getId());
        builder.stateQualifier = state;

        searchString = SolrQueryBuilder.replaceSOLRSyntaxisCharacters(searchString.toLowerCase());
        builder.searchWords = searchString.split(SPACE_PATTERN);
        return builder;
    }
}
