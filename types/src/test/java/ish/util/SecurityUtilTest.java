package ish.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecurityUtilTest {

    @Test
    public void generateUSISoftwareId() {
        String id = SecurityUtil.generateUSISoftwareId();
        assertEquals(id.length(), 10);
        int checkSumm = Character.getNumericValue(id.charAt(9));
        int summ = 0;
        for (char it :id.substring(0,9).toCharArray()) {
            summ += Character.getNumericValue(it);
        }
        assertEquals(checkSumm, summ % 10);
    }
}
