/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import ish.math.Country
import ish.math.Money
import ish.math.MoneyManager
import ish.oncourse.server.money.MoneyContextProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
class MoneyFormatterTest {

    @BeforeAll
    static void setupEnvironment() {
        def context = new MoneyContextProvider()
        context.updateCountry(Country.AUSTRALIA)
        MoneyManager.updateSystemContext(context)
    }

    static Collection<Arguments> values() {
        def data = [
                [Money.of( 15), '$15.00'],
                [Money.of(15.0), '$15.00'],
                [Money.of(15.5), '$15.50'],
                [Money.of(15.05), '$15.05'],
                [Money.of(15.50), '$15.50'],
                [Money.of(0.5), '$0.50'],
                [Money.of(15), '$15.00'],
                [Money.of(15.0), '$15.00'],
                [Money.of(15.5), '$15.50'],
                [Money.of(15.05), '$15.05'],
                [Money.of(15.50), '$15.50'],
                [Money.of(-15), '-$15.00'],
                [Money.of(-15.0), '-$15.00'],
                [Money.of(-15.5), '-$15.50'],
                [Money.of(-15.05), '-$15.05'],
                [Money.of(-15.50), '-$15.50'],
                [Money.of(-0.5), '-$0.50'],
                [Money.of(-15), '-$15.00'],
                [Money.of(-15.0), '-$15.00'],
                [Money.of(-15.5), '-$15.50'],
                [Money.of(-15.05), '-$15.05'],
                [Money.of(-15.50), '-$15.50'],
                [Money.of(-0.5), '-$0.50']
        ]
        Collection<Arguments> dataList = new ArrayList<>()
        for (List test : data) {
            dataList.add(Arguments.of(test[0], test[1]))
        }
        return dataList
    }


    @ParameterizedTest
    @MethodSource("values")
    void testAustraliaFormat(Money input, String correctResult) throws Exception {
        Assertions.assertEquals(input.toString(), correctResult)
    }
}