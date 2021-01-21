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
package ish.oncourse.server.upgrades;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Document;
import ish.oncourse.server.cayenne.DocumentVersion;
import ish.oncourse.server.document.DocumentService;
import ish.s3.AmazonS3Service;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

/**
 * Migration of BinaryData to S3. The upgrade will run on every server start up until there will be no
 * BinaryInfos without fileUUID set in the database.
 * <p/>
 * Upload process fetches all BinaryInfos without fileUUID and then spawns separate thread which uploads
 * data to S3 logging every successful and failed attempt.
 */
public class MigrateBinaryDataToS3  {

    private static final Logger logger = LogManager.getLogger();

    private DocumentService documentService;

    private boolean shouldRunNextTime = true;
    private ICayenneService cayenneService;

    public MigrateBinaryDataToS3(ICayenneService cayenneService, DocumentService documentService) {
        this.cayenneService = cayenneService;
        this.documentService = documentService;
    }

    protected void runUpgrade() throws Exception {

        // if S3 is not configured then skip upgrade for now, will try on the next server restart
        if (!documentService.isUsingExternalStorage()) {
            return;
        }

        final var s3Service = new AmazonS3Service(documentService);

        final ObjectContext context = cayenneService.getNewContext();

        final var recordsToProcess =
                context.select(SelectQuery.query(Document.class, Document.FILE_UUID.isNull()));

        if (!recordsToProcess.isEmpty()) {
            var s3UploadThread = new Thread(() -> {

                for (var document : recordsToProcess) {
                    try {
                        var versions = document.getVersions();

                        Ordering.orderList(versions, Collections.singletonList(DocumentVersion.TIMESTAMP.asc()));

                        for (var documentVersion : versions) {

                            if (documentVersion.getVersionId() == null) {

                                var fileUuid = document.getFileUUID();
                                String versionId;

                                if (fileUuid == null) {
                                    var uploadResult = s3Service.putFile(
                                            documentVersion.getAttachmentData().getContent(),
                                            documentVersion.getFileName(),
                                            document.getWebVisibility());

                                    fileUuid = uploadResult.getUuid();
                                    versionId = uploadResult.getVersionId();

                                    document.setFileUUID(fileUuid);
                                } else {
                                    versionId = s3Service.putFile(
                                            fileUuid,
                                            documentVersion.getFileName(),
                                            documentVersion.getAttachmentData().getContent(),
                                            document.getWebVisibility());
                                }

                                logger.warn("Attachment '{}' was successfully uploaded to S3 under fileUUID '{}'",
                                        documentVersion.getFileName(), fileUuid);


                                documentVersion.setVersionId(versionId);
                            }
                        }

                        context.commitChanges();
                    } catch (Exception e) {
                        logger.error("Error uploading attachment '{}' to S3", document.getName(), e);
                        context.rollbackChangesLocally();
                    }
                }
            });

            s3UploadThread.start();
        } else {
            // all records are processed - no need to run this upgrade any more
            this.shouldRunNextTime = false;
        }
    }

}
