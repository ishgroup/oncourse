package ish.math;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Observer, which notifies all money instances about currency changing
 */
public class CurrencyLocalizer {

    private static List<WeakReference<Money>> instances = new LinkedList<>();
    private static Locale currentLocale = Locale.getDefault();

    private CurrencyLocalizer() {}

    /**
     * Subscribe money isntance to currency changing event
     * @param instance money instance
     */
    public static void subscribe(Money instance) {
        WeakReference<Money> softReference = new WeakReference<>(instance);
        instance.setLocale(currentLocale);
        instances.add(softReference);
    }

    /**
     * Notify all subscribed money instances about currency changing
     * @param locale new locale
     */
    public static void updateLocale(Locale locale) {
        currentLocale = locale;

        instances.forEach(ref -> {
            Money money = ref.get();
            if (money != null) {
                money.setLocale(currentLocale);
            }
        });
    }

    /**
     * Get current money currency
     * @return current currency
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
