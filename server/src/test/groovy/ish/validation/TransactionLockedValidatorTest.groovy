package ish.validation

import ish.persistence.Preferences
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.Assert.assertEquals

/**
 * Created by anarut on 5/25/16.
 */
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
        assertEquals(1, result.size())
        assertEquals(TransactionLockedErrorCode.allDaysFinalised, result.get(prefName))

        Map<String, TransactionLockedErrorCode> result1 = TransactionLockedValidator.valueOf(yesterday, today).validate()
        assertEquals(1, result1.size())
        assertEquals(TransactionLockedErrorCode.allDaysFinalised, result1.get(prefName))

        Map<String, TransactionLockedErrorCode> result2 = TransactionLockedValidator.valueOf(pastDate, today).validate()
        assertEquals(1, result2.size())
        assertEquals(TransactionLockedErrorCode.todayOrInFuture, result2.get(prefName))

        Map<String, TransactionLockedErrorCode> result3 = TransactionLockedValidator.valueOf(pastDate, futureDate).validate()
        assertEquals(1, result3.size())
        assertEquals(TransactionLockedErrorCode.todayOrInFuture, result3.get(prefName))

        Map<String, TransactionLockedErrorCode> result4 = TransactionLockedValidator.valueOf(pastDate, pastDateMinus14).validate()
        assertEquals(1, result4.size())
        assertEquals(TransactionLockedErrorCode.beforeCurrentValue, result4.get(prefName))

        Map<String, TransactionLockedErrorCode> result5 = TransactionLockedValidator.valueOf(pastDate, yesterday).validate()
        assertEquals(0, result5.size())

        Map<String, TransactionLockedErrorCode> result6 = TransactionLockedValidator.valueOf(pastDateMinus14, pastDate).validate()
        assertEquals(0, result6.size())
    }
}
