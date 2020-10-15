import ish.common.types.AccountTransactionType
import ish.common.types.PaymentSource
import ish.common.types.ProductType
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.VoucherProduct
import org.apache.cayenne.query.ObjectSelect

def run (args) {
    def context = args.context

    def invoiceLines = ObjectSelect.query(InvoiceLine)
            .where(InvoiceLine.INVOICE.dot(Invoice.SOURCE).eq(PaymentSource.SOURCE_WEB))
            .and(InvoiceLine.ENROLMENT.isNull())
            .and(InvoiceLine.PRODUCT_ITEMS.isNotNull())
            .and(InvoiceLine.PRODUCT_ITEMS.dot(ProductItem.TYPE).eq(ProductType.VOUCHER.databaseValue))
            .select(context)

    invoiceLines.each { il ->

        Account rightAccount = ((VoucherProduct) il.productItems[0].product).liabilityAccount
        Account accountToFix = il.account

        if (rightAccount.id != accountToFix.id) {
            il.account = rightAccount

            List<AccountTransaction> transactionsToFix = ObjectSelect.query(AccountTransaction)
                    .where(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.INVOICE_LINE))
                    .and(AccountTransaction.FOREIGN_RECORD_ID.eq(il.id))
                    .and(AccountTransaction.ACCOUNT.eq(accountToFix))
                    .select(context)

            transactionsToFix.each { at ->
                at.account = rightAccount
            }

            context.commitChanges()
        }
    }
}