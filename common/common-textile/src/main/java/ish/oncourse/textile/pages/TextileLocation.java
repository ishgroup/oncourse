package ish.oncourse.textile.pages;

import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.LocationTextileAttribute;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TextileLocation {
    private final static Logger logger = LogManager.getLogger();

    public static final Pattern NAME_PATTERN = Pattern.compile("(?<name>.+)\\:(?<subrb>.+)\\-(?<post>[0-9]+)\\/(?<dist>[0-9]+)");

    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

    @Inject
    private ITagService tagService;

    @Inject
    private IWebSiteService webSiteService;

    @Property
    private String subrb;

    @Property
    private String postcode;


    @Property
    private String name;

    @Property
    private String dist;



    @Property
    private String km;

    @Property
    private String near;

    @Property
    private String identifier;

    @Property
    private Long counter;

    @SetupRender
    public void setupRender() {
        Map<String, String> parameters = ( Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_LOCATION_PAGE_PARAM);
        String value = StringUtils.trimToEmpty(parameters.get(LocationTextileAttribute.NAME.getValue()));
        identifier = value.replaceAll("[^A-Za-z0-9]", "_");
        Matcher matcher = NAME_PATTERN.matcher(value);
        if (matcher.matches()){
            name = matcher.group("name");
            subrb = matcher.group("subrb");
            postcode = matcher.group("post");
            dist = matcher.group("dist");
        }
        near = String.format("%s %s", subrb, postcode);
        km = dist;

        initCounter();
    }

    private void initCounter() {
        SearchParamsParser.ParametersProvider provider = new SearchParamsParser.ParametersProvider() {
            @Override
            public String getParameter(String name) {
                switch (name) {
                    case "near":
                        return near;
                    case "km":
                        return km;
                    default:
                        return null;
                }
            }

            @Override
            public String[] getParameters(String name) {
                return new String[0];
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }
        };

        try {
            SearchParamsParser parser = SearchParamsParser.valueOf(provider, searchService, tagService, webSiteService.getTimezone());
            parser.parse();
            QueryResponse response = searchService.searchCourses(parser.getSearchParams(), 0, 0);
            counter = response.getResults().getNumFound();
        } catch (Exception e) {
            logger.error(e);
            counter = 0L;
        }
    }
}
