package ish.oncourse.webservices.soap.v14;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

/**
 * Created by pavel on 28/8/17.
 */
public class V14TestEnv {
	private String dataSetFile;
	private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
	private TestEnv<V14TransportConfig> testEnv;


	public V14TestEnv(String dataSetFile, Map<Object, Object> replacements) {
		this.dataSetFile = dataSetFile;
		this.replacements = replacements == null ? this.replacements : replacements;
	}

	public V14TestEnv start() {
		testEnv = new TestEnv<>((TestEnv<V14TransportConfig> testEnv) -> {
			V14TransportConfig transportConfig = new V14TransportConfig(testEnv);
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
