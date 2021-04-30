package ish.oncourse.server.accounting.builder

import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static junit.framework.TestCase.assertEquals
import static junit.framework.TestCase.assertTrue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class PaymentOutTransactionsBuilderTest {

    private PaymentOutLine paymentOutLine
    private PaymentMethod paymentMethod
    private Account primaryAccount
    private Account secondaryAccountUndeposit
    private Account secondaryAccountDeposit
    private Money amount
    private LocalDate transactionDate
    private Long foreignRecordId

    @BeforeEach
    void prepareData() {
        foreignRecordId = 123456L
        
        primaryAccount = mock(Account)
        when(primaryAccount.id).thenReturn(55L)

        secondaryAccountUndeposit = mock(Account)
        when(secondaryAccountUndeposit.id).thenReturn(77L)
        secondaryAccountDeposit = mock(Account)
        when(secondaryAccountDeposit.id).thenReturn(88L)
        
        
        transactionDate = LocalDate.of(2016, Month.JUNE, 12)

        Banking banking = mock(Banking)
        paymentMethod = mock(PaymentMethod)
        when(paymentMethod.bankedAutomatically).thenReturn(true)
        

        PaymentOut paymentOut = mock(PaymentOut)
        when(paymentOut.paymentDate).thenReturn(transactionDate)
        when(paymentOut.paymentMethod).thenReturn(paymentMethod)
        when(paymentOut.banking).thenReturn(banking)
        when(paymentOut.undepositedFundsAccount).thenReturn(secondaryAccountUndeposit)
        when(paymentOut.accountOut).thenReturn(secondaryAccountDeposit)
        
        amount = new Money(99)
        
        Invoice invoice = mock(Invoice)
        when(invoice.debtorsAccount).thenReturn(primaryAccount)
        
        paymentOutLine = mock(PaymentOutLine)
        when(paymentOutLine.paymentOut).thenReturn(paymentOut)
        
        when(paymentOutLine.amount).thenReturn(amount)
        when(paymentOutLine.invoice).thenReturn(invoice)
        when(paymentOutLine.id).thenReturn(foreignRecordId)
    }
    
    
    @Test
    void test() {
        TransactionSettings settings = PaymentOutTransactionsBuilder.valueOf(paymentOutLine).build()
        assertTrue(settings.isInitialTransaction)
        assertEquals(1, settings.details.size())
        AccountTransactionDetail detail = settings.details[0]
        assertEquals(primaryAccount.id, detail.primaryAccount.id)
        assertEquals(secondaryAccountDeposit.id, detail.secondaryAccount.id)
        assertEquals(amount, detail.amount)
        assertEquals(foreignRecordId, detail.foreignRecordId)
        assertEquals(AccountTransactionType.PAYMENT_OUT_LINE, detail.tableName)
        assertEquals(transactionDate, detail.transactionDate)
        
        
        
        when(paymentMethod.bankedAutomatically).thenReturn(false)
        settings = PaymentOutTransactionsBuilder.valueOf(paymentOutLine).build()
        assertTrue(settings.isInitialTransaction)
        assertEquals(1, settings.details.size())
        detail = settings.details[0]
        assertEquals(primaryAccount.id, detail.primaryAccount.id)
        assertEquals(secondaryAccountUndeposit.id, detail.secondaryAccount.id)
        assertEquals(amount, detail.amount)
        assertEquals(foreignRecordId, detail.foreignRecordId)
        assertEquals(AccountTransactionType.PAYMENT_OUT_LINE, detail.tableName)
        assertEquals(transactionDate, detail.transactionDate)
    }
}
