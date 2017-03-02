package ish.oncourse.util.tapestry;

import ish.math.Money;

import java.text.*;
import java.util.Date;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatPeriodISO;

/**
 * Format utils for Tapestry components classes
 * Created by pavel on 3/1/17.
 */
public class TapestryFormatUtils {

    /**
     * Returns duration between two dates in ISO-8601 format
     * @param startDate start date
     * @param endDate end date
     * @return string ISO-8601 representation
     * @throws IllegalArgumentException if startMillis is greater than endMillis
     */
    public String duration(Date startDate, Date endDate) {
        return formatPeriodISO(startDate.getTime(), endDate.getTime());
    }

    /**
     * Just String.format method wrapper
     * @param format format pattern
     * @param args array of arguments
     * @return formatted string
     */
    public String format(String format, Object... args) {
        return String.format(format, args);
    }

    /**
     * Format money by pattern
     * @param money money
     * @param pattern DecimalFormat pattern string
     * @return formatted value
     */
    public String formatMoney(Money money, String pattern) {
        NumberFormat format = new DecimalFormat(pattern);
        return format.format(money);
    }

    /**
     * Format date by pattern
     * @param date date
     * @param pattern SimpleDateFormat pattern
     * @return formatted value
     */
    public String formatDate(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * Truncate a string to the closest word boundary after a number of characters
     * @param src input string
     * @param length maximum number of characters
     * @return truncated string
     */
    public String truncate(String src, int length) {
        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(src);
        int first_after = bi.preceding(length + 1) ;
        return src.substring(0, first_after).trim();
    }
}
