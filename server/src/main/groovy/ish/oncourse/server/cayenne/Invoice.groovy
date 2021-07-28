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

package ish.oncourse.server.cayenne

import com.google.inject.Inject
import ish.common.types.EnrolmentStatus
import ish.common.types.InvoiceType
import ish.oncourse.API
import ish.oncourse.cayenne.InvoiceInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Invoice
import ish.oncourse.server.services.IAutoIncrementService
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory

import javax.annotation.Nonnull

/**
 * Invoices are where the accounting side of onCourse meets the training and delivery parts of the
 * database. Invoices can be created to any contact. The financial data on them is immutable, that is
 * the dates and dollar amounts cannot be modified after creation.
 *
 * Invoice lines may join to enrolments or products for sale.
 *
 * Negative invoices represent credit notes.
 */
@API
@QueueableEntity
class Invoice extends _Invoice implements InvoiceInterface {

    @Inject
    private transient IAutoIncrementService autoIncrementService

    @Override
    InvoiceType getType() {
        return InvoiceType.INVOICE
    }

    Class<InvoiceLine> getLinePersistentClass() {
        return InvoiceLine.class
    }

    List<InvoiceLine> getLines() {
        return getInvoiceLines()
    }

    @Override
    void postAdd() {
        super.postAdd()
        if (getInvoiceNumber() == null) {
            setInvoiceNumber(autoIncrementService.getNextInvoiceNumber())
        }
    }

    /**
     * @return list of invoice line records linked to this invoice
     */
    @Nonnull
    @API
    @Override
    List<InvoiceLine> getInvoiceLines() {
        return super.getInvoiceLines()
    }

    /**
     * Follow this join to find all payments made against this invoice. Remember that onCourse supports multiple payments
     * against one invoice and also partial payments against an invoice, so you can follow this join to many payments
     * and some of those payment may also link to other invoices.
     *
     * @return all payementInLines against this invoice
     */
    @Nonnull
    @API
    @Override
    List<PaymentInLine> getPaymentInLines() {
        return super.getPaymentInLines()
    }

    /**
     * If this invoice was a credit note, then a PaymentOut (or several) might be linked against it.
     *
     * @return all payementOutLines against this invoice
     */
    @Nonnull
    @API
    @Override
    List<PaymentOutLine> getPaymentOutLines() {
        return super.getPaymentOutLines()
    }

    /**
     * Get a list of paymentInLines and paymentOutLines linked to this invoice
     * @return all paymentLines against this invoice
     */
    @Nonnull
    @API
    List<PaymentLineInterface> getPaymentLines() {
        ArrayList<PaymentLineInterface> list = new ArrayList<>()
        list.addAll(getPaymentInLines())
        list.addAll(getPaymentOutLines())
        return list
    }

    void removeFromPaymentLines(PaymentLineInterface pLine) {
        if (pLine instanceof PaymentInLine) {
            removeFromPaymentInLines((PaymentInLine) pLine)
        } else if (pLine instanceof PaymentOutLine) {
            removeFromPaymentOutLines((PaymentOutLine) pLine)
        }
    }

    /**
     * Builds a list of enrolments from the invoice lines mapped per student.
     *
     * @return a map of enrolments (values as Collection) per student (keys)
     */
    @Nonnull
    @API
    Map<Student, Set<Enrolment>> enrolmentsPerStudent() {
        final Map<Student, Set<Enrolment>> results = new HashMap<>()

        for (final InvoiceLine aLine : getInvoiceLines()) {
            final Enrolment anEnrolment = aLine.getEnrolment()
            final Student aStudent = anEnrolment.getStudent()
            Set<Enrolment> studentEnrolments = results.get(aStudent)
            if (studentEnrolments == null) {
                studentEnrolments = new HashSet<>()
                results.put(aStudent, studentEnrolments)
            }
        }
        return results
    }

    /**
     * @return a list of enrolments with a specified status
     */
    @Nonnull
    @API
    List<Enrolment> getEnrolmentsWithStatus(final EnrolmentStatus enrolmentStatus) {
        List<Enrolment> result = new ArrayList<>()
        Expression expr = ExpressionFactory.noMatchExp(InvoiceLine.ENROLMENT.name, null)
        expr = expr.andExp(ExpressionFactory.matchExp(InvoiceLine.ENROLMENT.name + "." + Enrolment.STATUS.name, enrolmentStatus))

        final List<InvoiceLine> invoiceLines = expr.filterObjects(getLines())
        for (InvoiceLine invoiceLine : invoiceLines) {
            if (result == null) {
                result = new ArrayList<>()
            }
            if (invoiceLine != null && invoiceLine.getEnrolment() != null) {
                result.add(invoiceLine.getEnrolment())
            }
        }
        return result
    }

    //fixme: temporary workaround OD-12674
    @Override
    void addToPaymentOutLines(PaymentOutLine obj) {
        if (obj != null) {
            obj.setInvoice((Invoice) this)
        }
    }

    @Override
    void removeFromPaymentOutLines(PaymentOutLine obj) {
        if (obj != null) {
            obj.setInvoice(null)
        }
    }
    @Override
    void addToPaymentInLines(PaymentInLine obj) {
        if (obj != null) {
            obj.setInvoice((Invoice) this)
        }
    }
    @Override
    void removeFromPaymentInLines(PaymentInLine obj) {
        if (obj != null) {
            obj.setInvoice(null)
        }
    }

    @Override
    void addToInvoiceLines(InvoiceLine obj) {
        if (obj != null) {
            obj.setInvoice((Invoice) this)
        }
    }
    @Override
    void removeFromInvoiceLines(InvoiceLine obj) {
        if (obj != null) {
            obj.setInvoice(null)
        }
    }

    @Override
    String getSummaryDescription() {
        if (getDescription() == null) {
            return "#" + getInvoiceNumber()
        }
        return "#" + getInvoiceNumber() + " " + getDescription()
    }

}
