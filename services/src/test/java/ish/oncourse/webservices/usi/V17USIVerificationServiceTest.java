/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.oncourse.webservices.soap.v17.PaymentPortType;
import ish.oncourse.webservices.soap.v17.RealWSTransportTest;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v17.stubs.replication.ParametersMap;
import org.junit.Test;

public class V17USIVerificationServiceTest extends RealWSTransportTest {
	@Test
	public void testUSI() throws Exception {

		new USITest(new USITest.Parent() {
			@Override
			public GenericParametersMap verifyUSI(GenericParametersMap var1) throws Exception {
				return ((PaymentPortType) testEnv.getTestEnv().getTransportConfig().getPaymentPortType()).verifyUSI((ParametersMap) var1);
			}

			@Override
			public void authenticate() throws Exception {
				testEnv.getTestEnv().authenticate();
			}

			@Override
			public SupportedVersions getVersion() {
				return SupportedVersions.V17;
			}
		}).testUSI();
	}
}
