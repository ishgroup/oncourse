package ish.oncourse.cms.pages;

import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Site {
	
	@Inject
	private Request request;
	
	@Property
	@Inject
	private IWebMenuService webMenuService;
}
