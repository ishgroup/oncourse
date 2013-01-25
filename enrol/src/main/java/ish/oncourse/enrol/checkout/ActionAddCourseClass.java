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
    private boolean enableEnrolment = true;

    @Override
    protected void makeAction() {


        List<Contact> contacts = getModel().getContacts();
        for (Contact contact : contacts) {
            Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
            if (enrolment == null)
            {
                enrolment = getController().createEnrolment(courseClass, contact.getStudent());
                getModel().addEnrolment(enrolment);

                if (isEnableEnrolment())
                {
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

    public boolean isEnableEnrolment() {
        return enableEnrolment;
    }

    public void setEnableEnrolment(boolean enableEnrolment) {
        this.enableEnrolment = enableEnrolment;
    }
}
