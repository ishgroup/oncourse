/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@CompileStatic
class MoneyFormatterTest {

    MoneyFormatter formatter = MoneyFormatter.defaultInstance()

    static Collection<Arguments> values() {
        def data = [
                ['15', '$15.00'],
                ['15.', '$15.00'],
                ['15.5', '$15.50'],
                ['15.05', '$15.05'],
                ['15.50', '$15.50'],
                ['.5', '$0.50'],
                ['0.5', '$0.50'],
                ['$15', '$15.00'],
                ['$15.', '$15.00'],
                ['$15.5', '$15.50'],
                ['$15.05', '$15.05'],
                ['$15.50', '$15.50'],
                ['$.5', '$0.50'],
                ['$0.5', '$0.50'],
                ['-15', '-$15.00'],
                ['-15.', '-$15.00'],
                ['-15.5', '-$15.50'],
                ['-15.05', '-$15.05'],
                ['-15.50', '-$15.50'],
                ['-.5', '-$0.50'],
                ['-0.5', '-$0.50'],
                ['-$15', '-$15.00'],
                ['-$15.', '-$15.00'],
                ['-$15.5', '-$15.50'],
                ['-$15.05', '-$15.05'],
                ['-$15.50', '-$15.50'],
                ['-$.5', '-$0.50'],
                ['-$0.5', '-$0.50']
        ]
        Collection<Arguments> dataList = new ArrayList<>()
        for (List test : data) {
            dataList.add(Arguments.of(test[0], test[1]))
        }
        return dataList
    }


    @ParameterizedTest
    @MethodSource("values")
    void testBoth(String stringInput, String correctResult) throws Exception {
        String result = formatter.valueToString(formatter.stringToValue(stringInput))
        Assertions.assertEquals(correctResult, result)
    }
}