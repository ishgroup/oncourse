package ish.oncourse.webservices.soap.v22;

import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.function.TestEnvFunctions;
import ish.oncourse.webservices.function.TransportConfigFunctions;
import ish.oncourse.webservices.soap.v22.PaymentPortType;
import ish.oncourse.webservices.soap.v22.ReplicationPortType;
import ish.oncourse.webservices.soap.v7.ReferencePortType;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v22.stubs.replication.*;
import ish.oncourse.webservices.v7.stubs.reference.ReferenceStub;

import static ish.oncourse.webservices.soap.TestConstants.DEFAULT_COLLEGE_KEY;

public class TransportConfig extends TransportConfigFunctions<
		TransactionGroup, ParametersMap, ReplicationRecords, ReplicationResult,
		ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

	public TransportConfig(TestEnvFunctions testEnv) {
		this.serverURI(testEnv.getURI());
		this.replicationVersion(SupportedVersions.V22);
		this.referenceVersion(SupportedVersions.V7);
		this.communicationKey(() -> testEnv.getPageTester().getService(ICollegeService.class)
				.findBySecurityCode(DEFAULT_COLLEGE_KEY).getCommunicationKey());
		this.securityCode(() -> testEnv.getPageTester().getService(ICollegeService.class)
				.findBySecurityCode(DEFAULT_COLLEGE_KEY).getWebServicesSecurityCode());
	}
}