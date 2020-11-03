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
package ish.oncourse.commercial.replication.reference;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.modules.ISoapPortLocator;
import ish.oncourse.server.reference.updater.IReferenceUpdater;
import ish.oncourse.server.reference.updater.IReferenceUpdaterFactory;
import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 */
@DisallowConcurrentExecution
public class ReferenceJob implements Job {

	private static final Logger logger = LogManager.getLogger();

	private final ISoapPortLocator soapPortLocator;

	private final ICayenneService cayenneService;

	private final IReferenceUpdaterFactory referenceUpdaterFactory;

	private final PreferenceController pref;

	/**
	 * Constructor for DI.
	 *
	 * @param soapPortLocator reference webservice client.
	 * @param cayenneService cayenne service.
	 * @param referenceUpdaterFactory reference updater factory.
	 * @param pref preference controller.
	 */
	@Inject
	public ReferenceJob(ISoapPortLocator soapPortLocator, ICayenneService cayenneService, IReferenceUpdaterFactory referenceUpdaterFactory,
                        PreferenceController pref) {
		super();
		this.soapPortLocator = soapPortLocator;
		this.cayenneService = cayenneService;
		this.referenceUpdaterFactory = referenceUpdaterFactory;
		this.pref = pref;
	}

	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
		logger.debug("reference job process started");
		try {
			var port = this.soapPortLocator.referencePort();

			Long willowMaxVersion = port.getMaximumVersion();

			// updating reference data

			var angelMaxVersion = this.pref.getReferenceDataVersion();

			for (var version = angelMaxVersion + 1; version <= willowMaxVersion; version++) {
				ObjectContext ctx = this.cayenneService.getNewNonReplicatingContext();

				var referenceUpdater = (IReferenceUpdater<GenericReferenceStub>) this.referenceUpdaterFactory
						.newReferenceUpdater(ctx);
				var records = port.getRecords(version).getGenericCountryOrLanguageOrModule();

				logger.info("Received {} records for version:{}", records.size(), version);

				for (var record : records) {
					referenceUpdater.updateRecord(record);
				}

				ctx.commitChanges();

				this.pref.setReferenceDataVersion(version);
			}
		} catch (Exception e) {
			logger.error("Failed to update reference data.", e);
			throw new JobExecutionException("Failed to upddate reference data.", e);
		}
		logger.debug("reference job process finished");
	}
}
