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
     * Returns duration between two dates in ISO-8601 format
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
     * Just String.format method wrapper
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
     * Format money by pattern
     *
     * @param money   money
     * @param pattern DecimalFormat pattern string
     * @return formatted value
     */
    public String formatMoney(Money money, String pattern) {
        String formatted = StringUtils.EMPTY;
        try {
            NumberFormat format = new DecimalFormat(pattern);
            formatted = format.format(money);
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during money formatting (money: %s; pattern: %s).", money, pattern),
                    ex);
        }
        return formatted;
    }

    /**
     * Format date by pattern
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
     * Truncate a string to the closest word boundary after a number of characters
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
            int first_after = bi.preceding(length + 1);
            truncated = src.substring(0, first_after).trim();
        } catch (Exception ex) {
            logger.error(
                    String.format("in Tapestry component during string truncation (string: %s; maxlength: %d).", src, length),
                    ex);
        }
        return truncated;
    }
}
