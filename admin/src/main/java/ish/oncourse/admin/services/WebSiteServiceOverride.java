package ish.oncourse.admin.services;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;
import java.util.TimeZone;

public class WebSiteServiceOverride implements IWebSiteService {

	@Inject
	private ICayenneService cayenneService;
	
	@Override
	public WebSite getCurrentWebSite() {
		return null;
	}

	@Override
	public College getCurrentCollege() {
		return null;
	}

	@Override
	public TimeZone getTimezone() {
		return null;
	}

	@Override
	public List<WebSite> getSiteTemplates() {
		return ObjectSelect.query(WebSite.class).where(WebSite.SITE_KEY.like("template-%")).cacheStrategy(QueryCacheStrategy.SHARED_CACHE,
				WebSite.class.getSimpleName()).orderBy(WebSite.SITE_KEY.asc()).select(cayenneService.newContext());

	}
}
