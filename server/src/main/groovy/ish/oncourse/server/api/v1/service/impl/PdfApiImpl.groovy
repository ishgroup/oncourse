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
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.service.ReportApiService
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.v1.function.CommonFunctions.badRequest
import ish.oncourse.server.api.v1.function.export.CertificatePrintFilter
import ish.oncourse.server.api.v1.function.export.CertificatePrintPreProcessor
import ish.oncourse.server.api.v1.function.export.PrintFilter
import ish.oncourse.server.api.v1.function.export.PrintPreProcessor
import ish.oncourse.server.api.v1.function.export.PrintRequestBuilder
import ish.oncourse.server.api.v1.model.PrintRequestDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.PdfApi
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.print.PrintWorker
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.users.SystemUserService
import ish.print.PrintRequest
import ish.print.PrintResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.ForbiddenException
import javax.ws.rs.core.Context
import java.time.LocalDateTime
import java.util.concurrent.Callable

@CompileStatic
class PdfApiImpl implements PdfApi {

    private static Logger logger = LogManager.logger

    private final Map<String, ? extends PrintFilter> printFilters = [
            (Certificate.class.simpleName)  : new CertificatePrintFilter()
    ]

    private final Map<String, ? extends PrintPreProcessor> preProcessors = [
            (Certificate.class.simpleName)  : new CertificatePrintPreProcessor()
    ]

    @Inject
    private ICayenneService cayenneService

    @Inject
    private ExecutorManager executorManager

    @Inject
    private DocumentService documentService

    @Inject
    private AqlService aqlService

    @Inject
    private ReportApiService reportService

    @Inject
    private IPermissionService permissionService

    @Inject
    private SystemUserService systemUserService

    @Context
    private HttpServletResponse response

    @Override
    String execute(String entityName, PrintRequestDTO printRequest) {
        def context = cayenneService.newContext
        PrintRequestBuilder builder = PrintRequestBuilder.valueOf(
                context,
                aqlService,
                reportService,
                printRequest.report,
                printRequest.sorting,
                entityName,
                printRequest.overlay,
                printRequest.variables,
                printRequest.search,
                printRequest.filter,
                printRequest.tagGroups,
                printRequest.createPreview,
                printFilters.get(entityName),
                preProcessors.get(entityName),
                permissionService,
                systemUserService.currentUser
        )

        executorManager.submit(new Callable<Object>() {
            @Override
            Object call() throws Exception {
                PrintRequest request
                try {
                    request = builder.build()
                } catch (ForbiddenException e) {
                    PrintResult result = new PrintResult(PrintResult.ResultType.FAILED)
                    result.error = (e.response.entity as ValidationErrorDTO).errorMessage
                    return result
                }

                PrintWorker worker = new PrintWorker(request, cayenneService, documentService)
                worker.run()
                worker.result
            }
        })
    }

    @Override
    byte[] get(String entityName, String processId) {
        try {
            PrintResult result = executorManager.getResult(processId) as PrintResult
            addContentDispositionHeader(result.reportName)
            result.result
        } catch (Exception e) {
            logger.catching(e)
            throw badRequest(e.message)
        }
    }


    void addContentDispositionHeader(String reportName) {
        response.addHeader('Content-Disposition', "inline;filename=\"$reportName-${LocalDateTime.now().format('yyMMddHHmmss')}.pdf\"")
    }
}
