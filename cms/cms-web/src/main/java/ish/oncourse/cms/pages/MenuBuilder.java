package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


public class MenuBuilder {	
	
	@Inject
	private IWebMenuService webMenuService;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@AfterRender
	public void afterRender() {
		javaScriptSupport.addScript("jQuery('#%s').menubuilder();");
	}
	
	public WebMenu getMenu() {
		return webMenuService.getMainMenu();
	}

}
