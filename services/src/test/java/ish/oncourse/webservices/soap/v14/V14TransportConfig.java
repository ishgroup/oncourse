package ish.oncourse.webservices.soap.v14;

import ish.oncourse.webservices.function.TransportConfig;
import ish.oncourse.webservices.soap.TestServer;
import ish.oncourse.webservices.soap.v6.ReferencePortType;
import ish.oncourse.webservices.v14.stubs.replication.*;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;

/**
 * Created by alex on 8/24/17.
 */
public class V14TransportConfig extends TransportConfig<TransactionGroup, ParametersMap, ReplicationRecords, ReplicationResult,
        ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

    private V14TransportConfig() {}

    public static V14TransportConfig valueOf(TestServer server){
        V14TransportConfig config = new V14TransportConfig();
        return config;
    }
}
