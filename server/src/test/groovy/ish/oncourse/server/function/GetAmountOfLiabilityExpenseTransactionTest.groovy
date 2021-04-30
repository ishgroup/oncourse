package ish.oncourse.server.function

import ish.math.Money
import ish.oncourse.server.cayenne.*
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GetAmountOfLiabilityExpenseTransactionTest {
    private VoucherProduct voucherProduct
    private Voucher voucher
    private PaymentInLine paymentInLine
    private PaymentIn paymentIn
    private InvoiceLine invoiceLine
    private List<VoucherPaymentIn> voucherPayments


    /**
     * Standard structure with money voucher, which price is equal to amount.
     * Voucher real price wasn't change, when it was bought.
     * Voucher covers whole paymentIn's amount.
     */
    @BeforeEach
    void before(){
        VoucherPaymentIn voucherPaymentIn = mock(VoucherPaymentIn.class)
        voucherProduct = mock(VoucherProduct.class)
        voucher = mock(Voucher.class)
        paymentInLine = mock(PaymentInLine.class)
        paymentIn = mock(PaymentIn.class)
        voucherPayments = new ArrayList<>()
        voucherPayments.add(voucherPaymentIn)
        invoiceLine = mock(InvoiceLine.class)

        when(paymentIn.getVoucherPayments()).thenReturn(voucherPayments)
        when(paymentIn.getAmount()).thenReturn(new Money("100"))

        when(voucherPaymentIn.getPaymentIn()).thenReturn(paymentIn)
        when(voucherPaymentIn.getVoucher()).thenReturn(voucher)

        when(paymentInLine.getPaymentIn()).thenReturn(paymentIn)
        when(paymentInLine.getAmount()).thenReturn(new Money("100"))

        when(voucherProduct.getMaxCoursesRedemption()).thenReturn(null) // money voucher
        when(voucherProduct.getPriceExTax()).thenReturn(new Money("100")) // voucher's default price

        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("100")) //voucher's price when buy it

        when(voucher.getVoucherPaymentsIn()).thenReturn(voucherPayments) // all previous voucher payments to calculate the balance
        when(voucher.getVoucherProduct()).thenReturn(voucherProduct)
        when(voucher.getInvoiceLine()).thenReturn(invoiceLine)
        when(voucher.getRedeemedCourseCount()).thenReturn(0) // money voucher
        when(voucher.getValueOnPurchase()).thenReturn(new Money("100")) //voucher's amount
    }

    
    /**
     * Standard and the most common situation.
     * money voucher, price = amount.
     * only 1 voucher was used to pay. Voucher's amount equal to payment's amount.
     */
    @Test
    void standardVoucherPay() {
        Assert.assertEquals(new Money("100"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    
    /**
     * money voucher, price = amount = 100$.
     * voucher was used before.
     * Previous pay = 50$, current pay = 50$.
     * Voucher's amount equal to payment's amount.
     */
    @Test
    void voucherPaySeveralTimes() {
        PaymentIn previousPaymentIn = mock(PaymentIn.class)
        when(previousPaymentIn.getAmount()).thenReturn(new Money("50"))

        VoucherPaymentIn previousVoucherPaymentIn = mock(VoucherPaymentIn.class)
        when(previousVoucherPaymentIn.getPaymentIn()).thenReturn(previousPaymentIn)
        when(previousVoucherPaymentIn.getVoucher()).thenReturn(voucher)
        voucherPayments.add(0, previousVoucherPaymentIn)

        when(paymentInLine.getAmount()).thenReturn(new Money("50"))
        when(paymentIn.getAmount()).thenReturn(new Money("50"))

        Assert.assertEquals(new Money("50"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    /**
     * money voucher, price < amount.
     * voucher was used before. 
     * Previous pay = 50$, current pay = 50$. 
     * Voucher's amount 100$. 
     * Voucher's price 50$.
     */
    @Test
    void voucherPaySeveralTimesPriceLessThanAmount() {
        PaymentIn previousPaymentIn = mock(PaymentIn.class)
        when(previousPaymentIn.getAmount()).thenReturn(new Money("50"))

        VoucherPaymentIn previousVoucherPaymentIn = mock(VoucherPaymentIn.class)
        when(previousVoucherPaymentIn.getPaymentIn()).thenReturn(previousPaymentIn)
        when(previousVoucherPaymentIn.getVoucher()).thenReturn(voucher)
        voucherPayments.add(0, previousVoucherPaymentIn)

        when(paymentInLine.getAmount()).thenReturn(new Money("50"))
        when(paymentIn.getAmount()).thenReturn(new Money("50"))

        when(voucherProduct.getPriceExTax()).thenReturn(new Money("50"))
        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("50"))

        Assert.assertEquals(new Money("0"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    
    /**
     * money voucher, price = amount = 100$.
     * payment amount is more than voucher amount
     */
    @Test
    void voucherCoversPartOfPayment() {
        when(paymentInLine.getAmount()).thenReturn(new Money("200"))
        when(paymentIn.getAmount()).thenReturn(new Money("200"))

        Assert.assertEquals(new Money("100"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    
    /**
     * money voucher, price < amount, pay with money > price.
     *
     * paymentIn contains 1 paymentInLine with amount 100$
     * voucher's amount = 100$
     * voucher's real price = 10$
     *
     * Expected: Liability account's amount = 10$
     */
    @Test
    void voucherPayMoreThanItsPrice() {
        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("10"))

        Assert.assertEquals(new Money("10"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    
    /**
     * money voucher, price < amount, pay with money < price.
     * 
     * paymentIn contains 1 paymentInLine with amount 20$
     * voucher's amount = 100$
     * voucher's default price = 100$
     * voucher's real price = 50$
     * 
     * Expected: Liability account's amount = 20$
     */
    @Test
    void voucherPayLessThanItsPrice() {
        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("50"))
        when(paymentInLine.getAmount()).thenReturn(new Money("20"))
        when(paymentIn.getAmount()).thenReturn(new Money("20"))

        Assert.assertEquals(new Money("20"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }


    /**
     * money voucher, price differs from its real price when buy. Also pay with money > vouchers real price.
     *
     * paymentIn contains 1 paymentInLine with amount 100$
     * voucher's amount = 100$
     * voucher's default price = 100$
     * voucher's real price = 65$
     *
     * Expected: Liability account's amount = 65$
     */
    @Test
    void voucherPurchaseAmountDiffersFromDefaultAmountLessThanItsPrice() {
        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("65"))

        Assert.assertEquals(new Money("65"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    
    /**
     * enrolment voucher, price < enrolment's price
     * enrolment voucher real price = 75$
     * paymentIn for a class contains 1 paymentInLine with amount 100$
     */
    @Test
    void enrolmentVoucherPay() {
        when(voucherProduct.getMaxCoursesRedemption()).thenReturn(1)
        when(voucher.getRedeemedCourseCount()).thenReturn(0)
        when(invoiceLine.getPriceEachExTax()).thenReturn(new Money("75"))
        when(voucher.getValueOnPurchase()).thenReturn(new Money("75"))
        when(voucherProduct.getPriceExTax()).thenReturn(new Money("75"))


        Assert.assertEquals(new Money("75"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }

    /**
     * enrolment voucher, price > enrolment's price
     * enrolment voucher real price = 100$
     * paymentIn for a class contains 1 paymentInLine with amount 25$
     */
    @Test
    void enrolmentVoucherPayPriceHigherThanEnrolmentAmount() {
        when(voucherProduct.getMaxCoursesRedemption()).thenReturn(1)
        when(voucher.getRedeemedCourseCount()).thenReturn(0)

        when(paymentIn.getAmount()).thenReturn(new Money("25"))
        when(paymentInLine.getAmount()).thenReturn(new Money("25"))

        Assert.assertEquals(new Money("25"), GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get())
    }
}
