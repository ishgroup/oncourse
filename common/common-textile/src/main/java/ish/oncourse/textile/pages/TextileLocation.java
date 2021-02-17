package ish.oncourse.textile.pages;

import ish.oncourse.model.Site;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.LocationTextileAttribute;
import ish.oncourse.solr.query.Count;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TextileLocation {
    private final static Logger logger = LogManager.getLogger();

    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

    @Inject
    private ITagService tagService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ICayenneService cayenneService;


    @Property
    private String display;

    @Property
    private String suburb;

    @Property
    private String postcode;

    @Property
    private String distance;

    @Property
    private String siteId;

    @Property
    private String identifier;

    @Property
    private Long counter = 0l;

    @Property
    private String path;

    @SetupRender
    public void setupRender() {
        Map<String, String> parameters = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_LOCATION_PAGE_PARAM);
        display = parameters.get(LocationTextileAttribute.DISPLAY.getValue());
        suburb = parameters.get(LocationTextileAttribute.SUBURB.getValue());
        postcode = parameters.get(LocationTextileAttribute.POSTCODE.getValue());
        distance = parameters.get(LocationTextileAttribute.DISTANCE.getValue());
        siteId = parameters.get(LocationTextileAttribute.SITE.getValue());

        if (display != null) {
            List<String> values = new ArrayList<>();
            values.add(suburb);
            if (postcode != null) {
                values.add(postcode);
            }
            if (distance != null) {
                values.add(distance);
            }
            values.add(display);

            path = StringUtils.join(values, "/");
            identifier = path.replaceAll("[^A-Za-z0-9]", "_");

            SearchParamsParser parser = SearchParamsParser.valueOf(request, webSiteService.getCurrentCollege(),
                    searchService, tagService, webSiteService.getTimezone());
            parser.parse();
            Map<String, Count> result = searchService.getCountersForLocations(parser.getSearchParams(),
                    Collections.singletonList(Count.valueOf(identifier, path, 0)));
            Count count = result.get(identifier);
            if (count != null) {
                counter = count.getCounter();
            }
        } else if (siteId != null) {
            ObjectContext objectContext = cayenneService.sharedContext();
            Site site = ObjectSelect.query(Site.class).and(ExpressionFactory.inDbExp(Site.ID_PK_COLUMN, Long.valueOf(siteId))).selectFirst(objectContext);
            display = site.getName();
            path = "site=" + siteId;
            identifier = siteId;
        }
    }
}
