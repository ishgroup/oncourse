package ish.oncourse.admin.components;

import ish.oncourse.admin.pages.Index;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class Menu {

	@Inject
	private JavaScriptSupport jsSupport;
	
	@InjectPage
	private Index index;
	
	@Inject
	private Request request;
	
	
	@Parameter
	private String selected;
	
	@AfterRender
	void afterRender() {
		jsSupport.addScript("jQuery('#%s').parents('li.selectable').addClass('active');", selected);
	}
}
