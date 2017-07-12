package ish.oncourse.util.tapestry;

import ish.math.Money;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.awt.peer.SystemTrayPeer;
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

        assertEquals("P0Y0M1DT0H0M0.000S",
                formatUtils.duration(startDate, increasedByDay));

        assertEquals(StringUtils.EMPTY,
                formatUtils.duration(null, null));
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

        assertEquals("this is test coverage 15",
                formatUtils.format("this is %s %s %d", substr1, substr2, 15));

        assertEquals(StringUtils.EMPTY,
                formatUtils.format(null, null, null));

    }

    /**
     * String truncation for several length cases
     *
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

        assertEquals("test",
                formatUtils.truncate("test message blah blah blah", 8));

        //case when length is less than first word
        assertEquals(StringUtils.EMPTY,
                formatUtils.truncate("abracadabra", 5));

        //case for null input
        assertEquals(StringUtils.EMPTY,
                formatUtils.truncate(null, 5));

        //case when length is greater than string
        assertEquals("This class is available for all ages",
                formatUtils.truncate("This class is available for all ages", 200));

        //case when length is equals string
        assertEquals("This class is available for all ages",
                formatUtils.truncate("This class is available for all ages", 36));

        //case when length is less than string
        assertEquals("This class is available for all",
                formatUtils.truncate("This class is available for all ages", 35));
    }

    /**
     * Format date by SimpleDateFormat pattern
     *
     * @throws Exception exception
     */
    @Test
    public void formatDate() throws Exception {
        String pattern = "YYYY/DD/MM-HH:mm:ss";
        Date current = new Date(System.currentTimeMillis());

        assertEquals(new SimpleDateFormat(pattern).format(current),
                formatUtils.formatDate(current, pattern));

        assertEquals(StringUtils.EMPTY,
                formatUtils.formatDate(null, pattern));
    }

    /**
     * Format money by DecimalFormat pattern
     *
     * @throws Exception exception
     */
    @Test
    public void formatMoney() throws Exception {
        String pattern = "###,###.###";
        BigDecimal value = new BigDecimal(1000.99);

        Money money = new Money(value);

        assertEquals(new DecimalFormat(pattern).format(money.toBigDecimal()),
                formatUtils.formatMoney(money, pattern));

        assertEquals(StringUtils.EMPTY,
                formatUtils.formatMoney(null, pattern));
    }
}