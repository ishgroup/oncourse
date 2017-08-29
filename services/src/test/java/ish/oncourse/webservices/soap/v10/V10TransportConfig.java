/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v10;

import ish.oncourse.services.system.ICollegeService;
import ish.oncourse.webservices.function.TestEnv;
import ish.oncourse.webservices.function.TransportConfig;
import ish.oncourse.webservices.soap.v5.ReferencePortType;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v10.stubs.replication.*;
import ish.oncourse.webservices.v5.stubs.reference.ReferenceStub;

import static ish.oncourse.webservices.soap.TestConstants.DEFAULT_COLLEGE_KEY;


public class V10TransportConfig extends TransportConfig<
		TransactionGroup, ParametersMap, ReplicationRecords, ReplicationResult,
		ReplicationStub, ReferenceStub, ReferencePortType, ReplicationPortType, PaymentPortType> {

	public V10TransportConfig(TestEnv testEnv) {
		this.serverURI(testEnv.getURI());
		this.replicationVersion(SupportedVersions.V10);
		this.referenceVersion(SupportedVersions.V5);
		this.communicationKey(() -> testEnv.getPageTester().getService(ICollegeService.class)
				.findBySecurityCode(DEFAULT_COLLEGE_KEY).getCommunicationKey());
		this.securityCode(() -> testEnv.getPageTester().getService(ICollegeService.class)
				.findBySecurityCode(DEFAULT_COLLEGE_KEY).getWebServicesSecurityCode());
	}
}
