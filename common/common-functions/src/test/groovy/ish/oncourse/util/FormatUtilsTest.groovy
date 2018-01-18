package ish.oncourse.util

import ish.math.Money
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.junit.Assert.assertEquals

@RunWith(Parameterized)
class FormatUtilsTest {
    
    private Money amount
    private String expectedResult
    private String expectedFResult
    
    @Parameterized.Parameters
    static Collection primeNumbers() {
        [
                [new Money('10'), '$10.00', '$10'],
                [new Money('10.40'), '$10.40', '$10.40'],
                [new Money('100'), '$100.00', '$100'],
                [new Money('100.60'), '$100.60', '$100.60'],
                [new Money('1000'), '$1,000.00', '$1,000'],
                [new Money('1000.60'), '$1,000.60', '$1,000.60']
        ]*.toArray()
    }
    
    FormatUtilsTest(Money amount, String expectedResult, String expectedFResult) {
        this.amount = amount
        this.expectedResult = expectedResult
        this.expectedFResult = expectedFResult
    }

    @Test
    void testMoneyFormat() {
        assertEquals(expectedResult, amount.toString())
        
        def formatter = FormatUtils.chooseMoneyFormat(amount)
        def result = formatter.format(amount)
        assertEquals(expectedFResult, result)
    }
}
