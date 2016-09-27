package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteService implements IWebSiteService {

    private static final String CURRENT_COLLEGE = "currentCollege";

    private final static Logger logger = LogManager.getLogger();

    private static final Pattern TECHNICAL_SITES_DOMAIN_PATTERN = Pattern
            .compile("([a-z0-9,-]+)([.].+[.]oncourse[.]net[.]au)");

	private static final Pattern SHORT_SITES_DOMAIN_PATTERN = Pattern
			.compile("([a-z0-9,-]+)([.]oncourse[.]cc)");

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
		String siteKey = null;
        String serverName = request.getServerName().toLowerCase();

		Matcher matcher = SHORT_SITES_DOMAIN_PATTERN.matcher(serverName);
		
        boolean siteKeyFound = matcher.matches();
        if (siteKeyFound) {
            siteKey = matcher.group(1);
        } else {
			matcher = TECHNICAL_SITES_DOMAIN_PATTERN.matcher(serverName);
			if (matcher.matches()) {
				siteKey = matcher.group(1);
			}
		}
		
        return siteKey;
    }

    WebHostName getCurrentDomain() {
        WebHostName collegeDomain;

        String serverName = request.getServerName().toLowerCase();
        String siteKey = getSiteKey();

        if (siteKey != null) {
            // there's not domain for technical site
            return null;
        } else {
            collegeDomain = ObjectSelect.query(WebHostName.class)
                    .and(WebHostName.NAME.eq(serverName))
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebHostName.class.getSimpleName())
                    .selectFirst(cayenneService.sharedContext());
        }

        // commented as seems to be useless - uncomment if it will cause
        // troubles
        // if (collegeDomain == null) {
        // collegeDomain = new WebHostName();
        // collegeDomain.setName(serverName);
        // }

        if (collegeDomain != null) {
            logger.debug("Request server name: {} for Request #{} ; returning domain object with {}", request.getServerName(), request.hashCode(), collegeDomain.getName());
        }

        return collegeDomain;
    }

    public WebSite getCurrentWebSite() {
        WebSite currentWebSite = (WebSite) request.getAttribute(CURRENT_WEB_SITE);
        if (currentWebSite != null) {
            return currentWebSite;
        }
        WebHostName currentDomain = getCurrentDomain();
        WebSite site = null;
        if (currentDomain == null) {
            site = ObjectSelect.query(WebSite.class)
                    .where(WebSite.SITE_KEY.eq(getSiteKey()))
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebSite.class.toString())
                    .selectFirst(cayenneService.sharedContext());
        } else {
            site = currentDomain.getWebSite();
        }
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
        query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);

        college = (College) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
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
