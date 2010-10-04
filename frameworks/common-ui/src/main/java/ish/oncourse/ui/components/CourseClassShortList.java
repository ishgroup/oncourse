package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * Based on CourseClassCookieView
 * 
 * @author ksenia
 * 
 */
public class CourseClassShortList {

	@Property
	@Parameter
	private List<CourseClass> orderedClasses;

	@Property
	private CourseClass courseClass;

	public boolean isHasObjects() {
		return orderedClasses != null && !orderedClasses.isEmpty();
	}

	public boolean isSessionPage() {
		// ISHWebObjects/com.ish.webobjects.appserver.ISHComponent.hasSecureParentPage[357]
		// WOComponent parent = parentPage();
		// return parent instanceof ISHComponent &&
		// SecurityPolicy.SECURE.equals( ( ( ISHComponent )parent
		// ).securityPolicy( null ) );
		return false;
	}

	public boolean isShowEnrolNow() {
		// WillowDynamicColleges/willow.view.CourseClassCookieView.showEnrolNow[102]
		return false;
	}

	public String getEnrolLinkText() {
		return "Enrol in "
				+ (orderedClasses.size() > 1 ? "these classes" : "this class");
	}
}
