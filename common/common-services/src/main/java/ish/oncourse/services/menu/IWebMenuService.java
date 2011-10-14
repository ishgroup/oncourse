package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import ish.oncourse.services.IBaseService;

public interface IWebMenuService {	
	/**
	 * Returns top level menu item.
	 * @return top level item.
	 */
	WebMenu getRootMenu();
	
	/**
	 * @see IBaseService#findById(Long)
	 */
	WebMenu findById(Long willowId);
}
