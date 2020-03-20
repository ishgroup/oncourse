/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.tapestry;

import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class CryptoKeysBuilder implements ServiceBuilder<CryptoKeys> {

	private static final Logger logger = LogManager.getLogger();

	@Override
	public CryptoKeys buildService(ServiceResources resources) {

		final СredentialStoreReader reader = СredentialStoreReader.valueOf(System.getProperty("credentialStore"), System.getProperty("credentialStorePassword"));
		try {
			reader.read();
		} catch (ParserConfigurationException | SAXException e) {
			logger.error("Can not parse credential store file for USI.");
		} catch (IOException e) {
			logger.error("Credential store file not found.");
		}

		return new CryptoKeys() {
			@Override
			public String getServicesSecurityKey() {
				return reader.getId();
			}

			@Override
			public String getCertificate() {
				return reader.getPublicCertificate();
			}

			@Override
			public String getPrivateKey() {
				return reader.getPrivateKey();
			}

			@Override
			public String getSalt() {
				return reader.getSalt();
			}

			@Override
			public String getPassword() {
				return reader.getPassword();
			}
		};
	}
}
