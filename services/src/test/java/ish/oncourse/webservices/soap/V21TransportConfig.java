package ish.oncourse.webservices.soap;

import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.function.TestEnv;
import ish.oncourse.webservices.function.TransportConfig;
import ish.oncourse.webservices.soap.v21.PaymentPortType;
import ish.oncourse.webservices.soap.v21.ReplicationPortType;
import ish.oncourse.webservices.soap.v7.ReferencePortType;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v21.stubs.replication.*;
import ish.oncourse.webservices.v7.stubs.reference.ReferenceStub;

import static ish.oncourse.webservices.soap.TestConstants.DEFAULT_COLLEGE_KEY;

public class V21TransportConfig extends TransportConfig<
        TransactionGroup, ParametersMap, ReplicationRecords, ReplicationResult,
        ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

    public V21TransportConfig(TestEnv testEnv) {
        this.serverURI(testEnv.getURI());
        this.replicationVersion(SupportedVersions.V21);
        this.referenceVersion(SupportedVersions.V7);
        this.communicationKey(() -> testEnv.getPageTester().getService(ICollegeService.class)
                .findBySecurityCode(DEFAULT_COLLEGE_KEY).getCommunicationKey());
        this.securityCode(() -> testEnv.getPageTester().getService(ICollegeService.class)
                .findBySecurityCode(DEFAULT_COLLEGE_KEY).getWebServicesSecurityCode());
    }
}