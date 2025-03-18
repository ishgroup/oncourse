/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import javax.money.Monetary;
import java.util.Locale;

public class LocaleUtil {

    public static boolean serverLocaleUnknown(Locale locale) {
        try {
            return Monetary.getCurrency(locale) == null;
        } catch (javax.money.MonetaryException ignored) {}
        return true;
    }

    public static Locale getDefaultLocale() {
        return !serverLocaleUnknown(Locale.getDefault()) ? Locale.getDefault() : Country.AUSTRALIA.locale();
    }
}
