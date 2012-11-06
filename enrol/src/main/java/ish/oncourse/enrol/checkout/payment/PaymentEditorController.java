package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.enrol.checkout.ActionChangePayer;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.components.AnalyticsTransaction;
import ish.oncourse.model.*;
import ish.oncourse.util.payment.PaymentProcessController;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.*;

public class PaymentEditorController implements PaymentEditorDelegate{
	private PurchaseController purchaseController;
	private PaymentProcessController paymentProcessController;
	private Map<String,String>  errors = new HashMap<String, String>();

	@Override
	public boolean isResultState() {
		return paymentProcessController.isFinalState();
	}

	public boolean isPaymentSuccess()
	{
		return paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.SUCCESS;
	}

	public void makePayment()
	{
		boolean changePayerResult = true;
		if (errors.isEmpty())
		{
			if (! getPaymentIn().getContact().getId().equals(purchaseController.getModel().getPayer().getId()))
			{
				ActionChangePayer payer = PurchaseController.Action.changePayer.createAction(purchaseController);
				payer.setContact(getPaymentIn().getContact());
				changePayerResult = payer.action();

			}
			if (changePayerResult)
			{
				purchaseController.getModel().getObjectContext().commitChanges();
				paymentProcessController.processAction(MAKE_PAYMENT);
				paymentProcessController.processAction(PaymentProcessController.PaymentAction.UPDATE_PAYMENT_GATEWAY_STATUS);
			}
		}
		tryFinalizeProcess();
	}

	private void tryFinalizeProcess() {
		if (paymentProcessController.isProcessFinished())
		{
			PurchaseController.ActionParameter ap = new PurchaseController.ActionParameter(PurchaseController.Action.finishPayment);
			purchaseController.performAction(ap);
		}
	}

	public void tryAgain()
	{
		paymentProcessController.processAction(TRY_ANOTHER_CARD);
		purchaseController.getModel().setPayment(paymentProcessController.getPaymentIn());
	}

	public void abandon(){
		paymentProcessController.processAction(ABANDON_PAYMENT);
		tryFinalizeProcess();
	}

	public boolean isNeedConcessionReminder()
	{
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

	public void changePayer(Contact contact)
	{
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.changePayer);
		actionParameter.setValue(contact);
		purchaseController.performAction(actionParameter);
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
	public AnalyticsTransaction.Transaction getAnalyticsTransaction() {
		String googleAnalyticsAccount = purchaseController.getWebSiteService().getCurrentWebSite().getGoogleAnalyticsAccount();

		if (googleAnalyticsAccount != null && StringUtils.trimToNull(googleAnalyticsAccount) != null) {
			if (isPaymentSuccess()) {
				List<Enrolment> enrolments = purchaseController.getModel().getAllEnabledEnrolments();
				List<AnalyticsTransaction.Item> transactionItems = new ArrayList<AnalyticsTransaction.Item>(enrolments.size());
				for (Enrolment enrolment : enrolments) {
					AnalyticsTransaction.Item item = new AnalyticsTransaction.Item();

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
				AnalyticsTransaction.Transaction transaction = new AnalyticsTransaction.Transaction();
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
				transaction.setTotal(getPaymentIn().getAmount());
				return transaction;
			}
		}
		return null;
	}
}
