package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.model.InvoiceLine;

/**
 * Class _InvoiceLineDiscount was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _InvoiceLineDiscount extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String DISCOUNT_PROPERTY = "discount";
    @Deprecated
    public static final String INVOICE_LINE_PROPERTY = "invoiceLine";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Discount> DISCOUNT = new Property<Discount>("discount");
    public static final Property<InvoiceLine> INVOICE_LINE = new Property<InvoiceLine>("invoiceLine");

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

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setDiscount(Discount discount) {
        setToOneTarget("discount", discount, true);
    }

    public Discount getDiscount() {
        return (Discount)readProperty("discount");
    }


    public void setInvoiceLine(InvoiceLine invoiceLine) {
        setToOneTarget("invoiceLine", invoiceLine, true);
    }

    public InvoiceLine getInvoiceLine() {
        return (InvoiceLine)readProperty("invoiceLine");
    }


}
