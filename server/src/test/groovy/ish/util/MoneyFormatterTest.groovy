/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.math.Money
import org.junit.Test

class MoneyFormatterTest {

	private static HashMap<String, Money> listOne = new HashMap<>()
    private static HashMap<String, String> listTwo = new HashMap<>()

    void setUp() throws Exception {
        listOne.put('15', new Money('15'))
        listOne.put('15.5', new Money('15.5'))
        listOne.put('15.05', new Money('15.05'))
        listOne.put('15.50', new Money('15.5'))
        listOne.put('0.50', new Money('0.5'))
        listOne.put('$15', new Money('15'))
        listOne.put('$15.5', new Money('15.5'))
        listOne.put('$15.05', new Money('15.05'))
        listOne.put('$15.50', new Money('15.5'))
        listOne.put('$0.50', new Money('0.5'))
        listOne.put('-15', new Money('-15'))
        listOne.put('-15.5', new Money('-15.5'))
        listOne.put('-15.05', new Money('-15.05'))
        listOne.put('-15.50', new Money('-15.5'))
        listOne.put('-0.50', new Money('-0.5'))
        listOne.put('-$15', new Money('-15'))
        listOne.put('-$15.5', new Money('-15.5'))
        listOne.put('-$15.05', new Money('-15.05'))
        listOne.put('-$15.50', new Money('-15.5'))
        listOne.put('-$0.50', new Money('-0.5'))

        listTwo.put('15', '$15.00')
        listTwo.put('15.', '$15.00')
        listTwo.put('15.5', '$15.50')
        listTwo.put('15.05', '$15.05')
        listTwo.put('15.50', '$15.50')
        listTwo.put('.5', '$0.50')
        listTwo.put('$15', '$15.00')
        listTwo.put('$15.', '$15.00')
        listTwo.put('$15.5', '$15.50')
        listTwo.put('$15.05', '$15.05')
        listTwo.put('$15.50', '$15.50')
        listTwo.put('$.5', '$0.50')
        listTwo.put('-15', '-$15.00')
        listTwo.put('-15.', '-$15.00')
        listTwo.put('-15.5', '-$15.50')
        listTwo.put('-15.05', '-$15.05')
        listTwo.put('-15.50', '-$15.50')
        listTwo.put('-.5', '-$0.50')
        listTwo.put('-$15', '-$15.00')
        listTwo.put('-$15.', '-$15.00')
        listTwo.put('-$15.5', '-$15.50')
        listTwo.put('-$15.05', '-$15.05')
        listTwo.put('-$15.50', '-$15.50')
        listTwo.put('-$.5', '-$0.50')
    }

//	@Test
//    void testFormatMoney() throws Exception {
//		System.out.println("testFormatMoney")
//
//        for (String input : listOne.keySet()) {
//			// System.out.println("testFormatValue input: " + input);
//			Money output = (Money) MoneyFormatter.getInstance().stringToValue(input)
//            // System.out.println("testFormatValue output: " + output);
//			assertEquals(listOne.get(input).doubleValue(), output.doubleValue(), 0.001)
//        }
//	}

	@Test
    void testBoth() throws Exception {
		System.out.println("testBoth")
        Iterator<Map.Entry<String, String>> iter = listTwo.entrySet().iterator()
        MoneyFormatter formatter = MoneyFormatter.getInstance()
        while (iter.hasNext()) {
			Map.Entry<String, String> entry = iter.next()
            String correctResult = entry.getValue()
            String stringInput = entry.getKey()
            // System.out.println("testBoth input: " + stringInput);
			String result = formatter.valueToString(formatter.stringToValue(stringInput))
            // System.out.println("testBoth output: " + result);
			assertEquals(correctResult, correctResult, result)
        }
	}

//	@Test
//    void testBoth2() throws Exception {
//		System.out.println("testBoth2")
//        Iterator<Money> iter = listOne.values().iterator()
//        MoneyFormatter formatter = MoneyFormatter.getInstance()
//        while (iter.hasNext()) {
//			Money input = iter.next()
//            Money output = (Money) formatter.stringToValue(formatter.valueToString(input))
//
//            assertEquals(input.doubleValue(), output.doubleValue(), 0.001)
//        }
//	}
}