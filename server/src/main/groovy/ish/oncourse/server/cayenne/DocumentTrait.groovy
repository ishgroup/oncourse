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

package ish.oncourse.server.cayenne

import ish.oncourse.server.document.DocumentService
import ish.s3.AmazonS3Service

trait DocumentTrait {
    private static volatile AmazonS3Service s3ServiceSingleton = null

    abstract List<AttachmentRelation> getAttachmentRelations()

    abstract String getLink()

    Integer getAttachedRecordsCount() {
        return attachmentRelations.size()
    }

    /**
     * Returns S3Service as a singleton
     * Uses double checked locking & volatile singleton model
     * @return a S3Service instance
     */
    def AmazonS3Service getS3ServiceInstance(DocumentService documentService) {
        AmazonS3Service s3Service = s3ServiceSingleton
        if (s3Service == null) {
            synchronized (DocumentTrait.class) {
                s3Service = s3ServiceSingleton
                if (s3Service == null) {
                    if (documentService.usingExternalStorage) {
                        s3ServiceSingleton = s3Service = new AmazonS3Service(documentService)
                    }
                }
            }
        }
        return s3Service
    }
}
