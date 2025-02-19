package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.math.Country
import ish.math.Money
import ish.math.MoneyManager
import ish.oncourse.server.money.MoneyContextProvider
import net.sf.jasperreports.engine.util.DefaultFormatFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.text.NumberFormat

@CompileStatic
class AngelFormatFactoryTest {

    private String pattern = "\$#,###,##0.00;\$(-#,###,##0.00)"

    @BeforeAll
    static void setupEnvironment() {
        def context = new MoneyContextProvider()
        context.updateCountry(Country.AUSTRALIA)
        MoneyManager.updateSystemContext(context)
    }

    @Test
    void testFormatMoneyType() {
        NumberFormat defaultNumberFormat = new DefaultFormatFactory().createNumberFormat(pattern, Money.ZERO.currencyContext.locale)
        NumberFormat customNumberFormat = new AngelFormatFactory().createNumberFormat(pattern, Money.ZERO.currencyContext.locale)

        String expected = "\$10,000.00"
        BigDecimal decimal = new BigDecimal("10000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))

        decimal = new BigDecimal("10000.000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))

        expected = "\$(-10,000.00)"
        decimal = new BigDecimal("-10000")
        Assertions.assertEquals(expected, defaultNumberFormat.format(decimal))
        Assertions.assertEquals(expected, customNumberFormat.format(decimal))
    }
}
