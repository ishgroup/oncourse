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
package ish.oncourse.server.security;

import com.google.inject.Inject;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.security.KeyStore;
import java.util.Date;

import static ish.oncourse.server.security.KeystoreGenerator.KEYSTORE;
import static ish.oncourse.server.security.KeystoreGenerator.KEYSTORE_PASSWORD;

@DisallowConcurrentExecution
public class CertificateUpdateWatcher implements Job {

    private Long lastUpdate = new Date().getTime();
    private SslContextFactory sslContextFactory;

    @Inject
    public CertificateUpdateWatcher(SslContextFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            var keyStore = new File(KEYSTORE);
            Long modified = keyStore.lastModified();
            if (modified > lastUpdate) {
                updateCertificate();
                lastUpdate = modified;
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    private void updateCertificate() throws Exception {
        var keyStore = KeystoreGenerator.getClientServerKeystore();
        sslContextFactory.reload(sslContextFactory -> {
            sslContextFactory.setKeyStorePassword(KEYSTORE_PASSWORD);
            sslContextFactory.setKeyStore(keyStore);
        });
    }

}
