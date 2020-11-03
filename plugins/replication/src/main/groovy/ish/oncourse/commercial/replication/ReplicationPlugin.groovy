/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication

import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.Scopes
import com.google.inject.name.Names
import ish.oncourse.commercial.replication.builders.AngelStubBuilderImpl
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.handler.*
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.commercial.replication.modules.SoapPortLocatorImpl
import ish.oncourse.commercial.replication.reference.ReferenceJob
import ish.oncourse.commercial.replication.reference.updater.IReferenceUpdaterFactory
import ish.oncourse.commercial.replication.reference.updater.ReferenceUpdaterFactory
import ish.oncourse.commercial.replication.services.AngelQueueService
import ish.oncourse.commercial.replication.services.IAngelQueueService
import ish.oncourse.commercial.replication.services.ReplicationJob
import ish.oncourse.commercial.replication.services.TransactionGroupProcessorImpl
import ish.oncourse.commercial.replication.updaters.AngelUpdaterImpl
import ish.oncourse.commercial.replication.updaters.IAngelUpdater
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.OnStart
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.webservices.ITransactionGroupProcessor
import org.quartz.Scheduler


@Plugin(type = 15)
class ReplicationPlugin {
    int DEBUG_REFERENCE_JOB_INTERVAL = 40
    int PRODUCTION_REFERENCE_JOB_INTERVAL = 4 * 60 * 60
    int DEBUG_PRIMARY_REPLICATION_INTERVAL = 30
    long REPLICATION_INTERVAL = 5 * 60 * 1000 //5 minutes
    int PRODUCTION_PRIMARY_REPLICATION_INTERVAL = 60
    int DISABLED_PRIMARY_REPLICATION_INTERVAL = 60 * 60 * 24

    String REFERENCE_HANDLER_JOB_ID = "referenceHandlerJob"
    String REPLICATION_JOBS_GROUP_ID = "replicationJobs"
    String PRIMARY_REPLICATION_SERVICE_JOB_ID = "primaryReplicationServiceJob"
    
    private Injector injector
    private IntegrationConfiguration config
    
    
    //@OnConfigure
    static void configure(Binder binder) {

        binder.bind(ISoapPortLocator).to(SoapPortLocatorImpl).asEagerSingleton()
        binder.bind(ReplicationJob)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Outbound")).to(OutboundReplicationHandler)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Inbound")).to(InboundReplicationHandler)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Instruction")).to(InstructionHandler)
        binder.bind(IVoucherValidationService).to(VoucherValidationService)

        binder.bind(IAngelStubBuilder).to(AngelStubBuilderImpl)
        binder.bind(IAngelUpdater).to(AngelUpdaterImpl)
        binder.bind(IReferenceUpdaterFactory).to(ReferenceUpdaterFactory)
        binder.bind(IAngelQueueService).to(AngelQueueService).in(Scopes.SINGLETON)

        binder.bind(ITransactionGroupProcessor).to(TransactionGroupProcessorImpl)
    }
    

    @OnStart
    void start() {
        Scheduler scheduler = injector.getInstance(Scheduler)
        scheduler.getContext().put(ReplicationJob.LAST_REPLICATION_TIME, Long.MIN_VALUE)

        LicenseService licenseService = injector.getInstance(LicenseService)
        ISchedulerService schedulerService = injector.getInstance(ISchedulerService)
        PreferenceController prefController = injector.getInstance(PreferenceController)
        // reference update job schedule every 40 sec when in debug and
        // every 4 hours when in production mode

        if (!licenseService.isReplicationDisabled()) {
            int referenceJobScheduleInterval = licenseService.isReplication_debug() ?
                    DEBUG_REFERENCE_JOB_INTERVAL : PRODUCTION_REFERENCE_JOB_INTERVAL
            schedulerService.schedulePeriodicJob(ReferenceJob.class, REFERENCE_HANDLER_JOB_ID,
                    REPLICATION_JOBS_GROUP_ID, referenceJobScheduleInterval, true, false)


            // primary replication schedule every 30 sec when in debug and
            // every 1 min when in production mode
            int primaryReplicationScheduleInterval =  licenseService.isReplication_debug() ?
                    DEBUG_PRIMARY_REPLICATION_INTERVAL : PRODUCTION_PRIMARY_REPLICATION_INTERVAL

            // if replication is not disabled by REPLICATION_DISABLED
            // property then check if it is not disabled by preference
            if (prefController.getReplicationEnabled()) {
                schedulerService.schedulePeriodicJob(ReplicationJob.class,
                        PRIMARY_REPLICATION_SERVICE_JOB_ID, REPLICATION_JOBS_GROUP_ID,
                        primaryReplicationScheduleInterval, true, false)

            } else {
                // if replication is not enabled then scheduling it
                // every 24 hours to receive preferences
                schedulerService.schedulePeriodicJob(ReplicationJob.class,
                        PRIMARY_REPLICATION_SERVICE_JOB_ID, REPLICATION_JOBS_GROUP_ID,
                        DISABLED_PRIMARY_REPLICATION_INTERVAL, false, false)
            }
        }
    }
    
    
}
