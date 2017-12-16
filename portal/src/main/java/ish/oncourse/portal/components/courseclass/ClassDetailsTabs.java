package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.dashboard.ClassTab;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.portal.services.dashboard.ClassTab.DETAILS;

public class ClassDetailsTabs {

	@Property
	@Parameter
	private boolean isTutor;

	@Parameter
	private ClassTab activeTab = DETAILS;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPortalService portalService;

	@Inject
	private PreferenceController preferenceController;
	
	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	private Request request;

	public String getContextPath() {
		return request.getContextPath();
	}

	public boolean hasResults(){
		CourseClass courseClass = cayenneService.newContext().localObject(this.courseClass);
		return portalService.hasResult(courseClass);
	}

	public boolean hasResources(){
		return  !portalService.getResourcesBy(this.courseClass).isEmpty();
	}

	public boolean allowToEditOutcomes() {
		return  isTutor && preferenceController.getOutcomeMarkingViaPortal() && !courseClass.getValidEnrolments().isEmpty();
	}
	
	public String getActiveClass(String tabKey) {
		return activeTab.getKey().equals(tabKey) ? "active" : StringUtils.EMPTY;
	}
}
