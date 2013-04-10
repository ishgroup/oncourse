package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
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
		return getController().getModel().getContacts().contains(contact);
	}

    /**
     * Check if there are in_transaction payments on enroling contact. If finds any it abandons them.
     */
    private void completeInTransactionPayments() {
        if (contact.getObjectId().isTemporary())
            return;
        ObjectContext context = getController().getCayenneService().newContext();

        SelectQuery q = new SelectQuery(PaymentIn.class);
        q.andQualifier(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.IN_TRANSACTION, PaymentStatus.CARD_DETAILS_REQUIRED));
        q.andQualifier(ExpressionFactory.matchExp(PaymentIn.CONTACT_PROPERTY, contact));
        q.andQualifier(ExpressionFactory.matchExp(PaymentIn.SOURCE_PROPERTY, PaymentSource.SOURCE_WEB));


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);
        q.andQualifier(ExpressionFactory.greaterExp(PaymentIn.CREATED_PROPERTY, calendar.getTime()));

        List<PaymentIn> payments = context.performQuery(q);

        PaymentIn current = getModel().getPayment();
        for (PaymentIn p : payments) {
            if (!current.getObjectId().isTemporary() &&
                    current.getId().equals(p.getId()))
                continue;

			if (getController().getPaymentService().isProcessedByGateway(p))
			{
				getController().addError(PurchaseController.Message.payerHadUnfinishedPayment, contact.getFullName());
				break;
			}

            if (!getController().getWarnings().containsKey(PurchaseController.Message.payerHadUnfinishedPayment))
                getController().addWarning(PurchaseController.Message.payerHadUnfinishedPayment, contact.getFullName());
            p.abandonPayment();
        }

        context.commitChanges();
    }


    @Override
    protected void makeAction() {
        completeInTransactionPayments();

        Contact oldPayer = getController().getModel().getPayer();

        if (oldPayer != null) {
            getController().getModel().removeAllProductItems(oldPayer);
        }

        getController().getModel().setPayer(contact);

        for (Product product : getController().getModel().getProducts()) {
            ProductItem productItem = getController().createProductItem(contact, product);
            getController().getModel().addProductItem(productItem);
            ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
            actionEnableProductItem.setProductItem(productItem);
            actionEnableProductItem.action();
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
