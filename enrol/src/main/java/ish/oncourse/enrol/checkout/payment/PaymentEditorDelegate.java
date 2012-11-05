package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;

import java.util.List;
import java.util.Map;

public interface PaymentEditorDelegate {
	public void makePayment();

	public void tryAnotherCard();
	public void abandon();
	public void changePayer(Contact contact);

	public boolean isResultState();

	public List<Contact> getContacts();

	public PaymentIn getPaymentIn();

	public boolean isNeedConcessionReminder();

	public Map<String, String> getErrors();

	public void setErrors(Map<String, String> errors);


}
