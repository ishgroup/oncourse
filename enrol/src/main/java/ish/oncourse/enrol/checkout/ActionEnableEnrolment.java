package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.enrol.checkout.model.CreatePaymentPlanInvoiceNode;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

public class ActionEnableEnrolment extends APurchaseAction {

    private Enrolment enrolment;

    @Override
    protected void makeAction() {
        getModel().enableEnrolment(enrolment);

        if (enrolment.getCourseClass().getPaymentPlanLines().isEmpty() || !getController().isSupportPaymentPlan()) {
            InvoiceLine il = getController().getInvoiceProcessingService()
					.createInvoiceLineForEnrolment(enrolment, getModel().getPayer().getTaxOverride());
            il.setInvoice(getModel().getInvoice());
            il.setEnrolment(getEnrolment());
        } else {
            InvoiceNode invoiceNode = CreatePaymentPlanInvoiceNode.valueOf(enrolment, getController()).create();
            getController().getModel().addPaymentPlanInvoice(invoiceNode);
        }
        //we set status IN_TRANSACTION for enable enrolment in transaction to consider the enrolment in places check
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
		getController().updateDiscountApplied();
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
