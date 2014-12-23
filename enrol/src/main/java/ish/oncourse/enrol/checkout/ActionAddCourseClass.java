package ish.oncourse.enrol.checkout;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.Application;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;

import java.util.List;

/**
 * User: andrey
 */
public class ActionAddCourseClass extends APurchaseAction {

    private CourseClass courseClass;

    @Override
    protected void makeAction() {


        List<Contact> contacts = getModel().getContacts();
        for (Contact contact : contacts) {
			if (!contact.getIsCompany()) {
				
				//if courses enrolments available only by application and the student has no offered application for this course
				//then create new application
				//else proceed to enrolment creation 
				if (CourseEnrolmentType.ENROLMENT_BY_APPLICATION.equals(courseClass.getCourse().getEnrolmentType()) &&
						getController().getApplicationService().findOfferedApplicationBy(courseClass.getCourse(),contact.getStudent()) == null) {
					
					Application application = getModel().getApplicationBy(contact, courseClass.getCourse());
					if (application == null) {
						application = getController().createApplication(contact.getStudent(), courseClass.getCourse());
						getModel().addApplication(application);

						if (!getModel().addedAsGuardian(contact)) {
							ActionEnableApplication action = PurchaseController.Action.enableApplication.createAction(getController());
							action.setApplication(application);
							action.action();
						}
					}
					
				} else {
					Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
					if (enrolment == null) {
						enrolment = getController().createEnrolment(courseClass, contact.getStudent());
						getModel().addEnrolment(enrolment);

						if (!getModel().addedAsGuardian(contact)) {
							ActionEnableEnrolment action = PurchaseController.Action.enableEnrolment.createAction(getController());
							action.setEnrolment(enrolment);
							action.action();
						}
					}
				}
            }
        }
    }

    @Override
    protected void parse() {
        if (getParameter() != null)
            courseClass = getParameter().getValue(CourseClass.class);
    }

    @Override
    protected boolean validate() {
        return true;
    }

    public CourseClass getCourseClass() {
        return courseClass;
    }

    public void setCourseClass(CourseClass courseClass) {
        this.courseClass = courseClass;
    }
}
