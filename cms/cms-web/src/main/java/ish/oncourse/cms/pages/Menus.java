package ish.oncourse.cms.pages;

import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Menus {
	
	@Property
	@Inject
	private IWebMenuService webMenuService;
}
