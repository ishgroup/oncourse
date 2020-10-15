import groovy.json.JsonSlurper
import ish.util.ProductUtil
import ish.util.SecurityUtil

def run(args) {

	def dir = new File("/usr/local/onCourse/onCourseAPI/Voucher")

	dir.listFiles().findAll {  file ->

		def voucherData = new JsonSlurper().parse(file)

        VoucherProduct product = ObjectSelect.query(VoucherProduct).where(VoucherProduct.ID.eq(voucherData.productId)).selectOne(args.context)
        Contact contact = ObjectSelect.query(Contact).where(Contact.ID.eq(voucherData.contactId)).selectOne(args.context)

		if (contact != null && product != null && product.value != null) {
			Invoice invoice = args.context.newObject(Invoice)
			invoice.contact = contact
			invoice.amountOwing = Money.ZERO
			invoice.source = PaymentSource.SOURCE_ONCOURSE
			invoice.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

            InvoiceLine invoiceLine = args.context.newObject(InvoiceLine)
			invoiceLine.invoice = invoice
			invoiceLine.account = product.liabilityAccount
			invoiceLine.tax = product.tax
			invoiceLine.title = product.name
			invoiceLine.priceEachExTax = Money.ZERO
			invoiceLine.taxEach = Money.ZERO
			invoiceLine.discountEachExTax = Money.ZERO
			invoiceLine.quantity = 1
			invoiceLine.description = "${contact.getName(true)} (${product.sku} ${product.name})"

            Voucher voucher = args.context.newObject(Voucher)
			voucher.product = product
			voucher.redeemableBy = contact
			voucher.code = SecurityUtil.generateVoucherCode()
			voucher.source = PaymentSource.SOURCE_ONCOURSE
			voucher.status = ProductStatus.ACTIVE
			voucher.invoiceLine = invoiceLine
			voucher.redemptionValue = product.value
			voucher.valueOnPurchase = product.value
			voucher.redeemedCourseCount = 0
			voucher.expiryDate = ProductUtil.calculateExpiryDate(ProductUtil.getToday(), product.expiryType, product.expiryDays)
			voucher.confirmationStatus = ConfirmationStatus.NOT_SENT

			args.context.commitChanges()
		}

		file.delete()
	}
}
