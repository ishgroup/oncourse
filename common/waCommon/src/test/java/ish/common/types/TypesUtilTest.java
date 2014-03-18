package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import org.junit.Test;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TypesUtilTest {

	@Test
	public void testMaps() {
		// check if maps are the same
		Map<String, MessageType> mapM = TypesUtil.getValuesAsMap(MessageType.class);

		Map<String, MessageType> mapM2 = new LinkedHashMap<>();
		for (MessageType t : MessageType.values()) {
			mapM2.put(t.getDisplayName(), t);
		}

		assertEquals(mapM, mapM2);

		// check if the values are the same
		Map<String, PaymentType> mapP = TypesUtil.getValuesAsMap(PaymentType.class);
		assertEquals(mapP.size(), PaymentType.values().length);

		for (String s : mapP.keySet()) {
			boolean valueFound = false;
			for (PaymentType t : PaymentType.values()) {
				valueFound = valueFound || t.equals(mapP.get(s));
			}
			assertTrue(valueFound);
		}
	}

	@Test
	public void testFaultingDatabaseValues() {
		// check matching value class
		MessageType mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue(), MessageType.class);
		assertEquals(MessageType.EMAIL, mt);

		// check string value class
		mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue() + "", MessageType.class);
		assertEquals(MessageType.EMAIL, mt);

		// check Long value class
		mt = TypesUtil.getEnumForDatabaseValue(MessageType.EMAIL.getDatabaseValue().longValue(), MessageType.class);
		assertEquals(MessageType.EMAIL, mt);

		// check BigInteger value class
		mt = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(MessageType.EMAIL.getDatabaseValue().longValue()), MessageType.class);
		assertEquals(MessageType.EMAIL, mt);

		// check non existing enum value
		mt = TypesUtil.getEnumForDatabaseValue(10000, MessageType.class);
		assertNull(mt);

		// check null value
		mt = TypesUtil.getEnumForDatabaseValue(null, MessageType.class);
		assertNull(mt);
	}

	@Test
	public void testFaultingDisplayValues() {
		MessageStatus ms = TypesUtil.getEnumForDisplayName(MessageStatus.QUEUED.getDisplayName(), MessageStatus.class);
		assertEquals(MessageStatus.QUEUED, ms);

		ms = TypesUtil.getEnumForDisplayName("invalid value", MessageStatus.class);
		assertNull(ms);

		ms = TypesUtil.getEnumForDisplayName(null, MessageStatus.class);
		assertNull(ms);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNonCompliantArgument1() {
		TypesUtil.getValuesAsMap(FakeEnum.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNonCompliantArgument2() {
		TypesUtil.getEnumForDatabaseValue("something", FakeEnum.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNonCompliantArgument3() {
		TypesUtil.getEnumForDisplayName("something", FakeEnum.class);
	}

	private class FakeEnum implements DisplayableExtendedEnumeration {

		public Object getDatabaseValue() {
			return null;
		}

		public String getDisplayName() {
			return null;
		}
	}
}
