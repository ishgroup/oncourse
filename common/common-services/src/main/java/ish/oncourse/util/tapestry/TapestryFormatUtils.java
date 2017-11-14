package ish.oncourse.util.tapestry;

import ish.math.Money;
import org.apache.commons.lang.StringUtils;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatPeriodISO;

/**
 * Format utils for Tapestry components classes
 * Created by pavel on 3/1/17.
 */
public class TapestryFormatUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Returns duration between two dates in ISO-8601 format, startDate must be less than endDate,
     * otherwise method returns empty string.<br/><br/>
     * <i>Examples:</i><br/><br/>
     * startDate -> Mon Mar 06 09:53:44 MSK 2017<br/>
     * endDate -> Tue Mar 07 09:53:44 MSK 2017<br/>
     * out -> P0Y0M1DT0H0M0.000S<br/><br/>
     * startDate -> Tue Mar 07 09:53:44 MSK 2017 2017<br/>
     * endDate -> Mon Mar 06 09:53:44 MSK<br/>
     * out ->
     *
     * @param startDate start date
     * @param endDate   end date
     * @return string ISO-8601 representation
     * @throws IllegalArgumentException if startMillis is greater than endMillis
     */
    public String duration(Date startDate, Date endDate) {
        String value = StringUtils.EMPTY;
        try {
            value = formatPeriodISO(startDate.getTime(), endDate.getTime());
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during format duration between dates (startDate: %s; endDate: %s).", startDate, endDate),
                    ex);
        }
        return value;
    }

    /**
     * Exception-safe wrapper for String.format() method, in exceptional case returns empty string.<br/><br/>
     * <i>Examples:</i><br/><br/>
     * pattern -> this is %s %s %d<br/>
     * arg0 -> test<br/>
     * arg1 -> coverage<br/>
     * arg2 -> 15<br/>
     * out -> this is test coverage 15<br/><br/>
     * pattern -> this is %s %s %m<br/>
     * arg0 -> test<br/>
     * arg1 -> coverage<br/>
     * arg2 -> 15<br/>
     * out ->
     *
     * @param format format pattern
     * @param args   array of arguments
     * @return formatted string
     */
    public String format(String format, Object... args) {
        String formatted = StringUtils.EMPTY;
        try {
            formatted = String.format(format, args);
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during string formatting (format: %s; args: %s).", format, Arrays.toString(args)),
                    ex);
        }
        return formatted;
    }

    /**
     * Exception-safe method for formatting Money objects to string according to DecimalFormat standards,
     * in case of exception returns empty string.<br/><br/>
     * <i>Example:</i><br/><br/>
     * money -> new Money(new BigDecimal(1000.99))<br/>
     * pattern -> ###,###.###<br/>
     * out -> 1,000.99
     *
     * @param money   money
     * @param pattern DecimalFormat pattern string
     * @return formatted value
     */
    public String formatMoney(Money money, String pattern) {
        String formatted = StringUtils.EMPTY;
        try {
            NumberFormat format = new DecimalFormat(pattern);
            formatted = format.format(money.toBigDecimal());
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during money formatting (money: %s; pattern: %s).", money, pattern),
                    ex);
        }
        return formatted;
    }

    /**
     * Exception-safe method for formatting Date objects to string according to SimpleDateFormat standards,
     * in case of exception returns empty string.<br/><br/>
     * <i>Example:</i><br/><br/>
     * date -> Mon Mar 06 10:18:45 MSK 2017<br/>
     * pattern -> YYYY/DD/MM-HH:mm:ss<br/>
     * out -> 2017/65/03-10:18:45
     *
     * @param date    date
     * @param pattern SimpleDateFormat pattern
     * @return formatted value
     */
    public String formatDate(Date date, String pattern) {
        String formatted = StringUtils.EMPTY;
        try {
            DateFormat format = new SimpleDateFormat(pattern);
            formatted = format.format(date);
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during date formatting (date: %s; pattern: %s).", date, pattern),
                    ex);
        }
        return formatted;
    }

    /**
     * Truruncate string by closest word for specified length, result cannot have length more than in parameter.
     * If closest word is first returns empty string, in exceptional case returns empty string too.
     * All spaces in the end of string are removes.<br/><br/>
     * <i>Examples:</i><br/><br/>
     * src -> test message blah blah blah<br/>
     * length -> 8<br/>
     * out -> test<br/><br/>
     * src -> abracadabra<br/>
     * length -> 5<br/>
     * out -><br/><br/>
     * src -> Lorem ipsum dolor sit amet,<br/>
     * length -> 27<br/>
     * out -> Lorem ipsum dolor sit amet,
     *
     * @param src    input string
     * @param length maximum number of characters
     * @return truncated string
     */
    public String truncate(String src, int length) {
        String truncated = StringUtils.EMPTY;
        try {
            BreakIterator bi = BreakIterator.getWordInstance();
            bi.setText(src);
            if (src.length() > length) {
                int first_after = bi.preceding(length + 1);
                truncated = src.substring(0, first_after).trim();
            } else {
                truncated = src;
            }
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during string truncation (string: %s; maxlength: %d).", src, length),
                    ex);
        }
        return truncated;
    }
}
