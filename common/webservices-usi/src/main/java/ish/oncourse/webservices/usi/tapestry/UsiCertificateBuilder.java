package ish.oncourse.webservices.usi.tapestry;


import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.abr.akm.credential.store.ABRProperties;
import au.gov.abr.akm.exceptions.*;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.webservices.usi.crypto.UsiCertificate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;
import java.util.Set;

import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE;
import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE_PASSWORD;

public class UsiCertificateBuilder implements ServiceBuilder<UsiCertificate> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public UsiCertificate buildService(ServiceResources resources) {

        String xmlCredentialPath = Configuration.getValue(CREDENTIAL_STORE);
        String passwordPath = Configuration.getValue(CREDENTIAL_STORE_PASSWORD);

        СredentialStoreReader reader = СredentialStoreReader.valueOf(xmlCredentialPath, passwordPath);
        final X509Certificate certificate;
        try {
            reader.read();
            certificate = getCertificate(xmlCredentialPath, reader.getId());
        } catch (Exception e) {
            logger.error("Cannot read usi keystore {} {}", xmlCredentialPath, passwordPath );
            logger.catching(e);
            return null;
        }

        return new UsiCertificate() {
            @Override
            public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
                certificate.checkValidity();
            }

            @Override
            public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
                certificate.checkValidity();
            }

            @Override
            public int getVersion() {
                return certificate.getVersion();
            }

            @Override
            public BigInteger getSerialNumber() {
                return certificate.getSerialNumber();
            }

            @Override
            public Principal getIssuerDN() {
                return certificate.getIssuerDN();
            }

            @Override
            public Principal getSubjectDN() {
                return certificate.getSubjectDN();
            }

            @Override
            public Date getNotBefore() {
                return certificate.getNotBefore();
            }

            @Override
            public Date getNotAfter() {
                return certificate.getNotAfter();
            }

            @Override
            public byte[] getTBSCertificate() throws CertificateEncodingException {
                return certificate.getTBSCertificate();
            }

            @Override
            public byte[] getSignature() {
                return certificate.getSignature();
            }

            @Override
            public String getSigAlgName() {
                return certificate.getSigAlgName();
            }

            @Override
            public String getSigAlgOID() {
                return certificate.getSigAlgOID();
            }

            @Override
            public byte[] getSigAlgParams() {
                return certificate.getSigAlgParams();
            }

            @Override
            public boolean[] getIssuerUniqueID() {
                return certificate.getIssuerUniqueID();
            }

            @Override
            public boolean[] getSubjectUniqueID() {
                return certificate.getSubjectUniqueID();
            }

            @Override
            public boolean[] getKeyUsage() {
                return certificate.getKeyUsage();
            }

            @Override
            public int getBasicConstraints() {
                return certificate.getBasicConstraints();
            }

            @Override
            public byte[] getEncoded() throws CertificateEncodingException {
                return certificate.getEncoded();
            }

            @Override
            public void verify(PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
                certificate.verify(publicKey);
            }

            @Override
            public void verify(PublicKey publicKey, String s) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
                certificate.verify(publicKey, s);
            }

            @Override
            public String toString() {
                return certificate.toString();
            }

            @Override
            public PublicKey getPublicKey() {
                return certificate.getPublicKey();
            }

            @Override
            public boolean hasUnsupportedCriticalExtension() {
                return certificate.hasUnsupportedCriticalExtension();
            }

            @Override
            public Set<String> getCriticalExtensionOIDs() {
                return certificate.getCriticalExtensionOIDs();
            }

            @Override
            public Set<String> getNonCriticalExtensionOIDs() {
                return certificate.getNonCriticalExtensionOIDs();
            }

            @Override
            public byte[] getExtensionValue(String s) {
                return certificate.getExtensionValue(s);
            }
        };
    }

    private X509Certificate getCertificate(String xmlCredentialPath, String auskeyAlias) throws IOException, SDKExpiredException, KeyStoreLoadException, NullReferenceException, CertificateChainException, InvalidP7CException, NoSuchAliasException {

        File keystorefile = new File(xmlCredentialPath);

        if (!keystorefile.exists()) {
            throw new FileNotFoundException(keystorefile.getCanonicalPath());
        }
        ABRProperties.setSoftwareInfo("ish pty ltd", "Ish onCourse", "v1.0", "20-10-2006");

        ABRKeyStore keyStore = new ABRKeyStore(new FileInputStream(keystorefile));

        ABRCredential abrCredential = keyStore.getCredential(auskeyAlias);
        X509Certificate[] certificate = abrCredential.getCertificateChain();
        return certificate[0];

    }
}
