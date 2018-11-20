package ish.math;

import java.util.Locale;

public final class CurrencyFormat {

    private static CurrencyFormatter formatter = new DefaultCurrencyFormatter();

    private CurrencyFormat() {}

    public static void registerNewFormatter(CurrencyFormatter currencyFormatter) {
        formatter = currencyFormatter;
    }

    public static void updateLocale(Locale locale) {
        formatter.updateLocale(locale);
    }

    /**
     * Get current money currency
     * @return current currency
     */
    public static Locale getCurrentLocale() {
        return formatter.getCurrentLocale();
    }

    public static String formatMoney(Money money) {
        return formatter.format(money);
    }
}
