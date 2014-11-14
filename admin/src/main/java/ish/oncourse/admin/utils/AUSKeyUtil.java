/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class AUSKeyUtil {
	
	private static final String CREDENTIAL_TAG = "credential";
	private static final String CERTIFICATE_TAG = "publicCertificate";
	private static final String PRIVATE_KEY_TAG = "protectedPrivateKey";
	private static final String SALT_ATTRIBUTE = "credentialSalt";
	
	public static AUSKey parseKeystoreXml(InputStream xml) {
		AUSKey ausKey = new AUSKey();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();

			Document doc = documentBuilder.parse(xml);

			NodeList credentials = doc.getElementsByTagName(CREDENTIAL_TAG);
			Node credential = credentials.item(0);

			if (credential != null) {
				ausKey.setSalt(credential.getAttributes().getNamedItem(SALT_ATTRIBUTE).getNodeValue());

				NodeList credentialChildNodes = credential.getChildNodes();
				for (int i = 0; i < credentialChildNodes.getLength(); i++) {
					Node node = credentialChildNodes.item(i);
					
					switch (node.getNodeName()) {
						case CERTIFICATE_TAG:
							ausKey.setCertificate(node.getTextContent());
							break;
						case PRIVATE_KEY_TAG:
							ausKey.setPrivateKey(node.getTextContent());
							break;
						default:
							// continue iterating
					}
				}
			}

			return ausKey;
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse keystore file.", e);
		}
	}
	
	public static class AUSKey {
		private String certificate;
		private String privateKey;
		private String salt;


		public String getCertificate() {
			return certificate;
		}

		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
		}

		public String getSalt() {
			return salt;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}
		
		public boolean isFilled() {
			return certificate != null && privateKey != null && salt != null;
		}
	}
}
