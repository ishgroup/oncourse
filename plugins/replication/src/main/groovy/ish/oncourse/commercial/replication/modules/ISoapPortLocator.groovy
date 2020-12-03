/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.modules

import ish.oncourse.webservices.soap.v23.PaymentPortType;
import ish.oncourse.webservices.soap.v23.ReplicationPortType;
import ish.oncourse.webservices.soap.v7.ReferencePortType;

/**
 */
interface ISoapPortLocator {

    ReferencePortType referencePort();

    PaymentPortType paymentPort();

    ReplicationPortType replicationPort();

    void resetReplicationPort();
}
