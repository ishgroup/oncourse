package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

public class V22TestEnv {
    private String dataSetFile;
    private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
    private TestEnv<V22TransportConfig> testEnv;


    public V22TestEnv(String dataSetFile, Map<Object, Object> replacements) {
        this.dataSetFile = dataSetFile;
        this.replacements = replacements == null ? this.replacements : replacements;
    }

    public V22TestEnv start() {
        testEnv = new TestEnv<>((TestEnv<V22TransportConfig> testEnv) -> {
            V22TransportConfig transportConfig = new V22TransportConfig(testEnv);
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
