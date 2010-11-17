package ish.oncourse.services.menu;

import java.util.List;

import ish.oncourse.model.WebMenu;

public interface IWebMenuService {
	WebMenu getRootMenu();
	List<WebMenu> loadByIds(Object... ids);
}
