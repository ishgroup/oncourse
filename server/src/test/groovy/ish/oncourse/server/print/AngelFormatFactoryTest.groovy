package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.math.Country
import ish.math.Money
import ish.math.MoneyContextFactory
import ish.math.MoneyManager
import ish.math.format.MoneyFormatter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class AngelFormatFactoryTest {

    @Test
    void testAustraliaFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.AUSTRALIA))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "\$10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-\$10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testEnglandFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.ENGLAND))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "£10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-£10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testEuropeFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.EUROPE))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "10,000.00€"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-10,000.00€"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testUSFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.US))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "\$10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-\$10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testHongKongFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.HONG_KONG))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "\$10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-\$10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testSwitzerlandFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.SWITZERLAND))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "SFr.10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-SFr.10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testNorwayFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.NORWAY))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "kr10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-kr10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

    @Test
    void testSouthAfricaFormatMoneyType() {
        MoneyManager.updateSystemContext(MoneyContextFactory.create(Country.SOUTH_AFRICA))
        MoneyFormatter formatter = MoneyManager.systemContext.formatter

        String expected = "R10,000.00"
        Money money = Money.of("10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        money = Money.of("10000.000")
        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))

        expected = "-R10,000.00"
        money = Money.of("-10000")

        Assertions.assertEquals(expected, money.toString())
        Assertions.assertEquals(expected, formatter.format(money))
    }

}
