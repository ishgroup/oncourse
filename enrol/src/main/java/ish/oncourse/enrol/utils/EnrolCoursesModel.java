package ish.oncourse.enrol.utils;

import ish.math.Money;
import ish.oncourse.analytics.Transaction;
import ish.oncourse.model.*;

import java.text.Format;
import java.util.List;


/**
 * The data model which contain all the data required for web enrolment.
 * @author vdavidovich
 *
 */
@Deprecated //will be removed after checkout/payemnt page will be ready
public class EnrolCoursesModel {
	//EnrolCourses properties
    private List<CourseClass> classesToEnrol;
	
    private List<Contact> contacts;
    
    private List<Contact> actualContacts;
	
    private Contact contact;
	
    private Enrolment[][] enrolments;

    private InvoiceLine[][] invoiceLines;

    private PaymentIn payment;

    private Invoice invoice;
    
    private Format moneyFormat;
    
    private PaymentIn failedPayment;
    
    //EnrolmentPaymentEntry properties
	private Money totalIncGst;
	
	private List<Enrolment> enrolmentsList;
	
	//EnrolmentPaymentResult property
	private Transaction transaction;
	
	

	/**
	 * @return the actualContacts
	 */
	public synchronized List<Contact> getActualContacts() {
		return actualContacts;
	}

	/**
	 * @param actualContacts the actualContacts to set
	 */
	public synchronized void setActualContacts(List<Contact> actualContacts) {
		this.actualContacts = actualContacts;
	}

	/**
	 * @return the classesToEnrol
	 */
	public synchronized List<CourseClass> getClassesToEnrol() {
		return classesToEnrol;
	}

	/**
	 * @param classesToEnrol the classesToEnrol to set
	 */
	public synchronized void setClassesToEnrol(List<CourseClass> classesToEnrol) {
		this.classesToEnrol = classesToEnrol;
	}

	/**
	 * @return the contacts
	 */
	public synchronized List<Contact> getContacts() {
		return contacts;
	}

	/**
	 * @param contacts the contacts to set
	 */
	public synchronized void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	/**
	 * @return the contact
	 */
	public synchronized Contact getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public synchronized void setContact(Contact contact) {
		this.contact = contact;
	}

	/**
	 * @return the enrolments
	 */
	public synchronized Enrolment[][] getEnrolments() {
		return enrolments;
	}

	/**
	 * @param enrolments the enrolments to set
	 */
	public synchronized void setEnrolments(Enrolment[][] enrolments) {
		this.enrolments = enrolments;
	}

	/**
	 * @return the invoiceLines
	 */
	public synchronized InvoiceLine[][] getInvoiceLines() {
		return invoiceLines;
	}

	/**
	 * @param invoiceLines the invoiceLines to set
	 */
	public synchronized void setInvoiceLines(InvoiceLine[][] invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

	/**
	 * @return the payment
	 */
	public synchronized PaymentIn getPayment() {
		return payment;
	}

	/**
	 * @param payment the payment to set
	 */
	public synchronized void setPayment(PaymentIn payment) {
		this.payment = payment;
	}

	/**
	 * @return the invoice
	 */
	public synchronized Invoice getInvoice() {
		return invoice;
	}

	/**
	 * @param invoice the invoice to set
	 */
	public synchronized void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	/**
	 * @return the moneyFormat
	 */
	public synchronized Format getMoneyFormat() {
		return moneyFormat;
	}

	/**
	 * @param moneyFormat the moneyFormat to set
	 */
	public synchronized void setMoneyFormat(Format moneyFormat) {
		this.moneyFormat = moneyFormat;
	}

	/**
	 * @return the failedPayment
	 */
	public synchronized PaymentIn getFailedPayment() {
		return failedPayment;
	}

	/**
	 * @param failedPayment the failedPayment to set
	 */
	public synchronized void setFailedPayment(PaymentIn failedPayment) {
		this.failedPayment = failedPayment;
	}

	/**
	 * @return the totalIncGst
	 */
	public synchronized Money getTotalIncGst() {
		return totalIncGst;
	}

	/**
	 * @param totalIncGst the totalIncGst to set
	 */
	public synchronized void setTotalIncGst(Money totalIncGst) {
		this.totalIncGst = totalIncGst;
	}

	/**
	 * @return the enrolmentsList
	 */
	public synchronized List<Enrolment> getEnrolmentsList() {
		return enrolmentsList;
	}

	/**
	 * @param enrolmentsList the enrolmentsList to set
	 */
	public synchronized void setEnrolmentsList(List<Enrolment> enrolmentsList) {
		this.enrolmentsList = enrolmentsList;
	}

	/**
	 * @return the transaction
	 */
	public synchronized Transaction getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction the transaction to set
	 */
	public synchronized void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
}
