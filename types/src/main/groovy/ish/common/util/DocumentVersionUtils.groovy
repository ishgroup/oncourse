/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.util


import org.apache.logging.log4j.Logger

import static ish.common.util.ImageHelper.*

class DocumentVersionUtils {

    static void initVersionSizesAndThumbnail(DocumentVersionInterface version, byte[] content,
                                             String documentName, Logger logger) throws DocumentUploadException {
        if (isImage(content, version.mimeType)) {
            version.pixelWidth = imageWidth(content)
            version.pixelHeight = imageHeight(content)
            try {
                version.thumbnail = ThumbnailGenerator.generateForImg(content, version.mimeType)
            } catch (IOException e) {
                logger.warn("Attempted to process document with name $documentName as an image, but it wasn't.")
                logger.catching(e)
                throw new DocumentUploadException(e.message)
            }
        } else {
            try {
                switch (version.mimeType) {
                    case {it instanceof String && isDoc(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForDoc(content)
                        break
                    case {it instanceof String && isExcel(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForExcel(content)
                        break
                    case {it instanceof String && isCsv(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForCsv(content)
                        break
                    case {it instanceof String && isText(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForText(content)
                        break
                    default:
                        version.thumbnail = generatePdfPreview(content)
                }
            } catch (NotActiveException e) {
                logger.warn("Attempted to process document with name $documentName failed. Angel can not generate privew")
                logger.catching(e)
                throw new DocumentUploadException(e.message)
            }
        }
    }
}
