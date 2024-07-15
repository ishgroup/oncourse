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
import ish.oncourse.server.cayenne.Archive
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.AuditService
import ish.oncourse.server.util.DbConnectionUtils
import ish.oncourse.types.AuditAction
import ish.s3.AmazonS3Service
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLTemplate
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.file.Files
import java.nio.file.Path
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.GZIPOutputStream

class ArchivingMessagesService {
    private static final long MIN_YEARS_BEFORE_TO_ARCHIVE = 3
    private static final String TEMP_ARCHIVES_DIRECTORY = "temp-messages-archives"
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")
    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("MM_yyyy")

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
    ArchivingMessagesService(DocumentService documentService) {
        this.s3Service = new AmazonS3Service(documentService)
        this.documentService = documentService
    }

    void archiveMessages(LocalDate localDateToArchive) {
        def yearsBetween = ChronoUnit.YEARS.between(localDateToArchive, LocalDate.now())
        if (yearsBetween < MIN_YEARS_BEFORE_TO_ARCHIVE) {
            validator.throwClientErrorException("archiveDate", "You cannot archive all messages before date, less then $MIN_YEARS_BEFORE_TO_ARCHIVE year ago")
        }

        def dateToArchive = localDateToArchive.toDate()

        while(archiveInProgress.get() == Boolean.TRUE)
            continue
        archiveInProgress.set(true)

        preferenceController.setDateMessageExpectedBeforeArchived(dateToArchive)
        Message firstMessage = null
        try {
            firstMessage = firstByDateMessagesBefore(dateToArchive)
            if(!firstMessage){
                validator.throwClientErrorException("archiveDate", "There are no messages before date $dateToArchive")
            }
        } catch (Exception e) {
            archiveInProgress.set(false)
            preferenceController.setDateMessageExpectedBeforeArchived(null)
            throw e
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor()
        executorService.execute({ runnable ->
            try {
                logger.warn("Full start time of archiving: " + System.currentTimeMillis())

                String fileName = buildCompressedCsvForExcludeIntervalAndGetFilename(firstMessage.createdOn, dateToArchive)
                String uuid = uploadArchiveAndGetKey(fileName)
                new File("$TEMP_ARCHIVES_DIRECTORY/$fileName").delete()
                removeMessagesCreatedInInterval(dateToArchive)

                preferenceController.setDateMessageBeforeArchived(dateToArchive)

                def archive = cayenneService.newContext.newObject(Archive)
                archive.createdOn = new Date()
                archive.fileName = fileName
                archive.uniqueCode = uuid
                archive.dateFrom = firstMessage.createdOn
                archive.dateTo = dateToArchive
                archive.context.commitChanges()

                auditService.submit(archive, AuditAction.MESSAGES_ARCHIVING_COMPLETED, "Messages before $dateToArchive archived successfully")
                logger.warn("Full end time of archiving: " + System.currentTimeMillis())
            } catch (Exception e) {
                auditService.submit(firstMessage, AuditAction.MESSAGES_ARCHIVING_FAILED, e.getMessage())
            } finally {
                archiveInProgress.set(false)
                preferenceController.setDateMessageExpectedBeforeArchived(null)
            }
        })
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
        String fileName = "$collegeName-$formattedStart-$formattedEnd" + ".csv.gz"

        def file = new File(TEMP_ARCHIVES_DIRECTORY + "/" + fileName)
        if(file.exists())
            file.delete()

        if (s3Service.fileExists(fileName)) {
            validator.throwClientErrorException("archiveDate", "File with this interval already exists in s3 storage. Contact ish support")

        }
        return fileName
    }

    private String uploadArchiveAndGetKey(String fileName) {
        def file = new File("$TEMP_ARCHIVES_DIRECTORY/$fileName")
        def inputStream = new FileInputStream(file)
        String key = UUID.randomUUID().toString()
        s3Service.putFileFromStream(key, fileName, inputStream, AttachmentInfoVisibility.PRIVATE, (int) file.length())
        return key
    }

    private void removeMessagesCreatedInInterval(Date endDate) {
        SQLTemplate sqlTemplate = new SQLTemplate(Message.class, "Delete from Message where createdOn < '${SQL_DATE_FORMAT.format(endDate)}'")
        cayenneService.newContext.performGenericQuery(sqlTemplate)
    }

    String getLink(Long archiveId){
        def archive = SelectById.query(Archive, archiveId).selectOne(cayenneService.newReadonlyContext)
        return s3Service.getFileUrl(archive.uniqueCode, AttachmentInfoVisibility.PRIVATE)
    }
}
