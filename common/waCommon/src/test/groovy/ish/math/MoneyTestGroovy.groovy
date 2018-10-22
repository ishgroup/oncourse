package ish.math

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertTrue

class MoneyTestGroovy {

    private static final BigDecimal DEFAULT_MONEY_VALUE = 100.0

    @Test
    void testMoneyLocalization() {
        Money money = new Money(DEFAULT_MONEY_VALUE)
        money.setLocale(Country.EUROPE.locale())
        // 100,00 €
        assertEquals("100,00 " + Country.EUROPE.currencyShortSymbol(), money.toString())

        money.setLocale(Country.NORWAY.locale())
        // kr 100,00
        assertEquals(Country.NORWAY.currencyShortSymbol() + " 100,00", money.toString())

        money.setLocale(Country.ENGLAND.locale())
        // £100.00
        assertEquals(Country.ENGLAND.currencyShortSymbol() + "100.00" , money.toString())

        money.setLocale(Country.SOUTH_AFRICA.locale())
        // R 100.00
        assertEquals(Country.SOUTH_AFRICA.currencyShortSymbol() + " 100.00" , money.toString())

        money.setLocale(Country.AUSTRALIA.locale())
        // $100.00
        assertEquals(Country.AUSTRALIA.currencyShortSymbol() + "100.00" , money.toString())

        money.setLocale(Country.US.locale())
        // $100.00
        assertEquals(Country.US.currencyShortSymbol() + "100.00" , money.toString())
    }
    
    @Test
    void testMultiplyReturnType() {
        def defValue = 1.0
        assertEquals(Money, Money.ONE.multiply(defValue).class)

        long longValue = 1
        assertEquals(Money, Money.ONE.multiply(longValue).class)

        Long longClassValue = 1
        assertEquals(Money, Money.ONE.multiply(longClassValue).class)

        double doubleValue = 1.0
        assertEquals(Money, Money.ONE.multiply(doubleValue).class)

        Double doubleClassValue = 1.0
        assertEquals(Money, Money.ONE.multiply(doubleClassValue).class)

        BigDecimal bigDecimalValue = 1.0
        assertEquals(Money, Money.ONE.multiply(bigDecimalValue).class)

        Money moneyValue = new Money('1.0')
        assertEquals(Money, Money.ONE.multiply(moneyValue).class)
    }
    
    @Test
    void testRelationalOperators() {
        assertEquals(Money.ONE, Money.ONE)
        assertNotEquals(Money.ONE, Money.ZERO)
        assertNotEquals(Money.ZERO, Money.ONE)
        assertEquals(Money.ONE * 11.11, Money.ONE * 11.11)
        assertNotEquals(Money.ONE * 11.22, Money.ONE * 11.11)
        assertNotEquals(Money.ONE * 11.11, Money.ONE * 11.22)


        assertTrue(Money.ONE == Money.ONE)
        assertFalse(Money.ONE == Money.ZERO)
        assertFalse(Money.ZERO == Money.ONE)
        assertTrue(Money.ONE * 11.11 == Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.22 == Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.11 == Money.ONE * 11.22)


        assertFalse(Money.ONE != Money.ONE)
        assertTrue(Money.ONE != Money.ZERO)
        assertTrue(Money.ZERO != Money.ONE)
        assertFalse(Money.ONE * 11.11 != Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.22 != Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.11 != Money.ONE * 11.22)


        assertFalse(Money.ONE > Money.ONE)
        assertTrue(Money.ONE > Money.ZERO)
        assertFalse(Money.ZERO > Money.ONE)
        assertFalse(Money.ONE * 11.11 > Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.22 > Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.11 > Money.ONE * 11.22)


        assertTrue(Money.ONE >= Money.ONE)
        assertTrue(Money.ONE >= Money.ZERO)
        assertFalse(Money.ZERO >= Money.ONE)
        assertTrue(Money.ONE * 11.11 >= Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.22 >= Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.11 >= Money.ONE * 11.22)


        assertFalse(Money.ONE < Money.ONE)
        assertFalse(Money.ONE < Money.ZERO)
        assertTrue(Money.ZERO < Money.ONE)
        assertFalse(Money.ONE * 11.11 < Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.22 < Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.11 < Money.ONE * 11.22)


        assertTrue(Money.ONE <= Money.ONE)
        assertFalse(Money.ONE <= Money.ZERO)
        assertTrue(Money.ZERO <= Money.ONE)
        assertTrue(Money.ONE * 11.11 <= Money.ONE * 11.11)
        assertFalse(Money.ONE * 11.22 <= Money.ONE * 11.11)
        assertTrue(Money.ONE * 11.11 <= Money.ONE * 11.22)

        assertTrue(Money.ONE == 1)
        assertFalse(Money.ONE != 1)
        assertFalse(Money.ONE > 1)
        assertTrue(Money.ONE >= 1)
        assertFalse(Money.ONE < 1)
        assertTrue(Money.ONE <= 1)
        
        assertEquals(new Money('2.00'), Money.ONE + Money.ONE)
        assertEquals(Money.ONE.add(Money.ONE), Money.ONE + Money.ONE)
        
        assertEquals(Money.ZERO, Money.ONE - Money.ONE)
        assertEquals(Money.ONE.subtract(Money.ONE), Money.ONE - Money.ONE)
        
        assertEquals(new Money('5.5'), Money.ONE * 5.5)
        assertEquals(Money.ONE.multiply(5.5), Money.ONE * 5.5)
        
        assertEquals(new Money('0.5'), Money.ONE / 2)
        assertEquals(Money.ONE.divide(2), Money.ONE / 2)
    }
}
