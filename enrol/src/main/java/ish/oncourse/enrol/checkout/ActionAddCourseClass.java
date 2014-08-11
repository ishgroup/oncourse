package ish.oncourse.enrol.checkout;

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
            if (!getModel().addedAsGuardian(contact)) {
                Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
                if (enrolment == null) {
                    enrolment = getController().createEnrolment(courseClass, contact.getStudent());
                    getModel().addEnrolment(enrolment);

                    ActionEnableEnrolment action = PurchaseController.Action.enableEnrolment.createAction(getController());
                    action.setEnrolment(enrolment);
                    action.action();
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
