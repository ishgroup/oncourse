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
	
	public void removeEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).removeEnrolment(e);
	}
	
	public void addConcession(StudentConcession concession) {
		getContactNode(concession.getStudent().getContact()).addConcession(concession.getConcessionType());
	}
	
	public void addProduct(ProductItem p) {
		getContactNode(payer).addProduct(p);
	}
	
	public void removeProduct(ProductItem p) {
		getContactNode(payer).removeProduct(p);
	}
	
	public void addVoucherPayment(VoucherPaymentIn vp) {
		this.voucherPayments.add(vp);
	}
	
	public Collection<VoucherPaymentIn> getVoucherPayments() {
		return Collections.unmodifiableCollection(voucherPayments);
	}
	
	public void enableEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).enableEnrolment(e);
	}
	
	public void disableEnrolment(Enrolment e) {
		getContactNode(e.getStudent().getContact()).disableEnrolment(e);
	}
	
	public void enableProduct(ProductItem p) {
		getContactNode(payer).enableProduct(p);
	}
	
	public void disableProduct(ProductItem p) {
		getContactNode(payer).disableProduct(p);
	}
	
	public Collection<Enrolment> getEnabledEnrolments(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).enabledEnrolments);
	}
	
	public Collection<Enrolment> getDisabledEnrolments(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).disabledEnrolments);
	}
	
	public Collection<ProductItem> getEnabledProducts(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).enabledProducts);
	}
	
	public Collection<ProductItem> getDisabledProducts(Contact contact) {
		return Collections.unmodifiableCollection(getContactNode(contact).disabledProducts);
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

		private List<Enrolment> enabledEnrolments;
		private List<Enrolment> disabledEnrolments;
		
		private List<ProductItem> enabledProducts;
		private List<ProductItem> disabledProducts;
		
		public ContactNode() {
			this.concessions = new ArrayList<ConcessionType>();
			
			this.enabledEnrolments = new ArrayList<Enrolment>();
			this.disabledEnrolments = new ArrayList<Enrolment>();
			
			this.enabledProducts = new ArrayList<ProductItem>();
			this.disabledProducts = new ArrayList<ProductItem>();
		}
		
		public void addConcession(ConcessionType c) {
			this.concessions.add(c);
		}
		
		public void addEnrolment(Enrolment e) {
			this.enabledEnrolments.add(e);
		}
		
		public void removeEnrolment(Enrolment e) {
			if (!this.enabledEnrolments.remove(e)) {
				this.disabledEnrolments.remove(e);
			}
		}
		
		public void addProduct(ProductItem p) {
			this.enabledProducts.add(p);
		}
		
		public void removeProduct(ProductItem p) {
			if (!this.enabledProducts.remove(p)) {
				this.disabledProducts.remove(p);
			}
		}
		
		public void enableEnrolment(Enrolment e) {
			if (disabledEnrolments.contains(e)) {
				disabledEnrolments.remove(e);
				enabledEnrolments.add(e);
			}
		}
		
		public void disableEnrolment(Enrolment e) {
			if (enabledEnrolments.contains(e)) {
				enabledEnrolments.remove(e);
				disabledEnrolments.add(e);
			}
		}
		
		public void enableProduct(ProductItem p) {
			if (disabledProducts.contains(p)) {
				disabledProducts.remove(p);
				enabledProducts.add(p);
			}
		}
		
		public void disableProduct(ProductItem p) {
			if (enabledProducts.contains(p)) {
				enabledProducts.remove(p);
				disabledProducts.add(p);
			}
		}
	}

}
