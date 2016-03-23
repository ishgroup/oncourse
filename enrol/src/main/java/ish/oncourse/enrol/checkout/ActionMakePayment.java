package ish.oncourse.enrol.checkout;

import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.ProductStatus;
import ish.oncourse.enrol.checkout.model.InvoiceNode;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.ProductItem;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;


public class ActionMakePayment extends APurchaseAction {
	@Override
	protected void makeAction() {
		if (getController().isEditCorporatePass()) {
			makeCorporatePass();
		} else if (getController().isEditPayment()) {
			makePayment();
		} else
			throw new IllegalArgumentException();

		adjustSortOrder();
        getModel().deleteDisabledItems();
		getModel().getObjectContext().commitChanges();
	}

	private void makeCorporatePass() {
		getModel().deletePayment();
		if (getModel().getInvoice().getInvoiceLines().isEmpty()) {
			getModel().deleteInvoice();
		}
		getController().setState(PurchaseController.State.paymentResult);
		List<Enrolment> enrolments = getController().getModel().getAllEnabledEnrolments();
		for (Enrolment enrolment : enrolments) {
			enrolment.setStatus(EnrolmentStatus.SUCCESS);
		}
		List<ProductItem> productItems = getController().getModel().getAllEnabledProductItems();
		for (ProductItem productItem : productItems) {
			productItem.setStatus(ProductStatus.ACTIVE);
		}
		getController().setConfirmationStatus(ConfirmationStatus.NOT_SENT);
		getController().commitApplications();
	}

	private void makePayment() {
		//check that student don't make any purchase or enrolments here, also check that no amount owing and credit nodes applied
		if (isApplicationOnly()) {
			getModel().deletePayment();
			getModel().deleteInvoice();
			getController().setState(PurchaseController.State.paymentResult);
			getController().setConfirmationStatus(ConfirmationStatus.NOT_SENT);
			getController().commitApplications();
		} else {
			if (getModel().getInvoice().getInvoiceLines().isEmpty()) {
				getModel().deleteInvoice();
			}
			getController().setState(PurchaseController.State.paymentProgress);
		}
	}

	private boolean isApplicationOnly() {
		return !getModel().getAllEnabledApplications().isEmpty() &&
				getModel().getInvoice().getInvoiceLines().isEmpty() &&
				getModel().getPaymentPlanInvoices().isEmpty() &&
				getController().getPaymentEditorDelegate().isZeroPayment();
	}

	private void adjustSortOrder() {
		adjustSortOrder(getModel().getInvoice());
		for (InvoiceNode invoice : getModel().getPaymentPlanInvoices()) {
			adjustSortOrder(invoice.getInvoice());
		}
	}

	private void adjustSortOrder(Invoice invoice) {
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
		for (int i = 0; i < invoiceLines.size(); i++) {
			InvoiceLine invoiceLine = invoiceLines.get(i);
			invoiceLine.setSortOrder(i);
		}
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		if (getController().isEditCorporatePass() && !getController().isCorporatePassPaymentEnabled())
		{
			getController().addError(corporatePassNotEnabled);
			return false;
		}

		if (getController().isEditPayment() && !getController().isCreditCardPaymentEnabled())
		{
			getController().addError(creditCardPaymentNotEnabled);
			return false;
		}

		for (Enrolment enrolment : getModel().getAllEnabledEnrolments()) {
			boolean hasPlaces = getController().hasAvailableEnrolmentPlaces(enrolment);
			if (!hasPlaces) {
				String message = noPlacesLeft.getMessage(getController().getMessages(),
						getController().getClassName(enrolment.getCourseClass()));
				getController().getModel().setErrorFor(enrolment,message);
				getController().getErrors().put(noPlacesLeft.name(), message);

				return false;
			}
		}

		if (getController().isEditCorporatePass()) {
			if (getController().getModel().getCorporatePass() == null) {
				getController().addError(corporatePassShouldBeEntered);
				return false;
			} else
				return getController().getModelValidator().validate();
		} else if (getController().isEditPayment()) {
			return getController().getModelValidator().validate();
		} else
			throw new IllegalArgumentException();
	}
}
