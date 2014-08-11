package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.model.*;
import ish.oncourse.util.InvoiceUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.*;

/**
 * Model class for purchase controllers.
 *
 * @author dzmitry
 */
public class PurchaseModel {

    //input data for model
    private College college;
    private WebSite webSite;
    private List<CourseClass> classes = new ArrayList<>();
    private List<Discount> discounts = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    private ObjectContext objectContext;


    private Map<Contact, ContactNode> contacts = new HashMap<>();
    private Contact payer;
    private Invoice invoice;

    private PaymentIn payment;
    private Money totalDiscountAmountIncTax = Money.ZERO;


    private Map<Voucher, VoucherNode> vouchers = new HashMap<>();

    private boolean applyPrevOwing = false;

    private Boolean allowToUsePrevOwing = false;
    private CorporatePass corporatePass;

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }

    public void removeDiscount(Long discountId) {
        Discount discount = getDiscountBy(discountId);
        if (discount != null)
            discounts.remove(discount);
    }

    public Discount getDiscountBy(Long discountId) {
        List<Discount> discounts = Collections.unmodifiableList(this.discounts);
        for (Discount discount : discounts) {

            if (discount.getId().equals(discountId))
                return discount;
        }
        return null;
    }

    public boolean containsDiscount(Long discountId) {
        return getDiscountBy(discountId) != null;
    }


    public void addContact(Contact contact, boolean isGuardian) {
        ContactNode contactNode = new ContactNode();
        contactNode.setGuardian(isGuardian);
        this.contacts.put(contact, contactNode);
    }

    public boolean addedAsGuardian(Contact contact)
    {
         return getContactNode(contact).isGuardian();
    }

    public void addContact(Contact contact) {
        this.addContact(contact, false);
    }

    public List<Contact> getContacts() {
        return Collections.unmodifiableList(new ArrayList<>(contacts.keySet()));
    }

    public void setPayer(Contact payer) {
        this.payer = payer;
        getInvoice().setContact(payer);
        getInvoice().setBillToAddress(payer.getAddress());
        getPayment().setContact(payer);
    }

    public Contact getPayer() {
        return payer;
    }

    public Invoice getInvoice() {
        if (invoice == null) {
            invoice = objectContext.newObject(Invoice.class);
            // fill the invoice with default values
            invoice.setInvoiceDate(new Date());
            invoice.setAmountOwing(Money.ZERO);
            invoice.setDateDue(new Date());
            invoice.setSource(PaymentSource.SOURCE_WEB);
            invoice.setCollege(college);
            invoice.setContact(payer);
            invoice.setWebSite(getWebSite());
        }
        return invoice;
    }

    public void setPayment(PaymentIn payment) {
        this.payment = payment;
    }


    public PaymentIn getPayment() {
        if (payment == null)
            payment = createPayment();
        return payment;
    }

    void deletePayment() {
        List<PaymentInLine> paymentInLines = payment.getPaymentInLines();
        objectContext.deleteObjects(paymentInLines);
        objectContext.deleteObjects(payment);
    }

    private PaymentIn createPayment() {
        PaymentIn payment = objectContext.newObject(PaymentIn.class);
        payment.setStatus(PaymentStatus.NEW);
        payment.setSource(PaymentSource.SOURCE_WEB);
        payment.setType(PaymentType.CREDIT_CARD);
        payment.setCollege(college);
        payment.setContact(getPayer());

        PaymentInLine paymentInLine = getObjectContext().newObject(PaymentInLine.class);
        paymentInLine.setInvoice(getInvoice());
        paymentInLine.setPaymentIn(payment);
        paymentInLine.setCollege(college);
        return payment;
    }

    public Money getTotalDiscountAmountIncTax() {
        return totalDiscountAmountIncTax;
    }

    void setTotalDiscountAmountIncTax(Money totalDiscountAmountIncTax) {
        this.totalDiscountAmountIncTax = totalDiscountAmountIncTax;
    }


    public void addEnrolment(Enrolment e) {
        getContactNode(e.getStudent().getContact()).addEnrolment(e);
    }

    public void removeEnrolment(Enrolment e) {
        getContactNode(e.getStudent().getContact()).removeEnrolment(e);
        List<InvoiceLine> invoiceLines = new ArrayList<>(e.getInvoiceLines());
        for (InvoiceLine invoiceLine : invoiceLines) {
            objectContext.deleteObjects(invoiceLine);
        }
        objectContext.deleteObjects(e);
    }

    public void addConcession(StudentConcession concession) {
        getContactNode(concession.getStudent().getContact()).addConcession(concession.getConcessionType());
    }

    public void removeConcession(Contact contact, ConcessionType concession) {
        getContactNode(contact).removeConcession(concession);
    }


    public void addProductItem(ProductItem p) {
        getContactNode(p.getContact()).addProductItem(p);
    }

    public void removeProductItem(Contact contact, ProductItem p) {
        InvoiceLine invoiceLine = p.getInvoiceLine();
        getContactNode(contact).removeProductItem(p);
        objectContext.deleteObjects(p);
        if (invoiceLine != null)
            objectContext.deleteObjects(invoiceLine);
    }


    public void removeAllProductItems(Contact contact) {
        List<ProductItem> productItems = getAllProductItems(contact);
        for (ProductItem productItem : productItems) {
            this.removeProductItem(contact, productItem);
        }
    }

    public void removeVoucherProductItems(Contact contact) {
        List<ProductItem> productItems = getAllProductItems(contact);
        for (ProductItem productItem : productItems) {
            if (productItem.getProduct() instanceof VoucherProduct) {
                this.removeProductItem(contact, productItem);
            }
        }
    }


    public void enableEnrolment(Enrolment e) {
        getContactNode(e.getStudent().getContact()).enableEnrolment(e);
    }

    public void disableEnrolment(Enrolment e) {
        List<InvoiceLine> invoiceLines = new ArrayList<>(e.getInvoiceLines());
        for (InvoiceLine invoiceLine : invoiceLines) {
            invoiceLine.setEnrolment(null);
        }
        objectContext.deleteObjects(invoiceLines);
        getContactNode(e.getStudent().getContact()).disableEnrolment(e);
    }

    public void enableProductItem(ProductItem p, Contact contact) {
        getContactNode(contact).enableProductItem(p);
    }

    public void disableProductItem(ProductItem p) {
        InvoiceLine il = p.getInvoiceLine();
        p.setInvoiceLine(null);
        objectContext.deleteObjects(il);
        getContactNode(p.getContact()).disableProductItem(p);
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

    Enrolment getEnrolmentBy(Contact contact, CourseClass courseClass) {
        List<Enrolment> enrolments = getContactNode(contact).getAllEnrolments();
        for (Enrolment enrolment : enrolments) {
            if (enrolment.getCourseClass().getId().equals(courseClass.getId()))
                return enrolment;
        }
        return null;
    }

    ProductItem getProductItemBy(Contact contact, Product product) {
        List<ProductItem> productItems = getContactNode(contact).getAllProductItems();
        for (ProductItem productItem : productItems) {
            if (productItem.getProduct().getId().equals(product.getId()))
                return productItem;
        }
        return null;
    }


    public boolean isEnrolmentEnabled(Enrolment enrolment) {
        return getEnabledEnrolments(enrolment.getStudent().getContact()).contains(enrolment);
    }

    public boolean isProductItemEnabled(ProductItem productItem) {
        return getEnabledProductItems(productItem.getContact()).contains(productItem);
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
    ContactNode getContactNode(Contact c) {
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
        return Collections.unmodifiableList(classes);
    }

    public void setClasses(List<CourseClass> classes) {
        this.classes = new ArrayList<>(classes);
    }

    public void addClass(CourseClass courseClass) {
        this.classes.add(courseClass);
    }


    public List<Discount> getDiscounts() {
        return new ArrayList<>(discounts);
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
        ArrayList<T> list = new ArrayList<>();
        for (T t : objectsForLocalize) {
            if (t.getObjectContext().equals(objectContext)) {
                list.add(t);
            } else {
                list.add((T) objectContext.localObject(t.getObjectId(), null));
            }
        }
        return list;
    }

    public boolean containsContactWith(ContactCredentials contactCredentials) {
        for (Contact contact : getContacts()) {
            if (contact.getFamilyName().equalsIgnoreCase(contactCredentials.getLastName()) &&
                    contact.getGivenName().equalsIgnoreCase(contactCredentials.getFirstName()) &&
                    contact.getEmailAddress().equalsIgnoreCase(contactCredentials.getEmail()))
                return true;
        }
        return false;
    }

    public void deleteDisabledItems() {
        for (Contact contact : getContacts()) {
            deleteDisabledEnrollments(contact);
            deleteDisabledProductItems(contact);
        }
    }


    void deleteDisabledProductItems(Contact contact) {
        List<ProductItem> productItems = new ArrayList<>(getDisabledProductItems(contact));
        for (ProductItem productItem : productItems) {
            removeProductItem(contact, productItem);
        }
    }

    void deleteDisabledEnrollments(Contact contact) {
        List<Enrolment> enrolments = new ArrayList<>(getDisabledEnrolments(contact));
        for (Enrolment enrolment : enrolments) {
            removeEnrolment(enrolment);
        }
    }

    public List<Enrolment> getAllEnabledEnrolments() {

        ArrayList<Enrolment> result = new ArrayList<>();
        for (Contact contact : getContacts()) {
            result.addAll(getEnabledEnrolments(contact));
        }
        return result;
    }

    public List<ProductItem> getAllEnabledProductItems() {

        ArrayList<ProductItem> result = new ArrayList<>();
        for (Contact contact : getContacts()) {
            result.addAll(getEnabledProductItems(contact));
        }
        return result;
    }

    public String setErrorFor(Enrolment enrolment, String error) {
        ContactNode contactNode = contacts.get(enrolment.getStudent().getContact());
        return contactNode.setErrorFor(enrolment, error);
    }

    public String getErrorBy(Enrolment enrolment) {
        ContactNode contactNode = contacts.get(enrolment.getStudent().getContact());
        return contactNode.getErrorBy(enrolment);
    }

    public WebSite getWebSite() {
        return webSite;
    }

    public void setWebSite(WebSite webSite) {
        this.webSite = webSite;
    }

    public boolean containsClassWith(Long classId) {
        for (CourseClass courseClass : classes) {
            if (courseClass.getId().equals(classId))
                return true;
        }
        return false;
    }

    public Money getPreviousOwing() {
        if (allowToUsePrevOwing) {
            if (getPayer().getObjectId().isTemporary())
                return Money.ZERO;
            else {
                Money amountOwing = InvoiceUtils.amountOwingForPayer(getPayer());
                /**
                 * we should not subtract current invoice value when the invoice is not committed.
                 * Because InvoiceUtils.amountOwingForPayer loads invoices from database and this loadded list
                 * does not contain this invoice
                 */
                if (!getInvoice().getObjectId().isTemporary()) {
                    Money amountInvoice = getInvoice().getAmountOwing();
                    amountOwing = amountOwing.subtract(amountInvoice);
                }
                return amountOwing;
            }
		}
		else
		{
			return Money.ZERO;
		}
    }

    /**
     * We apply the owing/credit when this flag is true.
     */
    public boolean isApplyPrevOwing() {
        return applyPrevOwing;
    }

    public void setApplyPrevOwing(boolean applyPrevOwing) {
        this.applyPrevOwing = applyPrevOwing;
    }

	public Boolean getAllowToUsePrevOwing() {
		return allowToUsePrevOwing;
	}

	public void setAllowToUsePrevOwing(Boolean allowToUsePrevOwing) {
		this.allowToUsePrevOwing = allowToUsePrevOwing;
	}

    public void setCorporatePass(CorporatePass corporatePass) {
        this.corporatePass = corporatePass;
    }

    public CorporatePass getCorporatePass() {
        return corporatePass;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public boolean containsProduct(Long productId) {
        for (Product product : products) {
            if (product.getId().equals(productId))
                return true;
        }
        return false;
    }

	public List<Enrolment> getAllEnableEnrolmentBy(CourseClass courseClass) {

		Expression expression = ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY, courseClass);
		List<Enrolment> result = new ArrayList<>();

		for (ContactNode node : contacts.values()) {
			result.addAll(expression.filterObjects(node.enabledEnrolments));
		}
		return result;
	}

    public List<Voucher> getVouchers() {

        return Collections.unmodifiableList(new ArrayList<>(vouchers.keySet()));
    }


    public List<Voucher> getSelectedVouchers() {
        ArrayList<Voucher> result = new ArrayList<>();
        for (Map.Entry<Voucher, VoucherNode> entry : vouchers.entrySet()){
            if (entry.getValue().selected) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableList(result);
    }

    public void addVoucher(Voucher voucher) {
        vouchers.put(voucher, new VoucherNode());
    }


    public void setVoucherPayments(Map<Voucher, PaymentIn> voucherPayments) {
        for (Map.Entry<Voucher, VoucherNode> entry : vouchers.entrySet()) {
            PaymentIn paymentIn = voucherPayments.get(entry.getKey());
            if (paymentIn != null) {
                entry.getValue().paymentIn = paymentIn;
            } else {
                entry.getValue().paymentIn = null;
            }
        }
    }

    public void selectVoucher(Voucher voucher)
    {
        VoucherNode voucherNode = vouchers.get(voucher);
        voucherNode.selected = true;
    }

    public void deselectVoucher(Voucher voucher)
    {
        VoucherNode voucherNode = vouchers.get(voucher);
        voucherNode.selected = false;
    }

    public PaymentIn getVoucherPaymentBy(Voucher voucher) {
        return vouchers.get(voucher).paymentIn;
    }

    public boolean isSelectedVoucher(Voucher voucher) {
        return vouchers.get(voucher).selected;
    }


    public List<PaymentIn> getVoucherPayments() {
        ArrayList<PaymentIn> result = new ArrayList<>();
        for (Map.Entry<Voucher, VoucherNode> entry : vouchers.entrySet()) {
            PaymentIn paymentIn = entry.getValue().paymentIn;
            if (paymentIn != null) {
                result.add(paymentIn);
            }
        }
        return result;
    }

    public Contact getGuardianFor(Contact contact) {
        List<ContactRelation> contactRelation =  contact.getFromContacts();
        for (ContactRelation relation : contactRelation) {
            ContactRelationType type = relation.getRelationType();
            if (type.getAngelId() == -1)
                return relation.getFromContact();
        }
        return null;
    }


    public class VoucherNode {
        private boolean selected = false;
        private PaymentIn paymentIn = null;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public PaymentIn getPaymentIn() {
            return paymentIn;
        }

        public void setPaymentIn(PaymentIn paymentIn) {
            this.paymentIn = paymentIn;
        }
    }


    private class ContactNode {

        private List<ConcessionType> concessions;

        /**
         * map contains error for by course class id. we use courseClassId as key because Enrolment can be recreated
         */
        private Map<Long, String> courseClassErrors = new HashMap<>();

        private List<Enrolment> enabledEnrolments;
        private List<Enrolment> disabledEnrolments;
		/**
		 * This property was introduced to keep enrolment ordering.
		 */
		private List<Enrolment> allEnrolments;
		private List<ProductItem> allProductItem;


		private List<ProductItem> enabledProductItems;
        private List<ProductItem> disabledProductItems;

        private boolean isGuardian = false;

        public ContactNode() {
            this.concessions = new ArrayList<>();

            this.enabledEnrolments = new ArrayList<>();
            this.disabledEnrolments = new ArrayList<>();
			this.allEnrolments = new ArrayList<>();

            this.enabledProductItems = new ArrayList<>();
            this.disabledProductItems = new ArrayList<>();
			this.allProductItem = new ArrayList<>();

		}

        private List<Enrolment> getAllEnrolments() {
			ArrayList<Enrolment> result = new ArrayList<>(allEnrolments);
			return Collections.unmodifiableList(result);
		}

        private List<ProductItem> getAllProductItems() {
			ArrayList<ProductItem> result = new ArrayList<>(allProductItem);
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
            this.disabledEnrolments.add(e);
			this.allEnrolments.add(e);
        }

        public void removeEnrolment(Enrolment e) {
            if (!this.enabledEnrolments.remove(e)) {
                this.disabledEnrolments.remove(e);
            }
			this.allEnrolments.remove(e);
        }

        public void addProductItem(ProductItem p) {
            this.disabledProductItems.add(p);
			this.allProductItem.add(p);
		}

        public void removeProductItem(ProductItem p) {
            if (!this.enabledProductItems.remove(p)) {
                this.disabledProductItems.remove(p);
            }
			this.allProductItem.remove(p);
		}

		public void enableEnrolment(Enrolment e) {
            courseClassErrors.remove(e.getCourseClass().getId());
            if (disabledEnrolments.contains(e)) {
                disabledEnrolments.remove(e);
                enabledEnrolments.add(e);
            }
        }

        public void disableEnrolment(Enrolment e) {
            if (enabledEnrolments.contains(e)) {
                enabledEnrolments.remove(e);
                disabledEnrolments.add(0, e);
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

        public String getErrorBy(Enrolment enrolment) {
            return courseClassErrors.get(enrolment.getCourseClass().getId());
        }

        public String setErrorFor(Enrolment enrolment, String error) {
            return courseClassErrors.put(enrolment.getCourseClass().getId(), error);
        }

        public boolean isGuardian() {
            return isGuardian;
        }

        public void setGuardian(boolean isGuardian) {
            this.isGuardian = isGuardian;
        }
    }
}
