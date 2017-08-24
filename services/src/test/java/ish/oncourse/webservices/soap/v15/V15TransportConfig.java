package ish.oncourse.webservices.soap.v15;

import ish.oncourse.webservices.soap.TestServer;
import ish.oncourse.webservices.soap.TransportConfig;
import ish.oncourse.webservices.soap.v6.ReferencePortType;
import ish.oncourse.webservices.v15.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v15.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;

/**
 * Created by alex on 8/24/17.
 */
public class V15TransportConfig extends TransportConfig<TransactionGroup, ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

    private V15TransportConfig() {
    }

    public static V15TransportConfig valueOf(TestServer server) {
        V15TransportConfig config = new V15TransportConfig();
        config.server(server);

        return config;
    }
}