package ish.oncourse.enrol.utils;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.VoucherPaymentIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class for purchase controllers.
 * 
 * @author dzmitry
 */
public class PurchaseModel {
	
	private Map<Contact, ContactNode> contacts;
	private Contact payer;
	private Invoice invoice;
	
	private PaymentIn payment;
	private List<VoucherPaymentIn> voucherPayments;
	
	public PurchaseModel() {
		this.contacts = new HashMap<Contact, PurchaseModel.ContactNode>();
		this.voucherPayments = new ArrayList<VoucherPaymentIn>();
	}
	
	public void addContact(Contact contact) {
		this.contacts.put(contact, new ContactNode());
	}
	
	public Collection<Contact> getContacts() {
		return Collections.unmodifiableCollection(contacts.keySet());
	}
	
	public void setPayer(Contact payer) {
		this.payer = payer;
	}
	
	public Contact getPayer() {
		return payer;
	}
	
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public void setPayment(PaymentIn payment) {
		this.payment = payment;
	}
	
	public PaymentIn getPayment() {
		return payment;
	}
	
	public void addEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).addEnrolment(e);
	}
	
	public void addConcession(StudentConcession concession) {
		getContactNode(concession.getStudent().getContact()).addConcession(concession.getConcessionType());
	}
	
	public void addProduct(ProductItem p) {
		getContactNode(payer).addProduct(p);
	}
	
	public void addVoucherPayment(VoucherPaymentIn vp) {
		this.voucherPayments.add(vp);
	}
	
	public Collection<VoucherPaymentIn> getVoucherPayments() {
		return Collections.unmodifiableCollection(voucherPayments);
	}
	
	public void toggleEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).toggleEnrolment(e);
	}
	
	public void untoggleEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).untoggleEnrolment(e);
	}
	
	public void toggleProduct(ProductItem p) {
		getContactNode(payer).toggleProduct(p);
	}
	
	public void utoggleProduct(ProductItem p) {
		getContactNode(payer).untoggleProduct(p);
	}
	
	public Collection<Enrolment> getToggledEnrolments(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).toggledEnrolments);
	}
	
	public Collection<Enrolment> getUntoggledEnrolments(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).untoggledEnrolments);
	}
	
	public Collection<ProductItem> getToggledProducts(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).toggledProducts);
	}
	
	public Collection<ProductItem> getUntoggledProducts(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).untoggledProducts);
	}
	
	/**
	 * Always returns non null {@link ContactNode} instance. If specified contact doesn't exist throws {@link IllegalArgumentException}.
	 * 
	 * @param contact
	 * @return contact node structure
	 */
	private ContactNode getContactNode(Contact c) {
		ContactNode cn = this.contacts.get(c);
		if (cn != null) {
			return cn;
		} else {
			throw new IllegalArgumentException("Requested contact is not presented in model's contact list.");
		}
	}
	
	private class ContactNode {
		
		private List<ConcessionType> concessions;

		private List<Enrolment> toggledEnrolments;
		private List<Enrolment> untoggledEnrolments;
		
		private List<ProductItem> toggledProducts;
		private List<ProductItem> untoggledProducts;
		
		public ContactNode() {
			this.concessions = new ArrayList<ConcessionType>();
			
			this.toggledEnrolments = new ArrayList<Enrolment>();
			this.untoggledEnrolments = new ArrayList<Enrolment>();
			
			this.toggledProducts = new ArrayList<ProductItem>();
			this.untoggledProducts = new ArrayList<ProductItem>();
		}
		
		public void addConcession(ConcessionType c) {
			this.concessions.add(c);
		}
		
		public void addEnrolment(Enrolment e) {
			this.toggledEnrolments.add(e);
		}
		
		public void addProduct(ProductItem p) {
			this.toggledProducts.add(p);
		}
		
		public void toggleEnrolment(Enrolment e) {
			if (untoggledEnrolments.contains(e)) {
				untoggledEnrolments.remove(e);
				toggledEnrolments.add(e);
			}
		}
		
		public void untoggleEnrolment(Enrolment e) {
			if (toggledEnrolments.contains(e)) {
				toggledEnrolments.remove(e);
				untoggledEnrolments.add(e);
			}
		}
		
		public void toggleProduct(ProductItem p) {
			if (untoggledProducts.contains(p)) {
				untoggledProducts.remove(p);
				toggledProducts.add(p);
			}
		}
		
		public void untoggleProduct(ProductItem p) {
			if (toggledProducts.contains(p)) {
				toggledProducts.remove(p);
				untoggledProducts.add(p);
			}
		}
	}

}
