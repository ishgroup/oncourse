package ish.oncourse.admin.components;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class Menu {

	@Inject
	private JavaScriptSupport jsSupport;
	
	@Parameter
	private String selected;
	
	@AfterRender
	void afterRender() {
		jsSupport.addScript("jQuery('#%s').parent().addClass('active');", selected);
	}
}
