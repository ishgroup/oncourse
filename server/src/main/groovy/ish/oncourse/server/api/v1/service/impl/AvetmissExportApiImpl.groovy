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

package ish.oncourse.server.api.v1.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import com.google.inject.Inject
import groovy.json.JsonGenerator
import groovy.json.JsonSlurper
import ish.common.types.OutcomeStatus
import ish.common.types.TaskResultType
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.function.avetmiss.AvetmissExportPreviewBuilder
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.AvetmissExportApi
import ish.oncourse.server.cayenne.FundingUpload
import ish.oncourse.server.cayenne.FundingUploadOutcome
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cluster.ClusteredExecutorManager
import ish.oncourse.server.cluster.TaskResult
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.oncourse.server.export.avetmiss8.Avetmiss8ExportRunner
import ish.oncourse.server.users.SystemUserService
import ish.oncourse.types.FundingStatus
import ish.oncourse.types.OutputType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AvetmissExportApiImpl implements AvetmissExportApi {

    private static final Logger logger = LogManager.getLogger()

    @Inject
    private ICayenneService cayenneService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private CertificateService certificateService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private ClusteredExecutorManager executorManager
    
    private ExecutorService executorService = Executors.newSingleThreadExecutor()

    @Override
    String exportAvetmiss8(AvetmissExportRequestDTO requestParameters) {
        Long systemUserId = systemUserService.currentUser.id

        logger.warn("AVETMISS export started. Parameters: {}", requestParameters.toString())

        executorManager.submit(new Callable<TaskResult>() {
            @Override
            TaskResult call() throws Exception {
                String settings = null
                AvetmissExportResult result
                ExportJurisdiction exportJurisdiction = ExportJurisdiction.values().find { it.displayName == requestParameters.settings.flavour.toString() }

                result = Avetmiss8ExportRunner.export(
                        cayenneService.newContext,
                        certificateService,
                        preferenceController,
                        exportJurisdiction,
                        requestParameters.defaultStatus ? OutcomeStatus.STATUS_ASSESSABLE_CONTINUING_ENROLMENT : null,
                        LocalDate.now().plusDays(7),
                        requestParameters.settings?.outcomesEnd ?: LocalDate.now(),
                        requestParameters.settings.noAssessment,
                        requestParameters.ids
                )

                if (requestParameters.settings.enrolmentIds.empty && requestParameters.settings.classIds.empty) {
                    // save the settings so that this can be 'run again'
                    settings = new ObjectMapper().writeValueAsString(requestParameters.settings)
                }

                Long start = System.currentTimeMillis()
                createFundingUploadRecords(systemUserId, requestParameters.ids, settings)

                Long end = start - System.currentTimeMillis()
                logger.info("Create uploads in {}ms", end)

                String zipName = 'avetmiss8.zip'
                def stream = new ByteArrayOutputStream()
                ZipOutputStream zipFile = new ZipOutputStream(stream)
                result.files.each { filename, data ->
                    ZipEntry entry = new ZipEntry(filename)
                    def databytes = data.toByteArray()
                    entry.size = databytes.length
                    zipFile.putNextEntry(entry)
                    zipFile.write(databytes)
                    zipFile.closeEntry()
                }
                zipFile.close()

                TaskResult output = new TaskResult(TaskResultType.SUCCESS);
                output.setName(zipName)
                output.setData(stream.toByteArray())
                output
            }
        })
    }

    /**
     * Using a non-replication context, we reduce chance of a replication queue hanging.
     * During the export process, huge amount of data can be handle.
     * <p>
     * Willow's side doesn't store information about the FundingUpload&Outcome relationship
     * cause it don't provide useful information. However, getting into the replication queue clogs it up.
     * <p>
     * Data in the queue is processed sequentially (queue without priority).
     * Thus, data falling into a clogged queue will not be processed until the queue processes all the data before them.
     *
     * @param systemUserId
     * @param outcomeIds
     * @param settings
     */
    void createFundingUploadRecords(Long systemUserId, List<Long> outcomeIds, String settings) {
        ObjectContext context = cayenneService.newContext
        SystemUser systemUser = SelectById.query(SystemUser, systemUserId).selectOne(context)
        FundingUpload fundingUpload = context.newObject(FundingUpload)

        fundingUpload.setOutcomeCount((long) outcomeIds.size())
        fundingUpload.setStatus(FundingStatus.EXPORTED)
        fundingUpload.setSystemUser(systemUser)
        fundingUpload.setSettings(settings)
        context.commitChanges()

        executorService.submit {
            context = cayenneService.newNonReplicatingContext
            fundingUpload = context.localObject(fundingUpload)
            outcomeIds.each { id ->
                Outcome outcome = SelectById.query(Outcome, id).selectOne(context)
                FundingUploadOutcome fundingUploadOutcome = context.newObject(FundingUploadOutcome)
                fundingUploadOutcome.fundingUpload = fundingUpload
                fundingUploadOutcome.outcome = outcome
                fundingUploadOutcome.startDate = outcome.startDate
                fundingUploadOutcome.endDate = outcome.endDate
                context.commitChanges()
            }
        }
    }

    /**
     * client has requested a preview with outcome and enrolments in groups
     * @param settings
     * @return
     */
    @Override
    String findExportOutcomes(AvetmissExportSettingsDTO settings) {
        validateAvetmissInputSettings(settings)

        logger.warn("AVETMISS search outcomes started. Parameters: {}", settings.toString())

        executorManager.submit(new Callable<TaskResult>() {
            @Override
            TaskResult call() throws Exception {
                Set<Outcome> outcomes =  Avetmiss8ExportRunner.getOutcomes(settings.classIds,
                        settings.enrolmentIds,
                        settings.fundingContracts,
                        settings.includeLinkedOutcomes,
                        settings.outcomesEnd,
                        settings.outcomesStart,
                        settings.fee.collect {it.dbType},
                        settings.flavour.dbType,
                        cayenneService.newContext)

                def generator = new JsonGenerator.Options()
                        .addConverter(AvetmissExportOutcomeStatusDTO){ value -> value.toString()}
                        .addConverter(AvetmissExportTypeDTO){ value -> value.toString()}
                        .addConverter(AvetmissExportOutcomeCategoryDTO){ value -> value.toString()}
                        .build()
                def json = generator.toJson(new AvetmissExportPreviewBuilder(outcomes).build())

                TaskResult output = new TaskResult(TaskResultType.SUCCESS)
                output.setResultOutputType(OutputType.JSON)
                output.setData(json.bytes)
                output
            }
        })
    }

    @Override
    File getExport(String processId) {
        try {
            // TODO: change API to send byte[] directly
            def result = executorManager.getResult(processId) as TaskResult
            if(result.data == null){
                return null
            }
            def file = new File(result.getName())
            Files.write(result.data, file)
            file
        } catch (Exception e) {
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, e.message)).build())
        }
    }

    @Override
    List<AvetmissExportOutcomeDTO> getExportOutcomes(String processId) {
        try {
            TaskResult result = executorManager.getResult(processId) as TaskResult
            def jsonSlurper = new JsonSlurper()
            while(result.type == TaskResultType.IN_PROGRESS){
                Thread.sleep(10000)
                result = executorManager.getResult(processId) as TaskResult
            }
            assert result.type != TaskResultType.IN_PROGRESS
            jsonSlurper.parse(result.data) as List<AvetmissExportOutcomeDTO>
        } catch (Exception e) {
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, e.message)).build())
        }
    }

    private static void validateAvetmissInputSettings(AvetmissExportSettingsDTO settings) {
        if ((settings.getFlavour() == null) || (AvetmissExportFlavourDTO.fromValue(settings.getFlavour().name()))) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(null, null, "Invalid 'flavour' value.")).build())
        }
    }

}
