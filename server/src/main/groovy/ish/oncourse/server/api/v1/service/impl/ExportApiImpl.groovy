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
import ish.common.types.TaskResultType
import ish.oncourse.server.cluster.ClusteredExecutorManager
import ish.oncourse.server.cluster.TaskResult
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.types.OutputType
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.ExportTemplateApiService
import static ish.oncourse.server.api.v1.function.CommonFunctions.badRequest
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.getSelectedRecordIds
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.getSelectedRecords
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.checkPermissionToExportXMLAndCSV
import ish.oncourse.server.api.v1.function.export.TransformationIterable
import ish.oncourse.server.api.v1.model.ExportRequestDTO
import ish.oncourse.server.api.v1.service.ExportApi
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.ExportTemplate
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.export.ExportService
import ish.print.PrintTransformationsFactory
import ish.print.transformations.PrintTransformation
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Context
import java.util.concurrent.Callable

class ExportApiImpl implements ExportApi {

    private static Logger logger = LogManager.logger

    @Inject
    private ExportService exportService

    @Inject
    private ClusteredExecutorManager clusteredExecutorManager

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ExportTemplateApiService service

    @Inject
    private AqlService aqlService

    @Inject
    private IPermissionService permissionService

    @Context
    private HttpServletResponse response

    @Override
    String execute(String entityName, ExportRequestDTO request) {

        checkPermissionToExportXMLAndCSV(permissionService)

        ExportTemplate template = service.getEntityAndValidateExistence(cayenneService.newContext, request.template)
        Map<String, Object> variables = service.getVariablesMap(request.variables, template)
        PrintTransformation transformation = null
        if (template.entity != request.entityName) {
            transformation = PrintTransformationsFactory
                    .getPrintTransformationFor(request.entityName, template.entity, null)
            if (!transformation) {
                EntityValidator.throwClientErrorException("entity", "$template.name is not valid for $request.entityName records")
            }
        }

        return clusteredExecutorManager.submit(new Callable<TaskResult>() {
            @Override
            TaskResult call() throws Exception {
                ObjectContext context = cayenneService.newReadonlyContext

                Iterable<? extends CayenneDataObject> records
                if (transformation) {
                    List<Long> ids = getSelectedRecordIds(request.entityName, request.search, request.filter, request.tagGroups, request.sorting, aqlService, context)

                    records = new TransformationIterable<>(transformation, ids, context)
                } else {
                    records = getSelectedRecords(request.entityName, request.search, request.filter, request.tagGroups, request.sorting, aqlService, context)
                }

                TaskResult result = new TaskResult(TaskResultType.SUCCESS)
                result.setResultOutputType(template.outputType)
                result.setData(exportService.performExport(template, records, variables, request.exportToClipboard, request.createPreview).toString().bytes)
                return result
            }
        })
    }

    @Override
    byte[] get(String entityName, String processId) {
        try {
            TaskResult result = clusteredExecutorManager.getResult(processId)
            if(result.data != null)
                response.setContentType(result.getResultOutputType().mimeType)
            result.getData()
        } catch (Exception e) {
            logger.catching(e)
            throw badRequest(e.message)
        }
    }
}
