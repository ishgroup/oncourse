package ish.oncourse.willow.checkout.persistent

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.ConfirmationStatus
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Voucher
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.payment.ProductItemInvoiceLine
import ish.util.ProductUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext

class CreateVoucher {
    
    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Voucher v
    private Contact contact
    private Invoice invoice
    private ProductStatus status

    CreateVoucher(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Voucher v, Contact contact, Invoice invoice, ProductStatus status) {
        this.context = context
        this.college = college
        this.v = v
        this.contact = contact
        this.invoice = invoice
        this.status = status
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void create() {
        VoucherProduct voucherProduct = new GetProduct(context, college, v.productId).get() as VoucherProduct
        Voucher voucher = context.newObject(Voucher)
        voucher.code = SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH)
        voucher.college = college

        voucher.source = PaymentSource.SOURCE_WEB
        voucher.status = status
        voucher.product = voucherProduct
        voucher.redeemedCoursesCount = 0
        voucher.confirmationStatus = ConfirmationStatus.NOT_SENT

        voucher.expiryDate = ProductUtil.calculateExpiryDate(new Date(), voucherProduct.expiryType, voucherProduct.expiryDays)

        InvoiceLine invoiceLine
        Money price = null
        if (voucherProduct.redemptionCourses.empty && voucherProduct.priceExTax == null) {
            price = v.value.toMoney()
            voucher.redemptionValue = price
            voucher.valueOnPurchase = price
        } else if (voucherProduct.priceExTax != null) {
            voucher.redemptionValue = voucherProduct.value
            voucher.valueOnPurchase = voucherProduct.value
            price = voucherProduct.priceExTax
        }
        invoiceLine = new ProductItemInvoiceLine(voucher, contact, price?: voucherProduct.priceExTax, null).create()
        invoiceLine.invoice = invoice
    }

}
