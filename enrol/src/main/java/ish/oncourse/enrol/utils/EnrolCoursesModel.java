package ish.oncourse.enrol.utils;

import ish.math.Money;
import ish.oncourse.enrol.components.AnalyticsTransaction.Transaction;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import java.text.Format;
import java.util.List;

import org.apache.tapestry5.annotations.Persist;

/**
 * The data model which contain all the data required for web enrolment.
 * @author vdavidovich
 *
 */
public class EnrolCoursesModel {
	//EnrolCourses properties
    @Persist
    private List<CourseClass> classesToEnrol;
	
	@Persist
    private List<Contact> contacts;
	
    private Contact contact;
	
    @Persist
    private Enrolment[][] enrolments;

    @Persist
    private InvoiceLine[][] invoiceLines;

    @Persist
    private PaymentIn payment;

    @Persist
    private Invoice invoice;
    
    @Persist
    private Format moneyFormat;
    
    @Persist
    private PaymentIn failedPayment;
    
    //EnrolmentPaymentEntry properties
	private Money totalIncGst;
	
	private List<Enrolment> enrolmentsList;
	
	//EnrolmentPaymentResult property
	private Transaction transaction;

	/**
	 * @return the classesToEnrol
	 */
	public List<CourseClass> getClassesToEnrol() {
		return classesToEnrol;
	}

	/**
	 * @param classesToEnrol the classesToEnrol to set
	 */
	public void setClassesToEnrol(List<CourseClass> classesToEnrol) {
		this.classesToEnrol = classesToEnrol;
	}

	/**
	 * @return the contacts
	 */
	public List<Contact> getContacts() {
		return contacts;
	}

	/**
	 * @param contacts the contacts to set
	 */
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	/**
	 * @return the contact
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	/**
	 * @return the enrolments
	 */
	public Enrolment[][] getEnrolments() {
		return enrolments;
	}

	/**
	 * @param enrolments the enrolments to set
	 */
	public void setEnrolments(Enrolment[][] enrolments) {
		this.enrolments = enrolments;
	}

	/**
	 * @return the invoiceLines
	 */
	public InvoiceLine[][] getInvoiceLines() {
		return invoiceLines;
	}

	/**
	 * @param invoiceLines the invoiceLines to set
	 */
	public void setInvoiceLines(InvoiceLine[][] invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

	/**
	 * @return the payment
	 */
	public PaymentIn getPayment() {
		return payment;
	}

	/**
	 * @param payment the payment to set
	 */
	public void setPayment(PaymentIn payment) {
		this.payment = payment;
	}

	/**
	 * @return the invoice
	 */
	public Invoice getInvoice() {
		return invoice;
	}

	/**
	 * @param invoice the invoice to set
	 */
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * @return the moneyFormat
	 */
	public Format getMoneyFormat() {
		return moneyFormat;
	}

	/**
	 * @param moneyFormat the moneyFormat to set
	 */
	public void setMoneyFormat(Format moneyFormat) {
		this.moneyFormat = moneyFormat;
	}

	/**
	 * @return the failedPayment
	 */
	public PaymentIn getFailedPayment() {
		return failedPayment;
	}

	/**
	 * @param failedPayment the failedPayment to set
	 */
	public void setFailedPayment(PaymentIn failedPayment) {
		this.failedPayment = failedPayment;
	}

	/**
	 * @return the totalIncGst
	 */
	public Money getTotalIncGst() {
		return totalIncGst;
	}

	/**
	 * @param totalIncGst the totalIncGst to set
	 */
	public void setTotalIncGst(Money totalIncGst) {
		this.totalIncGst = totalIncGst;
	}

	/**
	 * @return the enrolmentsList
	 */
	public List<Enrolment> getEnrolmentsList() {
		return enrolmentsList;
	}

	/**
	 * @param enrolmentsList the enrolmentsList to set
	 */
	public void setEnrolmentsList(List<Enrolment> enrolmentsList) {
		this.enrolmentsList = enrolmentsList;
	}

	/**
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	
}
