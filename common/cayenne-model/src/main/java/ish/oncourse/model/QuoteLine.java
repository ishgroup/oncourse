package ish.oncourse.model;

import ish.oncourse.model.auto._QuoteLine;

import java.util.List;

public class QuoteLine extends _QuoteLine {

    @Override
    public Class<? extends AbstractInvoice> getInvoicePersistentClass() {
        return Quote.class;
    }

    public boolean isAsyncReplicationAllowed() {
        return getQuote() != null && getQuote().isAsyncReplicationAllowed();
    }

    @Override
    public AbstractInvoice getInvoice() {
        return getQuote();
    }

    @Override
    public void setInvoice(AbstractInvoice abstractInvoice) {
        super.setQuote((Quote) abstractInvoice);
    }

    @Override
    public List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return super.getQuoteLineDiscounts();
    }

    @Override
    public void addToInvoiceLineDiscounts(InvoiceLineDiscount invoiceLineDiscount) {
        super.addToQuoteLineDiscounts(invoiceLineDiscount);
    }
}
