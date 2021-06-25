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
import ish.oncourse.model.ProductItem
import ish.oncourse.model.Voucher
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.payment.ProductsItemInvoiceLine
import ish.oncourse.willow.functions.field.FieldHelper
import ish.oncourse.willow.model.field.FieldHeading
import ish.util.ProductUtil
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext

import static ish.oncourse.willow.functions.field.FieldHelper.separateFields

class CreateVoucher {

    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Voucher v
    private Contact contact
    private Invoice invoice
    private ProductStatus status
    private ConfirmationStatus confirmationStatus

    CreateVoucher(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Voucher v, Contact contact, Invoice invoice, ProductStatus status, ConfirmationStatus confirmationStatus) {
        this.context = context
        this.college = college
        this.v = v
        this.contact = contact
        this.invoice = invoice
        this.status = status
        this.confirmationStatus = confirmationStatus
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    void create() {
        VoucherProduct voucherProduct = new GetProduct(context, college, v.productId).get() as VoucherProduct

        Money price = null

        List<FieldHeading> separatedFieldHeadings = separateFields(v.fieldHeadings, v.quantity)

        List<ProductItem> vouchers = new ArrayList<>()
        if (voucherProduct.redemptionCourses.empty && voucherProduct.priceExTax == null) {
            price = v.value.toMoney()
            (1..v.quantity).each {
                Voucher voucher = createVoucher(college, status, voucherProduct, confirmationStatus)
                voucher.redemptionValue = price
                voucher.valueOnPurchase = price
                FieldHelper.valueOf([] as Set).populateFields(Arrays.asList(separatedFieldHeadings.get(it - 1)), voucher)
                vouchers << voucher
            }
        } else if (voucherProduct.priceExTax != null) {
            price = voucherProduct.priceExTax
            (1..v.quantity).each {
                Voucher voucher = createVoucher(college, status, voucherProduct, confirmationStatus)
                voucher.redemptionValue = voucherProduct.value != null ? voucherProduct.value : price
                voucher.valueOnPurchase = voucherProduct.value != null ? voucherProduct.value : price
                FieldHelper.valueOf([] as Set).populateFields(Arrays.asList(separatedFieldHeadings.get(it - 1)), voucher)
                vouchers << voucher
            }
        }

        InvoiceLine invoiceLine

        invoiceLine = new ProductsItemInvoiceLine(context, vouchers, contact, price, null).create()
        invoiceLine.invoice = invoice
    }

    private Voucher createVoucher(College college, ProductStatus status, VoucherProduct voucherProduct, ConfirmationStatus confirmationStatus) {
        Voucher voucher = context.newObject(Voucher)
        voucher.code = SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH)
        voucher.college = college

        voucher.source = PaymentSource.SOURCE_WEB
        voucher.status = status
        voucher.product = voucherProduct
        voucher.redeemedCoursesCount = 0
        voucher.confirmationStatus = confirmationStatus

        voucher.expiryDate = ProductUtil.calculateExpiryDate(new Date(), voucherProduct.expiryType, voucherProduct.expiryDays)
        voucher
    }

}
