/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.api.test.client;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class NoValidationX509TrustManager implements X509TrustManager {


    private X509Certificate[] certificate = null;
    private boolean trust;

    public final static String EXCEPTION_MESSAGE = "Trust manager declined certificate";

    /**
     * default constructor, allows all clients and servers
     */
    public NoValidationX509TrustManager() {
        this(true);
    }

    public NoValidationX509TrustManager(boolean trust) {
        this.trust = trust;
    }

    /**
     * Checks if a certificate chain sent by the client is trusted. If not it throws an exception.
     *
     * @throws CertificateException
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (!trust) {
            throw new CertificateException(EXCEPTION_MESSAGE + " (client) " + chain);
        }
    }

    /**
     * Checks if a certificate chain sent by the server is trusted. If not it throws an exception.
     *
     * @throws CertificateException
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (!trust) {
            throw new CertificateException(EXCEPTION_MESSAGE + " (server) " + chain);
        }
        if (chain != null) {
            this.certificate = chain.clone();
        }
    }

    /**
     * Returns the list of trusted issuer certificates currently in use.
     *
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.certificate;
    }

}