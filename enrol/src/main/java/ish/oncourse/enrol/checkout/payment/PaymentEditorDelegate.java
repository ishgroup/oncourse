package ish.oncourse.enrol.checkout.payment;

import ish.oncourse.analytics.Transaction;
import ish.oncourse.model.*;

import java.util.List;
import java.util.Map;


public interface PaymentEditorDelegate {
	public void makePayment();
    public void changePayer();
    public void addPayer(Boolean isCompany);

	public Map<String,String> addCorporatePass(String corporatePass);

	public void tryAgain();
	public void abandon();
	public List<Contact> getContacts();

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
	
	public boolean isEmptyInvoice();
	
	public boolean isZeroPayment();

	public List<Application> getApplications();
	
	public Transaction getAnalyticsTransaction();


    void updatePaymentStatus();

	College getCollege();
}
