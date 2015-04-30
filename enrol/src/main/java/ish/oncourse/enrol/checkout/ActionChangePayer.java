package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentInAbandon;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Calendar;
import java.util.List;

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
                getController().getModel().getContacts().contains(contact);

    }

    /**
     * Check if there are in_transaction payments on enrolling contact. If finds any it abandons them.
	 *
     */
    private boolean validateAndCompleteInTransactionPayments() {
        if (contact.getObjectId().isTemporary())
            return true;
        ObjectContext context = getController().getCayenneService().newContext();

	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);
	    
		List<PaymentIn> payments = ObjectSelect.query(PaymentIn.class).
				where(PaymentIn.STATUS.in(PaymentStatus.IN_TRANSACTION, PaymentStatus.CARD_DETAILS_REQUIRED)).
	            and(PaymentIn.CONTACT.eq(contact)).
	            and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
				and(PaymentIn.CREATED.gt(calendar.getTime())).
				select(context);
	    
	    PaymentIn current = getModel().getPayment();

        for (PaymentIn p : payments) {
            if (!current.getObjectId().isTemporary() &&
                    current.getId().equals(p.getId()))
                continue;

			if (!getController().getPaymentService().isProcessedByGateway(p))
			{
				getController().addError(PurchaseController.Message.dpsHasNotFinishedProcessPreviousPayment, contact.getFullName());
				return false;
			}

            if (!getController().getWarnings().containsKey(PurchaseController.Message.payerHadUnfinishedPayment.name()))
                getController().addWarning(PurchaseController.Message.payerHadUnfinishedPayment, contact.getFullName());
            PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(p).build().getModel();
            PaymentInAbandon.valueOf(model, false);
        }

        context.commitChanges();
		return true;
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
					ProductItem productItem = getController().createProductItem(contact, product);
					getController().getModel().addProductItem(productItem);
					ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
					actionEnableProductItem.setProductItem(productItem);
					actionEnableProductItem.setPrice(Money.ZERO);
					actionEnableProductItem.action();
				}
			}
		}

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
