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

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.PreferenceEnumDTO
import ish.oncourse.server.preference.UserPreferenceService
import ish.util.ImageHelper
import ish.util.ImageRequest

import static ish.oncourse.server.api.v1.function.export.PdfFunctions.toDbOverlay
import static ish.oncourse.server.api.v1.function.export.PdfFunctions.toRestOverlay
import static ish.oncourse.server.api.v1.function.export.PdfFunctions.validateAddOverlay
import ish.oncourse.server.api.v1.model.ReportOverlayDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.ReportOverlayApi
import static ish.oncourse.server.api.validation.EntityValidator.throwClientErrorException
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.ReportOverlay
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import javax.ws.rs.BadRequestException
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.util.stream.Collectors

@CompileStatic
class  ReportOverlayApiImpl implements ReportOverlayApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    void create(String fileName, File overlay) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateAddOverlay(fileName, overlay, context)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        toDbOverlay(context, fileName, overlay)

        context.commitChanges()
    }



    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        ReportOverlay report = SelectById.query(ReportOverlay, id)
                .selectOne(context)

        List<String> reportList = ObjectSelect.query(Report)
                .where(Report.BACKGROUND.eq(report))
                .select(context)
                .stream()
                .map({ currReport -> currReport.name })
                .collect(Collectors.toList())
        if(!reportList.isEmpty()) {
            throwClientErrorException("id", "Report overlay is set as background in reports: " +
                    String.join(",", reportList) + ".")
        }

        if (report) {
            context.deleteObject(report)
            context.commitChanges()
        } else {
            throw new BadRequestException("Report background with id: $id doesn't exist")
        }
    }

    @Override
    void update(String fileName, Long id, File overlay) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateAddOverlay(id, fileName, overlay, context)
        if (error) {
            throwClientErrorException(error)
        }
        ReportOverlay report = SelectById.query(ReportOverlay, id)
                .selectOne(context)
        if (!report) {
            throwClientErrorException("id", "Report overlay with id: $id doesn't exist")
        }

        toDbOverlay(report, context, fileName, overlay)

        context.commitChanges()
    }

    @Override
    ReportOverlayDTO get(Long id) {
        ReportOverlay report = SelectById.query(ReportOverlay, id)
                .selectOne(cayenneService.newContext)
        if (report) {
            return toRestOverlay(report)
        }
        return null
    }

    @Override
    List<byte[]> getOriginal(Long id) {
        ReportOverlay report = SelectById.query(ReportOverlay, id)
                .selectOne(cayenneService.newContext)
        return ImageHelper.generateOriginalHighQuality(report?.overlay).each {it -> Base64.getEncoder().encode(it)};
    }
}
