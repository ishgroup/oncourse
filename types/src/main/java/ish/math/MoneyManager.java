/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.math;

import ish.math.context.MoneyContext;

import javax.money.CurrencyUnit;
import java.util.Objects;

/**
 * The {@code MoneyManager} class provides functionality to manage the configuration of the
 * {@link Money} type used within the application.
 * This class manages the settings for locale, currency, and other related configurations
 * that will be applied during operations involving the {@link Money} type.
 *
 * <p>
 * This class is thread-safe and uses a volatile variable to manage the system-wide
 * money context, ensuring that updates to the context are visible across threads.
 * </p>
 *
 * <p>
 * Users can set a custom {@link MoneyContext} for specific operations and check
 * whether a custom context is being used. If no custom context is set, the
 * system context is used by default.
 * </p>
 *
 * <p>
 * Using custom contexts is a specialized operation; in most cases, users can rely
 * on the default context mechanism and its synchronization with the application configs.
 * </p>
 *
 * <strong>Usage Example:</strong>
 * <pre>
 * MoneyManager.updateSystemContext(new MoneyContext(...));
 * MoneyContext currentContext = MoneyManager.getContext();
 * </pre>
 */

public class MoneyManager {

    /**
     * By default, system {@link MoneyContext} for the {@link Money} type.
     * <p>This context could not be used in a {@link Money} instance unless the user understands the consequences
     * and wishes to create instance with a custom Locale and Currency configuration.</p>
     */
    static volatile MoneyContext systemContext = MoneyAmountFactory.DEFAULT_MONEY_CONTEXT;

    /**
     * Custom MoneyContext set by the user.
     */
    private MoneyContext customContext;

    /**
     * Updates system {@code MoneyContext} configuration.
     *
     * @param systemContext the new system money context to set
     */
    public static void updateSystemContext(MoneyContext systemContext) {
        MoneyManager.systemContext = systemContext;
    }

    /**
     * Retrieves the {@link CurrencyUnit} of the system context.
     *
     * @return the currency unit of the system context
     */
    public static MoneyContext getSystemContext() {
        return systemContext;
    }

    /**
     * Sets a custom {@code MoneyContext} for this instance.
     *
     * @param systemContext the custom money context to set
     */
    public void setUpCustomContext(MoneyContext systemContext) {
        customContext = systemContext;
    }

    /**
     * Checks if a custom {@code MoneyContext} has been set for this {@link Money} instance.
     *
     * @return {@code true} if a custom context is set; {@code false} otherwise
     */
    public boolean isCustomized() {
        return Objects.nonNull(customContext);
    }

    /**
     * Retrieves the current {@code MoneyContext} in use for the {@link Money} instance.
     *
     * <p>
     * If a custom context is set, it returns that as the current context;
     * otherwise, it returns the default system context.
     * </p>
     *
     * @return the current {@code MoneyContext} for the {@link Money} instance
     */
    public MoneyContext getContext() {
        return Objects.requireNonNullElse(customContext, systemContext);
    }
}
