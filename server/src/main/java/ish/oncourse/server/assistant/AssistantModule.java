/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.assistant;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import ish.oncourse.server.assistant.model.AssistantModelProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the onCourse Assistant's AI model and limits from the {@code assistant:} section of onCourse.yml
 * and clears conversations on logout and when the assistant is turned off for their users.
 */
public class AssistantModule extends ConfigModule {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void configure(Binder binder) {
        // listens for logouts from the start, so conversations never outlive the session
        binder.bind(AssistantLogoutListener.class).asEagerSingleton();
        binder.bind(AssistantSettingsListener.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    public AssistantConfig createConfig(ConfigurationFactory configFactory) {
        try {
            return configFactory.config(AssistantConfig.class, getConfigPrefix());
        } catch (RuntimeException e) {
            // a broken assistant section must not stop the server
            logger.error("Cannot read the onCourse Assistant configuration, the assistant has no model", e);
            return new AssistantConfig();
        }
    }

    @Singleton
    @Provides
    public AssistantModelProvider createModelProvider(AssistantConfig config) {
        return config.createProvider();
    }

    @Singleton
    @Provides
    public AssistantRateLimiter createRateLimiter(AssistantConfig config) {
        return new AssistantRateLimiter(config.getMaxMessagesPerMinute());
    }
}
