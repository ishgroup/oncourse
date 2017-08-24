/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v10;

import ish.oncourse.webservices.soap.TestServer;
import ish.oncourse.webservices.soap.TransportConfig;
import ish.oncourse.webservices.soap.v5.ReferencePortType;
import ish.oncourse.webservices.v10.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v10.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v5.stubs.reference.ReferenceStub;

import static ish.oncourse.webservices.util.SupportedVersions.*;

public class V10TransportConfig extends TransportConfig<TransactionGroup, ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {
    
    static V10TransportConfig valueOf(TestServer testServer) {
        V10TransportConfig config =  new V10TransportConfig();
        config.server(testServer).replicationVersion(V10).referenceVersion(V5).init();
        return config;
    }
}
