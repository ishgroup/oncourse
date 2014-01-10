package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class ClassTabs {



    @Inject
    private IPortalService portalService;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	@Property
	private IAuthenticationService authService;

	@Inject
	private Request request;
	
	public String getContextPath() {
		return request.getContextPath();
	}

    public boolean isClassWithModules(){

       boolean result=false;

             if(authService.getUser().getStudent()!=null){

                for(Enrolment enrolment : courseClass.getValidEnrolments()){

                    if(enrolment.getStudent().getContact().getId().equals(authService.getUser().getId())){
                        result=true;
                        break;
                    }

                }
             }

        return (!courseClass.getCourse().getModules().isEmpty() || courseClass.getCourse().getQualification()!=null) &&  result;
    }


    public boolean isHasResources(){

        return  !portalService.getAttachedFiles(courseClass, authService.getUser()).isEmpty();
    }




}
