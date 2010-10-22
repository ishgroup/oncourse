package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebMenuService implements IWebMenuService {

	private static final Logger LOGGER = Logger.getLogger(WebMenuService.class);

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public WebMenu getMainMenu() {
		Expression rootMenuExp = ExpressionFactory.matchExp(
				WebMenu.PARENT_WEB_MENU_PROPERTY, null);
		
		SelectQuery query = new SelectQuery(WebMenu.class, siteQualifier().andExp(rootMenuExp));
		query.addPrefetch(WebMenu.PARENT_WEB_MENU_PROPERTY);
		
		List<WebMenu> results = cayenneService.sharedContext().performQuery(
				query);
		
		return results.isEmpty() ? null : results.get(0);
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? ExpressionFactory.matchExp(
				WebMenu.WEB_SITE_PROPERTY + "." + WebSite.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()) : ExpressionFactory
				.matchExp(WebMenu.WEB_SITE_PROPERTY, site);

		return expression;
	}
}
