package ish.math;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class DefaultCurrencyFormatter implements CurrencyFormatter {

    private static final String EMPTY_SYMBOL = "";
    private static final String MINUS_SYMBOL = "-";
    private static final String CLEAR_UP_REGEX = "[-\\(\\)\\s]";
    private static final char MONEY_DECIMAL_SEPARATOR = '.';
    private static final char MONEY_THOUSAND_SEPARATOR = ',';

    protected Locale currentLocale;
    private NumberFormat formatter;

    public DefaultCurrencyFormatter() {
        currentLocale = Locale.getDefault();
        formatter = initializeFormatter(currentLocale);
    }

    @Override
    public String format(Money money) {
        String value = formatter
                .format(money.toBigDecimal())
                .replaceAll(CLEAR_UP_REGEX, EMPTY_SYMBOL);
        if (money.isNegative()) {
            value = MINUS_SYMBOL + value;
        }
        return value;
    }

    @Override
    public void updateLocale(Locale locale) {
        currentLocale = locale;
        formatter = initializeFormatter(currentLocale);
    }

    @Override
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    private NumberFormat initializeFormatter(Locale locale) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        DecimalFormatSymbols formatSymbols = format.getDecimalFormatSymbols();
        formatSymbols.setMonetaryDecimalSeparator(MONEY_DECIMAL_SEPARATOR);
        formatSymbols.setGroupingSeparator(MONEY_THOUSAND_SEPARATOR);
        format.setDecimalFormatSymbols(formatSymbols);
        return format;
    }
}
