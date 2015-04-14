package ish.oncourse.admin.components;

import ish.oncourse.model.College;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class EditTabs {

	@Parameter
	@Property
	private College college;
	
	@Parameter
	private String selected;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}
	
	@AfterRender
	void afterRender() {
		javaScriptSupport.addScript("jQuery('#%s').parent().addClass('active');", selected);
	}
	
	public String getOverviewPageName() {
		return "college/overview";
	}
	
	public String getWebPageName() {
		return "college/web";
	}
	
	public String getPreferencesPageName() {
		return "college/preferences";
	}
	
	public String getBillingPageName() {
		return "college/billing";
	}
	
	public String getInstructionsPageName() {
		return "college/instructions";
	}

	public String getSupportLoginPageName() {
		return "college/supportLogin";
	}
}
