//file:noinspection ChangeToOperator
package ish.math

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class MoneyTestGroovy {

    @Test
    void testMultiplyReturnType() {
        def defValue = 1.0
        Assertions.assertEquals(Money, Money.ONE.multiply(defValue).class)

        long longValue = 1
        Assertions.assertEquals(Money, Money.ONE.multiply(longValue).class)

        Long longClassValue = 1
        Assertions.assertEquals(Money, Money.ONE.multiply(longClassValue).class)

        double doubleValue = 1.0
        Assertions.assertEquals(Money, Money.ONE.multiply(doubleValue).class)

        Double doubleClassValue = new Double(doubleValue)
        Assertions.assertEquals(Money, Money.ONE.multiply(doubleClassValue).class)

        BigDecimal bigDecimalValue = 1.0
        Assertions.assertEquals(Money, Money.ONE.multiply(bigDecimalValue).class)

        Money moneyValue = new Money('1.0')
        Assertions.assertEquals(Money, Money.ONE.multiply(moneyValue).class)
    }

    @Test
    void testRelationalOperators() {
        Assertions.assertEquals(Money.ONE, Money.ONE)
        Assertions.assertNotEquals(Money.ONE, Money.ZERO)
        Assertions.assertNotEquals(Money.ZERO, Money.ONE)
        Assertions.assertEquals(Money.ONE * 11.11, Money.ONE * 11.11)
        Assertions.assertNotEquals(Money.ONE * 11.22, Money.ONE * 11.11)
        Assertions.assertNotEquals(Money.ONE * 11.11, Money.ONE * 11.22)


        Assertions.assertTrue(Money.ONE == Money.ONE)
        Assertions.assertFalse(Money.ONE == Money.ZERO)
        Assertions.assertFalse(Money.ZERO == Money.ONE)
        Assertions.assertTrue(Money.ONE * 11.11 == Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.22 == Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.11 == Money.ONE * 11.22)


        Assertions.assertFalse(Money.ONE != Money.ONE)
        Assertions.assertTrue(Money.ONE != Money.ZERO)
        Assertions.assertTrue(Money.ZERO != Money.ONE)
        Assertions.assertFalse(Money.ONE * 11.11 != Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.22 != Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.11 != Money.ONE * 11.22)


        Assertions.assertFalse(Money.ONE > Money.ONE)
        Assertions.assertTrue(Money.ONE > Money.ZERO)
        Assertions.assertFalse(Money.ZERO > Money.ONE)
        Assertions.assertFalse(Money.ONE * 11.11 > Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.22 > Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.11 > Money.ONE * 11.22)


        Assertions.assertTrue(Money.ONE >= Money.ONE)
        Assertions.assertTrue(Money.ONE >= Money.ZERO)
        Assertions.assertFalse(Money.ZERO >= Money.ONE)
        Assertions.assertTrue(Money.ONE * 11.11 >= Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.22 >= Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.11 >= Money.ONE * 11.22)


        Assertions.assertFalse(Money.ONE < Money.ONE)
        Assertions.assertFalse(Money.ONE < Money.ZERO)
        Assertions.assertTrue(Money.ZERO < Money.ONE)
        Assertions.assertFalse(Money.ONE * 11.11 < Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.22 < Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.11 < Money.ONE * 11.22)


        Assertions.assertTrue(Money.ONE <= Money.ONE)
        Assertions.assertFalse(Money.ONE <= Money.ZERO)
        Assertions.assertTrue(Money.ZERO <= Money.ONE)
        Assertions.assertTrue(Money.ONE * 11.11 <= Money.ONE * 11.11)
        Assertions.assertFalse(Money.ONE * 11.22 <= Money.ONE * 11.11)
        Assertions.assertTrue(Money.ONE * 11.11 <= Money.ONE * 11.22)

        Assertions.assertTrue(Money.ONE == 1)
        Assertions.assertFalse(Money.ONE != 1)
        Assertions.assertFalse(Money.ONE > 1)
        Assertions.assertTrue(Money.ONE >= 1)
        Assertions.assertFalse(Money.ONE < 1)
        Assertions.assertTrue(Money.ONE <= 1)

        Assertions.assertEquals(new Money('2.00'), Money.ONE + Money.ONE)
        Assertions.assertEquals(Money.ONE.add(Money.ONE), Money.ONE + Money.ONE)

        Assertions.assertEquals(Money.ZERO, Money.ONE - Money.ONE)
        Assertions.assertEquals(Money.ONE.subtract(Money.ONE), Money.ONE - Money.ONE)

        Assertions.assertEquals(new Money('5.5'), Money.ONE * 5.5)
        Assertions.assertEquals(Money.ONE.multiply(5.5), Money.ONE * 5.5)

        Assertions.assertEquals(new Money('0.5'), Money.ONE / 2)
        Assertions.assertEquals(Money.ONE.divide(2.0), Money.ONE / 2)
    }
}
