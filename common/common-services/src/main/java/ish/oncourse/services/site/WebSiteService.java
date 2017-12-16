package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;
import java.util.TimeZone;

public class WebSiteService implements IWebSiteService {

    private static final String CURRENT_COLLEGE = "currentCollege";
    
    private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";

    public static final String CURRENT_WEB_SITE = "currentWebSite";

    @Inject
    private Request request;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private ICookiesService cookiesService;

    /**
     * Default constructor for Tapestry ioc injection.
     */
    public WebSiteService() {

    }

    /**
     * Constructor for unit tests.
     *
     * @param request        tapestry5 http request
     * @param cayenneService cayenne service
     */
    public WebSiteService(Request request, ICayenneService cayenneService) {
        this.request = request;
        this.cayenneService = cayenneService;
    }

    String getSiteKey() {
       return new GetSiteKey(request.getServerName()).get();
    }

    WebHostName getCurrentDomain() {
        return new GetDomain(request.getServerName(), cayenneService.newContext()).get();
    }

    public WebSite getCurrentWebSite() {
        WebSite currentWebSite = (WebSite) request.getAttribute(CURRENT_WEB_SITE);
        if (currentWebSite != null) {
            return currentWebSite;
        }
        
        WebSite site = new GetWebSite(request.getServerName(), cayenneService.newContext()).get();
        request.setAttribute(CURRENT_WEB_SITE, site);
        return site;
    }

    public College getCurrentCollege() {
        College currentCollege = (College) request.getAttribute(CURRENT_COLLEGE);
        if (currentCollege != null) {
            return currentCollege;
        }
        WebSite site = getCurrentWebSite();
        College college = null;
        SelectQuery query = new SelectQuery(College.class, ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, site
                .getCollege().getId()));
        query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);

        college = (College) Cayenne.objectForQuery(cayenneService.newContext(), query);
        request.setAttribute(CURRENT_COLLEGE, college);
        return college;
    }

    public TimeZone getTimezone() {
        TimeZone timezone = cookiesService.getClientTimezone();
        if (timezone == null) {
            timezone = cookiesService.getSimpleClientTimezone();
            if (timezone == null) {
                timezone = TimeZone.getTimeZone(this.getCurrentCollege().getTimeZone());
            }
        }
        return timezone;
    }

    @Override
    public List<WebSite> getSiteTemplates() {
        throw new UnsupportedOperationException();
    }
}
