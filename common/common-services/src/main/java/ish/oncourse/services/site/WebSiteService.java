package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class WebSiteService implements IWebSiteService {

	private static final String CURRENT_COLLEGE = "currentCollege";

	private final static Logger LOGGER = Logger.getLogger(WebSiteService.class);

	private static final Pattern TECHNICAL_SITES_DOMAIN_PATTERN = Pattern
			.compile("([a-z,-]+)([.].+[.]oncourse[.]net[.]au)");

	private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";
	
	public static final String CURRENT_WEB_SITE = "currentWebSite";

	@Inject
	private Request request;

	@Inject
	private ICayenneService cayenneService;

	/**
	 * Default constructor for Tapestry ioc injection.
	 */
	public WebSiteService() {

	}

	/**
	 * Constructor for unit tests.
	 * 
	 * @param request
	 *            tapestry5 http request
	 * @param cayenneService
	 *            cayenne service
	 */
	public WebSiteService(Request request, ICayenneService cayenneService) {
		this.request = request;
		this.cayenneService = cayenneService;
	}

	private String getSiteKey() {
		String serverName = request.getServerName().toLowerCase();
		Matcher matcher = TECHNICAL_SITES_DOMAIN_PATTERN.matcher(serverName);
		boolean siteKeyFound = matcher.matches();
		String siteKey = null;
		if (siteKeyFound) {
			siteKey = matcher.group(1);
		}
		return siteKey;
	}

	public WebHostName getCurrentDomain() {

		WebHostName collegeDomain = null;

		String serverName = request.getServerName().toLowerCase();
		String siteKey = getSiteKey();

		if (siteKey != null) {
			// there's not domain for technical site
			return null;
		} else {
			SelectQuery query = new SelectQuery(WebHostName.class);
			query.andQualifier(ExpressionFactory.matchExp(WebHostName.NAME_PROPERTY, serverName));
			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(COLLEGE_DOMAIN_CACHE_GROUP);

			collegeDomain = (WebHostName) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
		}

		// commented as seems to be useless - uncomment if it will cause
		// troubles
		// if (collegeDomain == null) {
		// collegeDomain = new WebHostName();
		// collegeDomain.setName(serverName);
		// }

		if (collegeDomain != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request server name: " + request.getServerName() + " for Request #" + request.hashCode()
						+ " ; returning domain object with " + collegeDomain.getName());
			}
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
		SelectQuery query = new SelectQuery(WebSite.class);
		if (currentDomain == null) {
			query.andQualifier(ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY, getSiteKey()));
		} else {
			query.andQualifier(ExpressionFactory.matchDbExp(WebSite.ID_PK_COLUMN, currentDomain.getWebSite().getId()));
		}
		site = (WebSite) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
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

		college = (College) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
		request.setAttribute(CURRENT_COLLEGE, college);
		return college;
	}
}
