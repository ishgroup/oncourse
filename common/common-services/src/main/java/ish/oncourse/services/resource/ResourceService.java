package ish.oncourse.services.resource;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ResourceService implements IResourceService {

	private final static String WEB_FOLDER = "s";
	
	private IWebSiteService siteService;
	private IWebSiteVersionService siteVersionService;
	private ICayenneService cayenneService;

	private static final Logger logger = LogManager.getLogger();

	public ResourceService(	@Inject IWebSiteService siteService, 
			@Inject ICayenneService cayenneService, @Inject IWebSiteVersionService siteVersionService) {

		this.siteService = siteService;
		this.cayenneService = cayenneService;
		this.siteVersionService = siteVersionService;
	}
	
	@Override
	public org.apache.tapestry5.ioc.Resource getDbTemplateResource(String layoutKey, String fileName) {
		ObjectContext context = cayenneService.sharedContext();

		SelectQuery query = new SelectQuery(WebTemplate.class);
		query.andQualifier(ExpressionFactory.matchExp(
				WebTemplate.LAYOUT_PROPERTY + "." + WebSiteLayout.WEB_SITE_VERSION_PROPERTY, 
				siteVersionService.getCurrentVersion()));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY + "." + WebSiteLayout.LAYOUT_KEY_PROPERTY, layoutKey));
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.NAME_PROPERTY, fileName));

		WebTemplate template = (WebTemplate) Cayenne.objectForQuery(context, query);
		
		return template != null ? new DatabaseTemplateResource(template) : null;
	}

	public Resource getWebResource(String fileName) {
		return new PublicFileResource(WEB_FOLDER, fileName);
	}
}
