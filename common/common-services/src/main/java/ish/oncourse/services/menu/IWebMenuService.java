package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.IBaseService;

public interface IWebMenuService {
	/**
	 * Returns top level menu item.
	 *
	 * @return top level item.
	 */
	WebMenu getRootMenu();

	/**
	 * @see IBaseService#findById(Long)
	 */
	WebMenu findById(Long willowId);

	/**
	 * @param currentSite
	 * @return new menu item with unique name
	 */

	WebMenu createMenu(WebSite currentSite);

	/**
	 * @param name
	 * @return null or WebMenu if it exists with this name
	 */
	WebMenu getMenuByNameAndParentMenu(String name, WebMenu parentMenu);
}
