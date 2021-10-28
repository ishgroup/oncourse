/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.oncourse.webservices.soap.v24.AuthFailure
import ish.oncourse.webservices.soap.v24.ReplicationFault
import ish.oncourse.webservices.soap.v24.ReplicationPortType
import ish.oncourse.webservices.v24.stubs.replication.*

/**
 */
@CompileStatic
class AbstractReplicationPortType implements ReplicationPortType {

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#sendRecords(ish.oncourse.webservices.v24.stubs.replication.ReplicationRecords)
	 */
	@Override
	ReplicationResult sendRecords(ReplicationRecords records) {
		return null
	}

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#getRecords()
	 */
	@Override
	ReplicationRecords getRecords() {
		return null
	}

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#sendResults(ish.oncourse.webservices.v24.stubs.replication.ReplicationResult)
	 */
	@Override
	int sendResults(ReplicationResult replResult) {
		return 0
	}

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#confirmExecution(java.lang.Long, java.lang.String)
	 */
	@Override
	void confirmExecution(Long arg0, String arg1) {
	}

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#getInstructions()
	 */
	@Override
	List<InstructionStub> getInstructions() {
		return null
	}

	/**
	 * @throws ish.oncourse.webservices.soap.v24.AuthFailure
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#authenticate(java.lang.String, long)
	 */
	@Override
	long authenticate(String securityCode, long lastCommunicationKey) throws AuthFailure {
		return 0
	}

	/**
	 * @see ish.oncourse.webservices.soap.v24.ReplicationPortType#getUnreplicatedEntities()
	 */
	@Override
	List<UnreplicatedEntitiesStub> getUnreplicatedEntities() {
		return null
	}

	@Override
	TransactionGroup getRecordByInstruction(String s) throws ReplicationFault {
		return null
	}
}
