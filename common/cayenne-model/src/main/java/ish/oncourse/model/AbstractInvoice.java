package ish.oncourse.model;


import ish.common.types.ConfirmationStatus;
import ish.common.types.InvoiceType;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.auto._AbstractInvoice;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.invoice.GetAmountOwing;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;


public abstract class AbstractInvoice extends _AbstractInvoice implements Queueable {

    private static final long serialVersionUID = 2985348837041766278L;

    /**
     * Returns the primary key property - id of {@link Invoice}.
     *
     * @return
     */
    public Long getId() {
        return QueueableObjectUtils.getId(this);
    }

    @Override
    public boolean isAsyncReplicationAllowed() {
        // If invoice is not yet linked to any payments.
        for (AbstractInvoiceLine line : getLines()) {
            Enrolment enrol = line.getEnrolment();
            if (enrol != null) {
                ObjectIdQuery q = new ObjectIdQuery(enrol.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
                enrol = (Enrolment) Cayenne.objectForQuery(getObjectContext(), q);
                if (!enrol.isAsyncReplicationAllowed()) {
                    return false;
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public List<Invoice> getRefundedInvoices() {
        return ObjectSelect.query(Invoice.class).
                where(Invoice.CONTACT.eq(getContact())).
                and(Invoice.COLLEGE.eq(getCollege())).
                and(Invoice.AMOUNT_OWING.eq(Money.ZERO.subtract(getAmountOwing()))).
                select(getObjectContext());
    }

    public void updateAmountOwing() {
        setAmountOwing(GetAmountOwing.valueOf(this).get());
    }


    @Override
    protected void onPostAdd() {
        if (getSource() == null) {
            setSource(PaymentSource.SOURCE_WEB);
        }
        // getType() is overloaded in subtype, so check field directly
        if (type == null) {
            setType(InvoiceType.INVOICE);
        }
        if (getCreated() == null) {
            setCreated(new Date());
        }
        if (getModified() == null) {
            setModified(getCreated());
        }
        if (getAllowAutoPay() == null) {
            setAllowAutoPay(false);
        }
        if (getConfirmationStatus() == null) {
            setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
        }
        if (getAllowAutoPay() == null) {
            setAllowAutoPay(false);
        }
    }

    @Override
    protected void onPrePersist() {
        onPostAdd();
    }

    @Override
    protected void onPreUpdate() {
    }

    public abstract void addToLines(AbstractInvoiceLine invoiceLine);
    public abstract List<? extends AbstractInvoiceLine> getLines();

    public abstract List<PaymentInLine> getPaymentInLines();

    public abstract List<InvoiceDueDate> getInvoiceDueDates();

    public abstract Contact getContact();
    public abstract void setContact(Contact contact);

//    public abstract CorporatePass getCorporatePassUsed();
//    public abstract void setCorporatePassUsed(CorporatePass corporatePass);
//
//    public abstract PaymentIn getAuthorisedRebillingCard();
//    public abstract void setAuthorisedRebillingCard(PaymentIn paymentIn);

    public abstract College getCollege();
    public abstract void setCollege(College college);

    public abstract WebSite getWebSite();
    public abstract void setWebSite(WebSite webSite);
}
