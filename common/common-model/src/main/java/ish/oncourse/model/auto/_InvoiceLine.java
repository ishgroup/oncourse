package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.InvoicePayableLine;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.VoucherPaymentIn;

/**
 * Class _InvoiceLine was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _InvoiceLine extends InvoicePayableLine {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String DISCOUNT_EACH_EX_TAX_PROPERTY = "discountEachExTax";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PRICE_EACH_EX_TAX_PROPERTY = "priceEachExTax";
    public static final String QUANTITY_PROPERTY = "quantity";
    public static final String SORT_ORDER_PROPERTY = "sortOrder";
    public static final String TAX_EACH_PROPERTY = "taxEach";
    public static final String TITLE_PROPERTY = "title";
    public static final String UNIT_PROPERTY = "unit";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String ENROLMENT_PROPERTY = "enrolment";
    public static final String INVOICE_PROPERTY = "invoice";
    public static final String INVOICE_LINE_DISCOUNTS_PROPERTY = "invoiceLineDiscounts";
    public static final String PRODUCT_ITEMS_PROPERTY = "productItems";
    public static final String VOUCHER_PAYMENTS_IN_PROPERTY = "voucherPaymentsIn";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDescription(String description) {
        writeProperty(DESCRIPTION_PROPERTY, description);
    }
    public String getDescription() {
        return (String)readProperty(DESCRIPTION_PROPERTY);
    }

    public void setDiscountEachExTax(Money discountEachExTax) {
        writeProperty(DISCOUNT_EACH_EX_TAX_PROPERTY, discountEachExTax);
    }
    public Money getDiscountEachExTax() {
        return (Money)readProperty(DISCOUNT_EACH_EX_TAX_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPriceEachExTax(Money priceEachExTax) {
        writeProperty(PRICE_EACH_EX_TAX_PROPERTY, priceEachExTax);
    }
    public Money getPriceEachExTax() {
        return (Money)readProperty(PRICE_EACH_EX_TAX_PROPERTY);
    }

    public void setQuantity(BigDecimal quantity) {
        writeProperty(QUANTITY_PROPERTY, quantity);
    }
    public BigDecimal getQuantity() {
        return (BigDecimal)readProperty(QUANTITY_PROPERTY);
    }

    public void setSortOrder(Integer sortOrder) {
        writeProperty(SORT_ORDER_PROPERTY, sortOrder);
    }
    public Integer getSortOrder() {
        return (Integer)readProperty(SORT_ORDER_PROPERTY);
    }

    public void setTaxEach(Money taxEach) {
        writeProperty(TAX_EACH_PROPERTY, taxEach);
    }
    public Money getTaxEach() {
        return (Money)readProperty(TAX_EACH_PROPERTY);
    }

    public void setTitle(String title) {
        writeProperty(TITLE_PROPERTY, title);
    }
    public String getTitle() {
        return (String)readProperty(TITLE_PROPERTY);
    }

    public void setUnit(String unit) {
        writeProperty(UNIT_PROPERTY, unit);
    }
    public String getUnit() {
        return (String)readProperty(UNIT_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setEnrolment(Enrolment enrolment) {
        setToOneTarget(ENROLMENT_PROPERTY, enrolment, true);
    }

    public Enrolment getEnrolment() {
        return (Enrolment)readProperty(ENROLMENT_PROPERTY);
    }


    public void setInvoice(Invoice invoice) {
        setToOneTarget(INVOICE_PROPERTY, invoice, true);
    }

    public Invoice getInvoice() {
        return (Invoice)readProperty(INVOICE_PROPERTY);
    }


    public void addToInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        addToManyTarget(INVOICE_LINE_DISCOUNTS_PROPERTY, obj, true);
    }
    public void removeFromInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        removeToManyTarget(INVOICE_LINE_DISCOUNTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return (List<InvoiceLineDiscount>)readProperty(INVOICE_LINE_DISCOUNTS_PROPERTY);
    }


    public void addToProductItems(ProductItem obj) {
        addToManyTarget(PRODUCT_ITEMS_PROPERTY, obj, true);
    }
    public void removeFromProductItems(ProductItem obj) {
        removeToManyTarget(PRODUCT_ITEMS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProductItems() {
        return (List<ProductItem>)readProperty(PRODUCT_ITEMS_PROPERTY);
    }


    public void addToVoucherPaymentsIn(VoucherPaymentIn obj) {
        addToManyTarget(VOUCHER_PAYMENTS_IN_PROPERTY, obj, true);
    }
    public void removeFromVoucherPaymentsIn(VoucherPaymentIn obj) {
        removeToManyTarget(VOUCHER_PAYMENTS_IN_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<VoucherPaymentIn> getVoucherPaymentsIn() {
        return (List<VoucherPaymentIn>)readProperty(VOUCHER_PAYMENTS_IN_PROPERTY);
    }


}
