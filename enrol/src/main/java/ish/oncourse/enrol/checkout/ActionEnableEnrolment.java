package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionEnableEnrolment extends APurchaseAction {

    private Enrolment enrolment;

    @Override
    protected void makeAction() {
        getModel().enableEnrolment(enrolment);
        InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForEnrolment(enrolment, getModel().getDiscounts());
        il.setInvoice(getModel().getInvoice());
        enrolment.setInvoiceLine(il);
        //we set status IN_TRANSACTION for enable enrolment in transaction to consider the enrolment in places check
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
    }

    @Override
    protected void parse() {
        if (getParameter() != null)
            enrolment = getParameter().getValue(Enrolment.class);
    }

    @Override
    protected boolean validate() {
        return !getModel().isEnrolmentEnabled(enrolment) && validateEnrolment();
    }

    boolean validateEnrolment() {
        /**
         * If enrolment was committed so we should not check these conditions
         */
        if (enrolment.getObjectId().isTemporary()) {
            if (enrolment.isDuplicated()) {
                getController().getModel().setErrorFor(enrolment,
                        duplicatedEnrolment.getMessage(getController().getMessages(), enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getCode()));
                return false;
            }
        }

        boolean hasPalces = hasAvailableEnrolmentPlaces(enrolment.getCourseClass());
        if (!hasPalces) {
            getController().getModel().setErrorFor(enrolment,
                    noCourseClassPlaces.getMessage(getController().getMessages(),
                            getController().getClassName(enrolment.getCourseClass()),
                            enrolment.getCourseClass().getCourse().getCode()));
            return false;
        }
        if (enrolment.getCourseClass().hasEnded()) {
            getController().getModel().setErrorFor(enrolment,
                    courseClassEnded.getMessage(getController().getMessages(),
                            getController().getClassName(enrolment.getCourseClass()),
                            enrolment.getCourseClass().getCourse().getCode()));
            return false;
        }
        return true;
    }

    boolean hasAvailableEnrolmentPlaces(CourseClass courseClass) {
        List<Enrolment> enrolments = courseClass.getEnrolments();

        Expression expression = ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS, EnrolmentStatus.IN_TRANSACTION);
        List<Enrolment> activeEnrolments = expression.filterObjects(enrolments);
        int count = activeEnrolments.contains(enrolment) ? activeEnrolments.size() : activeEnrolments.size() + 1;
        return courseClass.getMaximumPlaces() >= count;
    }

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(Enrolment enrolment) {
        this.enrolment = enrolment;
    }
}
