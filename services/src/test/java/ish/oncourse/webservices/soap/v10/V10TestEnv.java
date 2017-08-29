/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v10;

import ish.oncourse.webservices.function.TestEnv;

import java.util.Collections;
import java.util.Map;


public class V10TestEnv {
    private String dataSetFile;
    private Map<Object, Object> replacements = Collections.singletonMap("[null]", null);
    private TestEnv<V10TransportConfig> testEnv;


    public V10TestEnv(String dataSetFile, Map<Object, Object> replacements) {
        this.dataSetFile = dataSetFile;
        this.replacements = replacements == null ? this.replacements : replacements;
    }

    public V10TestEnv start() {
        testEnv = new TestEnv<>((TestEnv<V10TransportConfig> testEnv) -> {
            V10TransportConfig transportConfig = new V10TransportConfig(testEnv);
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
