package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;

import java.util.List;

public interface PaymentEditorDelegate {
	public void makePayment();

	public void tryAnotherCard();
	public void abandon();
	public void changePayer(Contact contact);

	public boolean isResultState();

	public List<Contact> getContacts();

	public PaymentIn getPaymentIn();

	public boolean isNeedConcessionReminder();

}
