package ish.oncourse.enrol.checkout;

import ish.common.types.ConfirmationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.ProductStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.ProductItem;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;


public class ActionMakePayment extends APurchaseAction {
	@Override
	protected void makeAction() {
		if (getController().isEditCorporatePass()) {
			getModel().deletePayment();
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
		} else if (getController().isEditPayment()) {
			//check that student don't make any purchase or enrolments here, also check that no amount owing and credit nodes applied
			if (getModel().getInvoice().getInvoiceLines().isEmpty() && getController().getPaymentEditorDelegate().isZeroPayment()) {
				getModel().deletePayment();
				getModel().deleteInvoice();
				getController().setState(PurchaseController.State.paymentResult);
				getController().setConfirmationStatus(ConfirmationStatus.NOT_SENT);
			} else {
				getController().setState(PurchaseController.State.paymentProgress);
			}
		} else
			throw new IllegalArgumentException();

		adjustSortOrder();
		getModel().getObjectContext().commitChanges();
	}

	private void adjustSortOrder() {
		List<InvoiceLine> invoiceLines = getModel().getInvoice().getInvoiceLines();
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
