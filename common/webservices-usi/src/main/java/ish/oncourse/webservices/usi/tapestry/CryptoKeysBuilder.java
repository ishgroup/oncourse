/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.tapestry;

import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class CryptoKeysBuilder implements ServiceBuilder<CryptoKeys> {

	@Override
	public CryptoKeys buildService(ServiceResources resources) {
		final PreferenceController preferenceController = resources.getService(PreferenceController.class);
		return new CryptoKeys() {
			@Override
			public String getServicesSecurityKey() {
				return preferenceController.getServicesSecurityKey();
			}

			@Override
			public String getAuskeyCertificate() {
				return preferenceController.getAuskeyCertificate();
			}

			@Override
			public String getAuskeyPrivateKey() {
				return preferenceController.getAuskeyPrivateKey();
			}

			@Override
			public String getAuskeySalt() {
				return preferenceController.getAuskeySalt();
			}

			@Override
			public String getAuskeyPassword() {
				return preferenceController.getAuskeyPassword();
			}
		};
	}
}
