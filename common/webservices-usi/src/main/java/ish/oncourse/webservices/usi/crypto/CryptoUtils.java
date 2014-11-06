package ish.oncourse.webservices.usi.crypto;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


public final class CryptoUtils {
	
	private static final int STOREKEY_ITERATION_COUNT = 1024;
	private static final String CERTIFICATE_TYPE = "X.509";
	private static final String ENCRYPTION_ALGORITHM_NAME = "RSA";
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static PrivateKey decryptPrivateKey(byte[] encryptedKey, char[] password, byte[] salt) 
			throws IOException, InvalidCipherTextException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		PaddedBufferedBlockCipher cipher = getEncryptionDecryptionCipher(false, password, salt, STOREKEY_ITERATION_COUNT);
		byte[] keyData = getEncryptedData(encryptedKey);
		byte[] keyCipher = new byte[cipher.getOutputSize(keyData.length)];
		int cipherLength = cipher.processBytes(keyData, 0, keyData.length, keyCipher, 0);
		cipherLength += cipher.doFinal(keyCipher, cipherLength);
		byte[] cipherCopy = new byte[cipherLength];

		System.arraycopy(keyCipher, 0, cipherCopy, 0, cipherCopy.length);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(cipherCopy);
		KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
		return keyFactory.generatePrivate(keySpec);
	}

	public static X509Certificate[] getCertificateChain(byte[] cert) throws IOException, CertificateException {
		ASN1StreamParser streamParser = new ASN1StreamParser(cert);
		DEREncodable encodable = streamParser.readObject();
		ContentInfo contentInfo = new ContentInfo((ASN1Sequence) (encodable.getDERObject()));
		SignedData signedData = new SignedData((ASN1Sequence)contentInfo.getContent());
		ASN1Set asnCerts = signedData.getCertificates();
		X509Certificate[] certs = new X509Certificate[asnCerts.size()];
		CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);

		for (int i = 0; i < asnCerts.size(); ++i) {
			ByteArrayInputStream is = new ByteArrayInputStream(asnCerts.getObjectAt(i).getDERObject().getEncoded());
			certs[i] = (X509Certificate) certFactory.generateCertificate(is);
		}

		return certs;
	}

	private static PaddedBufferedBlockCipher getEncryptionDecryptionCipher(boolean forEncryption, char[] password, byte[] salt, int iterationCount) {
		
		byte[] passwordBytes = PBEParametersGenerator.PKCS12PasswordToBytes(password);
		PKCS12ParametersGenerator paramGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
		paramGenerator.init(passwordBytes, salt, iterationCount);
		CBCBlockCipher cbcCipher = new CBCBlockCipher(new DESedeEngine());
		ParametersWithIV params = (ParametersWithIV) paramGenerator.generateDerivedParameters(192, cbcCipher.getBlockSize() * 8);
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbcCipher, new PKCS7Padding());
		cipher.init(forEncryption, params);
		return cipher;
	}

	private static byte[] getEncryptedData(byte[] data) throws IOException {
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
		ASN1InputStream is = new ASN1InputStream(byteInputStream);
		EncryptedPrivateKeyInfo info = new EncryptedPrivateKeyInfo((ASN1Sequence) is.readObject());
		byte[] encryptedData = info.getEncryptedData();
		is.close();
		return encryptedData;
	}
}
