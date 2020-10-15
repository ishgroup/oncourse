/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ish.util.SecurityUtil
import org.apache.commons.lang3.StringUtils
import ish.oncourse.server.api.checkout.GetInvoiceLineTitle
import ish.oncourse.server.api.checkout.GetInvoiceLineDescription
import static ish.oncourse.server.api.v1.function.TaxFunctions.nonSupplyTax
import static java.math.BigDecimal.ONE

List<Voucher> vouchers = []

record.successAndQueuedEnrolments.each { Enrolment enrolment ->
    VoucherProduct product = ObjectSelect.query(VoucherProduct.class)
            .where(VoucherProduct.TYPE.eq(3).andExp(VoucherProduct.SKU.eq(sku)))
            .selectOne(context)
    if (product) {
        Invoice invoice = context.newObject(Invoice)
        invoice.amountOwing = Money.ZERO
        invoice.source  = PaymentSource.SOURCE_ONCOURSE
        invoice.confirmationStatus = ConfirmationStatus.NOT_SENT
        invoice.contact = enrolment.student.contact
        if (StringUtils.trimToNull(enrolment.student.contact.getAddress())) {
            invoice.setBillToAddress(enrolment.student.contact.getAddress())
        }
        invoice.dateDue = LocalDate.now()
        invoice.invoiceDate = LocalDate.now()
        invoice.allowAutoPay = true

        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        invoiceLine.quantity = ONE
        invoiceLine.priceEachExTax = Money.ZERO
        invoiceLine.discountEachExTax = Money.ZERO
        invoiceLine.tax = nonSupplyTax(context)
        invoiceLine.account = product.liabilityAccount
        invoiceLine.recalculateTaxEach()
        invoiceLine.recalculatePrepaidFeesRemaining()

        Voucher voucher = context.newObject(Voucher)
        voucher.product = product
        voucher.status = ProductStatus.ACTIVE
        voucher.code = SecurityUtil.generateVoucherCode()
        voucher.source = PaymentSource.SOURCE_ONCOURSE
        voucher.invoiceLine = invoiceLine
        voucher.expiryDate = (new Date() + product.expiryDays).clearTime()
        voucher.confirmationStatus = ConfirmationStatus.DO_NOT_SEND
        voucher.redemptionValue = amount
        voucher.valueOnPurchase = amount
        voucher.redeemedCourseCount = 0

        invoiceLine.title = GetInvoiceLineTitle.valueOf(voucher).get()
        invoiceLine.description = GetInvoiceLineDescription.valueOf(voucher).get()

        context.commitChanges()

        vouchers.add(voucher)
    }
}

message {
    template template_keycode
    records vouchers
}
