/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication

import com.google.inject.Binder
import com.google.inject.Injector
import com.google.inject.Scopes
import com.google.inject.name.Names
import groovy.transform.CompileStatic
import ish.common.types.SystemEventType
import ish.oncourse.commercial.replication.builders.AngelStubBuilderImpl
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.event.CheckoutEventListener
import ish.oncourse.commercial.replication.event.WillowValidator
import ish.oncourse.commercial.replication.handler.InboundReplicationHandler
import ish.oncourse.commercial.replication.handler.InstructionHandler
import ish.oncourse.commercial.replication.handler.OutboundReplicationHandler
import ish.oncourse.commercial.replication.handler.ReplicationHandler
import ish.oncourse.commercial.replication.lifecycle.ReplicationListenersService
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
import ish.oncourse.commercial.replication.upgrades.QueueAllRecordsForFirstTimeReplication
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.OnConfigure
import ish.oncourse.server.integration.OnStart
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISchedulerService
import ish.oncourse.webservices.ITransactionGroupProcessor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.Scheduler

import java.util.concurrent.Executors

@CompileStatic
@Plugin(type = 15)
class ReplicationPlugin {

    private static final Logger logger = LogManager.getLogger()

    static final int PRODUCTION_REFERENCE_JOB_INTERVAL = 4 * 60 * 60
    static final int PRODUCTION_PRIMARY_REPLICATION_INTERVAL = 60
    static final long REPLICATION_INTERVAL = 5 * 60 * 1000 //5 minutes

    String REFERENCE_HANDLER_JOB_ID = "referenceHandlerJob"
    String REPLICATION_JOBS_GROUP_ID = "replicationJobs"
    String PRIMARY_REPLICATION_SERVICE_JOB_ID = "primaryReplicationServiceJob"

    private Injector injector

    @OnConfigure
    static void configure(Binder binder) {
        binder.bind(ISoapPortLocator).to(SoapPortLocatorImpl).asEagerSingleton()
        binder.bind(ReplicationJob)
        binder.bind(WillowValidator)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Outbound")).to(OutboundReplicationHandler)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Inbound")).to(InboundReplicationHandler)
        binder.bind(ReplicationHandler).annotatedWith(Names.named("Instruction")).to(InstructionHandler)

        binder.bind(IAngelStubBuilder).to(AngelStubBuilderImpl)
        binder.bind(IAngelUpdater).to(AngelUpdaterImpl)
        binder.bind(IReferenceUpdaterFactory).to(ReferenceUpdaterFactory)
        binder.bind(IAngelQueueService).to(AngelQueueService).in(Scopes.SINGLETON)

        binder.bind(ITransactionGroupProcessor).to(TransactionGroupProcessorImpl)
        binder.bind(ReplicationListenersService).asEagerSingleton()
    }


    @OnStart
    void start() {
        Scheduler scheduler = injector.getInstance(Scheduler)
        scheduler.getContext().put(ReplicationJob.LAST_REPLICATION_TIME, Long.MIN_VALUE)

        LicenseService licenseService = injector.getInstance(LicenseService)
        ISchedulerService schedulerService = injector.getInstance(ISchedulerService)

        WillowValidator willowValidator = injector.getInstance(WillowValidator)

        CheckoutEventListener checkoutEventListener = new CheckoutEventListener(licenseService, willowValidator)
        injector.getInstance(EventService).registerListener(checkoutEventListener, SystemEventType.VALIDATE_CHECKOUT)

        // every 4 hours
        if (!licenseService.isReplicationDisabled()) {

            int referenceJobScheduleInterval = PRODUCTION_REFERENCE_JOB_INTERVAL
            schedulerService.schedulePeriodicJob(ReferenceJob.class, REFERENCE_HANDLER_JOB_ID,
                    REPLICATION_JOBS_GROUP_ID, referenceJobScheduleInterval, true, false)


            // every 1 min when in production mode
            int primaryReplicationScheduleInterval = PRODUCTION_PRIMARY_REPLICATION_INTERVAL

            // if replication is not disabled by REPLICATION_DISABLED
            // property then check if it is not disabled by preference
            schedulerService.schedulePeriodicJob(ReplicationJob.class,
                    PRIMARY_REPLICATION_SERVICE_JOB_ID, REPLICATION_JOBS_GROUP_ID,
                    primaryReplicationScheduleInterval, true, false)

            Executors.newSingleThreadExecutor().submit {
                logger.warn("Queue all unreplicated records")
                new QueueAllRecordsForFirstTimeReplication(injector.getInstance(ICayenneService)).runUpgrade()
                logger.warn("Queueing all unreplicated records finished")
            }
        }

    }


}
