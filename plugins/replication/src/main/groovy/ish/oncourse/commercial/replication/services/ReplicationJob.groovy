/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.services

import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.CompileStatic
import ish.oncourse.commercial.replication.ReplicationPlugin
import ish.oncourse.commercial.replication.handler.ReplicationHandler
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.license.LicenseService
import ish.oncourse.webservices.soap.v23.AuthFailure
import ish.oncourse.webservices.soap.v23.ReplicationPortType
import org.apache.cxf.binding.soap.SoapFault
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quartz.*

import java.net.ConnectException

/**
 */
@DisallowConcurrentExecution
@CompileStatic
class ReplicationJob implements Job {

    private static final Logger logger = LogManager.getLogger()
    static final String LAST_REPLICATION_TIME = "last.replication.time"
    private final ReplicationHandler inboundHandler
    private final ReplicationHandler outboundHandler
    private final ReplicationHandler instructionHandler
    private final ISoapPortLocator soapPortLocator
    private final PreferenceController pref
    private final IAngelQueueService queueService
    private final LicenseService licenseService

    /**
     * Default constructor being used by AngelJobFactory.
     *
     * @param inboundHandler     handler for replication from willow to angel
     * @param outboundHandler    angel to willow replication handler
     * @param instructionHandler willow to angel instructions handler
     * @param pref               preferences controller
     */
    @Inject
    ReplicationJob(@Named("Inbound") ReplicationHandler inboundHandler,
                          @Named("Outbound") ReplicationHandler outboundHandler,
                          @Named("Instruction") ReplicationHandler instructionHandler,
                          ISoapPortLocator soapPortLocator, PreferenceController pref,
                          IAngelQueueService queueService, LicenseService licenseService) {
        super()
        this.inboundHandler = inboundHandler
        this.outboundHandler = outboundHandler
        this.instructionHandler = instructionHandler
        this.soapPortLocator = soapPortLocator
        this.pref = pref
        this.queueService = queueService
        this.licenseService = licenseService
    }

    private void saveLastReplicationTime(JobExecutionContext context) {
        try {
            context.getScheduler().getContext().put(LAST_REPLICATION_TIME, System.currentTimeMillis())
        } catch (SchedulerException e) {
            logger.error("Failed to put data into SchedulerContext", e)
        }
    }

    private long getLastReplicationTime(JobExecutionContext context) {
        try {
            return context.getScheduler().getContext().getLong(LAST_REPLICATION_TIME)
        } catch (Throwable e) {
            logger.error("Failed to get data from JobExecutionContext", e)
            return Long.MIN_VALUE
        }
    }

    private void executeReplication() throws JobExecutionException {
        def securityCode = this.licenseService.getSecurity_key()
        if (securityCode == null) {
            return
        }
        def lastKey = this.pref.getCommunicationKey()
        
        logger.info("Authenticate using current communicationKey:{}", lastKey)

        ReplicationPortType authPort
        Long newKey
        try {
            authPort = this.soapPortLocator.replicationPort()

            newKey = authPort.authenticate(securityCode, lastKey)

            logger.info("Received new communicationKey:{}", newKey)

            this.pref.setCommunicationKey(newKey)

            logger.info("new communicationKey:{} was saved to preferences", newKey)

            this.instructionHandler.replicate()
            // outbound replication should go first because angel's data can be changed before we get the latest changes from willow (task 14214)
            this.outboundHandler.replicate()
            this.inboundHandler.replicate()
            // the second outbound executeReplication is necessary to send changes which were done on angel as result of previous inbound replication
            // (Attendances,Outcomes)
            this.outboundHandler.replicate()

        } catch (AuthFailure e) {
            def message = String.format("Authentication failed with message from server: %s", e.getMessage())
            logger.info(message, e)
            throw new JobExecutionException(message, e)
        } catch (SoapFault sf) {
            def message = "Soap request failed."
            logger.info(message, sf)
            throw new JobExecutionException(message, sf, true)
        } catch (Exception e) {
            def message = "Generic replication exception."
            if (e.getCause() instanceof ConnectException) {
                logger.info(message, e)
            } else {
                logger.error(message, e)
            }
        } finally {
            this.soapPortLocator.resetReplicationPort()
        }
    }

    /**
     * @see Job#execute(JobExecutionContext)
     */
    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        /**
         * if replication is enabled we execute it in two cases:
         * 1. the replication queue is not empty
         * 2. the last replication was more then 5 minutes ago
         */

        def diff = Math.abs(System.currentTimeMillis() - getLastReplicationTime(context))
        def needReplicate = (diff >= ReplicationPlugin.REPLICATION_INTERVAL || queueService.getNumberOfTransactions() > 0)
    

        if (needReplicate)
        {
            executeReplication()
            saveLastReplicationTime(context)
        }
    }
}
