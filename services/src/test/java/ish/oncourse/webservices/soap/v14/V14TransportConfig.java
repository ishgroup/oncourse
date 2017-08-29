package ish.oncourse.webservices.soap.v14;

import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.function.TestEnv;
import ish.oncourse.webservices.function.TransportConfig;
import ish.oncourse.webservices.soap.v6.ReferencePortType;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v14.stubs.replication.*;
import ish.oncourse.webservices.v6.stubs.reference.ReferenceStub;

import static ish.oncourse.webservices.soap.TestConstants.DEFAULT_COLLEGE_KEY;

/**
 * Created by alex on 8/24/17.
 */
public class V14TransportConfig extends TransportConfig<
        TransactionGroup, ParametersMap, ReplicationRecords, ReplicationResult,
        ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

    public V14TransportConfig(TestEnv testEnv) {
        this.serverURI(testEnv.getURI());
        this.replicationVersion(SupportedVersions.V14);
        this.referenceVersion(SupportedVersions.V5);
        this.communicationKey(() -> testEnv.getPageTester().getService(ICollegeService.class)
                .findBySecurityCode(DEFAULT_COLLEGE_KEY).getCommunicationKey());
        this.securityCode(() -> testEnv.getPageTester().getService(ICollegeService.class)
                .findBySecurityCode(DEFAULT_COLLEGE_KEY).getWebServicesSecurityCode());
    }
}
