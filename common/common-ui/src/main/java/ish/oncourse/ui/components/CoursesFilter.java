package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CoursesFilter {
    private static final Logger logger = LogManager.getLogger();

    @Inject
    private Request request;

    @Inject
    private ITagService tagService;

    @Inject
    private ISearchService searchService;

    @Inject
    private IWebSiteService webSiteService;

    @Property
    private Long counter;

    @Property
    private List<Tag> tags;

    @Property
    private Tag tag;

    @SetupRender
    public void setupRender() {
        SearchParamsParser parser = SearchParamsParser.valueOf(request, searchService, tagService, webSiteService.getTimezone());
        parser.parse();
        SearchParams searchParams = parser.getSearchParams();

        tags = new ArrayList<>();
        Tag subject = searchParams.getSubject();
        if (subject != null) {
            tags.add(subject);
        }
        tags.addAll(searchParams.getTags());
        try {
            QueryResponse queryResponse = searchService.searchCourses(searchParams, 0, 0);
            counter = queryResponse.getResults().getNumFound();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
