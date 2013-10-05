package ish.oncourse.enrol.checkout;

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
		} else if (getController().isEditPayment()) {
			getController().setState(PurchaseController.State.paymentProgress);
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


		if (getController().isEditCorporatePass()) {
			if (getController().getModel().getCorporatePass() == null) {
				getController().addError(corporatePassShouldBeEntered);
				return false;
			} else
				return getController().validateEnrolments(true);
		} else if (getController().isEditPayment()) {
			return getController().validateEnrolments(true) && getController().validateProductItems();
		} else
			throw new IllegalArgumentException();
	}
}
