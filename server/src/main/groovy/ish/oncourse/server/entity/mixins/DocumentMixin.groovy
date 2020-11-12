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

package ish.oncourse.server.entity.mixins

import ish.oncourse.API
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.s3.S3Service
import org.apache.cayenne.query.Ordering

class DocumentMixin {

    private static volatile S3Service s3ServiceSingleton = null

    @API
    /**
     * Generates URL to access file.
     *
     * for 'Public' documents it is static address taht can be acceassable in any time.
     * for non 'Public' documents generate signed link which can be used in next 10 minutes only
     */
    static String getLink(Document self) {
        def s3Service = getS3ServiceInstance()
        return s3Service != null ? s3Service.getFileUrl(self.getFileUUID(), self.getWebVisibility()) : ""
    }

    static DocumentVersion getCurrentVersion(Document self) {
        List<DocumentVersion> versions = self.getVersions()

        Ordering.orderList(versions, Collections.singletonList(DocumentVersion.TIMESTAMP.desc()))

        return versions.get(0)
    }

    /**
     * Returns S3Service as a singleton
     * Uses double checked locking & volatile singleton model
     * @return a S3Service instance
     */
    private static S3Service getS3ServiceInstance() {
        S3Service s3Service = s3ServiceSingleton
        if (s3Service == null) {
            synchronized (DocumentMixin.class) {
                s3Service = s3ServiceSingleton
                if (s3Service == null) {
                    if (PreferenceController.getController().isUsingExternalStorage()) {
                        s3ServiceSingleton = s3Service = new S3Service(
                                PreferenceController.getController().getStorageAccessId(),
                                PreferenceController.getController().getStorageAccessKey(),
                                PreferenceController.getController().getStorageBucketName())
                    }
                }
            }
        }
        return s3Service
    }
}
