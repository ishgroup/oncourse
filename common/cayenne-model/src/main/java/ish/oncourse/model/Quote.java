package ish.oncourse.model;

import ish.common.types.InvoiceType;
import ish.oncourse.model.auto._Quote;

import java.util.ArrayList;
import java.util.List;

public class Quote extends _Quote {

    private static final long serialVersionUID = 1L;

    @Override
    public InvoiceType getType() {
        return InvoiceType.QUOTE;
    }

    @Override
    public void addToLines(AbstractInvoiceLine abstractLine) {
        super.addToQuoteLines((QuoteLine) abstractLine);
    }

    @Override
    public List<? extends AbstractInvoiceLine> getLines() {
        return getQuoteLines();
    }

    @Override
    public List<PaymentInLine> getPaymentInLines() {
        return new ArrayList<>();
    }

    @Override
    public List<InvoiceDueDate> getInvoiceDueDates() {
        return new ArrayList<>();
    }
}
