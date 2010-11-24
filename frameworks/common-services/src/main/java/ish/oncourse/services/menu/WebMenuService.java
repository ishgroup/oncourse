package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebMenuService implements IWebMenuService {

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	public WebMenu newMenu() {
		ObjectContext ctx = cayenneService.sharedContext();
		
		WebMenu menu = ctx.newObject(WebMenu.class);
		menu.setName("New menu item");
		menu.setWebSite(webSiteService.getCurrentWebSite());
		
		ctx.commitChanges();
		
		return menu;
	}

	public WebMenu getRootMenu() {
		Expression rootMenuExp = ExpressionFactory.matchExp(
				WebMenu.PARENT_WEB_MENU_PROPERTY, null);

		SelectQuery query = new SelectQuery(WebMenu.class, siteQualifier()
				.andExp(rootMenuExp));
		
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

	public List<WebMenu> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(WebMenu.class);
		q.andQualifier(ExpressionFactory.inDbExp(WebMenu.ID_PK_COLUMN, ids));

		return cayenneService.sharedContext().performQuery(q);
	}
}
