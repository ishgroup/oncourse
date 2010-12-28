package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.services.Request;


@Scope("perthread")
public class WebSiteService implements IWebSiteService {

	private static final String COLLEGE_DOMAIN_CACHE_GROUP = "webhosts";
	
	@Inject
	private Request request;

	@Inject
	private ICayenneService cayenneService;

	private transient WebHostName collegeDomain;
	
	private final static Logger LOGGER = Logger.getLogger(WebSiteService.class);

	public WebHostName getCurrentDomain() {

		if (collegeDomain == null) {

			String serverName = request.getServerName().toLowerCase();

			SelectQuery query = new SelectQuery(WebHostName.class);
			query.andQualifier(ExpressionFactory.matchExp(
					WebHostName.NAME_PROPERTY, serverName));

			query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
			query.setCacheGroups(COLLEGE_DOMAIN_CACHE_GROUP);

			collegeDomain = (WebHostName) DataObjectUtils.objectForQuery(
					cayenneService.sharedContext(), query);


			if (collegeDomain == null) {
				throw new IllegalStateException(
						"Can't determine college domain for server name: '"
						+ serverName + "'");
			}
		}

		if (collegeDomain != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request server name: " + request.getServerName()
						+ " for Request #" + request.hashCode()
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
