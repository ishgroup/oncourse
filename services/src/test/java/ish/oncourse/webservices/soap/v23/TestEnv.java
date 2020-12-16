/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v23;

import ish.oncourse.webservices.function.TestEnvFunctions;

import java.util.Collections;
import java.util.Map;

public class TestEnv {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnvFunctions testEnv;


	public TestEnv(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? this.replacements : replacements;
	}

	public TestEnv start() {
		this.testEnv = new TestEnvFunctions(testEnv -> {
			TransportConfig transportConfig = new TransportConfig(this.testEnv);
			transportConfig.init();
			transportConfig.pingReplicationPort();
			transportConfig.pingPaymentPort();
			transportConfig.pingReferencePort();
			return transportConfig;
		}, this.dataSetFile, this.replacements);
		testEnv.start();
		return this;

	}


	public void shutdown() {
		testEnv.shutdown();
	}

	public TestEnvFunctions getTestEnv() {
		return testEnv;
	}
}
