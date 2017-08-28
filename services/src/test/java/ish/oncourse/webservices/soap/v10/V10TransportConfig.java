/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v10;

import ish.oncourse.webservices.function.TransportConfig;
import ish.oncourse.webservices.soap.TestServer;
import ish.oncourse.webservices.soap.v5.ReferencePortType;
import ish.oncourse.webservices.v10.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v10.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v10.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v5.stubs.reference.ReferenceStub;

public class V10TransportConfig extends TransportConfig<TransactionGroup, ParametersMap, ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {
    
    static V10TransportConfig valueOf(TestServer testServer) {
        V10TransportConfig config =  new V10TransportConfig();
        return config;
    }
}
