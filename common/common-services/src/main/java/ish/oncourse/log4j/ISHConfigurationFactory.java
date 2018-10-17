/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.log4j;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import java.io.File;
import java.net.URI;


/**
 * Default XmlConfigurationFactory overwritten to avoid an error message when custom log4j2 configuration file does not exist
 */
@Plugin(name = "XmlConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(5)
public class ISHConfigurationFactory extends XmlConfigurationFactory {
    public static final String[] SUFFIXES = new String[] {".xml"};

    @Override
    public String[] getSupportedTypes() {
        return SUFFIXES;
    }

    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        if (!isActive()) {
            return null;
        }
        if (configLocation != null) {
            if (! new File(configLocation).exists()) {
                LOGGER.info("Log4j2 configuration file {} is not found.", configLocation.toString());
                return null;
            }
            final ConfigurationSource source = ConfigurationSource.fromUri(configLocation);
            if (source != null) {
                return getConfiguration(loggerContext, source);
            }
        }
        return null;
    }


}
