package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentProcessController;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.*;

public class PaymentEditorController implements PaymentEditorDelegate {

    private PurchaseController purchaseController;
    private PaymentProcessController paymentProcessController;
    private Map<String, String> errors = new HashMap<String, String>();

    @Override
    public boolean isProcessFinished() {
        return paymentProcessController.isProcessFinished();
    }

    public boolean isPaymentSuccess() {
        return paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.SUCCESS;
    }

    public void updatePaymentStatus() {
        paymentProcessController.processAction(PaymentProcessController.PaymentAction.UPDATE_PAYMENT_GATEWAY_STATUS);
        if (paymentProcessController.isFinalState()) {
            finalizeProcess();
        }
    }

    public void changePayer() {
        if (!getPaymentIn().getContact().getId().equals(purchaseController.getModel().getPayer().getId())) {

            PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.changePayer);
            actionParameter.setValue(getPaymentIn().getContact());
            purchaseController.performAction(actionParameter);
        }
    }

    @Override
    public void addPayer() {
        PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addPayer);
        purchaseController.performAction(parameter);
    }

	@Override
	public Map<String,String> addCorporatePass(String corporatePass) {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCorporatePass);
		parameter.setValue(corporatePass);
		purchaseController.performAction(parameter);
		return purchaseController.getErrors();
	}

	public void makePayment() {
        purchaseController.setErrors(errors);
        if (errors.isEmpty()) {
            PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.makePayment);
            purchaseController.performAction(actionParameter);
            if (purchaseController.getErrors().isEmpty()) {
                purchaseController.getModel().getObjectContext().commitChanges();
                paymentProcessController.processAction(MAKE_PAYMENT);
            }
        }
    }

    private void finalizeProcess() {
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.showPaymentResult);
        actionParameter.setErrors(errors);
        purchaseController.performAction(actionParameter);
    }

    public void tryAgain() {
        paymentProcessController.processAction(TRY_ANOTHER_CARD);
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.proceedToPayment);
        actionParameter.setValue(paymentProcessController.getPaymentIn());
        purchaseController.performAction(actionParameter);
    }

    public void abandon() {
        paymentProcessController.processAction(ABANDON_PAYMENT);
        finalizeProcess();
    }

    public boolean isNeedConcessionReminder() {
        return purchaseController.isNeedConcessionReminder();
    }

    @Override
    public boolean isEnrolmentFailedNoPlaces() {
        return false;
    }

    public List<Contact> getContacts() {
        return purchaseController.getModel().getContacts();
    }

    public PaymentIn getPaymentIn() {
        return paymentProcessController.getPaymentIn();
    }

	@Override
	public Invoice getInvoice() {
		return purchaseController.getModel().getInvoice();
	}

	public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }

    public PaymentProcessController getPaymentProcessController() {
        return paymentProcessController;
    }

    public void setPaymentProcessController(PaymentProcessController paymentProcessController) {
        this.paymentProcessController = paymentProcessController;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    @Override
    public Transaction getAnalyticsTransaction() {
        String googleAnalyticsAccount = purchaseController.getWebSiteService().getCurrentWebSite().getGoogleAnalyticsAccount();

        if (googleAnalyticsAccount != null && StringUtils.trimToNull(googleAnalyticsAccount) != null) {
            if (isPaymentSuccess()) {
                List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
                List<Item> transactionItems = new ArrayList<Item>(enrolments.size());
                for (Enrolment enrolment : enrolments) {
                    Item item = new Item();

                    for (Tag tag : purchaseController.getTagService().getTagsForEntity(Course.class.getSimpleName(), enrolment.getCourseClass().getCourse().getId())) {
                        if (Tag.SUBJECTS_TAG_NAME.equalsIgnoreCase(tag.getRoot().getName())) {
                            item.setCategoryName(tag.getDefaultPath().replace('/', '.').substring(1));
                            break;
                        }
                    }
                    item.setProductName(enrolment.getCourseClass().getCourse().getName());
                    item.setQuantity(1);
                    item.setSkuCode(enrolment.getCourseClass().getCourse().getCode());
                    item.setUnitPrice(enrolment.getInvoiceLine().getDiscountedPriceTotalExTax().toBigDecimal());
                    transactionItems.add(item);
                }
                Transaction transaction = new Transaction();
                transaction.setAffiliation(null);
                transaction.setCity(getPaymentIn().getContact().getSuburb());
                transaction.setCountry("Australia");
                transaction.setItems(transactionItems);
                transaction.setOrderNumber("W" + getPaymentIn().getId());
                transaction.setShippingAmount(null);
                transaction.setState(getPaymentIn().getContact().getState());
                BigDecimal tax = new BigDecimal(0);
                for (PaymentInLine pil : getPaymentIn().getPaymentInLines()) {
                    for (InvoiceLine invoiceLine : pil.getInvoice().getInvoiceLines()) {
                        tax = tax.add(invoiceLine.getTotalTax().toBigDecimal());
                    }
                }
                transaction.setTax(tax);
                transaction.setTotal(getPaymentIn().getAmount().toBigDecimal());
                return transaction;
            }
        }
        return null;
    }
}
