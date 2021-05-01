package ish.oncourse.server.accounting.builder

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class DepositTransactionsBuilderTest {

    private PaymentOutLine paymentOutLine
    private Account depositAccountOut
    private Account undepositAccountOut
    private Money amountOut
    private Long pOutId

    private PaymentInLine paymentInLine
    private Account depositAccountIn
    private Account undepositAccountIn
    private Money amountIn
    private Long pInId

    private LocalDate transactionDate1
    private LocalDate transactionDate2

    @BeforeEach
    void prepareData() {
        undepositAccountOut = mock(Account)
        when(undepositAccountOut.id).thenReturn(111L)
        depositAccountOut = mock(Account)
        when(depositAccountOut.id).thenReturn(222L)
        PaymentOut paymentOut = mock(PaymentOut)
        when(paymentOut.undepositedFundsAccount).thenReturn(undepositAccountOut)
        when(paymentOut.accountOut).thenReturn(depositAccountOut)
        amountOut = new Money(666.0)
        pOutId = 23L
        paymentOutLine = mock(PaymentOutLine)
        when(paymentOutLine.paymentOut).thenReturn(paymentOut)
        when(paymentOutLine.amount).thenReturn(amountOut)
        when(paymentOutLine.id).thenReturn(pOutId)


        undepositAccountIn = mock(Account)
        when(undepositAccountIn.id).thenReturn(333L)
        depositAccountIn = mock(Account)
        when(depositAccountIn.id).thenReturn(444L)
        PaymentIn paymentIn = mock(PaymentIn)
        when(paymentIn.undepositedFundsAccount).thenReturn(undepositAccountIn)
        when(paymentIn.accountIn).thenReturn(depositAccountIn)
        amountIn = new Money(555.0)
        pInId = 24L
        paymentInLine = mock(PaymentInLine)
        when(paymentInLine.paymentIn).thenReturn(paymentIn)
        when(paymentInLine.amount).thenReturn(amountIn)
        when(paymentInLine.id).thenReturn(pInId)

        transactionDate1 = LocalDate.of(2017, Month.MAY, 12)
        transactionDate2 = LocalDate.of(2017, Month.OCTOBER, 22)
    }


    @Test
    void test() {
        TransactionSettings settings = DepositTransactionsBuilder.valueOf(paymentOutLine, transactionDate1).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        AccountTransactionDetail detail = settings.details[0]
        Assertions.assertEquals(depositAccountOut.id, detail.primaryAccount.id)
        Assertions.assertEquals(undepositAccountOut.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountOut, detail.amount)
        Assertions.assertEquals(pOutId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_OUT_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate1, detail.transactionDate)


        settings = DepositTransactionsBuilder.valueOf(paymentOutLine, transactionDate1, transactionDate2).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(2, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(depositAccountOut.id, detail.primaryAccount.id)
        Assertions.assertEquals(undepositAccountOut.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountOut, detail.amount)
        Assertions.assertEquals(pOutId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_OUT_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate1, detail.transactionDate)
        detail = settings.details[1]
        Assertions.assertEquals(undepositAccountOut.id, detail.primaryAccount.id)
        Assertions.assertEquals(depositAccountOut.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountOut, detail.amount)
        Assertions.assertEquals(pOutId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_OUT_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate2, detail.transactionDate)


        settings = DepositTransactionsBuilder.valueOf(paymentInLine, transactionDate2).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(undepositAccountIn.id, detail.primaryAccount.id)
        Assertions.assertEquals(depositAccountIn.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountIn, detail.amount)
        Assertions.assertEquals(pInId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate2, detail.transactionDate)


        settings = DepositTransactionsBuilder.valueOf(paymentInLine, transactionDate2, transactionDate1).build()
        Assertions.assertFalse(settings.isInitialTransaction)
        Assertions.assertEquals(2, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(undepositAccountIn.id, detail.primaryAccount.id)
        Assertions.assertEquals(depositAccountIn.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountIn, detail.amount)
        Assertions.assertEquals(pInId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate2, detail.transactionDate)
        detail = settings.details[1]
        Assertions.assertEquals(depositAccountIn.id, detail.primaryAccount.id)
        Assertions.assertEquals(undepositAccountIn.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountIn, detail.amount)
        Assertions.assertEquals(pInId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate1, detail.transactionDate)
    }
}
