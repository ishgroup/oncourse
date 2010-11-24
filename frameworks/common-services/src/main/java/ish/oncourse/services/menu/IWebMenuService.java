package ish.oncourse.services.menu;

import java.util.List;

import ish.oncourse.model.WebMenu;

public interface IWebMenuService {
	
	/**
	 * Creates new menu item
	 * @return menu item
	 */
	WebMenu newMenu();
	
	/**
	 * Returns top level menu item.
	 * @return top level item.
	 */
	WebMenu getRootMenu();
	
	/**
	 * Loads menu items by id
	 * @param ids
	 * @return menu items
	 */
	List<WebMenu> loadByIds(Object... ids);
}
