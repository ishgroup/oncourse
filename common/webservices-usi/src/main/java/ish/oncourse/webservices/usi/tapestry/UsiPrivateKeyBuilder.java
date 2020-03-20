package ish.oncourse.webservices.usi.tapestry;

import ish.oncourse.webservices.usi.crypto.UsiPrivateKey;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import java.security.PrivateKey;


public class UsiPrivateKeyBuilder implements ServiceBuilder<UsiPrivateKey> {
    @Override
    public UsiPrivateKey buildService(ServiceResources resources) {

//        String xmlCredentialPath = System.getProperty("credentialStore");
//        String passwordPath = System.getProperty("credentialStorePassword");
//
//        СredentialStoreReader reader = СredentialStoreReader.valueOf(xmlCredentialPath, passwordPath);
//        reader.read();
//
//        final PrivateKey privateKey = getPrivateKey(xmlCredentialPath, reader.getId(), reader.getPassword());
//
//        return new UsiPrivateKey() {
//
//            @Override
//            public String getAlgorithm() {
//                return privateKey.getAlgorithm();
//            }
//
//            @Override
//            public String getFormat() {
//                return privateKey.getFormat();
//            }
//
//            @Override
//            public byte[] getEncoded() {
//                return privateKey.getEncoded();
//            }
//        };
        return null;
    }

//    private PrivateKey getPrivateKey(String xmlKeysorePath, String auskeyAlias, String auskeyPassword) {
//        try
//        {
//            File keystorefile = new File(xmlKeystorePath).getAbsoluteFile();
//
//            if (!keystorefile.exists()) {
//                throw new FileNotFoundException(keystorefile.getCanonicalPath());
//            }
//
//            ABRKeyStore keyStore = ABRKeyStore.getInstance(keystorefile);
//
//            return keyStore.getPrivateKey(auskeyAlias, auskeyPassword.toCharArray());
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//            return null;
//        }
//    }
}
