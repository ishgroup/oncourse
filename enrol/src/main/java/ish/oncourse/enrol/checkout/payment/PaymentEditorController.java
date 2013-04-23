package ish.oncourse.enrol.checkout.payment;

import ish.math.Money;
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

import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.ABANDON_PAYMENT;
import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.MAKE_PAYMENT;

public class PaymentEditorController implements PaymentEditorDelegate {

    private PurchaseController purchaseController;
    private PaymentProcessController paymentProcessController;
    private Map<String, String> errors = new HashMap<String, String>();


	public void init()
	{
		initPaymentProcessController();
	}

	@Override
	public College getCollege() {
		return purchaseController.getModel().getCollege();
	}

	public boolean isCorporatePass()
	{
		return purchaseController.getModel().getCorporatePass() != null;
	}

	@Override
	public boolean isFinalState() {
		return isCorporatePass() || paymentProcessController.isFinalState();
	}

	@Override
    public boolean isProcessFinished() {
        return isCorporatePass() || paymentProcessController.isProcessFinished();
    }

    public boolean isPaymentSuccess() {
        return isCorporatePass() || paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.SUCCESS;
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
            if (purchaseController.getErrors().isEmpty() &&
					purchaseController.getModel().getCorporatePass() == null) {
				if (paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.INIT)
					paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
				paymentProcessController.processAction(MAKE_PAYMENT);
            }
        }
    }

	private void initPaymentProcessController()
	{
		paymentProcessController = new PaymentProcessController();
		paymentProcessController.setStartWatcher(false);
		paymentProcessController.setObjectContext(purchaseController.getModel().getObjectContext());
		paymentProcessController.setPaymentIn(purchaseController.getModel().getPayment());
		paymentProcessController.setCayenneService(purchaseController.getCayenneService());
		paymentProcessController.setPaymentGatewayService(purchaseController.getPaymentGatewayServiceBuilder().buildService());
		paymentProcessController.setParallelExecutor(purchaseController.getParallelExecutor());
	}

    private void finalizeProcess() {
        PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.showPaymentResult);
        actionParameter.setErrors(errors);
        purchaseController.performAction(actionParameter);
    }

    public void tryAgain() {
        paymentProcessController.processAction(ABANDON_PAYMENT);
		purchaseController.setPaymentEditorController(null);
		purchaseController.cloneModel();
		initPaymentProcessController();
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

                    for (Tag tag : purchaseController.getTagService().getTagsForEntity(Course.class.getSimpleName(), 
                    	enrolment.getCourseClass().getCourse().getId())) {
                        if (Tag.SUBJECTS_TAG_NAME.equalsIgnoreCase(tag.getRoot().getName())) {
                            item.setCategoryName(tag.getDefaultPath().replace('/', '.').substring(1));
                            break;
                        }
                    }
                    StringBuilder productName = new StringBuilder(enrolment.getCourseClass().getCourse().getCode());
                    productName.append(": ").append(enrolment.getCourseClass().getCourse().getName());
                    item.setProductName(productName.toString());
                    item.setQuantity(1);
                    StringBuilder skuCode = new StringBuilder(enrolment.getCourseClass().getCourse().getCode());
                    skuCode.append("-").append(enrolment.getCourseClass().getCode());
                    item.setSkuCode(skuCode.toString());
                    Money unitPrice = Money.ZERO;
                    for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
                    	unitPrice = unitPrice.add(invoiceLine.getDiscountedPriceTotalExTax());
                    }
                    item.setUnitPrice(unitPrice.toBigDecimal());
                    transactionItems.add(item);
                }
                Transaction transaction = new Transaction();
                transaction.setAffiliation(Transaction.DEFAULT_WEB_AFFILIATION);
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
