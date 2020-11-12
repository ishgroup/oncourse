/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

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
