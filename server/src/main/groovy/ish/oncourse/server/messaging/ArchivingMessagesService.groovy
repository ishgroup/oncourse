/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import com.google.inject.Inject
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import ish.common.types.AttachmentInfoVisibility
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.document.ArchiveService
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.util.DbConnectionUtils
import ish.oncourse.types.AuditAction
import ish.s3.AmazonS3Service
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.file.Files
import java.nio.file.Path
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.GZIPOutputStream

class ArchivingMessagesService {
    private static final String TEMP_ARCHIVES_DIRECTORY = "temp-messages-archives"
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")
    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd")

    private static AtomicBoolean archiveInProgress = new AtomicBoolean(false)
    private static final Logger logger = LogManager.logger

    @Inject
    ICayenneService cayenneService

    @Inject
    LicenseService licenseService

    @Inject
    PreferenceController preferenceController

    @Inject
    AuditService auditService

    @Inject
    private EntityValidator validator

    private DocumentService documentService

    private AmazonS3Service s3Service

    @Inject
    ArchivingMessagesService(ArchiveService documentService) {
        this.s3Service = new AmazonS3Service(documentService)
        this.documentService = documentService
    }

    void archiveMessages(LocalDate localDateToArchive) {
        def dateToArchive = localDateToArchive.toDate()

        while (archiveInProgress.get() == Boolean.TRUE)
            continue
        archiveInProgress.set(true)

        preferenceController.setDateMessageExpectedBeforeArchived(dateToArchive)
        Message firstMessage = null
        try {
            firstMessage = firstByDateMessagesBefore(dateToArchive)
            if (!firstMessage) {
                validator.throwClientErrorException("archiveDate", "There are no messages before date $dateToArchive")
            }
        } catch (Exception e) {
            archiveInProgress.set(false)
            preferenceController.setDateMessageExpectedBeforeArchived(null)
            throw e
        }

        try {
            logger.warn("Full start time of archiving: " + System.currentTimeMillis())

            String fileName = buildCompressedCsvForExcludeIntervalAndGetFilename(firstMessage.createdOn, dateToArchive)
            uploadArchive(fileName)
            new File("$TEMP_ARCHIVES_DIRECTORY/$fileName").delete()
            removeMessagesCreatedInInterval(dateToArchive)

            preferenceController.setDateMessageBeforeArchived(dateToArchive)
            auditService.submit(firstMessage, AuditAction.MESSAGES_ARCHIVING_COMPLETED, "Messages before $dateToArchive archived successfully")
            logger.warn("Full end time of archiving: " + System.currentTimeMillis())
        } catch (Exception e) {
            logger.catching(e)
            auditService.submit(firstMessage, AuditAction.MESSAGES_ARCHIVING_FAILED, e.getMessage())
        } finally {
            archiveInProgress.set(false)
            preferenceController.setDateMessageExpectedBeforeArchived(null)
        }
    }


    private Message firstByDateMessagesBefore(Date date) {
        return ObjectSelect.query(Message)
                .where(Message.CREATED_ON.lt(date))
                .orderBy(Message.CREATED_ON.name)
                .selectFirst(cayenneService.newContext)
    }

    private String buildCompressedCsvForExcludeIntervalAndGetFilename(Date firstDate, Date endDate) {
        def collegeName = licenseService.getCollege_key()

        logger.warn("Start time of archiving for messages before $endDate: " + System.currentTimeMillis())

        def fileName = getFreeFileName(collegeName, firstDate, endDate)
        def directoryPath = Path.of(TEMP_ARCHIVES_DIRECTORY)
        if (!Files.exists(directoryPath))
            Files.createDirectory(directoryPath)

        def filePath = "$TEMP_ARCHIVES_DIRECTORY/$fileName"
        def file = new File(filePath)

        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(file))
        PrintWriter printWriter = new PrintWriter(gzipOutputStream)
        ICSVWriter csvWriter = new CSVWriterBuilder(printWriter)
                .build()

        def dataSource = cayenneService.getDataSource()
        def writeMessagesToGz = { Statement statement ->
            def startId = 0L
            ResultSet resultSet = null
            int iterations = 0
            int lastFetchSize = 0
            while (resultSet == null || lastFetchSize > 0) {
                resultSet = statement.executeQuery("Select * from Message m WHERE m.id>${startId}" +
                        " and m.createdOn < '${SQL_DATE_FORMAT.format(endDate)}' ORDER BY m.id LIMIT 500")
                csvWriter.writeAll(resultSet, resultSet == null)
                resultSet.last()
                lastFetchSize = resultSet.getRow()
                if (lastFetchSize > 0) {
                    startId = resultSet.getLong("id")
                }
                iterations++
            }
            logger.warn("End time of archiving messages before $endDate: " + System.currentTimeMillis())
        }

        try {
            DbConnectionUtils.executeWithClose(writeMessagesToGz, dataSource)
        } finally {
            gzipOutputStream.close()
        }
        return fileName
    }

    private String getFreeFileName(String collegeName, Date startDate, Date endDate) {
        def formattedStart = FILE_NAME_DATE_FORMAT.format(startDate)
        def formattedEnd = FILE_NAME_DATE_FORMAT.format(endDate)
        String fileName = "$collegeName/$formattedStart-$formattedEnd" + ".csv.gz"

        def file = new File(TEMP_ARCHIVES_DIRECTORY + "/" + fileName)
        if (file.exists())
            file.delete()

        if (s3Service.fileExists(fileName)) {
            validator.throwClientErrorException("archiveDate", "File with this interval already exists in s3 storage. Contact ish support")

        }
        return fileName
    }

    private String uploadArchive(String fileName) {
        def file = new File("$TEMP_ARCHIVES_DIRECTORY/$fileName")
        def inputStream = new FileInputStream(file)
        s3Service.putFileFromStream(fileName, fileName, inputStream, AttachmentInfoVisibility.PRIVATE, (int) file.length())
    }

    private void removeMessagesCreatedInInterval(Date endDate) {
        SQLTemplate sqlTemplate = new SQLTemplate(Message.class, "Delete from Message where createdOn < '${SQL_DATE_FORMAT.format(endDate)}'")
        cayenneService.newContext.performGenericQuery(sqlTemplate)
    }

}
