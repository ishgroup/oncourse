package ish.oncourse.webservices.soap.v15;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;

/**
 * Created by alex on 8/28/17.
 */
public class V15TestEnv {
    private String dataSetFile;
    private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
    private TestEnv<V15TransportConfig> testEnv;


    public V15TestEnv(String dataSetFile, Map<Object, Object> replacements) {
        this.dataSetFile = dataSetFile;
        this.replacements = replacements == null ? this.replacements : replacements;
    }

    public V15TestEnv start()  {
        testEnv = new TestEnv<>((TestEnv<V15TransportConfig> testEnv) -> {
            V15TransportConfig transportConfig = new V15TransportConfig(testEnv);
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
