/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference;

import com.google.inject.Inject;
import ish.oncourse.commercial.replication.modules.ISoapPortLocator;
import ish.oncourse.commercial.replication.reference.updater.IReferenceUpdater;
import ish.oncourse.commercial.replication.reference.updater.IReferenceUpdaterFactory;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.webservices.soap.v7.ReferencePortType;
import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

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
			ReferencePortType port = this.soapPortLocator.referencePort();

			long willowMaxVersion = port.getMaximumVersion();

			// updating reference data

			var angelMaxVersion = this.pref.getReferenceDataVersion();

			for (var version = angelMaxVersion + 1; version <= willowMaxVersion; version++) {
				ObjectContext ctx = this.cayenneService.getNewNonReplicatingContext();

				IReferenceUpdater<GenericReferenceStub> referenceUpdater = (IReferenceUpdater<GenericReferenceStub>) this.referenceUpdaterFactory
						.newReferenceUpdater(ctx);
				List<GenericReferenceStub> records = port.getRecords(version).getGenericCountryOrLanguageOrModule();

				logger.info("Received {} records for version:{}", records.size(), version);

				for (GenericReferenceStub record : records) {
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
