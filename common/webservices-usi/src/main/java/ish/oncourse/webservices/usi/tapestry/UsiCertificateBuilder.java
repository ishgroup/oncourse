package ish.oncourse.webservices.usi.tapestry;


import ish.oncourse.webservices.usi.crypto.UsiCertificate;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;
import java.util.Set;

public class UsiCertificateBuilder implements ServiceBuilder<UsiCertificate> {
    @Override
    public UsiCertificate buildService(ServiceResources resources) {

//        String xmlCredentialPath = System.getProperty("credentialStore");
//        String passwordPath = System.getProperty("credentialStorePassword");
//
//        СredentialStoreReader reader = СredentialStoreReader.valueOf(xmlCredentialPath, passwordPath);
//        reader.read();
//
//        final X509Certificate certificate = getCertificate(xmlCredentialPath, reader.getId());
//
//        return new UsiCertificate() {
//            @Override
//            public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
//                certificate.checkValidity();
//            }
//
//            @Override
//            public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
//                certificate.checkValidity();
//            }
//
//            @Override
//            public int getVersion() {
//                return certificate.getVersion();
//            }
//
//            @Override
//            public BigInteger getSerialNumber() {
//                return certificate.getSerialNumber();
//            }
//
//            @Override
//            public Principal getIssuerDN() {
//                return certificate.getIssuerDN();
//            }
//
//            @Override
//            public Principal getSubjectDN() {
//                return certificate.getSubjectDN();
//            }
//
//            @Override
//            public Date getNotBefore() {
//                return certificate.getNotBefore();
//            }
//
//            @Override
//            public Date getNotAfter() {
//                return certificate.getNotAfter();
//            }
//
//            @Override
//            public byte[] getTBSCertificate() throws CertificateEncodingException {
//                return certificate.getTBSCertificate();
//            }
//
//            @Override
//            public byte[] getSignature() {
//                return certificate.getSignature();
//            }
//
//            @Override
//            public String getSigAlgName() {
//                return certificate.getSigAlgName();
//            }
//
//            @Override
//            public String getSigAlgOID() {
//                return certificate.getSigAlgOID();
//            }
//
//            @Override
//            public byte[] getSigAlgParams() {
//                return certificate.getSigAlgParams();
//            }
//
//            @Override
//            public boolean[] getIssuerUniqueID() {
//                return certificate.getIssuerUniqueID();
//            }
//
//            @Override
//            public boolean[] getSubjectUniqueID() {
//                return certificate.getSubjectUniqueID();
//            }
//
//            @Override
//            public boolean[] getKeyUsage() {
//                return certificate.getKeyUsage();
//            }
//
//            @Override
//            public int getBasicConstraints() {
//                return certificate.getBasicConstraints();
//            }
//
//            @Override
//            public byte[] getEncoded() throws CertificateEncodingException {
//                return certificate.getEncoded();
//            }
//
//            @Override
//            public void verify(PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
//                certificate.verify(publicKey);
//            }
//
//            @Override
//            public void verify(PublicKey publicKey, String s) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
//                certificate.verify(publicKey, s);
//            }
//
//            @Override
//            public String toString() {
//                return certificate.toString();
//            }
//
//            @Override
//            public PublicKey getPublicKey() {
//                return certificate.getPublicKey();
//            }
//
//            @Override
//            public boolean hasUnsupportedCriticalExtension() {
//                return certificate.hasUnsupportedCriticalExtension();
//            }
//
//            @Override
//            public Set<String> getCriticalExtensionOIDs() {
//                return certificate.getCriticalExtensionOIDs();
//            }
//
//            @Override
//            public Set<String> getNonCriticalExtensionOIDs() {
//                return certificate.getNonCriticalExtensionOIDs();
//            }
//
//            @Override
//            public byte[] getExtensionValue(String s) {
//                return certificate.getExtensionValue(s);
//            }
//        };
        return null;
    }

//    private X509Certificate getCertificate(String xmlCredentialPath, String auskeyAlias) {
//        try
//        {
//            File keystorefile = new File(xmlCredentialPath).getAbsoluteFile();
//
//            if (!keystorefile.exists()) {
//                throw new FileNotFoundException(keystorefile.getCanonicalPath());
//            }
//
//            ABRKeyStore keyStore = ABRKeyStore.getInstance(keystorefile);
//
//            ABRCredential abrCredential = keyStore.getCredential(auskeyAlias);
//            X509Certificate[] certificate = abrCredential.getX509CertificateChain();
//            return certificate[0];
//
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//            return null;
//        }
//    }

}
