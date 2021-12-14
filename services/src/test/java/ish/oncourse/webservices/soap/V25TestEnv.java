package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

public class V25TestEnv {
    private String dataSetFile;
    private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
    private TestEnv<V25TransportConfig> testEnv;


    public V25TestEnv(String dataSetFile, Map<Object, Object> replacements) {
        this.dataSetFile = dataSetFile;
        this.replacements = replacements == null ? this.replacements : replacements;
    }

    public V25TestEnv start() {
        testEnv = new TestEnv<>((TestEnv<V25TransportConfig> testEnv) -> {
            V25TransportConfig transportConfig = new V25TransportConfig(testEnv);
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
