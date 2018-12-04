package ish.math;

import java.util.Locale;

/**
 * Interface for all currency formatters in the system.
 */
public interface CurrencyFormatter {

    /**
     * Formats money instance.
     * @param money for formatting.
     * @return formatted money.
     */
    String format(Money money);

    /**
     * Updates formatter locale.
     * @param locale which will be used.
     */
    void updateLocale(Locale locale);

    /**
     * Returns current locale which use formatter.
     * @return locale instance.
     */
    Locale getCurrentLocale();
}
