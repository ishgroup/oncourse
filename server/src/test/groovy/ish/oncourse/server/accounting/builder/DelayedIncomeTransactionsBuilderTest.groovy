package ish.oncourse.server.accounting.builder

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.InvoiceLine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class DelayedIncomeTransactionsBuilderTest {

    private InvoiceLine invoiceLine
    private invLineId
    private Account primaryAccount
    private Account secondaryAccount
    private Money amount
    private LocalDate transactionDate

    @BeforeEach
    void prepareData() {
        primaryAccount = mock(Account)
        when(primaryAccount.id).thenReturn(32L)
        secondaryAccount = mock(Account)
        when(secondaryAccount.id).thenReturn(61L)

        Long invLineId = 45L
        invoiceLine = mock(InvoiceLine)
        when(invoiceLine.account).thenReturn(primaryAccount)
        when(invoiceLine.prepaidFeesAccount).thenReturn(secondaryAccount)
        when(invoiceLine.id).thenReturn(invLineId)

        amount = new Money(230.0)
        transactionDate = LocalDate.of(2017, Month.MAY, 28)
    }

    @Test
    void test() {
        TransactionSettings settings = DelayedIncomeTransactionsBuilder.valueOf(invoiceLine, amount, transactionDate).build()

        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())

        AccountTransactionDetail detail = settings.details[0]

        Assertions.assertEquals(primaryAccount.id, detail.primaryAccount.id)
        Assertions.assertEquals(secondaryAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount, detail.amount)
        Assertions.assertEquals(invLineId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)
    }
}
