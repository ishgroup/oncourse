/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v16;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class V16TestEnv {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnv<V16TransportConfig> testEnv;


	public V16TestEnv(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? this.replacements : replacements;
	}

	public V16TestEnv start() {
		testEnv = new TestEnv<>((TestEnv<V16TransportConfig> testEnv) -> {
			V16TransportConfig transportConfig = new V16TransportConfig(testEnv);
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
