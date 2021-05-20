package ish.oncourse.server.accounting.builder

import groovy.transform.CompileStatic
import ish.common.types.AccountTransactionType
import ish.common.types.PaymentType
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
class PaymentInTransactionsBuilderTest {

    private PaymentInLine paymentInLine
    private Account voucherExpense
    private PaymentIn paymentIn
    private Money amountPaid
    private PaymentMethod paymentMethod
    private Account primaryAccountDeposit
    private Account primaryAccountUndeposit
    private Account secondaryAccount
    private Money amount
    private LocalDate transactionDate
    private Long foreignRecordId
    private Money amountOf2Tr

    @BeforeEach
    void prepareData() {
        foreignRecordId = 123456L

        voucherExpense = mock(Account)
        when(voucherExpense.id).thenReturn(321L)
        primaryAccountDeposit = mock(Account)
        when(primaryAccountDeposit.id).thenReturn(551L)
        primaryAccountUndeposit = mock(Account)
        when(primaryAccountUndeposit.id).thenReturn(771L)
        secondaryAccount = mock(Account)
        when(secondaryAccount.id).thenReturn(881L)


        transactionDate = LocalDate.of(2016, Month.JUNE, 12)


        paymentMethod = mock(PaymentMethod)
        when(paymentMethod.type).thenReturn(PaymentType.CASH)
        when(paymentMethod.bankedAutomatically).thenReturn(true)

        amountPaid = new Money(300 as BigDecimal)
        Banking banking = mock(Banking)
        paymentIn = mock(PaymentIn)
        when(paymentIn.paymentDate).thenReturn(transactionDate)
        when(paymentIn.paymentMethod).thenReturn(paymentMethod)
        when(paymentIn.banking).thenReturn(banking)
        when(paymentIn.undepositedFundsAccount).thenReturn(primaryAccountUndeposit)
        when(paymentIn.accountIn).thenReturn(primaryAccountDeposit)
        when(paymentIn.amount).thenReturn(amountPaid)

        amount = new Money(99 as BigDecimal)

        Invoice invoice = mock(Invoice)
        when(invoice.debtorsAccount).thenReturn(secondaryAccount)

        paymentInLine = mock(PaymentInLine)
        when(paymentInLine.paymentIn).thenReturn(paymentIn)
        when(paymentInLine.amount).thenReturn(amount)
        when(paymentInLine.invoice).thenReturn(invoice)
        when(paymentInLine.id).thenReturn(foreignRecordId)

        amountOf2Tr = new Money(33 as BigDecimal)
    }


    @Test
    void test() {
        TransactionSettings settings = PaymentInTransactionsBuilder.valueOf(paymentInLine, voucherExpense).build()
        Assertions.assertTrue(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        AccountTransactionDetail detail = settings.details[0]
        Assertions.assertEquals(primaryAccountDeposit.id, detail.primaryAccount.id)
        Assertions.assertEquals(secondaryAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount, detail.amount)
        Assertions.assertEquals(foreignRecordId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)


        when(paymentIn.banking).thenReturn(null)
        settings = PaymentInTransactionsBuilder.valueOf(paymentInLine, voucherExpense).build()
        Assertions.assertTrue(settings.isInitialTransaction)
        Assertions.assertEquals(1, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(primaryAccountUndeposit.id, detail.primaryAccount.id)
        Assertions.assertEquals(secondaryAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount, detail.amount)
        Assertions.assertEquals(foreignRecordId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)


        when(paymentMethod.type).thenReturn(PaymentType.VOUCHER)
        settings = PaymentInTransactionsBuilder.valueOf(paymentInLine, voucherExpense, { amountOf2Tr }).build()
        Assertions.assertTrue(settings.isInitialTransaction)
        Assertions.assertEquals(2, settings.details.size())
        detail = settings.details[0]
        Assertions.assertEquals(voucherExpense.id, detail.primaryAccount.id)
        Assertions.assertEquals(secondaryAccount.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amount, detail.amount)
        Assertions.assertEquals(foreignRecordId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)

        detail = settings.details[1]
        Assertions.assertEquals(voucherExpense.id, detail.primaryAccount.id)
        Assertions.assertEquals(primaryAccountDeposit.id, detail.secondaryAccount.id)
        Assertions.assertEquals(amountOf2Tr, detail.amount)
        Assertions.assertEquals(foreignRecordId, detail.foreignRecordId)
        Assertions.assertEquals(AccountTransactionType.PAYMENT_IN_LINE, detail.tableName)
        Assertions.assertEquals(transactionDate, detail.transactionDate)
    }

}
