package ish.math;

import java.util.Locale;

public interface CurrencyFormatter {

    String format(Money money);

    void updateLocale(Locale locale);

    Locale getCurrentLocale();
}
