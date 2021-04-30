package ish.common.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;

public class PaymentStatusTest {

	@Test
	public void testNumberToEnum() {
		// test integer
		PaymentStatus ps = TypesUtil.getEnumForDatabaseValue(3, PaymentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test bigint
		ps = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(3L), PaymentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test long
		ps = TypesUtil.getEnumForDatabaseValue(3L, PaymentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test string
		ps = TypesUtil.getEnumForDatabaseValue("3", PaymentStatus.class);
		Assertions.assertNotNull(ps, "");
	}
}
