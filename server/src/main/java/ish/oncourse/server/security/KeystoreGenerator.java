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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

/**
 * This class is responsible for providing a keystore with public/private key pairs and associated certificates which are need for establishing a client server
 * SSL connection.<BR>
 * The keytool does not allow to extract the private key, for this and other reasons we are not going to store the keys in db, but in keystore next to the
 * application.
 *
 */
public final class KeystoreGenerator {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Name of keystore
	 */
	static final String KEYSTORE = "onCourseSSL.keystore";

	/**
	 * Alias for keystore entry
	 */
	private static final String ALIAS = "onCourseClientServer";

	/**
	 * Key pair generation algorithm
	 */
	private static final String KEY_ALGORITHM = "RSA";

	/**
	 * Serial number
	 */
	private static final String SERIAL_NUMBER = "444455666";

	/**
	 * X.500 Distinguished Name
	 */
	protected static final String DNAME = "CN=ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU";

	/**
	 * Password which is used to protect the integrity of the keystore.
	 */
	public static final String KEYSTORE_PASSWORD = "ish2008";

	/**
	 * Password used to protect the private key of the generated key pair
	 */
	private static final String KEY_PASSWORD = "ish2008";

	/**
	 * Specify how many days the certificate is valid after creation
	 */
	protected static final int VALIDITY = 20 * 365; // about 20 years

	/**
	 * default keystore type, just specifying for future reference
	 */
	public static final String KEYSTORE_TYPE = "JKS";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private KeystoreGenerator() {}

    /**
	 * default and only method which should be used. Other methods in this package are only
	 *
	 * @return KeyStore used for client-server ssl encryption
	 * @throws Exception
	 */
	public static KeyStore getClientServerKeystore() throws Exception {
		return getClientServerKeystore(KEYSTORE, KEY_ALGORITHM, ALIAS, KEYSTORE_PASSWORD, KEY_PASSWORD);
	}

	// ------------ methods below are not to be used, they are protected for unit test purposes.-------

	protected static KeyStore getClientServerKeystore(String name, String algorithm, String alias, String keystorePassword, String keyPassword)
			throws Exception {
		if (!new File(name).exists()) {
			logger.debug("KeyStore file is not present, create new instance");
			// KeyStore file is not present, create new instance
			var ks = KeyStore.getInstance(KEYSTORE_TYPE);
			// initialise it
			logger.debug("initialise keyStore");
			ks.load(null, null);
			// add requried certificate
			generateIshClientServerCertificate(ks, algorithm, alias, keyPassword);
			// save on disk
			var fos = new FileOutputStream(name);
			try {
				logger.debug("store the keyStore to file {}", name);
				ks.store(fos, keystorePassword.toCharArray());
			} finally {
				logger.debug("KeyStore file exist, load from disk");
				IOUtils.closeQuietly(fos);
			}
		}
		// file exist, load from disk
		return loadKeystore(name, keystorePassword);
	}

	/**
	 * loads a KeyStore from a given file
	 *
	 * @return KeyStore
	 * @throws Exception
	 */
	protected static KeyStore loadKeystore(String location, String password) throws Exception {
		var ks = KeyStore.getInstance(KEYSTORE_TYPE);

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(location);
			ks.load(fis, password.toCharArray());
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return ks;
	}

	/**
	 * method specific to onCourse, generates the required certificate for SSL client-server connectivity.
	 *
	 * @param ks
	 * @param algorithm
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static boolean generateIshClientServerCertificate(KeyStore ks, String algorithm, String alias, String password) throws Exception {
		var gen = new RSAKeyPairGenerator();
		gen.init(new RSAKeyGenerationParameters(BigInteger.valueOf(3), new SecureRandom(), 1024, 80));

		var keypair = gen.generateKeyPair();
		var publicKey = (RSAKeyParameters) keypair.getPublic();
		var privateKey = (RSAPrivateCrtKeyParameters) keypair.getPrivate();

		// JCE format needed for the certificate - because getEncoded() is necessary...
		logger.debug("Generate RSAPublicKey ...");
		var pubKey = KeyFactory.getInstance(algorithm).generatePublic(new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getExponent()));
		// and this one for the KeyStore
		logger.debug("Generate private key ...");
		var privKey = KeyFactory.getInstance(algorithm).generatePrivate(
				new RSAPrivateCrtKeySpec(publicKey.getModulus(), publicKey.getExponent(), privateKey.getExponent(), privateKey.getP(), privateKey.getQ(),
						privateKey.getDP(), privateKey.getDQ(), privateKey.getQInv()));

		// DNAME = "CN=ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU"
		logger.debug("Create X509Certificate chain ...");
		var serverChain = new X509Certificate[1];
		var serverSubjectName = new X500Principal(DNAME);

		// set expiry dates
		var certificateValidFrom = new Date();
		var certificateValidTo = DateUtils.addDays(certificateValidFrom, VALIDITY);

		X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
				serverSubjectName, new BigInteger(SERIAL_NUMBER), certificateValidFrom, certificateValidTo, serverSubjectName, pubKey);

		X509ExtensionUtils x509ExtensionUtils = new JcaX509ExtensionUtils();
		var ski = x509ExtensionUtils.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(pubKey.getEncoded()));
		certBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);

		var contentSigner = new JcaContentSignerBuilder("MD5WithRSA").build(privKey);

		logger.debug("generate Certificate by org.bouncycastle.cert.X509v3CertificateBuilder ...");
		var certHolder = certBuilder.build(contentSigner);

		serverChain[0] = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

		logger.debug("add the Certificate to the key store ...");
		ks.setEntry(alias, new KeyStore.PrivateKeyEntry(privKey, serverChain), new KeyStore.PasswordProtection(password.toCharArray()));
		return true;
	}
}
