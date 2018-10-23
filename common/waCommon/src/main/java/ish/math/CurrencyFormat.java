package ish.math;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormat {

    private static Locale currentLocale = Locale.getDefault();

    private CurrencyFormat() {}

    public static void updateLocale(Locale locale) {
        currentLocale = locale;
    }

    /**
     * Get current money currency
     * @return current currency
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String formatMoney(Money money) {
        return NumberFormat.getCurrencyInstance(currentLocale).format(money.toBigDecimal());
    }
}
