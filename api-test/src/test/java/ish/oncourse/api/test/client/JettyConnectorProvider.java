/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.api.test.client;


import org.eclipse.jetty.client.HttpClient;
import org.glassfish.jersey.client.Initializable;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.jetty.connector.LocalizationMessages;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

public class JettyConnectorProvider  implements ConnectorProvider {

    @Override
    public Connector getConnector(Client client, Configuration runtimeConfig) {
        return new JettyHttp2Connector(client, runtimeConfig);
    }

    /**
     * Retrieve the underlying Jetty {@link org.eclipse.jetty.client.HttpClient} instance from
     * {@link org.glassfish.jersey.client.JerseyClient} or {@link org.glassfish.jersey.client.JerseyWebTarget}
     * configured to use {@code JettyConnectorProvider}.
     *
     * @param component {@code JerseyClient} or {@code JerseyWebTarget} instance that is configured to use
     *                  {@code JettyConnectorProvider}.
     * @return underlying Jetty {@code HttpClient} instance.
     *
     * @throws java.lang.IllegalArgumentException in case the {@code component} is neither {@code JerseyClient}
     *                                            nor {@code JerseyWebTarget} instance or in case the component
     *                                            is not configured to use a {@code JettyConnectorProvider}.
     * @since 2.8
     */
    public static HttpClient getHttpClient(Configurable<?> component) {
        if (!(component instanceof Initializable)) {
            throw new IllegalArgumentException(
                    LocalizationMessages.INVALID_CONFIGURABLE_COMPONENT_TYPE(component.getClass().getName()));
        }

        final Initializable<?> initializable = (Initializable<?>) component;
        Connector connector = initializable.getConfiguration().getConnector();
        if (connector == null) {
            initializable.preInitialize();
            connector = initializable.getConfiguration().getConnector();
        }

        if (connector instanceof JettyHttp2Connector) {
            return ((JettyHttp2Connector) connector).getHttpClient();
        }

        throw new IllegalArgumentException(LocalizationMessages.EXPECTED_CONNECTOR_PROVIDER_NOT_USED());
    }
}