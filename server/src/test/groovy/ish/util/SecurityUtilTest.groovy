package ish.util

import groovy.transform.CompileStatic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.util.regex.Pattern

import static ish.util.SecurityUtil.VOUCHER_CODE_LENGTH

@CompileStatic
class SecurityUtilTest {
    private static final String ILLEGAL_VOUCHER_CODE_CHARACTERS = "[^01LlOoIi]+"
    private static final Pattern VOUCHER_CODE_PATTERN = Pattern.compile(ILLEGAL_VOUCHER_CODE_CHARACTERS)
    private static final Logger logger = LogManager.getLogger()

    @Test
    void testDifferentPasswords() {
        Assertions.assertFalse(SecurityUtil.generateRandomPassword(10).equals(SecurityUtil.generateRandomPassword(10)), "Same password twice.")
    }

    @Test
    void testPasswordLength() {
        Assertions.assertTrue(SecurityUtil.generateRandomPassword(3).length() == 3, "Empty password")
        Assertions.assertTrue(SecurityUtil.generateRandomPassword(1).length() == 1, "Empty password")
        Assertions.assertTrue(SecurityUtil.generateRandomPassword(100).length() == 100, "Empty password")
    }

    @Test
    void testGenerateVoucherCode() {
        int repeatCount = 10000
        List<String> codes = new ArrayList<>(repeatCount)
        for (int i = 0; i < repeatCount; i++) {
            String voucherCode = SecurityUtil.generateVoucherCode()
            Assertions.assertNotNull(voucherCode, "Generated code should not be empty")
            Assertions.assertEquals(voucherCode.length(), VOUCHER_CODE_LENGTH, "Incorrect voucher code length")
            Assertions.assertTrue(VOUCHER_CODE_PATTERN.matcher(voucherCode).matches(), "Illegal characters in voucher code")
            Assertions.assertFalse(codes.contains(voucherCode), "Generated codes should not repeat")
            codes.add(voucherCode)
        }
    }

    @Test
    void testHashOfNullString() {
        String hash = SecurityUtil.hashByteArray(null)
        Assertions.assertTrue(hash == null)
    }

    @Test
    void testHashOfEmptyString() {
        String hash = SecurityUtil.hashByteArray("".getBytes())
        Assertions.assertTrue(hash != null)
        Assertions.assertTrue(hash.length() > 8)
        Assertions.assertTrue(hash.length() < 63)
    }

    @Test
    void testHashLength2() {
        String input = SecurityUtil.generateRandomPassword(1)

        for (int i = 0; i < 101; i++) {
            String hash = SecurityUtil.hashByteArray(input.getBytes())
            Assertions.assertTrue(hash != null)
            Assertions.assertTrue(hash.length() > 8)
            Assertions.assertTrue(hash.length() < 63)
            input = input + SecurityUtil.generateRandomPassword(10)
        }
    }

    static void main(String[] args) {
        FileWriter writer = null
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
