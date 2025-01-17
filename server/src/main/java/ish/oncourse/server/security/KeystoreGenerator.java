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

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * This class is responsible for providing a keystore with public/private key pairs and associated certificates which are need for establishing a client server
 * SSL connection.<BR>
 * The keytool does not allow to extract the private key, for this and other reasons we are not going to store the keys in db, but in keystore next to the
 * application.
 */
public final class KeystoreGenerator {

	private static final Logger logger = LogManager.getLogger();

	enum CertificateType {
		ROOT(0),
		Intermidiate(1),
		Issue(2);

		private int index;

		CertificateType(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static CertificateType indexOf(int index) {
			switch (index) {
				case 0:
					return ROOT;
				case 1:
					return Intermidiate;
				case 2:
					return Issue;
			}
			return null;
		}
	}

	/**
	 * Name of keystore
	 */
	static final String KEYSTORE = "onCourseSSL.pem";

	/**
	 * Alias for keystore entry
	 */
	private static final String ALIAS = "onCourseClientServer";

	/**
	 * Key pair generation algorithm
	 */
	private static final String KEY_ALGORITHM = "RSA";

	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

	private static final String BC_PROVIDER = "BC";

	/**
	 * X.500 Distinguished Name
	 */
	protected static final String ROOT_DNAME = "CN= ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU";

	protected static final String INTERMIDIATE_DNAME = "CN=intermidiate ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU";

	protected static final String ISSUE_DNAME = "CN=issue ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU";

	/**
	 * Password which is used to protect the integrity of the keystore.
	 */
	public static final String KEYSTORE_PASSWORD = "ish2008";

	/**
	 * Specify how many days the certificate is valid after creation
	 */
	protected static final int VALIDITY = 20 * 365; // about 20 years

	/**
	 * default keystore type, just specifying for future reference
	 */
	public static final String KEYSTORE_TYPE = "PKCS12";

	static {
		// Add the BouncyCastle Provider for certificate generator
		Security.addProvider(new BouncyCastleProvider());
	}

	private KeystoreGenerator() {}

    /**
	 * default and only method which should be used. Other methods in this package are only
	 *
	 * @return KeyStore used for client-server ssl encryption
	 */
	public static KeyStore getClientServerKeystore() throws Exception {
		return getClientServerKeystore(KEYSTORE, ALIAS, KEYSTORE_PASSWORD);
	}

	// ------------ methods below are not to be used, they are protected for unit test purposes.-------

