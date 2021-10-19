package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

public class V24TestEnv {
    private String dataSetFile;
    private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
    private TestEnv<V24TransportConfig> testEnv;


    public V24TestEnv(String dataSetFile, Map<Object, Object> replacements) {
        this.dataSetFile = dataSetFile;
        this.replacements = replacements == null ? this.replacements : replacements;
    }

    public V24TestEnv start() {
        testEnv = new TestEnv<>((TestEnv<V24TransportConfig> testEnv) -> {
            V24TransportConfig transportConfig = new V24TransportConfig(testEnv);
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
