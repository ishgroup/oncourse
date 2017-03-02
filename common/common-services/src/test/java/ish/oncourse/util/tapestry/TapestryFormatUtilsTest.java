package ish.oncourse.util.tapestry;

import ish.math.Money;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * TapestryFormatUtils test
 * Created by pavel on 3/1/17.
 */
public class TapestryFormatUtilsTest {

    private TapestryFormatUtils formatUtils = null;

    @Before
    public void initialize() {
        formatUtils = new TapestryFormatUtils();
    }

    /**
     * Shows ISO-8601 representation of duration between current datetime and increased this one by day
     *
     * @throws Exception exception
     */
    @Test
    public void duration() throws Exception {
        long current = System.currentTimeMillis();
        Date startDate = new Date(current);
        Date increasedByDay = new Date(current + 1000 * 60 * 60 * 24);

        assertEquals("P0Y0M1DT0H0M0.000S", formatUtils.duration(startDate, increasedByDay));
    }

    /**
     * Formatting by insertion strings and integer number by pattern
     *
     * @throws Exception exception
     */
    @Test
    public void format() throws Exception {
        String substr1 = "test";
        String substr2 = "coverage";
        assertEquals("this is test coverage 15", formatUtils.format("this is %s %s %d", substr1, substr2, 15));
    }

    /**
     * String truncation for several length cases
     * @throws Exception exception
     */
    @Test
    public void truncate() throws Exception {
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt"
                + " ut labore et dolore magna aliqua.";

        //case for middle of the word
        assertEquals("Lorem ipsum dolor sit amet,",
                formatUtils.truncate(loremIpsum, "Lorem ipsum dolor sit amet, consec".length()));

        //case for end of the word
        assertEquals("Lorem ipsum dolor sit amet, consectetur",
                formatUtils.truncate(loremIpsum, "Lorem ipsum dolor sit amet, consectetur".length()));

        //case for delimiter
        assertEquals("Lorem ipsum dolor sit amet,",
                formatUtils.truncate(loremIpsum, "Lorem ipsum dolor sit amet,".length()));

        //case for space character
        assertEquals("Lorem ipsum dolor sit",
                formatUtils.truncate(loremIpsum, "Lorem ipsum dolor sit ".length()));
    }

    /**
     * Format date by SimpleDateFormat pattern
     * @throws Exception exception
     */
    @Test
    public void formatDate() throws Exception {
        String pattern = "YYYY/DD/MM-HH:mm:ss";
        Date current = new Date(System.currentTimeMillis());

        assertEquals(new SimpleDateFormat(pattern).format(current),
                formatUtils.formatDate(current, pattern));
    }

    /**
     * Format money by DecimalFormat pattern
     * @throws Exception exception
     */
    @Test
    public void formatMoney() throws Exception {
        String pattern = "###,###.###";
        BigDecimal value = new BigDecimal(1000.99);

        Money money = new Money(value);
        assertEquals(new DecimalFormat(pattern).format(money.toBigDecimal()),
                formatUtils.formatMoney(money, ""));
    }
}