package ish.math;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormat {

    private static final String SPACE_SYMBOL = "\\s";
    private static final String EMPTY_SYMBOL = "";
    private static final String MINUS_SYMBOL = "-";

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
        String value = NumberFormat.getCurrencyInstance(currentLocale)
                .format(money.toBigDecimal())
                .replaceAll(SPACE_SYMBOL, EMPTY_SYMBOL)
                .replaceAll(MINUS_SYMBOL, EMPTY_SYMBOL);
        if (money.isNegative()) {
            value = MINUS_SYMBOL + value;
        }
        return value;
    }
}
