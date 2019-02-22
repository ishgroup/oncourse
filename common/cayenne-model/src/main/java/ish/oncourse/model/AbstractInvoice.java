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
        if (getType() == null) {
            setType(InvoiceType.INVOICE);
        }
        if (getCreated() == null) {
            setCreated(new Date());
        }
        if (getModified() == null) {
            setModified(getCreated());
        }
        if (getConfirmationStatus() == null) {
            setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
        }
    }

    @Override
    protected void onPrePersist() {
        onPostAdd();
    }

    @Override
    protected void onPreUpdate() {
    }

    /**
     * Check if async replication is allowed on this object.
     *
     * We need the method  to not add to the async replication a payment transactions
     * which have not got the final status yet (DPS processing).
     */
    public boolean isAsyncReplicationAllowed() {

        List<PaymentInLine> lines = getPaymentInLines();

        // We check linked payments, if one of them can replicate invoice can replicate too.
        if (!lines.isEmpty()) {
            for (PaymentInLine line : lines) {
                PaymentIn paymentIn = line.getPaymentIn();
                ObjectIdQuery q = new ObjectIdQuery(paymentIn.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
                paymentIn = (PaymentIn) Cayenne.objectForQuery(getObjectContext(), q);
                if (paymentIn.isAsyncReplicationAllowed()) {
                    return true;
                }
            }
            return false;
        }

        // If invoice is not yet linked to any payments.
        for (InvoiceLine invLine : getInvoiceLines()) {
            Enrolment enrol = invLine.getEnrolment();
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
}
