/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Document;
import ish.oncourse.server.document.DocumentService;
import ish.s3.AmazonS3Service;
import ish.util.LocalDateUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@DisallowConcurrentExecution
public class PermanentlyDeleteDocumentsJob implements Job {
    private static final Logger logger = LogManager.getLogger();

    private ICayenneService cayenneService;
    private DocumentService documentService;

    @Inject
    PermanentlyDeleteDocumentsJob(ICayenneService cayenneService, DocumentService documentService) {
        this.cayenneService = cayenneService;
        this.documentService = documentService;
    }

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        ObjectContext objectContext = cayenneService.getNewContext();
        AmazonS3Service s3Client = new AmazonS3Service(documentService);

        LocalDate searchDate = LocalDate.now().minus(30, ChronoUnit.DAYS);

        logger.debug("Will search documents which were modified before start of day {}", searchDate);

        List<Document> documents = ObjectSelect.query(Document.class)
                .where(Document.IS_REMOVED.eq(Boolean.TRUE))
                .and(Document.MODIFIED_ON.lt(LocalDateUtils.valueToDate(searchDate)))
                .prefetch(Document.VERSIONS.joint())
                .prefetch(Document.ATTACHMENT_RELATIONS.disjointById())
                .select(objectContext);

        if (documents.isEmpty()) {
            logger.debug("There are no documents which should be deleted.");
            return;
        }
        logger.debug("A number of documents which will be deleted is {}", documents.size());

        documents.forEach(document -> {
            s3Client.removeFile(document.getFileUUID());

            objectContext.deleteObjects(document.getAttachmentRelations());
            objectContext.commitChanges();
        });

        objectContext.deleteObjects(documents);
        objectContext.commitChanges();

        logger.debug("All documents removed succesfully");
    }
}
