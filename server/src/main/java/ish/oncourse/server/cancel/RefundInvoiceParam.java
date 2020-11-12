/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cancel;

import ish.math.Money;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.Tax;

import java.io.Serializable;

public class RefundInvoiceParam implements Serializable {
    private InvoiceLine lineToRefund;
    private Account account;
    private Tax tax;
    private Money cancellationFee;
    private Boolean sendInvoice;

    public RefundInvoiceParam(InvoiceLine lineToRefund, Account account, Tax tax, Money cancellationFee, Boolean sendInvoice) {
        this.lineToRefund = lineToRefund;
        this.account = account;
        this.tax = tax;
        this.cancellationFee = cancellationFee;
        this.sendInvoice = sendInvoice;
    }

    public InvoiceLine getLineToRefund() {
        return lineToRefund;
    }

    public void setLineToRefund(InvoiceLine lineToRefund) {
        this.lineToRefund = lineToRefund;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Money getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(Money cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public Boolean getSendInvoice() {
        return sendInvoice;
    }

    public void setSendInvoice(Boolean sendInvoice) {
        this.sendInvoice = sendInvoice;
    }
}
