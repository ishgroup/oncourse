package ish.common.types;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;

public class PaymentStatusTest {

	@Test
	public void testNumberToEnum() {
		// test integer
		PaymentStatus ps = TypesUtil.getEnumForDatabaseValue(3, PaymentStatus.class);
		assertNotNull("", ps);

		// test bigint
		ps = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(3L), PaymentStatus.class);
		assertNotNull("", ps);

		// test long
		ps = TypesUtil.getEnumForDatabaseValue(3L, PaymentStatus.class);
		assertNotNull("", ps);

		// test string
		ps = TypesUtil.getEnumForDatabaseValue("3", PaymentStatus.class);
		assertNotNull("", ps);
	}
}
