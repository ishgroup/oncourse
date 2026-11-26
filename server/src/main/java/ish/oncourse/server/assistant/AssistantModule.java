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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import ish.oncourse.server.assistant.model.AssistantModelProvider;
import ish.oncourse.server.assistant.model.UnconfiguredModelProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the onCourse Assistant's AI model from the {@code assistant:} section of onCourse.yml.
 */
public class AssistantModule extends ConfigModule {

    private static final Logger logger = LogManager.getLogger();

    @Singleton
    @Provides
    public AssistantModelProvider createModelProvider(ConfigurationFactory configFactory) {
        AssistantConfig config;
        try {
            config = configFactory.config(AssistantConfig.class, getConfigPrefix());
        } catch (RuntimeException e) {
            // a broken assistant section must not stop the server
            logger.error("Cannot read the onCourse Assistant configuration, the assistant has no model", e);
            return new UnconfiguredModelProvider();
        }
        return config.createProvider();
    }
}
