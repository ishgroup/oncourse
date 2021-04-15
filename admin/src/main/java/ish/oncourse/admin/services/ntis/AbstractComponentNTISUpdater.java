/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis;

import ish.oncourse.listeners.IshVersionHolder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.ObjectContext;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

public abstract class AbstractComponentNTISUpdater {

	protected static final int RESULTS_PAGE_SIZE = 1000;

	protected DateTimeOffset from;
	protected DateTimeOffset to;

	protected int totalNew;
	protected int totalModified;

	protected ReferenceService referenceService;
	protected ICayenneService cayenneService;

	public AbstractComponentNTISUpdater(
			ICayenneService cayenneService,
			ReferenceService referenceService,
			DateTimeOffset from,
			DateTimeOffset to) {
		this.cayenneService = cayenneService;
		this.referenceService = referenceService;
		this.from = from;
		this.to = to;
	}

	/**
	 * Save changes to database, increase ishVersion and saves to thread local.
	 *
	 * @param numberOfNew
	 *            number of new records.
	 * @param context
	 *            object context
	 * @return number of modified records
	 */
	protected long saveChanges(int numberOfNew, ObjectContext context) {

		Long ishVersion = referenceService.findMaxIshVersion() + 1;
		long modified = 0;

		try {
			IshVersionHolder.setIshVersion(ishVersion);
			context.commitChanges();

			long allTouchedRecords = referenceService.getNumberOfRecordsForIshVersion(ishVersion);

			if (allTouchedRecords >= numberOfNew) {
				modified = allTouchedRecords - numberOfNew;
			}
		} finally {
			IshVersionHolder.cleanUp();
		}

		return modified;
	}

	public abstract NTISResult doUpdate() throws Exception;
}
