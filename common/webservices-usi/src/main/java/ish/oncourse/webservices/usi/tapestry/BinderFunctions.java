/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.tapestry;

import ish.oncourse.webservices.usi.USIService;
import ish.oncourse.webservices.usi.crypto.CryptoKeys;
import ish.oncourse.webservices.usi.crypto.UsiCertificate;
import ish.oncourse.webservices.usi.crypto.UsiPrivateKey;
import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * User: akoiro
 * Date: 4/12/17
 */
public class BinderFunctions {
	public static void bindUSIServices(ServiceBinder binder) {
		binder.bind(CryptoKeys.class, new CryptoKeysBuilder()).eagerLoad();
		binder.bind(USIService.class, new USIServiceBuilder()).eagerLoad();
		binder.bind(UsiCertificate.class, new UsiCertificateBuilder()).eagerLoad();
		binder.bind(UsiPrivateKey.class, new UsiPrivateKeyBuilder()).eagerLoad();
	}

}
