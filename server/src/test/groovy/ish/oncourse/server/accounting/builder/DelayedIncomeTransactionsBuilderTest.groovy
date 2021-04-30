package ish.oncourse.server.accounting.builder

import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.InvoiceLine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertFalse
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

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

        invLineId = 45L
        invoiceLine = mock(InvoiceLine)
        when(invoiceLine.account).thenReturn(primaryAccount)
        when(invoiceLine.prepaidFeesAccount).thenReturn(secondaryAccount)
        when(invoiceLine.id).thenReturn(invLineId)
        
        amount = new Money(230)
        transactionDate = LocalDate.of(2017, Month.MAY, 28)
    }

    @Test
    void test() {
        TransactionSettings settings = DelayedIncomeTransactionsBuilder.valueOf(invoiceLine, amount, transactionDate).build()

        assertFalse(settings.isInitialTransaction)
        assertEquals(1, settings.details.size())
        
        AccountTransactionDetail detail = settings.details[0]
        
        assertEquals(primaryAccount.id, detail.primaryAccount.id)
        assertEquals(secondaryAccount.id, detail.secondaryAccount.id)
        assertEquals(amount, detail.amount)
        assertEquals(invLineId, detail.foreignRecordId)
        assertEquals(AccountTransactionType.INVOICE_LINE, detail.tableName)
        assertEquals(transactionDate, detail.transactionDate)
    }
}
