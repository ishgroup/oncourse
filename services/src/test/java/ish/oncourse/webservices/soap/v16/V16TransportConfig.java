package ish.oncourse.webservices.soap.v16;

import ish.oncourse.webservices.soap.TestServer;
import ish.oncourse.webservices.soap.TransportConfig;
import ish.oncourse.webservices.soap.v6.ReferencePortType;
import ish.oncourse.webservices.v16.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v16.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;

/**
 * Created by alex on 8/24/17.
 */
public class V16TransportConfig extends TransportConfig<TransactionGroup, ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

    private V16TransportConfig() {
    }

    public static V16TransportConfig valueOf(TestServer server) {
        V16TransportConfig config = new V16TransportConfig();
        config.server(server);

        return config;
    }
}