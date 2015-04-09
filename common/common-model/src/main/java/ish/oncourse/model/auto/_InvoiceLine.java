package ish.oncourse.model.auto;

import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.exp.Property;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Class _InvoiceLine was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _InvoiceLine extends InvoicePayableLine {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DESCRIPTION_PROPERTY = "description";
    @Deprecated
    public static final String DISCOUNT_EACH_EX_TAX_PROPERTY = "discountEachExTax";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String PRICE_EACH_EX_TAX_PROPERTY = "priceEachExTax";
    @Deprecated
    public static final String QUANTITY_PROPERTY = "quantity";
    @Deprecated
    public static final String SORT_ORDER_PROPERTY = "sortOrder";
    @Deprecated
    public static final String TAX_EACH_PROPERTY = "taxEach";
    @Deprecated
    public static final String TITLE_PROPERTY = "title";
    @Deprecated
    public static final String UNIT_PROPERTY = "unit";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    @Deprecated
    public static final String ENROLMENT_PROPERTY = "enrolment";
    @Deprecated
    public static final String INVOICE_PROPERTY = "invoice";
    @Deprecated
    public static final String INVOICE_LINE_DISCOUNTS_PROPERTY = "invoiceLineDiscounts";
    @Deprecated
    public static final String PRODUCT_ITEMS_PROPERTY = "productItems";
    @Deprecated
    public static final String VOUCHER_PAYMENTS_IN_PROPERTY = "voucherPaymentsIn";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DESCRIPTION = new Property<String>("description");
    public static final Property<Money> DISCOUNT_EACH_EX_TAX = new Property<Money>("discountEachExTax");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<Money> PRICE_EACH_EX_TAX = new Property<Money>("priceEachExTax");
    public static final Property<BigDecimal> QUANTITY = new Property<BigDecimal>("quantity");
    public static final Property<Integer> SORT_ORDER = new Property<Integer>("sortOrder");
    public static final Property<Money> TAX_EACH = new Property<Money>("taxEach");
    public static final Property<String> TITLE = new Property<String>("title");
    public static final Property<String> UNIT = new Property<String>("unit");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<CourseClass> COURSE_CLASS = new Property<CourseClass>("courseClass");
    public static final Property<Enrolment> ENROLMENT = new Property<Enrolment>("enrolment");
    public static final Property<Invoice> INVOICE = new Property<Invoice>("invoice");
    public static final Property<List<InvoiceLineDiscount>> INVOICE_LINE_DISCOUNTS = new Property<List<InvoiceLineDiscount>>("invoiceLineDiscounts");
    public static final Property<List<ProductItem>> PRODUCT_ITEMS = new Property<List<ProductItem>>("productItems");
    public static final Property<List<VoucherPaymentIn>> VOUCHER_PAYMENTS_IN = new Property<List<VoucherPaymentIn>>("voucherPaymentsIn");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setDiscountEachExTax(Money discountEachExTax) {
        writeProperty("discountEachExTax", discountEachExTax);
    }
    public Money getDiscountEachExTax() {
        return (Money)readProperty("discountEachExTax");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setPriceEachExTax(Money priceEachExTax) {
        writeProperty("priceEachExTax", priceEachExTax);
    }
    public Money getPriceEachExTax() {
        return (Money)readProperty("priceEachExTax");
    }

    public void setQuantity(BigDecimal quantity) {
        writeProperty("quantity", quantity);
    }
    public BigDecimal getQuantity() {
        return (BigDecimal)readProperty("quantity");
    }

    public void setSortOrder(Integer sortOrder) {
        writeProperty("sortOrder", sortOrder);
    }
    public Integer getSortOrder() {
        return (Integer)readProperty("sortOrder");
    }

    public void setTaxEach(Money taxEach) {
        writeProperty("taxEach", taxEach);
    }
    public Money getTaxEach() {
        return (Money)readProperty("taxEach");
    }

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }

    public void setUnit(String unit) {
        writeProperty("unit", unit);
    }
    public String getUnit() {
        return (String)readProperty("unit");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget("courseClass", courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty("courseClass");
    }


    public void setEnrolment(Enrolment enrolment) {
        setToOneTarget("enrolment", enrolment, true);
    }

    public Enrolment getEnrolment() {
        return (Enrolment)readProperty("enrolment");
    }


    public void setInvoice(Invoice invoice) {
        setToOneTarget("invoice", invoice, true);
    }

    public Invoice getInvoice() {
        return (Invoice)readProperty("invoice");
    }


    public void addToInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        addToManyTarget("invoiceLineDiscounts", obj, true);
    }
    public void removeFromInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        removeToManyTarget("invoiceLineDiscounts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return (List<InvoiceLineDiscount>)readProperty("invoiceLineDiscounts");
    }


    public void addToProductItems(ProductItem obj) {
        addToManyTarget("productItems", obj, true);
    }
    public void removeFromProductItems(ProductItem obj) {
        removeToManyTarget("productItems", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProductItems() {
        return (List<ProductItem>)readProperty("productItems");
    }


    public void addToVoucherPaymentsIn(VoucherPaymentIn obj) {
        addToManyTarget("voucherPaymentsIn", obj, true);
    }
    public void removeFromVoucherPaymentsIn(VoucherPaymentIn obj) {
        removeToManyTarget("voucherPaymentsIn", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<VoucherPaymentIn> getVoucherPaymentsIn() {
        return (List<VoucherPaymentIn>)readProperty("voucherPaymentsIn");
    }


    protected abstract void onPostAdd();

}
