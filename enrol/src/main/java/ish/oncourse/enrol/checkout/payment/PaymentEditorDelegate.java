package ish.oncourse.enrol.checkout.payment;

import ish.math.Money;
import ish.oncourse.model.*;

import java.util.List;
import java.util.Map;


public interface PaymentEditorDelegate {
	void makePayment();
    void changePayer();
    void addPayer(Boolean isCompany);

	Map<String,String> addCorporatePass(String corporatePass);

	void tryAgain();
	void abandon();
	List<Contact> getContacts();

	PaymentIn getPaymentIn();

	Invoice getInvoice();
	
	Money getTotalDiscountAmountIncTax();

	Map<String, String> getErrors();

	void setErrors(Map<String, String> errors);

	boolean isNeedConcessionReminder();
	boolean isEnrolmentFailedNoPlaces();
	boolean isPaymentSuccess();
	boolean isProcessFinished();
	boolean isWrongPaymentExpressResult();

	boolean isFinalState();

	boolean isCorporatePass();
	
	boolean isEmptyInvoice();
	
	boolean isZeroPayment();

	boolean isPaymentPlanZeroPayment();

	List<Application> getApplications();

    void updatePaymentStatus();

	College getCollege();

	boolean payerCanBeChanged();
}
