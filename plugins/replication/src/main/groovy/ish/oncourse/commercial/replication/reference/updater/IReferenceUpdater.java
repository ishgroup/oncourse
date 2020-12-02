/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.webservices.util.GenericReferenceStub;

/**
 * Common interface for classes which convert Soap stubs of reference service into server-side cayenne objects.
 *
 */
public interface IReferenceUpdater<T extends GenericReferenceStub> {
	void updateRecord(T record);
}
