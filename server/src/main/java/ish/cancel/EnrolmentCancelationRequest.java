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

package ish.cancel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EnrolmentCancelationRequest implements Serializable {

    private Long enrolmentId;
    private Boolean transfer = false;
    private List<InvoiceLineRequest> linesToRefund = new ArrayList<InvoiceLineRequest>();

    public Long getEnrolmentId() {
        return enrolmentId;
    }

    public void setEnrolmentId(Long enrolmentId) {
        this.enrolmentId = enrolmentId;
    }

    public Boolean getTransfer() {
        return transfer;
    }

    public void setTransfer(Boolean transfer) {
        this.transfer = transfer;
    }

    public List<InvoiceLineRequest> getLinesToRefund() {
        return linesToRefund;
    }

    public void addLineToRefund(InvoiceLineRequest invoiceLineRequest){
        linesToRefund.add(invoiceLineRequest);
    }


    public static class InvoiceLineRequest {
        private Long invoiceLineId;
        private Long accountId;
        private Long taxId;
        private BigDecimal cancellationFee;
        private Boolean sendInvoice;

        public InvoiceLineRequest() {
        }

        public Long getInvoiceLineId() {
            return invoiceLineId;
        }

        public void setInvoiceLineId(Long invoiceLineId) {
            this.invoiceLineId = invoiceLineId;
        }

        public Long getAccountId() {
            return accountId;
        }

        public void setAccountId(Long accountId) {
            this.accountId = accountId;
        }

        public Long getTaxId() {
            return taxId;
        }

        public void setTaxId(Long taxId) {
            this.taxId = taxId;
        }

        public BigDecimal getCancellationFee() {
            return cancellationFee;
        }

        public void setCancellationFee(BigDecimal cancellationFee) {
            this.cancellationFee = cancellationFee;
        }

        public Boolean getSendInvoice() {
            return sendInvoice;
        }

        public void setSendInvoice(Boolean sendInvoice) {
            this.sendInvoice = sendInvoice;
        }
    }
}
