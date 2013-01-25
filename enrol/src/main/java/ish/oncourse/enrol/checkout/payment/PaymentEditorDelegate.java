package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.analytics.Transaction;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;

import java.util.List;
import java.util.Map;


public interface PaymentEditorDelegate {
	public void makePayment();
    public void changePayer();

	public void tryAgain();
	public void abandon();
	public List<Contact> getContacts();

	public PaymentIn getPaymentIn();

	public Map<String, String> getErrors();

	public void setErrors(Map<String, String> errors);

	public boolean isNeedConcessionReminder();
	public boolean isEnrolmentFailedNoPlaces();
	public boolean isPaymentSuccess();
	public boolean isProcessFinished();

	public Transaction getAnalyticsTransaction();


    void updatePaymentStatus();
}
