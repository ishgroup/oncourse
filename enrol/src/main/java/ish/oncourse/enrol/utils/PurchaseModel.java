package ish.oncourse.enrol.utils;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.StudentConcession;
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
	private List<PaymentIn> voucherPayments;
	
	public PurchaseModel() {
		this.contacts = new HashMap<Contact, PurchaseModel.ContactNode>();
		this.voucherPayments = new ArrayList<PaymentIn>();
	}
	
	public void addContact(Contact contact) {
		this.contacts.put(contact, new ContactNode());
	}
	
	public List<Contact> getContacts() {
		return Collections.unmodifiableList(new ArrayList<Contact>(contacts.keySet()));
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
	
	public void removeConcession(Contact contact, ConcessionType concession) {
		getContactNode(contact).removeConcession(concession);
	}
	
	public void addProduct(ProductItem p) {
		getContactNode(payer).addProduct(p);
	}
	
	public void removeProduct(ProductItem p) {
		getContactNode(payer).removeProduct(p);
	}
	
	public void addVoucherPayments(Collection<PaymentIn> vps) {
		this.voucherPayments.addAll(vps);
	}
	
	public void clearVoucherPayments() {
		this.voucherPayments.clear();
	}
	
	public List<PaymentIn> getVoucherPayments() {
		return Collections.unmodifiableList(voucherPayments);
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
	
	public List<Enrolment> getEnabledEnrolments(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).enabledEnrolments);
	}
	
	public List<Enrolment> getDisabledEnrolments(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).disabledEnrolments);
	}
	
	public List<Enrolment> getAllEnrolments(Contact contact) {
		return getContactNode(contact).getAllEnrolments();
	}
	
	public Enrolment getEnrolmentByCourseClass(Contact contact, CourseClass courseClass) {
		List<Enrolment> allEnrolments = getAllEnrolments(contact);
		for (Enrolment enrolment : allEnrolments) {
			if (courseClass.getId().equals(enrolment.getCourseClass().getId())) {
				return enrolment;
			}
		}
		return null;
	}
	
	public boolean isEnrolmentEnabled(Enrolment enrolment) {
		return getEnabledEnrolments(enrolment.getStudent().getContact()).contains(enrolment);
	}
	
	public List<ProductItem> getAllProducts(Contact contact) {
		return getContactNode(contact).getAllProducts();
	}
	
	public ProductItem getProductItemByProduct(Contact contact, Product product) {
		List<ProductItem> allProductItems = getAllProducts(contact);
		for (ProductItem productItem : allProductItems) {
			if (product.getId().equals(productItem.getProduct().getId())) {
				return productItem;
			}
		}
		return null;
	}
	
	public List<ProductItem> getEnabledProducts(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).enabledProducts);
	}
	
	public List<ProductItem> getDisabledProducts(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).disabledProducts);
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
		
		private List<Enrolment> getAllEnrolments() {
			List<Enrolment> result = new ArrayList<Enrolment>(enabledEnrolments);
			result.addAll(disabledEnrolments);
			return Collections.unmodifiableList(result);
		}
		
		private List<ProductItem> getAllProducts() {
			List<ProductItem> result = new ArrayList<ProductItem>(enabledProducts);
			result.addAll(disabledProducts);
			return Collections.unmodifiableList(result);
		}
		
		public void addConcession(ConcessionType c) {
			this.concessions.add(c);
		}
		
		public void removeConcession(ConcessionType c) {
			if (this.concessions.contains(c)) {
				concessions.remove(c);
			}
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
