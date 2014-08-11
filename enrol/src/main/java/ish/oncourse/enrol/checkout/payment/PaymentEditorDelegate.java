package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.analytics.Transaction;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;

import java.util.List;
import java.util.Map;


public interface PaymentEditorDelegate {
	public void makePayment();
    public void changePayer();
    public void addPayer();

	public Map<String,String> addCorporatePass(String corporatePass);

	public void tryAgain();
	public void abandon();
	public List<Contact> getContacts();
    public List<Contact> getPayers();

	public PaymentIn getPaymentIn();

	public Invoice getInvoice();

	public Map<String, String> getErrors();

	public void setErrors(Map<String, String> errors);

	public boolean isNeedConcessionReminder();
	public boolean isEnrolmentFailedNoPlaces();
	public boolean isPaymentSuccess();
	public boolean isProcessFinished();

	public boolean isFinalState();

	public boolean isCorporatePass();

	public Transaction getAnalyticsTransaction();


    void updatePaymentStatus();

	College getCollege();
}
