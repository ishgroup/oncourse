package ish.oncourse.services.resource;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ResourceService implements IResourceService {

	private final static String WEB_FOLDER = "s";
	
	private IWebSiteVersionService siteVersionService;
	private ICayenneService cayenneService;

	private static final Logger logger = LogManager.getLogger();

	public ResourceService(@Inject ICayenneService cayenneService, @Inject IWebSiteVersionService siteVersionService) {
		this.cayenneService = cayenneService;
		this.siteVersionService = siteVersionService;
	}
	
	@Override
	public org.apache.tapestry5.ioc.Resource getDbTemplateResource(WebSiteLayout layout, String fileName) {
		String cache;
		if (layout != null 
				&& layout.getWebSiteVersion()!= null
				&& layout.getWebSiteVersion().getWebSite() != null)	{
			cache = layout.getWebSiteVersion().getWebSite().getSiteKey();
		} else {
			cache = WebTemplate.class.getSimpleName();
		}
		
		WebTemplate template = ObjectSelect.query(WebTemplate.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroup(cache)
				.and(WebTemplate.LAYOUT.eq(layout))
				.and(WebTemplate.NAME.eq(fileName)).selectOne(cayenneService.sharedContext());
		return template != null ? new DatabaseTemplateResource(template) : null;
	}

	public Resource getWebResource(String fileName) {
		return new PublicFileResource(WEB_FOLDER, fileName);
	}
}
