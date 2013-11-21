package ish.oncourse.portal.components.courseclass;



import ish.common.types.OutcomeStatus;
import ish.oncourse.model.*;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:28 AM
 */
public class ClassResults {

    @Property
    @Parameter
    private CourseClass courseClass;

    @Inject
    private IAuthenticationService authenticationService;

    @Property
    private List<Module> modules;

    @Property
    private Module module;

    private Enrolment enrolment;

    @SetupRender
    boolean setupRender() {

         modules=courseClass.getCourse().getModules();
         courseClass.getValidEnrolments();

         for (Enrolment enrolment : courseClass.getValidEnrolments())
             if(enrolment.getStudent().getContact().getId().equals(authenticationService.getUser().getId())){
                this.enrolment= enrolment;
                break;
             }
      return true;
     }



	private OutcomeStatus getOutComeStatus(Long moduleId){
		for (Outcome outcome : enrolment.getOutcomes()) {
			Module module = outcome.getModule();
			if (module != null && module.getId().equals(moduleId) && outcome.getStatus() != null) {
				return outcome.getStatus();
			}
		}
		return OutcomeStatus.STATUS_NOT_SET;
	}


    public String getOutComeDisplayName(Long moduleId){
          return  getOutComeStatus(moduleId).getDisplayName();
    }

    public String getOutComeResult(Long moduleId){
        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(getOutComeStatus(moduleId))){
            return "PASS";
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(getOutComeStatus(moduleId))){
            return "NO RESULT";
        }
        return "FAILED";

    }


    public String getOutComeClass(Long moduleId){

        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(getOutComeStatus(moduleId))){
            return "text-success";
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(getOutComeStatus(moduleId))){
            return "text-info";
        }
        return "text-danger";
    }

    public Qualification getQualification(){

        if(courseClass.getCourse().getQualification()!=null)
            return courseClass.getCourse().getQualification();

        return null;
    }


    public boolean isVisible(){

        boolean result=false;

        if(authenticationService.getUser().getStudent()!=null){
            for(Enrolment enrolment : authenticationService.getUser().getStudent().getEnrolments()){
                if(enrolment.getCourseClass().getId().equals(courseClass.getId())){
                    result=true;
                    break;
                }
            }
        }

        return (!courseClass.getCourse().getModules().isEmpty() || courseClass.getCourse().getQualification()!=null) &&  result;
    }


    public boolean isHasModules(){
        return !courseClass.getCourse().getModules().isEmpty();
    }
}
