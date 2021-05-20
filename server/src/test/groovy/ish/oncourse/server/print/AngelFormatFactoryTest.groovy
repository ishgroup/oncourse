package ish.oncourse.server.print


import groovy.transform.CompileStatic
import ish.math.Money
import net.sf.jasperreports.engine.util.DefaultFormatFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.text.NumberFormat

@CompileStatic
class AngelFormatFactoryTest {
    private String pattern = "\$#,###,##0.00;\$(-#,###,##0.00)"

    @Test
    void testFormatMoneyType() {
        NumberFormat defaultNumberFormat = new DefaultFormatFactory().createNumberFormat(pattern, Locale.default)
        NumberFormat customNumberFormat = new AngelFormatFactory().createNumberFormat(pattern, Locale.default)

        String expected = "\$10,000.00"
        BigDecimal decimal = new BigDecimal("10000")
        Money money = new Money("10000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))
        Assertions.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assertions.assertEquals(expected, customNumberFormat.format(money))

        decimal = new BigDecimal("10000.000")
        money = new Money("10000.000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))
        Assertions.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assertions.assertEquals(expected, customNumberFormat.format(money))

        expected = "\$(-10,000.00)"
        decimal = new BigDecimal("-10000")
        money = new Money("-10000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))
        Assertions.assertNotEquals(expected, defaultNumberFormat.format(money))
        Assertions.assertEquals(expected, customNumberFormat.format(money))
    }
}
