package ish.oncourse.ui.components;

import ish.oncourse.model.WebMenu;
import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class BodyLayout {
	@Inject
	private IWebMenuService webMenuService;
	
	public WebMenu getMenu(){
		return webMenuService.getMainMenu();
	}
}
