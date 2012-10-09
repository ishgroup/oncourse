package ish.oncourse.enrol.utils;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.*;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;

import java.math.BigDecimal;
import java.util.*;

/**
 * Model class for purchase controllers.
 * 
 * @author dzmitry
 */
public class PurchaseModel {

	//input data for model
	private College college;
	private List<CourseClass> classes;
	private List<Discount> discounts = new ArrayList<Discount>();
	private List<Product> products;

	private ObjectContext objectContext;


	private Map<Contact, ContactNode> contacts;
	private Contact payer;
	private Invoice invoice;
	
	private PaymentIn payment;
	private List<PaymentIn> voucherPayments;
	
	public PurchaseModel() {
		this.contacts = new HashMap<Contact, PurchaseModel.ContactNode>();
		this.voucherPayments = new ArrayList<PaymentIn>();
	}

	public void addDiscount(Discount discount)
	{
		discounts.add(localizeObject(discount));
	}


	public void addContact(Contact contact) {
		this.contacts.put(localizeObject(contact), new ContactNode());
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

	public Invoice getInvoice() {
		if(invoice == null)
		{
			invoice = objectContext.newObject(Invoice.class);
			// fill the invoice with default values
			invoice.setInvoiceDate(new Date());
			invoice.setAmountOwing(BigDecimal.ZERO);
			invoice.setDateDue(new Date());
			invoice.setSource(PaymentSource.SOURCE_WEB);
			invoice.setCollege(college);
			invoice.setContact(payer);
		}
		return invoice;
	}
	

	public PaymentIn getPayment() {
		if (payment == null)
		{
			payment = objectContext.newObject(PaymentIn.class);
			payment.setStatus(PaymentStatus.NEW);
			payment.setSource(PaymentSource.SOURCE_WEB);
			payment.setCollege(college);
		}
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


	public void addProductItem(ProductItem p) {
		getContactNode(payer).addProductItem(p);
	}
	
	public void removeProductItem(Contact contact, ProductItem p) {
		InvoiceLine invoiceLine = p.getInvoiceLine();
		getContactNode(contact).removeProductItem(p);
		objectContext.deleteObject(p);
		objectContext.deleteObject(invoiceLine);
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
		InvoiceLine il = e.getInvoiceLine();
		e.setInvoiceLine(null);
		objectContext.deleteObject(il);
		getContactNode(e.getStudent().getContact()).disableEnrolment(e);
	}
	
	public void enableProductItem(ProductItem p) {
		getContactNode(payer).enableProductItem(p);
	}
	
	public void disableProductItem(ProductItem p) {
		InvoiceLine il = p.getInvoiceLine();
		p.setInvoiceLine(null);
		objectContext.deleteObject(il);
		getContactNode(payer).disableProductItem(p);
	}
	
	public List<Enrolment> getEnabledEnrolments(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).enabledEnrolments);
	}

	public List<ProductItem> getEnabledProductItems(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).enabledProductItems);
	}


	public List<Enrolment> getDisabledEnrolments(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).disabledEnrolments);
	}
	
	public List<Enrolment> getAllEnrolments(Contact contact) {
		return getContactNode(contact).getAllEnrolments();
	}
	
	public Enrolment getEnrolmentBy(Contact contact, Integer index) {
		return getAllEnrolments(contact).get(index);
	}
	
	public boolean isEnrolmentEnabled(Enrolment enrolment) {
		return getEnabledEnrolments(enrolment.getStudent().getContact()).contains(enrolment);
	}

	public boolean isProductItemEnabled(ProductItem productItem) {
		return getEnabledProductItems(payer).contains(productItem);
	}


	public List<ProductItem> getAllProductItems(Contact contact) {
		return getContactNode(contact).getAllProductItems();
	}
	
	public ProductItem getProductItemBy(Contact contact, Integer integer) {
		return getAllProductItems(contact).get(integer);
	}
	
	public List<ProductItem> getDisabledProductItems(Contact contact) {
		return Collections.unmodifiableList(getContactNode(contact).disabledProductItems);
	}
	
	/**
	 * Always returns non null {@link ContactNode} instance. If specified contact doesn't exist throws {@link IllegalArgumentException}.
	 * 
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

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public List<CourseClass> getClasses() {
		return classes;
	}

	public void setClasses(List<CourseClass> classes) {
		this.classes = classes;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public ObjectContext getObjectContext() {
		return objectContext;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public <T extends Persistent> T localizeObject(T objectForLocalize) {
		if (objectForLocalize == null)
			return null;
		if (objectForLocalize.getObjectContext().equals(objectContext)) {
			return objectForLocalize;
		} else {
			return (T) objectContext.localObject(objectForLocalize.getObjectId(), null);
		}
	}

	public <T extends Persistent> List<T> localizeObjects(List<T> objectsForLocalize) {
		ArrayList<T> list = new ArrayList<T>();
		for (T t : objectsForLocalize) {
			if (t.getObjectContext().equals(objectContext)) {
				list.add(t);
			}
			else
			{
				list.add((T) objectContext.localObject(t.getObjectId(), null));
			}
		}
		return list;
	}


	private class ContactNode {
		
		private List<ConcessionType> concessions;

		private List<Enrolment> enabledEnrolments;
		private List<Enrolment> disabledEnrolments;
		
		private List<ProductItem> enabledProductItems;
		private List<ProductItem> disabledProductItems;
		
		public ContactNode() {
			this.concessions = new ArrayList<ConcessionType>();
			
			this.enabledEnrolments = new ArrayList<Enrolment>();
			this.disabledEnrolments = new ArrayList<Enrolment>();
			
			this.enabledProductItems = new ArrayList<ProductItem>();
			this.disabledProductItems = new ArrayList<ProductItem>();
		}
		
		private List<Enrolment> getAllEnrolments() {
			List<Enrolment> result = new ArrayList<Enrolment>(enabledEnrolments);
			result.addAll(disabledEnrolments);
			return Collections.unmodifiableList(result);
		}
		
		private List<ProductItem> getAllProductItems() {
			List<ProductItem> result = new ArrayList<ProductItem>(enabledProductItems);
			result.addAll(disabledProductItems);
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
		
		public void addProductItem(ProductItem p) {
			this.enabledProductItems.add(p);
		}
		
		public void removeProductItem(ProductItem p) {
			if (!this.enabledProductItems.remove(p)) {
				this.disabledProductItems.remove(p);
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
		
		public void enableProductItem(ProductItem p) {
			if (disabledProductItems.contains(p)) {
				disabledProductItems.remove(p);
				enabledProductItems.add(p);
			}
		}
		
		public void disableProductItem(ProductItem p) {
			if (enabledProductItems.contains(p)) {
				enabledProductItems.remove(p);
				disabledProductItems.add(p);
			}
		}
	}

}
