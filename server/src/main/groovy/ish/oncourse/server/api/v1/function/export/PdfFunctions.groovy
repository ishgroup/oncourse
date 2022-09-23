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

package ish.oncourse.server.api.v1.function.export

import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.ReportOverlayDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.ReportOverlay
import ish.util.ImageHelper
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils


@CompileStatic
class PdfFunctions {

    static ReportOverlayDTO toRestOverlay(ReportOverlay reportOverlay) {
        new ReportOverlayDTO().with { overlay ->
            overlay.id = reportOverlay.id
            overlay.name = reportOverlay.name
            overlay.preview = ImageHelper.generatePdfPreview(reportOverlay.overlay)
            overlay
        }
    }

    static ReportOverlay toDbOverlay(ReportOverlay reportOverlay = null,  ObjectContext context, String fileName, File overlay) {
        if (!reportOverlay) {
            reportOverlay = context.newObject(ReportOverlay)
        }
        reportOverlay.name = fileName

        if (overlay != null && overlay.size() > 0) {
            reportOverlay.overlay = FileUtils.readFileToByteArray(overlay)
        }
        reportOverlay
    }

    static ValidationErrorDTO validateAddOverlay(Long id = null, String fileName, File overlay, ObjectContext context) {
        if (!overlay) {
            return new ValidationErrorDTO(null, 'data', 'Data not found.')
        } else if (FileUtils.readFileToByteArray(overlay).length > 5 * 1024 * 1024) { //file over 2Mb
            return new ValidationErrorDTO(null, 'data', 'Files over 2Mb cannot be used as print background.')
        }

        if (StringUtils.isBlank(fileName)) {
            return new ValidationErrorDTO(null, 'name', 'Name is required.')
        } else if (fileName.length() > 100) {
            return new ValidationErrorDTO(id?.toString(), 'name', 'Name cannot be more than 100 characters.')
        } else  {

            ReportOverlay dbOverlay = ObjectSelect.query(ReportOverlay).where(ReportOverlay.NAME.eq(fileName)).selectFirst(context)

            if (dbOverlay != null && (id == null || dbOverlay.id != id)) {
                return new ValidationErrorDTO(null, 'name', 'Background with the same name is already exists.')
            }
        }

        null
    }
}
