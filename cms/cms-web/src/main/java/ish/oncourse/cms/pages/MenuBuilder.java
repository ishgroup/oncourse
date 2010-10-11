package ish.oncourse.cms.pages;

import ish.oncourse.services.node.IWebNodeService;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;


public class MenuBuilder {
	
	@Property
	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@AfterRender
	public void afterRender() {
		javaScriptSupport.addScript("jQuery('#%s').menubuilder();");
	}

}
