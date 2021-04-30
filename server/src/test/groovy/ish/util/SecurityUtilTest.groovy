package ish.util


import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Test

import java.util.regex.Pattern

import static ish.util.SecurityUtil.VOUCHER_CODE_LENGTH
import static junit.framework.TestCase.*

class SecurityUtilTest  {
	private static final String ILLEGAL_VOUCHER_CODE_CHARACTERS = "[^01LlOoIi]+"
    private static final Pattern VOUCHER_CODE_PATTERN = Pattern.compile(ILLEGAL_VOUCHER_CODE_CHARACTERS)
    private static final Logger logger = LogManager.getLogger()

    @Test
    void testDifferentPasswords() {
		assertFalse("Same password twice.", SecurityUtil.generateRandomPassword(10).equals(SecurityUtil.generateRandomPassword(10)))
    }

	@Test
    void testPasswordLength() {
		assertTrue("Empty password", SecurityUtil.generateRandomPassword(3).length() == 3)
        assertTrue("Empty password", SecurityUtil.generateRandomPassword(1).length() == 1)
        assertTrue("Empty password", SecurityUtil.generateRandomPassword(100).length() == 100)
    }

	@Test
    void testGenerateVoucherCode() {
		int repeatCount = 10000
        List<String> codes = new ArrayList<>(repeatCount)
        for (int i = 0; i < repeatCount; i++) {
			String voucherCode = SecurityUtil.generateVoucherCode()
            assertNotNull("Generated code should not be empty", voucherCode)
            assertEquals("Incorrect voucher code length", voucherCode.length(), VOUCHER_CODE_LENGTH)
            assertTrue("Illegal characters in voucher code", VOUCHER_CODE_PATTERN.matcher(voucherCode).matches())
            assertFalse("Generated codes should not repeat", codes.contains(voucherCode))
            codes.add(voucherCode)
        }
	}

	@Test
    void testHashOfNullString() {
		String hash = SecurityUtil.hashByteArray(null)
        assertTrue(hash == null)
    }

	@Test
    void testHashOfEmptyString() {
		String hash = SecurityUtil.hashByteArray("".getBytes())
        assertTrue(hash != null)
        assertTrue(hash.length() > 8)
        assertTrue(hash.length() < 63)
    }

	@Test
    void testHashLength2() {
		String input = SecurityUtil.generateRandomPassword(1)

        for (int i=0;i<101;i++) {
			String hash = SecurityUtil.hashByteArray(input.getBytes())
            assertTrue(hash != null)
            assertTrue(hash.length() > 8)
            assertTrue(hash.length() < 63)
            input = input + SecurityUtil.generateRandomPassword(10)
        }
	}

	static void main(String[] args) {
		FileWriter writer=null
        try {
			File out = new File("build/randomPasswordTest.txt")

            writer = new FileWriter(out, true)
            String newRand = SecurityUtil.generateRandomPassword(16)
            writer.write(newRand)
            writer.write(RuntimeUtil.LINE_SEPARATOR)
        } catch (Exception e) {
            logger.catching(e)
        } finally {
            writer.close()
            Runtime.getRuntime().exit(0)
        }
	}

}
