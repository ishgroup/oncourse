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
import com.google.inject.Inject
import ish.common.types.OutcomeStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.entity.services.OutcomeService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.v1.function.avetmiss.AvetmissExportPreviewBuilder
import ish.oncourse.server.api.v1.model.AvetmissExportFlavourDTO
import ish.oncourse.server.api.v1.model.AvetmissExportOutcomeDTO
import ish.oncourse.server.api.v1.model.AvetmissExportRequestDTO
import ish.oncourse.server.api.v1.model.AvetmissExportSettingsDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.AvetmissExportApi
import ish.oncourse.server.cayenne.FundingUpload
import ish.oncourse.server.cayenne.FundingUploadOutcome
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.oncourse.server.export.avetmiss8.Avetmiss8ExportRunner
import ish.oncourse.server.users.SystemUserService
import ish.oncourse.types.FundingStatus
import ish.persistence.GetInExpression
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.tx.TransactionalOperation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AvetmissExportApiImpl implements AvetmissExportApi {

    private static final Logger logger = LogManager.getLogger()

    @Inject
    private ICayenneService cayenneService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private OutcomeService outcomeService

    @Inject
    private CertificateService certificateService

    @Inject
    private SystemUserService systemUserService

    @Inject
    private ExecutorManager executorManager

    private ExecutorService executorService = Executors.newSingleThreadExecutor()

    @Override
    String exportAvetmiss8(AvetmissExportRequestDTO requestParameters) {
        Long systemUserId = systemUserService.currentUser.id

        logger.warn("AVETMISS export started. Parameters: {}", requestParameters.toString())

        executorManager.submit(new Callable<File>() {
            @Override
            File call() throws Exception {
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
                ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(zipName))
                result.files.each { filename, data ->
                    ZipEntry entry = new ZipEntry(filename)
                    def databytes= data.toByteArray()
                    entry.size = databytes.length
                    zipFile.putNextEntry(entry)
                    zipFile.write(databytes)
                    zipFile.closeEntry()
                }
                zipFile.close()

                return new File(zipName)
            }
        })
    }



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
            context = cayenneService.newContext
            fundingUpload = context.localObject(fundingUpload)
            outcomeIds.each { id ->
                Outcome outcome = SelectById.query(Outcome, id).selectOne(cayenneService.newContext)
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

        executorManager.submit(new Callable<List<AvetmissExportOutcomeDTO>>() {
            @Override
            List<AvetmissExportOutcomeDTO> call() throws Exception {
                Set<Outcome> outcomes =  Avetmiss8ExportRunner.getOutcomes(settings.classIds,
                        settings.enrolmentIds,
                        settings.fundingContracts,
                        settings.includeLinkedOutcomes,
                        settings.outcomesEnd,
                        settings.outcomesStart,
                        settings.fee.collect {it.dbType},
                        settings.flavour.dbType,
                        cayenneService.newContext)
                return new AvetmissExportPreviewBuilder(outcomes).build()
            }
        })
    }

    @Override
    File getExport(String processId) {
        try {
            executorManager.getResult(processId) as File
        } catch (Exception e) {
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, e.message)).build())
        }
    }

    @Override
    List<AvetmissExportOutcomeDTO> getExportOutcomes(String processId) {
        try {
            executorManager.getResult(processId) as List<AvetmissExportOutcomeDTO>
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
