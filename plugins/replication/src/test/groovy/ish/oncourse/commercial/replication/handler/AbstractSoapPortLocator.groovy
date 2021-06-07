/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.webservices.soap.v23.PaymentPortType
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import ish.oncourse.webservices.soap.v7.ReferencePortType

/**
 */
@CompileStatic
class AbstractSoapPortLocator implements ISoapPortLocator {

	@Override
	ReferencePortType referencePort() {
		return null
	}

	@Override
	PaymentPortType paymentPort() {
		return null
	}

	@Override
	ReplicationPortType replicationPort() {
		return null
	}

	@Override
	void resetReplicationPort() {}
}
