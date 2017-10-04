package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.dashboard.ClassTab;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.portal.services.dashboard.ClassTab.*;

public class Class {

	@Property
	private CourseClass courseClass;

	@Property
	private Boolean isTutor;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private PreferenceController preferenceController;

    @Inject
    @Property
    private Request request;

	@Property
	private ClassTab activeTab =  DETAILS;

	@Inject
	private IWebSiteService webSiteService;

	@InjectPage
	private Index indexPage;

    @Inject
    private IPortalService portalService;

	private static final String LOCATION = "location";

	@Property
	private String target;

	Object onActivate(String id, String tab) {

		for (ClassTab it : ClassTab.values()) {
			if (it.getKey().equals(tab)) {
				activeTab = it;
				break;
			}	
		}

		return onActivate(id);
	}

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+"))
        {
			long idLong = Long.parseLong(id);
			/**
			 * We need to use not shared cayenne context to be sure that we get actual data
			 * for all related objects of the course class (sessions, room, sites).
			 * It is important when we define timezone for start and end time.
			 */
            this.courseClass = portalService.getCourseClassBy(idLong);
			
			if (this.courseClass != null) {
				isTutor = portalService.getContact().getTutor() != null && portalService.isTutorFor(this.courseClass);
				return null;
			} else {
				return indexPage;
			}			
		} else {
			return indexPage;
        }
	}


    public boolean isTutor(){
        return portalService.getContact().getTutor() != null;
    }

    public boolean needApprove()
    {
        return portalService.getContact().getTutor() != null && !portalService.isApproved(courseClass);
    }

	public boolean hasResults() {
		CourseClass courseClass = cayenneService.sharedContext().localObject(this.courseClass);
		return portalService.hasResult(courseClass);
	}

	public boolean hasResources() {

		return !portalService.getResourcesBy(courseClass).isEmpty();
	}

	public String getUrl() {
		return portalService.getUrlBy(courseClass);
	}
	
	public boolean isActive(String tabKey) {
		return activeTab.getKey().equals(tabKey);
	}

	public boolean isShowOutcomes() {
		return isTutor && preferenceController.getOutcomeMarkingViaPortal();
	}
}
