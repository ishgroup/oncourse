/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.modules;

import ish.oncourse.server.security.KeystoreGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.security.KeyStore;

import static ish.oncourse.server.modules.AngelHttpsConnectorFactory.MAX_IDLE_TIME;

public class SslContextHolder {

    private static SslContextFactory.Server sslContextFactory;

    private static final Logger logger = LogManager.getLogger();

    public static SslContextFactory.Server get() {
        if (sslContextFactory == null) {
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
            sslContextFactory.addExcludeProtocols("SSLv3", "SSLv2", "TLSv1", "TLSv1.1", "TLSv1.2");

            SslContextHolder.sslContextFactory = sslContextFactory;
        }
        return sslContextFactory;
    }
}
