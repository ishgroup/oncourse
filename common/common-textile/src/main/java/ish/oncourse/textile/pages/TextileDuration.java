package ish.oncourse.textile.pages;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.DurationTextileAttribute;
import ish.oncourse.solr.query.Count;
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

public class TextileDuration {

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
    private String duration;
            
    @Property
    private String identifier;

    @Property
    private Long counter = 0l;

    @Property
    private String path;
    
    @SetupRender
    public void setupRender() {
        Map<String, String> parameters = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_DURATION_PAGE_PARAM);
        display = parameters.get(DurationTextileAttribute.DISPLAY.getValue());
        duration = parameters.get(DurationTextileAttribute.DISPLAY.getValue());

            List<String> values = new ArrayList<>();
            
            values.add(display);
            values.add(duration);
            
            path = "duration" + duration;
            identifier = path.replaceAll("[^A-Za-z0-9]", "_");

            SearchParamsParser parser = SearchParamsParser.valueOf(request, webSiteService.getCurrentCollege(),
                    searchService, tagService, webSiteService.getTimezone());
            parser.parse();
            Map<String, Count> result = searchService.getCountersForDurations(parser.getSearchParams(),
                    Collections.singletonList(Count.valueOf(identifier, path, 0)));
            Count count = result.get(identifier);
            if (count != null) {
                counter = count.getCounter();
            }
        
    }
}
