package ish.oncourse.portal.components.courseclass;


import ish.common.types.OutcomeStatus;
import ish.oncourse.model.*;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang.StringUtils;
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


	@Inject
	private ICayenneService cayenneService;

    @Inject
    private IPortalService portalService;

	@Parameter
	private boolean activeTab = false;

    @Property
    @Parameter
    private CourseClass courseClass;

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

		Student student = portalService.getContact().getStudent();
		Expression exp = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, student);
		enrolments = exp.filterObjects(courseClass.getValidEnrolments());

		return true;
    }

    public String getOutComeDisplayName(){
        return  outcome.getStatus() != null ? outcome.getStatus().getDisplayName() : OutcomeStatus.STATUS_NOT_SET.getDisplayName();
    }

    public String getOutComeResult(){
        if (OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED.equals(outcome.getStatus())){
            return messages.format("outcomeResult.STATUS_NON_ASSESSABLE_COMPLETED",
                    outcome.getEnrolment().getCourseClass().getCourse().getName());
        }

        if (OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED.equals(outcome.getStatus())){
            return messages.format("outcomeResult.STATUS_NON_ASSESSABLE_NOT_COMPLETED", outcome.getEnrolment().getCourseClass().getUniqueIdentifier());
        }

        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(outcome.getStatus())){
            return messages.get("outcomeResult.STATUSES_VALID_FOR_CERTIFICATE");
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus())){
            return messages.get("outcomeResult.STATUS_NOT_SET");
        }
        return messages.get("outcomeResult.NOT_YET_COMPETENT");

    }

    public String getOutComeClass(){

        if (OutcomeStatus.STATUS_NON_ASSESSABLE_COMPLETED.equals(outcome.getStatus()))
        {
            return messages.get("outcomeClass.STATUS_NON_ASSESSABLE_COMPLETED");
        }

        if (OutcomeStatus.STATUS_NON_ASSESSABLE_NOT_COMPLETED.equals(outcome.getStatus()))
        {
            return messages.get("outcomeClass.STATUS_NON_ASSESSABLE_NOT_COMPLETED");
        }

        if(OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE.contains(outcome.getStatus())){
            return messages.get("outcomeClass.STATUSES_VALID_FOR_CERTIFICATE");
        }
        if(OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus())){
            return messages.get("outcomeClass.STATUS_NOT_SET");
        }
        return messages.get("outcomeClass.NOT_YET_COMPETENT");
    }

    public Qualification getQualification(){
        return courseClass.getCourse().getQualification();
    }

    public boolean isHasModules(){
        return !courseClass.getCourse().getModules().isEmpty();
    }

	public boolean isNotSetStatus() {

		return OutcomeStatus.STATUS_NOT_SET.equals(outcome.getStatus());
	}

    public boolean needResult()
    {
        return isHasModules() || !isNotSetStatus();
    }

    public boolean isNew()
    {
        return portalService.isNew(outcome);
    }

	public String getActiveClass() {
		return activeTab ? "active" : StringUtils.EMPTY;
	}

}
