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

package ish.oncourse.server.modules;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.jetty.connector.ConnectorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;


@BQConfig
@JsonTypeName("https2")
public class AngelHttpsConnectorFactory extends ConnectorFactory {

    /**
     * Maximum idle time, equals to setting timeout. We set it to fairly large number because we have to be sure that it's always greater than the length of
     * payment session in willow application.
     *
     */
    static final int MAX_IDLE_TIME = 1000 * 60 * 40;
    static final long STS_MAX_AGE = 31536000;

    private static final Logger logger = LogManager.getLogger();

    protected ConnectionFactory[] buildHttpConnectionFactories(HttpConfiguration httpConfig) {
        return new ConnectionFactory[]{new HttpConnectionFactory(httpConfig)};
    }

    protected HttpConfiguration buildHttpConfiguration() {
        HttpConfiguration httpConfig = super.buildHttpConfiguration();
        httpConfig.setSendServerVersion(false);
        return httpConfig;
    }
}


