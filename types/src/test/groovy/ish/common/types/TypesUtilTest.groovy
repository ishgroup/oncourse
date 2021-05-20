package ish.common.types

import groovy.transform.CompileStatic
import ish.common.util.DisplayableExtendedEnumeration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class TypesUtilTest {

	@Test
    void testMaps() {
		// check if maps are the same
		Map<String, MessageType> mapM = TypesUtil.getValuesAsMap(MessageType.class)

        Map<String, MessageType> mapM2 = new LinkedHashMap<>()
        for (MessageType t : MessageType.values()) {
			mapM2.put(t.getDisplayName(), t)
        }

	Assertions.assertEquals(mapM, mapM2)

        // check if the values are the same
		Map<String, PaymentType> mapP = TypesUtil.getValuesAsMap(PaymentType.class)
        Assertions.assertEquals(mapP.size(), PaymentType.values().length)

        for (String s : mapP.keySet()) {
			boolean valueFound = false
            for (PaymentType t : PaymentType.values()) {
				valueFound = valueFound || t.equals(mapP.get(s))
            }
			Assertions.assertTrue(valueFound)
        }
	}

	@Test
    void testFaultingDatabaseValues() {
		// check matching value class
		MessageType mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue(), MessageType.class)
        Assertions.assertEquals(MessageType.EMAIL, mt)

        // check string value class
		mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue() + "", MessageType.class)
        Assertions.assertEquals(MessageType.EMAIL, mt)

        // check Long value class
		mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue().longValue(), MessageType.class)
        Assertions.assertEquals(MessageType.EMAIL, mt)

        // check BigInteger value class
		mt = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(MessageType.EMAIL.getDatabaseValue().longValue()), MessageType.class)
        Assertions.assertEquals(MessageType.EMAIL, mt)

        // check non existing enum value
		mt = TypesUtil.getEnumForDatabaseValue(10000, MessageType.class)
        Assertions.assertNull(mt)

        // check null value
		mt = TypesUtil.getEnumForDatabaseValue(null, MessageType.class)
        Assertions.assertNull(mt)
    }

	@Test
    void testFaultingDisplayValues() {
		MessageStatus ms = TypesUtil.getEnumForDisplayName(MessageStatus.QUEUED.getDisplayName(), MessageStatus.class)
        Assertions.assertEquals(MessageStatus.QUEUED, ms)

        ms = TypesUtil.getEnumForDisplayName("invalid value", MessageStatus.class)
        Assertions.assertNull(ms)

        ms = TypesUtil.getEnumForDisplayName(null, MessageStatus.class)
        Assertions.assertNull(ms)
    }

	@Test
    void testNonCompliantArgument1() {
		Assertions.assertThrows(IllegalArgumentException.class, { ->
			TypesUtil.getValuesAsMap(FakeEnum.class)
        })
    }

	@Test
    void testNonCompliantArgument2() {
		Assertions.assertThrows(IllegalArgumentException.class, { ->
			TypesUtil.getEnumForDatabaseValue("something", FakeEnum.class)
        })
    }

	@Test
    void testNonCompliantArgument3() {
		Assertions.assertThrows(IllegalArgumentException.class, { ->
			TypesUtil.getEnumForDisplayName("something", FakeEnum.class)
        })
    }

	private class FakeEnum implements DisplayableExtendedEnumeration {

		Object getDatabaseValue() {
			return null
        }

        String getDisplayName() {
			return null
        }
	}
}
