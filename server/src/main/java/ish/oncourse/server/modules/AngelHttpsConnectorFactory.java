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
import io.bootique.annotation.BQConfigProperty;
import io.bootique.jetty.connector.ConnectorFactory;
import io.bootique.resource.ResourceFactory;
import ish.oncourse.server.security.KeystoreGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.resource.URLResource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.net.URL;
import java.security.KeyStore;
import java.util.Objects;


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

    @Override
    protected ConnectionFactory[] buildHttpConnectionFactories(HttpConfiguration httpConfig) {
        var http2 = new HTTP2ServerConnectionFactory(httpConfig);
        var alpn = new ALPNServerConnectionFactory("h2");
        SslConnectionFactory ssl = new SslConnectionFactory(createSslContextFactory(), "alpn");
        return new ConnectionFactory[]{ssl, alpn, http2 };
    }


    SslContextFactory.Server createSslContextFactory()  {
        KeyStore keyStore;
        try {
            //read or create (first run) server key store
            keyStore = KeystoreGenerator.getClientServerKeystore();
        } catch (Exception e) {
            logger.catching(e);
            throw new RuntimeException("Can not get server key store");
        }

        // SSL Context Factory for HTTPS and HTTP/2
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStore(keyStore);
        sslContextFactory.setKeyStorePassword(KeystoreGenerator.KEYSTORE_PASSWORD);
        sslContextFactory.setSslSessionTimeout(MAX_IDLE_TIME);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);

        // SSL has security problems
        sslContextFactory.setProtocol("TLS");
        //Enforce higher levels of TLS. Only 1.3
        sslContextFactory.addExcludeProtocols("SSLv3", "SSLv2", "TLSv1","TLSv1.1","TLSv1.2");

        return sslContextFactory;
    }

    @Override
    public ServerConnector createConnector(Server server) {
        var connector = super.createConnector(server);
        connector.setIdleTimeout(MAX_IDLE_TIME);
        return connector;
    }

    @Override
    protected HttpConfiguration buildHttpConfiguration() {
        var config = super.buildHttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.addCustomizer(new SecureRequestCustomizer(false,false, STS_MAX_AGE, true));
        return config;
    }
}


