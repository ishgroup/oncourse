package ish.oncourse.textile.pages;

import ish.oncourse.services.search.ISearchService;
import ish.oncourse.services.search.SearchParamsParser;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.DurationTextileAttribute;
import ish.oncourse.solr.query.Duration;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class TextileDuration {
    
    @Inject
    private Request request;

    @Inject
    private ISearchService searchService;

    @Inject
    private ITagService tagService;

    @Inject
    private IWebSiteService webSiteService;

    @Property
    private String display;

    @Property
    private Duration duration;

    @Property
    private Long counter = 0l;

    @SetupRender
    public void setupRender() {
        Map<String, String> parameters = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_DURATION_PAGE_PARAM);
        
        duration = Duration.valueOf(
                parameters.get(DurationTextileAttribute.DURATION.getValue()),
                parameters.get(DurationTextileAttribute.DISPLAY.getValue())
        );
        
        SearchParamsParser parser = SearchParamsParser.valueOf(request, webSiteService.getCurrentCollege(),
                searchService, tagService, webSiteService.getTimezone());
        parser.parse();
        
        counter = searchService.getCounterForDuration(parser.getSearchParams(), duration).longValue();
    }
}
