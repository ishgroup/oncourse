/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.handler

import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.webservices.soap.v22.PaymentPortType
import ish.oncourse.webservices.soap.v22.ReplicationPortType
import ish.oncourse.webservices.soap.v7.ReferencePortType

/**
 */
class AbstractSoapPortLocator implements ISoapPortLocator {

	/**
	 * @see ish.oncourse.server.modules.ISoapPortLocator#referencePort()
	 */
	@Override
	ReferencePortType referencePort() {
		return null
	}

	/**
	 * @see ish.oncourse.server.modules.ISoapPortLocator#paymentPort()
	 */
	@Override
	PaymentPortType paymentPort() {
		return null
	}

	/**
	 * @see ish.oncourse.server.modules.ISoapPortLocator#replicationPort()
	 */
	@Override
	ReplicationPortType replicationPort() {
		return null
	}

	/**
	 * @see ish.oncourse.server.modules.ISoapPortLocator#resetReplicationPort()
	 */
	@Override
	void resetReplicationPort() {}
}
