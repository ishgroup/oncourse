package ish.validation

import ish.persistence.Preferences
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.time.LocalDate

class TransactionLockedValidatorTest {


    @Test
    void testTransactionLockedValidation() {
        String prefName = Preferences.FINANCE_TRANSACTION_LOCKED
        LocalDate today = LocalDate.now()
        LocalDate yesterday = LocalDate.now().minusDays(1)
        LocalDate futureDate = LocalDate.now().plusDays(7)
        LocalDate pastDate = LocalDate.now().minusDays(7)
        LocalDate pastDateMinus14 = LocalDate.now().minusDays(14)

        Map<String, TransactionLockedErrorCode> result = TransactionLockedValidator.valueOf(yesterday, futureDate).validate()
        Assertions.assertEquals(1, result.size())
        Assertions.assertEquals(TransactionLockedErrorCode.allDaysFinalised, result.get(prefName))

        Map<String, TransactionLockedErrorCode> result1 = TransactionLockedValidator.valueOf(yesterday, today).validate()
        Assertions.assertEquals(1, result1.size())
        Assertions.assertEquals(TransactionLockedErrorCode.allDaysFinalised, result1.get(prefName))

        Map<String, TransactionLockedErrorCode> result2 = TransactionLockedValidator.valueOf(pastDate, today).validate()
        Assertions.assertEquals(1, result2.size())
        Assertions.assertEquals(TransactionLockedErrorCode.todayOrInFuture, result2.get(prefName))

        Map<String, TransactionLockedErrorCode> result3 = TransactionLockedValidator.valueOf(pastDate, futureDate).validate()
        Assertions.assertEquals(1, result3.size())
        Assertions.assertEquals(TransactionLockedErrorCode.todayOrInFuture, result3.get(prefName))

        Map<String, TransactionLockedErrorCode> result4 = TransactionLockedValidator.valueOf(pastDate, pastDateMinus14).validate()
        Assertions.assertEquals(1, result4.size())
        Assertions.assertEquals(TransactionLockedErrorCode.beforeCurrentValue, result4.get(prefName))

        Map<String, TransactionLockedErrorCode> result5 = TransactionLockedValidator.valueOf(pastDate, yesterday).validate()
        Assertions.assertEquals(0, result5.size())

        Map<String, TransactionLockedErrorCode> result6 = TransactionLockedValidator.valueOf(pastDateMinus14, pastDate).validate()
        Assertions.assertEquals(0, result6.size())
    }
}
