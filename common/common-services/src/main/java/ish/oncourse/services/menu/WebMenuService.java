package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.BaseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashSet;
import java.util.List;

public class WebMenuService extends BaseService<WebMenu> implements IWebMenuService {


	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	@Override
	public WebMenu createMenu(WebSite site) {
		ObjectContext ctx = site.getObjectContext();
		
		WebMenu menu = ctx.newObject(WebMenu.class);		
		menu.setWebSiteVersion(ctx.localObject(webSiteVersionService.getCurrentVersion()));

		WebMenu rootMenu = getRootMenu();
		menu.setParentWebMenu(ctx.localObject(rootMenu));
		
		Integer size = 0;
		List<WebMenu> childrenMenus = rootMenu.getChildrenMenus();
		if (!childrenMenus.isEmpty()) {
			size = childrenMenus.size();
		}
		updateWeight(menu, size, null);
		setUniqueWebMenuName(menu);
		return menu;
	}



	public List<WebMenu> getChildrenBy(WebMenu parent) {
		return GetMenuChildren.valueOf(parent, cayenneService.sharedContext(), true).get();
	}


	/**
	 * @return All child menus with published nodes for this menu.
	 */
	public List<WebMenu> getNavigableChildrenBy(WebMenu parent) {
		List<WebMenu> childrent = getChildrenBy(parent);
		
		Expression filter =  webSiteVersionService.isEditor() ? WebMenu.WEB_NODE.isNotNull() 
				: WebMenu.WEB_NODE.dot(WebNode.PUBLISHED).eq(true);
		
		return filter.orExp(WebMenu.WEB_NODE.isNull().andExp(WebMenu.URL.isNotNull()))
						.filterObjects(childrent);
	}

	@Override
	public WebMenu getMenuByNameAndParentMenu(String name, WebMenu parentMenu) {
		ObjectContext ctx = cayenneService.sharedContext();

		return ObjectSelect.query(WebMenu.class).and(WebMenu.NAME.eq(name))
				.and(WebMenu.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion()))
				.and(WebMenu.PARENT_WEB_MENU.eq(parentMenu)).selectOne(ctx);
	}

	@Override
	public WebMenu getRootMenu() {
		return ObjectSelect.query(WebMenu.class)
				.and(siteQualifier())
				.and(WebMenu.PARENT_WEB_MENU.isNull())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroup(getCacheGroup())
				.selectOne(cayenneService.sharedContext());
	}


	public void updateWeight(WebMenu menu, int weight, WebMenu oldParent) {

		Integer oldWeight = menu.getWeight();
		menu.setWeight(weight);

		List<WebMenu> siblings = getChildrenBy(menu.getParentWebMenu());
		if (oldWeight == null) {
			oldWeight = siblings.size();
		}
		// if we drag menu from another parent, we should update the weights in
		// old tree
		if (oldParent != null && !oldParent.getId().equals(menu.getParentWebMenu().getId())) {
			List<WebMenu> oldSiblings = getChildrenBy(oldParent);
			for (int i = 0; i < oldSiblings.size(); i++) {
				oldSiblings.get(i).setWeight(i);
			}
			oldWeight = siblings.size();
		}

		for (int i = 0; i < siblings.size(); i++) {
			WebMenu m = siblings.get(i);
			if (m.getPersistenceState() != PersistenceState.NEW && !m.getId().equals(menu.getId())) {
				if (m.getWeight() == weight) {
					if (i < weight) {
						if (oldWeight > weight) {
							m.setWeight(i + 1);
						} else {
							m.setWeight(i);
						}
					} else {
						if (i == weight) {
							if (oldWeight > weight) {
								m.setWeight(i + 1);
							} else {
								m.setWeight(i - 1);
							}
						} else {
							m.setWeight(i);
						}
					}
				} else {
					m.setWeight(i);
				}
			}
		}
	}

	private Expression siteQualifier() {
		WebSite site = webSiteService.getCurrentWebSite();
		Expression expression = (site == null) ? WebMenu.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(webSiteService.getCurrentCollege()):
				WebMenu.WEB_SITE_VERSION.eq(webSiteVersionService.getCurrentVersion());
		return expression;
	}

	private void setUniqueWebMenuName(WebMenu menu) {
		String defaultName = "New menu item";

		ObjectContext ctx = menu.getObjectContext();

		List<WebMenu> menuList = ObjectSelect.query(WebMenu.class).and(WebMenu.NAME.like(defaultName + "%")).select(ctx);

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
