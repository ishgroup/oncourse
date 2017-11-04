package ish.math

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotEquals
import static org.junit.Assert.assertTrue

class MoneyTestGroovy {
    
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
    }
}
