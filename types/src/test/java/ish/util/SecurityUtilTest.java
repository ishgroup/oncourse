package ish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;



public class SecurityUtilTest {

    @Test
    public void generateUSISoftwareId() {
        String id = SecurityUtil.generateUSISoftwareId();
        Assertions.assertEquals(id.length(), 10);
        int checkSumm = Character.getNumericValue(id.charAt(9));
        int summ = 0;
        for (char it :id.substring(0,9).toCharArray()) {
            summ += Character.getNumericValue(it);
        }
        Assertions.assertEquals(checkSumm, summ % 10);
    }
}
