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

package ish.oncourse.server.duplicate;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceDueDate;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.InvoiceLineDiscount;
import ish.oncourse.server.cayenne.InvoiceNoteRelation;
import ish.util.InvoiceUtil;
import ish.util.NoteUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DuplicateInvoiceService {

    private static final String INITIAL_QUANTITY = "1.00";

    public Invoice duplicateAndReverseInvoice(Invoice invoiceToCopy) {
        var context = invoiceToCopy.getContext();

        var duplicateInvoice = context.newObject(Invoice.class);

        duplicateInvoice.setBillToAddress(invoiceToCopy.getBillToAddress());
        duplicateInvoice.setDescription(invoiceToCopy.getDescription());
        NoteUtil.copyNotes(invoiceToCopy, duplicateInvoice, InvoiceNoteRelation.class);
        duplicateInvoice.setPublicNotes(invoiceToCopy.getPublicNotes());
        duplicateInvoice.setShippingAddress(invoiceToCopy.getShippingAddress());
        duplicateInvoice.setInvoiceDate(LocalDate.now());
        duplicateInvoice.setCustomerReference(invoiceToCopy.getCustomerReference());
        duplicateInvoice.setDebtorsAccount(invoiceToCopy.getDebtorsAccount());
        duplicateInvoice.setContact(invoiceToCopy.getContact());
        duplicateInvoice.setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);

        var defaultTerms = PreferenceController.getController().getAccountInvoiceTerms();
        var contactTerms = duplicateInvoice.getContact().getInvoiceTerms();

        Integer daysToAdd = contactTerms != null ? contactTerms : defaultTerms != null ? defaultTerms : 0;
        var due_date = LocalDate.now().plusDays(daysToAdd);

        duplicateInvoice.setDateDue(due_date);

        for (var invoiceLineToCopy : invoiceToCopy.getInvoiceLines()) {
            var duplicateInvoiceLine = context.newObject(InvoiceLine.class);

            duplicateInvoiceLine.setAccount(invoiceLineToCopy.getAccount()); // account from courseClass
            duplicateInvoiceLine.setPrepaidFeesAccount(invoiceLineToCopy.getPrepaidFeesAccount());
            duplicateInvoiceLine.setDescription(invoiceLineToCopy.getDescription());
            duplicateInvoiceLine.setInvoice(duplicateInvoice);
            duplicateInvoiceLine.setSortOrder(invoiceLineToCopy.getSortOrder());
            duplicateInvoiceLine.setTitle(invoiceLineToCopy.getTitle());
            duplicateInvoiceLine.setUnit(invoiceLineToCopy.getUnit());
            duplicateInvoiceLine.setQuantity(new BigDecimal(INITIAL_QUANTITY));
            duplicateInvoiceLine.setTax(invoiceLineToCopy.getTax());
            duplicateInvoiceLine.setCourseClass(invoiceLineToCopy.getCourseClass());
            duplicateInvoiceLine.setEnrolment(invoiceLineToCopy.getEnrolment());
            duplicateInvoiceLine.setPriceEachExTax(invoiceLineToCopy.getPriceEachExTax().negate());
            duplicateInvoiceLine.setDiscountEachExTax(invoiceLineToCopy.getDiscountEachExTax().negate());

            duplicateInvoiceLine.setTaxEach(invoiceLineToCopy.getTaxEach().negate());

            duplicateInvoiceLine.setCosAccount(invoiceLineToCopy.getCosAccount());
            for (var ilDiscount : invoiceLineToCopy.getInvoiceLineDiscounts()) {
                var duplicateInvoiceLineDiscount = context.newObject(InvoiceLineDiscount.class);
                duplicateInvoiceLineDiscount.setInvoiceLine(duplicateInvoiceLine);
                duplicateInvoiceLineDiscount.setDiscount(ilDiscount.getDiscount());
            }

        }

        for (var dueDate : invoiceToCopy.getInvoiceDueDates()) {
            var duplicateDueDate = context.newObject(InvoiceDueDate.class);
            duplicateDueDate.setInvoice(duplicateInvoice);
            duplicateDueDate.setAmount(dueDate.getAmount().negate());
            duplicateDueDate.setDueDate(dueDate.getDueDate());
        }

        InvoiceUtil.updateAmountOwing(duplicateInvoice);

        return duplicateInvoice;
    }
}
