package ish.common.types;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;

public class EnrolmentStatusTest {

	@Test
	public void testNumberToEnum() {
		// test integer
		EnrolmentStatus ps = TypesUtil.getEnumForDatabaseValue(3, EnrolmentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test bigint
		ps = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(3L), EnrolmentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test long
		ps = TypesUtil.getEnumForDatabaseValue(3L, EnrolmentStatus.class);
		Assertions.assertNotNull(ps, "");

		// test string
		ps = TypesUtil.getEnumForDatabaseValue("3", EnrolmentStatus.class);
		Assertions.assertNotNull(ps, "");
	}
}
