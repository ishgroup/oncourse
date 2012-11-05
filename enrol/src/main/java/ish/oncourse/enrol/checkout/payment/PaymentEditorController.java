package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.PaymentProcessController;

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

	public void makePayment()
	{
		if (errors.isEmpty())
			paymentProcessController.processAction(MAKE_PAYMENT);
	}

	public void tryAnotherCard()
	{
		paymentProcessController.processAction(TRY_ANOTHER_CARD);
		purchaseController.getModel().setPayment(paymentProcessController.getPaymentIn());
	}

	public void abandon(){
		paymentProcessController.processAction(ABANDON_PAYMENT);
	}

	public boolean isNeedConcessionReminder()
	{
		return purchaseController.isNeedConcessionReminder();
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
}
