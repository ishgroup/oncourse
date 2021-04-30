package ish.oncourse.server.print

import ish.math.Money
import net.sf.jasperreports.engine.util.DefaultFormatFactory
import org.junit.Assert
import org.junit.jupiter.api.Test

import java.text.NumberFormat

class AngelFormatFactoryTest {
    private String pattern = "\$#,###,##0.00;\$(-#,###,##0.00)"
    
    @Test
    void testFormatMoneyType(){
        NumberFormat defaultNumberFormat = DefaultFormatFactory.createFormatFactory().createNumberFormat(pattern, Locale.default)
        NumberFormat customNumberFormat = new AngelFormatFactory().createNumberFormat(pattern, Locale.default)
        
        String expected = "\$10,000.00"
        BigDecimal decimal = new BigDecimal("10000")
        Money money = new Money("10000")
        Assert.assertEquals(expected,  defaultNumberFormat.format(decimal))
        Assert.assertEquals(expected, customNumberFormat.format(decimal))
        Assert.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assert.assertEquals(expected, customNumberFormat.format(money))
        
        decimal = new BigDecimal("10000.000")
        money = new Money("10000.000")
        Assert.assertEquals(expected,  defaultNumberFormat.format(decimal))
        Assert.assertEquals(expected, customNumberFormat.format(decimal))
        Assert.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assert.assertEquals(expected, customNumberFormat.format(money))

        expected = "\$(-10,000.00)"
        decimal = new BigDecimal("-10000")
        money = new Money("-10000")
        Assert.assertEquals(expected,  defaultNumberFormat.format(decimal))
        Assert.assertEquals(expected, customNumberFormat.format(decimal))
        Assert.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assert.assertEquals(expected, customNumberFormat.format(money))
    }
}
