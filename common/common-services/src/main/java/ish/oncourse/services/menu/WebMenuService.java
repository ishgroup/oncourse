package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashSet;
import java.util.List;

public class WebMenuService extends BaseService<WebMenu> implements IWebMenuService {


	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Override
	public WebMenu createMenu(WebSite site) {
		ObjectContext ctx = site.getObjectContext();

		WebMenu menu = ctx.newObject(WebMenu.class);
		menu.setWebSite(site);
		menu.setParentWebMenu(ctx.localObject(getRootMenu()));
		menu.updateWeight(0, null);
		setUniqueWebMenuName(menu);
		return menu;
	}

	@Override
	public WebMenu getMenuByName(String name) {
		WebMenu webMenu = null;
		ObjectContext ctx = cayenneService.sharedContext();
		Expression expression = ExpressionFactory.matchExp(WebMenu.NAME_PROPERTY, name);
		SelectQuery selectQuery = new SelectQuery(WebMenu.class, expression);
		List<WebMenu> menuList = ctx.performQuery(selectQuery);
		if (!menuList.isEmpty()) {
			webMenu = menuList.get(0);
		}
		return webMenu;
	}

	@Override
	public WebMenu getRootMenu() {
		Expression rootMenuExp = ExpressionFactory.matchExp(
				WebMenu.PARENT_WEB_MENU_PROPERTY, null);

		SelectQuery query = new SelectQuery(WebMenu.class, siteQualifier()
				.andExp(rootMenuExp));

		query.addPrefetch(WebMenu.PARENT_WEB_MENU_PROPERTY);
		query.addPrefetch(WebMenu.CHILDREN_MENUS_PROPERTY);

		@SuppressWarnings("unchecked")
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

	private void setUniqueWebMenuName(WebMenu menu) {
		String defaultName = "New menu item ";

		ObjectContext ctx = menu.getObjectContext();

		Expression expression = ExpressionFactory.likeIgnoreCaseExp(WebMenu.NAME_PROPERTY, defaultName + "%");
		SelectQuery selectQuery = new SelectQuery(WebMenu.class, expression);
		List<WebMenu> menuList = ctx.performQuery(selectQuery);
		//check list content
		if (!menuList.isEmpty()) {
			menu.setName(defaultName);
			HashSet<String> menuNames = new HashSet<>();
			for (WebMenu webMenu : menuList) {
				menuNames.add(webMenu.getName());
			}
			//check set content on defaultName
			if (menuNames.contains(defaultName)) {
				int i = 1;
				defaultName = defaultName + "(%d)";
				while (menuNames.contains(String.format(defaultName, i))) {
					i++;
				}
				defaultName = String.format(defaultName, i);
			}
		}
		menu.setName(defaultName);
	}
}
