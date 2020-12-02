/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.ObjectContext;

/**
 */
public interface IReferenceUpdaterFactory {
	IReferenceUpdater<? extends GenericReferenceStub> newReferenceUpdater(ObjectContext ctx);
}
