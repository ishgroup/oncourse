/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
