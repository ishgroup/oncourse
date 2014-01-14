package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.VoucherProduct;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

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
     * Check if there are in_transaction payments on enroling contact. If finds any it abandons them.
	 *
     */
    private boolean validateAndCompleteInTransactionPayments() {
        if (contact.getObjectId().isTemporary())
            return true;
        ObjectContext context = getController().getCayenneService().newContext();

		List<PaymentIn> payments = context.performQuery(getPaymentsSelectQuery());

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
            p.abandonPayment();
        }

        context.commitChanges();
		return true;
    }

	public SelectQuery getPaymentsSelectQuery()
	{
		SelectQuery q = new SelectQuery(PaymentIn.class);
		q.andQualifier(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.IN_TRANSACTION, PaymentStatus.CARD_DETAILS_REQUIRED));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.CONTACT_PROPERTY, contact));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.SOURCE_PROPERTY, PaymentSource.SOURCE_WEB));


		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);
		q.andQualifier(ExpressionFactory.greaterExp(PaymentIn.CREATED_PROPERTY, calendar.getTime()));

		return  q;
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
        getController().refreshPrevOwingStatus();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
