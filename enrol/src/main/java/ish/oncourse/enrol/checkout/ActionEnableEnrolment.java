package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

public class ActionEnableEnrolment extends APurchaseAction {

    private Enrolment enrolment;

    @Override
    protected void makeAction() {
        getModel().enableEnrolment(enrolment);
        InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForEnrolment(enrolment, getModel().getDiscounts());
        il.setInvoice(getModel().getInvoice());
        il.setEnrolment(getEnrolment());
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
        return !getModel().isEnrolmentEnabled(enrolment) &&
                EnrolmentValidator.valueOf(enrolment, false, getController()).validate();
    }

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(Enrolment enrolment) {
        this.enrolment = enrolment;
    }
}
