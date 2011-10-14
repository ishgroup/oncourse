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

	private final static Logger LOGGER = Logger.getLogger(WebSiteService.class);

	private static final Pattern TECHNICAL_SITES_DOMAIN_PATTERN = Pattern.compile("([a-z]+)([.].+[.]oncourse[.]net[.]au)");

	private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";

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

	public WebHostName getCurrentDomain() {

		WebHostName collegeDomain = null;

		String serverName = request.getServerName().toLowerCase();
		Matcher matcher = TECHNICAL_SITES_DOMAIN_PATTERN.matcher(serverName);
		boolean siteKeyFound = matcher.matches();

		if (siteKeyFound) {
			String siteKey = matcher.group(1);

			SelectQuery query = new SelectQuery(WebSite.class, ExpressionFactory.matchExp(WebSite.SITE_KEY_PROPERTY, siteKey));

			WebSite site = (WebSite) Cayenne.objectForQuery(cayenneService.sharedContext(), query);

			if (site != null) {
				// use fake WebHostName for the "technical" sites
				collegeDomain = new WebHostName();
				collegeDomain.setName(serverName);
				collegeDomain.setWebSite(site);
				collegeDomain.setCollege(site.getCollege());
			}

		} else {
			SelectQuery query = new SelectQuery(WebHostName.class);
			query.andQualifier(ExpressionFactory.matchExp(WebHostName.NAME_PROPERTY, serverName));

			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(COLLEGE_DOMAIN_CACHE_GROUP);

			collegeDomain = (WebHostName) Cayenne.objectForQuery(cayenneService.sharedContext(), query);
		}

		if (collegeDomain == null) {
			collegeDomain = new WebHostName();
			collegeDomain.setName(serverName);
		}

		if (collegeDomain != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request server name: " + request.getServerName() + " for Request #" + request.hashCode()
						+ " ; returning domain object with " + collegeDomain.getName());
			}
		}

		return collegeDomain;
	}

	public WebSite getCurrentWebSite() {
		return getCurrentDomain().getWebSite();
	}

	public College getCurrentCollege() {
		return getCurrentDomain().getCollege();
	}
}
