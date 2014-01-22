package ish.oncourse.portal.components.courseclass;




import ish.common.types.OutcomeStatus;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 10:28 AM
 */
public class ClassResults {

	private static final String KEY_withoutQualification = "withoutQualification";
	private static final String KEY_notLinkedToComponents = "notLinkedToComponents";

	@Inject
	private ICayenneService cayenneService;

    @Property
    @Parameter
    private CourseClass courseClass;

    @Inject
    private IAuthenticationService authenticationService;


    @Property
    private Outcome outcome;


	@Property
	private List<Enrolment> enrolments;

	@Property
    private Enrolment enrolment;

	@Inject
	private Messages messages;

    @SetupRender
    boolean setupRender() {

		CourseClass courseClass = cayenneService.sharedContext().localObject(this.courseClass);

		Student student = authenticationService.getUser().getStudent();
		Expression exp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student);
		enrolments = exp.filterObjects(courseClass.getValidEnrolments());

		return true;
    }

    public String getOutComeDisplayName(){
          return  outcome.getStatus().getDisplayName();
    }

    public String getOutComeResult(){
        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(outcome.getStatus())){
            return "PASS";
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus())){
            return "NO RESULT";
        }
        return "FAILED";

    }

    public String getOutComeClass(){

        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(outcome.getStatus())){
            return "text-success";
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus())){
            return "text-info";
        }
        return "text-danger";
    }

    public Qualification getQualification(){

        if(courseClass.getCourse().getQualification()!=null)
            return courseClass.getCourse().getQualification();
        return null;
    }

    public boolean isHasModules(){
        return !courseClass.getCourse().getModules().isEmpty();
    }

	public String getDefaultTitle() {

		return isHasModules() ? messages.get(KEY_withoutQualification) : messages.get(KEY_notLinkedToComponents);
	}

	public boolean isNotSetStatus() {

		return OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus());
	}

	public boolean isCompleted() {
		return OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED.equals(outcome.getStatus());
	}

}
