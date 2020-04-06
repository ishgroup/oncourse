package ish.oncourse.webservices.usi.tapestry;

import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.abr.akm.credential.store.ABRProperties;
import au.gov.abr.akm.exceptions.*;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.webservices.usi.crypto.UsiPrivateKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


public class UsiPrivateKeyBuilder implements ServiceBuilder<UsiPrivateKey> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public UsiPrivateKey buildService(ServiceResources resources) {
        String xmlCredentialPath = Configuration.getValue(null);
        String passwordPath = Configuration.getValue(null);

        СredentialStoreReader reader = СredentialStoreReader.valueOf(xmlCredentialPath, passwordPath);
        final PrivateKey privateKey;

        try {
            reader.read();
            privateKey = getPrivateKey(xmlCredentialPath, reader.getId(), reader.getPassword());
        } catch (Exception e) {
            logger.error("Cannot read usi keystore {} {}", xmlCredentialPath, passwordPath );
            logger.catching(e);
            return null;        }

        return new UsiPrivateKey() {

            @Override
            public String getAlgorithm() {
                return privateKey.getAlgorithm();
            }

            @Override
            public String getFormat() {
                return privateKey.getFormat();
            }

            @Override
            public byte[] getEncoded() {
                return privateKey.getEncoded();
            }
        };
    }

    private PrivateKey getPrivateKey(String xmlKeysorePath, String auskeyAlias, String auskeyPassword) throws FileNotFoundException, SDKExpiredException, KeyStoreLoadException, NullReferenceException, NoSuchAliasException, RenewalNotCalledException, IncorrectPasswordException, ABRUnhandledException {

        File keystorefile = new File(xmlKeysorePath).getAbsoluteFile();
        ABRProperties.setSoftwareInfo("ish pty ltd", "Ish onCourse", "v1.0", "20-10-2006");
        ABRKeyStore keyStore = new ABRKeyStore(new FileInputStream(keystorefile));
        ABRCredential abrCredential = keyStore.getCredential(auskeyAlias);
        if(abrCredential.isReadyForRenewal()) {
            abrCredential.renew(auskeyPassword.toCharArray());
        }
        return abrCredential.getPrivateKey(auskeyPassword.toCharArray());

    }
}
