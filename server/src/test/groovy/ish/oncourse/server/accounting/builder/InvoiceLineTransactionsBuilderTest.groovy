package ish.oncourse.server.accounting.builder

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Tax
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class InvoiceLineTransactionsBuilderTest {

    private InvoiceLine invoiceLine
    private Long invLineId

    private Account taxAccount
    private Account debtorsAccount
    private Account liabilityAccount
    private Account incomeAccount
    private Account discountAccount
    private Money amount1
    private Money amount2
    private Money amount3
    private Money amount4
    private LocalDate transactionDate

    @BeforeEach
    void prepareData() {
        taxAccount = mock(Account)
        when(taxAccount.id).thenReturn(32L)
        debtorsAccount = mock(Account)
        when(debtorsAccount.id).thenReturn(61L)
        liabilityAccount = mock(Account)
        when(liabilityAccount.id).thenReturn(14L)
        incomeAccount = mock(Account)
        when(incomeAccount.id).thenReturn(58L)
        discountAccount = mock(Account)
        when(discountAccount.id).thenReturn(93L)

        Tax tax = mock(Tax)
        when(tax.receivableFromAccount).thenReturn(taxAccount)

        transactionDate = LocalDate.of(2017, Month.MAY, 28)

        Invoice invoice = mock(Invoice)
        when(invoice.debtorsAccount).thenReturn(debtorsAccount)
        when(invoice.invoiceDate).thenReturn(transactionDate)

        invLineId = 22L
        amount1 = new Money(30.0)
        amount2 = new Money(200.0)
        amount4 = new Money(25.0)
        Money total = new Money(300.0)
        invoiceLine = mock(InvoiceLine)
        when(invoiceLine.invoice).thenReturn(invoice)
        when(invoiceLine.tax).thenReturn(tax)
        when(invoiceLine.id).thenReturn(invLineId)
        when(invoiceLine.prepaidFeesAccount).thenReturn(liabilityAccount)
        when(invoiceLine.account).thenReturn(incomeAccount)
        when(invoiceLine.cosAccount).thenReturn(discountAccount)
        when(invoiceLine.totalTax).thenReturn(amount1)
        when(invoiceLine.finalPriceToPayExTax).thenReturn(total)
        when(invoiceLine.prepaidFeesRemaining).thenReturn(amount2)
        when(invoiceLine.discountTotalExTax).thenReturn(amount4)
        amount3 = new Money(100.0)
    }

    @Test
    void test() {
        TransactionSettings settings = InvoiceLineTransactionsBuilder.valueOf(invoiceLine).build()
        Assertions.assertTrue(settings.isInitialTransaction)
        Assertions.assertEquals(4, settings.details.size())
        AccountTransactionDetail detail = settings.details[0]
        Assertions.assertEquals(taxAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(debtorsAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount1, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)

        detail = settings.details[1]
        Assertions.assertEquals(liabilityAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(debtorsAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount2, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)

        detail = settings.details[2]
        Assertions.assertEquals(incomeAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(debtorsAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount3, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)

        detail = settings.details[3]
        Assertions.assertEquals(incomeAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(discountAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount4, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)
    }
}
