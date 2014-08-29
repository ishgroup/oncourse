package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Class {

    @Persist
	@Property
	private CourseClass courseClass;

	@Inject
	private ICayenneService cayenneService;

    @Inject
    @Property
    private Request request;

	@Property
	private boolean showLocation = false;

	@Inject
	private IWebSiteService webSiteService;

	@InjectPage
	private PageNotFound pageNotFound;

    @Inject
    private IPortalService portalService;

	private static final String LOCATION = "location";

	@Property
	private String target;

	Object onActivate(String id, String tab) {

		if (tab.equals(LOCATION)) {
			showLocation = true;
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
            return this.courseClass == null ? pageNotFound:null;
		} else {
			return pageNotFound;
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
}
