/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v17;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class V17TestEnv {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnv<V17TransportConfig> testEnv;


	public V17TestEnv(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? this.replacements : replacements;
	}

	public V17TestEnv start() {
		testEnv = new TestEnv<>((TestEnv<V17TransportConfig> testEnv) -> {
			V17TransportConfig transportConfig = new V17TransportConfig(testEnv);
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

	public TestEnv getTestEnv() {
		return testEnv;
	}
}
