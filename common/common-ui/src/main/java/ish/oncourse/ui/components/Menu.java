package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.services.menu.IWebMenuService;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.Map;

public class Menu {

	@Property
	@Parameter
	private WebMenu rootMenu;

	@Inject
	private IWebMenuService webMenuService;

	@Property
	@Component(id = "menuItem", parameters = { "menu=currentMenu",
			"childPosition=currentChildPosition" })
	private MenuItem menuItem;

	private WebMenu currentMenu;

	private Map<WebMenu, Integer> childPositions;
	
	@SetupRender
	boolean beforeRender() {
		childPositions = new HashMap<>();
		if (rootMenu == null) {
			rootMenu = webMenuService.getRootMenu();
		}
		
		return rootMenu != null;
	}

	public WebMenu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(final WebMenu menu) {
		if (!childPositions.containsKey(menu)) {
			childPositions.put(menu, 0);
		}
		this.currentMenu = menu;
	}

	public int getCurrentChildPosition() {
		return childPositions.get(this.currentMenu);
	}

	public void setCurrentChildPosition(final int pos) {
		this.childPositions.put(currentMenu, pos);
	}
	
}