	protected static KeyStore getClientServerKeystore(String path, String alias, String keystorePassword) throws Exception {
		if (!new File(path).exists()) {
			logger.debug("KeyStore file is not present, create new instance");
			// KeyStore file is not present, create new instance
			var ks = KeyStore.getInstance(KEYSTORE_TYPE, BC_PROVIDER);
			// initialise it
			logger.debug("initialise keyStore");
			ks.load(null, null);
			// add requried certificate
			generateIshClientServerCertificate(ks, alias, keystorePassword);

			// save on disk on .PEM format
			try (JcaPEMWriter pemWriter = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(path)))) {
				pemWriter.writeObject(ks.getKey(CertificateType.Issue + "-" + alias, keystorePassword.toCharArray()));

				Arrays.stream(ks.getCertificateChain(CertificateType.Issue + "-" + alias)).forEach(certificate ->
				{
					try {
						pemWriter.writeObject(certificate);
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException("Can not create SSL Certificate. Server shut down...");
					}
				});
			}
		}
		return loadKeystore(path, alias, keystorePassword);
	}

	/**
	 * loads a KeyStore from a given file
	 *
	 * @return KeyStore
	 * @throws Exception
	 */
	protected static KeyStore loadKeystore(String location, String alias, String password) throws Exception {

		var ks = KeyStore.getInstance(KEYSTORE_TYPE, BC_PROVIDER);
		ks.load(null, null);

		try (FileReader keyReader = new FileReader(location)) {
			PEMParser pemParser = new PEMParser(keyReader);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

			var privateKey = converter.getPrivateKey(((PEMKeyPair) pemParser.readObject()).getPrivateKeyInfo());

			try (FileInputStream fis = new FileInputStream(location)) {

				final Collection collection = new CertificateFactory().engineGenerateCertificates(fis);
				Certificate[] serverChain = (X509Certificate[]) collection.toArray(new X509Certificate[collection.size()]);
				ks.setEntry(alias, new KeyStore.PrivateKeyEntry(privateKey, serverChain),
						new KeyStore.PasswordProtection(password.toCharArray())
				);
				// store chain in KeyStore
				// CA root certificate is optional
				if(serverChain.length == 3) {
					ks.setEntry(CertificateType.ROOT + "-" + alias,
							new KeyStore.PrivateKeyEntry(privateKey, new Certificate[]{serverChain[2]}),
							new KeyStore.PasswordProtection(password.toCharArray())
					);
				}
				ks.setEntry(CertificateType.Intermidiate + "-" + alias,
						new KeyStore.PrivateKeyEntry(privateKey, new Certificate[]{serverChain[1]}),
						new KeyStore.PasswordProtection(password.toCharArray())
				);
				ks.setEntry(CertificateType.Issue + "-" + alias,
						new KeyStore.PrivateKeyEntry(privateKey, new Certificate[]{serverChain[0]}),
						new KeyStore.PasswordProtection(password.toCharArray())
				);
			}
		}
		return ks;
	}

	/**
	 * method specific to onCourse, generates the required certificate for SSL client-server connectivity.
	 *
	 * @param ks
	 * @param alias
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static boolean generateIshClientServerCertificate(KeyStore ks, String alias, String password) throws Exception {

		KeyPairGenerator gen = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
		gen.initialize(2048);

		// init common SSL certificate info

		// DNAME = "CN=ish onCourse OU=ish group O=ish group L=Sydney S=NSW C=AU"
		var rootSubjectName = new X500Name(ROOT_DNAME);
		var intermidiateSubjectName = new X500Name(INTERMIDIATE_DNAME);
		var issueSubjectName = new X500Name(ISSUE_DNAME);

		// set expiry dates
		var certificateValidFrom = new Date();
		var certificateValidTo = DateUtils.addDays(certificateValidFrom, VALIDITY);

		// generate public/private key pair for all certificate chain
		KeyPair rootKeyPair =  gen.generateKeyPair();
		KeyPair intermidiateKeyPair = gen.generateKeyPair();
		KeyPair issueKeyPair = gen.generateKeyPair();

		logger.debug("Create X509Certificate chain ...");
		var serverChain = new X509Certificate[3];
		serverChain[0] = generateRootCertificate(rootSubjectName, certificateValidFrom, certificateValidTo, rootKeyPair);
		serverChain[1] = generateChainCertificate(CertificateType.Intermidiate, rootSubjectName, intermidiateSubjectName,
						certificateValidFrom, certificateValidTo, intermidiateKeyPair, serverChain[0], rootKeyPair
		);
		serverChain[2] = generateChainCertificate(CertificateType.Issue, intermidiateSubjectName, issueSubjectName,
				certificateValidFrom, certificateValidTo, issueKeyPair, serverChain[1], intermidiateKeyPair
		);

		// verify created certificates chain
		serverChain[1].verify(serverChain[0].getPublicKey(), BC_PROVIDER);
		serverChain[2].verify(serverChain[1].getPublicKey(), BC_PROVIDER);

		// store chain in KeyStore
		ks.setEntry(CertificateType.ROOT + "-" + alias,
				new KeyStore.PrivateKeyEntry(issueKeyPair.getPrivate(), new Certificate[]{serverChain[0]}),
				new KeyStore.PasswordProtection(password.toCharArray())
		);
		ks.setEntry(CertificateType.Intermidiate + "-" + alias,
				new KeyStore.PrivateKeyEntry(issueKeyPair.getPrivate(), new Certificate[]{serverChain[1]}),
				new KeyStore.PasswordProtection(password.toCharArray())
		);
		ks.setEntry(CertificateType.Issue + "-" + alias,
				new KeyStore.PrivateKeyEntry(issueKeyPair.getPrivate(), new Certificate[]{serverChain[2]}),
				new KeyStore.PasswordProtection(password.toCharArray())
		);
		return true;
	}

	private static X509Certificate generateRootCertificate(X500Name serverSubjectName, Date validFrom, Date validTo, KeyPair keyPair) throws OperatorCreationException, CertificateException, CertIOException, NoSuchAlgorithmException {

		// serial number for all certificate in chain must be unique
		BigInteger serialNumber = new BigInteger(Long.toString(new SecureRandom().nextLong()));

		// create CA certificate
		ContentSigner contentSigner = initContentSinger(CertificateType.ROOT,keyPair);

		// init certificate builder
		// basicConstraints extension allow used certificate as root for next certificate in the SSL chain
		// also extension mark certificate as CA certificate.
		// subjectKeyIdentifier extension is mandatory. For root certificate must used its public key
		JcaX509ExtensionUtils x509ExtensionUtils = new JcaX509ExtensionUtils();
		X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
					serverSubjectName, serialNumber, validFrom, validTo, serverSubjectName, keyPair.getPublic())
				.addExtension(Extension.basicConstraints, true, new BasicConstraints(true))
				.addExtension(Extension.subjectKeyIdentifier, false,
						x509ExtensionUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

		return generateCertificate(certBuilder,contentSigner);
	}

	private static X509Certificate generateChainCertificate(CertificateType certificateType,
															X500Name rootSubjectName, X500Name issuedCertSubject,
															Date validFrom, Date validTo,
															KeyPair keyPair,
															X509Certificate rootCertificate,
															KeyPair rootKeyPair)
			throws OperatorCreationException, CertificateException, CertIOException, NoSuchAlgorithmException {

		// serial number for all certificate in chain must be unique
		BigInteger serialNumber = new BigInteger(Long.toString(new SecureRandom().nextLong()));

		// Generate a CSR (Certificate Signing Request) using its root certificate in chain
		PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, keyPair.getPublic());
		ContentSigner contentSigner = initContentSinger(certificateType, rootKeyPair);
		PKCS10CertificationRequest csr = p10Builder.build(contentSigner);

		// init cerificate builder
		JcaX509ExtensionUtils x509ExtensionUtils = new JcaX509ExtensionUtils();
		X509v3CertificateBuilder certBuilder  = new X509v3CertificateBuilder(
				rootSubjectName, serialNumber, validFrom, validTo, csr.getSubject(), csr.getSubjectPublicKeyInfo())
				.addExtension(Extension.basicConstraints, true, new BasicConstraints(certificateType == CertificateType.Intermidiate))
				.addExtension(Extension.authorityKeyIdentifier, false,
						x509ExtensionUtils.createAuthorityKeyIdentifier(rootCertificate))
				.addExtension(Extension.subjectKeyIdentifier, false,
						x509ExtensionUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));

		X509CertificateHolder certHolder = certBuilder.build(contentSigner);
		return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(certHolder);
	}

	private static ContentSigner initContentSinger(CertificateType certificateType, KeyPair keyPair) throws OperatorCreationException {
		switch (certificateType) {
			case ROOT:
				return new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER).build(keyPair.getPrivate());
			case Intermidiate:
			case Issue:
				JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);
				return csrBuilder.build(keyPair.getPrivate());
		}
		return null;
	}

	private static X509Certificate generateCertificate(X509v3CertificateBuilder certBuilder, ContentSigner contentSigner) throws CertificateException {
		X509CertificateHolder certHolder = certBuilder.build(contentSigner);
		return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(certHolder);
	}
}
