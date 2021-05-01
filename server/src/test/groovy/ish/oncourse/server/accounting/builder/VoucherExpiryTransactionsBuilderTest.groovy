package ish.oncourse.server.accounting.builder

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherProduct
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class VoucherExpiryTransactionsBuilderTest {

    private Voucher voucher
    private VoucherProduct voucherProduct
    private Long invLineId
    private Account voucherExpiryAccount
    private Account voucherLiabilityAccount
    private Money amount1
    private Money amount2
    private LocalDate transactionDate

    @BeforeEach
    void prepareData() {
        voucherExpiryAccount = mock(Account)
        when(voucherExpiryAccount.id).thenReturn(32L)
        voucherLiabilityAccount = mock(Account)
        when(voucherLiabilityAccount.id).thenReturn(61L)

        Long invLineId = 43L
        InvoiceLine invoiceLine = mock(InvoiceLine)
        when(invoiceLine.finalPriceToPayExTax).thenReturn(new Money(300.0))
        when(invoiceLine.id).thenReturn(invLineId)

        voucherProduct = mock(VoucherProduct)
        when(voucherProduct.liabilityAccount).thenReturn(voucherLiabilityAccount)
        when(voucherProduct.maxCoursesRedemption).thenReturn(3)

        voucher = mock(Voucher)
        when(voucher.invoiceLine).thenReturn(invoiceLine)
        when(voucher.voucherProduct).thenReturn(voucherProduct)
        when(voucher.redeemedCourseCount).thenReturn(1)
        when(voucher.valueOnPurchase).thenReturn(new Money(300.0))
        when(voucher.redemptionValue).thenReturn(new Money(100.0))

        amount1 = new Money(200.0)
        amount2 = new Money(100.0)

        transactionDate = LocalDate.of(2017, Month.MAY, 28)
    }

    @Test
    void test() {
        TransactionSettings settings = VoucherExpiryTransactionsBuilder.valueOf(voucher, voucherExpiryAccount, transactionDate).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        AccountTransactionDetail detail = settings.details[0]
        Assertions.assertEquals(voucherExpiryAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(voucherLiabilityAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount1, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)


        when(voucherProduct.maxCoursesRedemption).thenReturn(null)

        settings = VoucherExpiryTransactionsBuilder.valueOf(voucher, voucherExpiryAccount, transactionDate).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(voucherExpiryAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(voucherLiabilityAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount2, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)
    }

}
