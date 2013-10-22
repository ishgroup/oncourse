package ish.oncourse.portal.components.courseclass;



import ish.common.types.OutcomeStatus;
import ish.oncourse.model.*;

import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.courseclass.ICourseClassService;
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
        for(Outcome outcome : enrolment.getOutcomes()){
          if(outcome.getModuleId().equals(moduleId))
              for(OutcomeStatus status : OutcomeStatus.values()){
                  if(status.getDatabaseValue().equals(outcome.getStatus()))
                      return status;
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



}
