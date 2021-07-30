package ish.oncourse.model;

import ish.oncourse.model.auto._AbstractInvoiceLine;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;
import java.util.List;

public abstract class AbstractInvoiceLine extends _AbstractInvoiceLine implements Queueable {

    /**
     * Returns the primary key property - id of {@link InvoiceLine}.
     *
     * @return id
     */
    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    protected void onPostAdd() {
        if (getCreated() == null) {
            setCreated(new Date());
        }
        if (getModified() == null) {
            setModified(getCreated());
        }
    }

    public abstract Class<? extends AbstractInvoice> getInvoicePersistentClass();

    public abstract Enrolment getEnrolment();
    public abstract void setEnrolment(Enrolment enrolment);

    public abstract CourseClass getCourseClass();
    public abstract void setCourseClass(CourseClass courseClass);

    public abstract List<InvoiceLineDiscount> getInvoiceLineDiscounts();
    public abstract void addToInvoiceLineDiscounts(InvoiceLineDiscount invoiceLineDiscount);

    public abstract College getCollege();
    public abstract void setCollege(College college);

    public abstract AbstractInvoice getInvoice();
    public abstract void setInvoice(AbstractInvoice abstractInvoice);
}
