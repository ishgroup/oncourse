package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Based on CourseClassCookieView
 * 
 * @author ksenia
 * 
 */
public class CourseClassShortList {

	@Inject
	private IWebSiteService webSiteService;

	@Property
	@Parameter
	private List<CourseClass> orderedClasses;

	@Property
	private CourseClass courseClass;

	public boolean isHasObjects() {
		return orderedClasses != null && !orderedClasses.isEmpty();
	}

	public boolean isPaymentGatewayEnabled() {
		return webSiteService.getCurrentCollege().getIsWebSitePaymentsEnabled();
	}

	public String getEnrolLinkText() {
		return "Enrol in " + (orderedClasses.size() > 1 ? "these classes" : "this class");
	}
}
