package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.util.payment.CompleteInTransactionPayments;
import org.apache.cayenne.ObjectContext;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.updateTax;
import static ish.oncourse.util.payment.CompleteInTransactionPayments.CompleteResult;

public class ActionChangePayer extends APurchaseAction {

    private Contact contact;

    @Override
    protected void parse() {
        if (getParameter() != null)
            contact = getParameter().getValue(Contact.class);
    }

    @Override
    protected boolean validate() {
        return !contact.equals(getModel().getPayer()) &&
                validateAndCompleteInTransactionPayments() &&
                getController().getModel().getContacts().contains(contact) && lockedByVoucher();

    }

	private boolean lockedByVoucher() {
		if (getModel().getSelectedVouchers().size() > 0) {
			for (Voucher voucher : getModel().getSelectedVouchers()) {
				if (voucher.getContact() != null && !voucher.getContact().equals(contact)) {
					return false;
				}
			}
		}
		return true;
	}

    /**
     * Check if there are in_transaction payments on enrolling contact. If finds any it abandons them.
	 *
     */
    private boolean validateAndCompleteInTransactionPayments() {
        if (contact.getObjectId().isTemporary())
            return true;
        ObjectContext context = getController().getCayenneService().newContext();
		PaymentIn current = getModel().getPayment();
		IPaymentService paymentService = getController().getPaymentService();

		CompleteResult result = CompleteInTransactionPayments.valueOf(context, current, contact, paymentService).complite();
		switch (result) {
			case COMPLETE:
				getController().addWarning(PurchaseController.Message.payerHadUnfinishedPayment, contact.getFullName());
			case NOTHING_TO_COMPLETE:
				return true;
			case NOT_COMPLETE:
				getController().addError(PurchaseController.Message.dpsHasNotFinishedProcessPreviousPayment, contact.getFullName());
				return false;
			default: throw new IllegalArgumentException();
		}
    }

    @Override
    protected void makeAction() {

        Contact oldPayer = getController().getModel().getPayer();

        if (oldPayer!= null && !contact.equals(oldPayer))
            getController().getModel().removeVoucherProductItems(oldPayer);

        getController().getModel().setPayer(contact);

		if (!contact.equals(oldPayer))
		{
			for (Product product : getController().getModel().getProducts()) {
				if(product instanceof VoucherProduct) {
                    ActionEnableProductItemBuilder.valueOf(contact, product, getController()).build().action();
				}
			}
		}
        ActionUpdateTax actionUpdateTax = updateTax.createAction(getController());
        actionUpdateTax.action();
		
        getController().getModelValidator().validate();
        getController().refreshPrevOwingStatus();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
