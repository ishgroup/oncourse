package ish.oncourse.services.alias;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class WebUrlAliasService extends BaseService<WebUrlAlias> implements
		IWebUrlAliasService {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	public WebUrlAlias getAliasByPath(String path) {
		ObjectSelect<WebUrlAlias> query = ObjectSelect.query(WebUrlAlias.class)
				.and(siteQualifier())
				.and(WebUrlAlias.URL_PATH.eq(path))
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroup(getCacheGroup());

		List<WebUrlAlias> aliases = query.select(cayenneService.sharedContext());
		int size = aliases.size();
		logger.debug("Found {} aliases for query: {}", size, query);

		if (size > 1) {
			logger.error("Expected one WebUrlAlias record, found {} for query: {}", size, query);
		}

		return (aliases.isEmpty()) ? null : aliases.get(0);
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		return (site == null) ?
				WebUrlAlias.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(webSiteService.getCurrentCollege()) :
				WebUrlAlias.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion());
	}

    @Override
    public List<WebUrlAlias> getRedirects() {
		return  GetRedirects.valueOf(webSiteVersionService.getCurrentVersion(), cayenneService.sharedContext(), QueryCacheStrategy.LOCAL_CACHE).get();
    }
}
