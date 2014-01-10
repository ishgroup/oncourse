package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class Class {

    @Persist
	@Property
	private CourseClass courseClass;

	@Inject
	private ICayenneService cayenneService;

    @Inject
    @Property
    private Request request;
	
	@InjectPage
	private PageNotFound pageNotFound;

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private IPortalService portalService;

	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+"))
        {
			long idLong = Long.parseLong(id);
			/**
			 * We need to use not shared cayenne context to be sure that we get actual data
			 * for all related objects of the course class (sessions, room, sites).
			 * It is important when we define timezone for start and end time.
			 */
            this.courseClass = Cayenne.objectForPK(cayenneService.newNonReplicatingContext(),CourseClass.class, idLong);
            return null;
		} else {
			return pageNotFound;
        }
	}


    public boolean isTutor(){
        return authenticationService.isTutor();
    }

    public boolean needApprove()
    {
        return authenticationService.isTutor() && !portalService.isApproved(authenticationService.getUser(), courseClass);
    }

	public boolean isClassWithModules(){

		boolean result=false;

		if(authenticationService.getUser().getStudent()!=null){

			for(Enrolment enrolment : courseClass.getValidEnrolments()){

				if(enrolment.getStudent().getContact().getId().equals(authenticationService.getUser().getId())){
					result=true;
					break;
				}

			}
		}

		return (!courseClass.getCourse().getModules().isEmpty() || courseClass.getCourse().getQualification()!=null) &&  result;
	}

	public boolean isHasResources(){

		return  !portalService.getAttachedFiles(courseClass, authenticationService.getUser()).isEmpty();
	}

}
