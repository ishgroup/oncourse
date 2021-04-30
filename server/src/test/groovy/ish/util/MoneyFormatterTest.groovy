/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.CayenneIshTestCase
import org.junit.jupiter.api.Test

import static junit.framework.TestCase.assertEquals

class MoneyFormatterTest extends CayenneIshTestCase {

	private static HashMap<String, String> list = new HashMap<>()

    static  {
        list.put('15', '$15.00')
        list.put('15.', '$15.00')
        list.put('15.5', '$15.50')
        list.put('15.05', '$15.05')
        list.put('15.50', '$15.50')
        list.put('.5', '$0.50')
        list.put('0.5', '$0.50')
        list.put('$15', '$15.00')
        list.put('$15.', '$15.00')
        list.put('$15.5', '$15.50')
        list.put('$15.05', '$15.05')
        list.put('$15.50', '$15.50')
        list.put('$.5', '$0.50')
        list.put('$0.5', '$0.50')
        list.put('-15', '-$15.00')
        list.put('-15.', '-$15.00')
        list.put('-15.5', '-$15.50')
        list.put('-15.05', '-$15.05')
        list.put('-15.50', '-$15.50')
        list.put('-.5', '-$0.50')
        list.put('-0.5', '-$0.50')
        list.put('-$15', '-$15.00')
        list.put('-$15.', '-$15.00')
        list.put('-$15.5', '-$15.50')
        list.put('-$15.05', '-$15.05')
        list.put('-$15.50', '-$15.50')
        list.put('-$.5', '-$0.50')
        list.put('-$0.5', '-$0.50')
    }

	@Test
    void testBoth() throws Exception {
        MoneyFormatter formatter = MoneyFormatter.getInstance()

        list.each { entry ->
            String correctResult = entry.getValue()
            String stringInput = entry.getKey()
            // System.out.println("testBoth input: " + stringInput);
            String result = formatter.valueToString(formatter.stringToValue(stringInput))
            // System.out.println("testBoth output: " + result);
            assertEquals(correctResult, correctResult, result)
        }
	}
}