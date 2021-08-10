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

import ish.oncourse.aql.AqlService
import ish.oncourse.server.api.service.ReportApiService
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.getSelectedRecords
import ish.oncourse.server.api.v1.model.SortingDTO
import ish.oncourse.server.api.v1.model.TagGroupDTO
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.ReportOverlay
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.security.api.IPermissionService
import ish.print.AdditionalParameters
import static ish.print.AdditionalParameters.BOOLEAN_FLAG
import static ish.print.AdditionalParameters.DATERANGE_FROM
import static ish.print.AdditionalParameters.DATERANGE_TO
import static ish.print.AdditionalParameters.LOCALDATERANGE_FROM
import static ish.print.AdditionalParameters.LOCALDATERANGE_TO
import static ish.print.AdditionalParameters.PRINT_QR_CODE
import ish.print.PrintRequest
import ish.print.PrintTransformationsFactory
import ish.print.transformations.PrintTransformation
import static ish.util.LocalDateUtils.stringToTimeValue
import static ish.util.LocalDateUtils.stringToValue
import static ish.util.LocalDateUtils.timeValueToDate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import java.util.stream.Collectors

class PrintRequestBuilder {

    private ObjectContext context
    private AqlService aqlService
    private ReportApiService reportService

    private Long reportId
    private String search
    private String filter
    private List<TagGroupDTO> tagGroups
    private List<SortingDTO> sorting
    private String entityName
    private Long overlayId
    private Map<String,String> variables
    private IPermissionService permissionService
    private SystemUser user

    private PrintFilter printFilter
    private PrintPreProcessor preProcessor

    private boolean createPreview = false

    private PrintRequestBuilder() {

    }

    static PrintRequestBuilder valueOf(ObjectContext context, AqlService aqlService, ReportApiService reportService,
                                       Long reportId, List<SortingDTO> sorting, String entityName, Long overlayId,
                                       Map<String,String> variables, String search, String filter, List<TagGroupDTO> tagGroups, boolean createPreview, PrintFilter printFilter,
                                       PrintPreProcessor preProcessor, IPermissionService permissionService, SystemUser user) {
        new PrintRequestBuilder().with { builder ->
            builder.context = context
            builder.aqlService = aqlService
            builder.reportService = reportService
            builder.reportId = reportId
            builder.sorting = sorting
            builder.entityName = entityName
            builder.overlayId = overlayId
            builder.variables = variables
            builder.search = search
            builder.filter = filter
            builder.tagGroups = tagGroups
            builder.printFilter = printFilter
            builder.preProcessor = preProcessor
            builder.createPreview = createPreview
            builder.permissionService = permissionService
            builder.user = user
            builder
        }
    }

    PrintRequest build() {
        Report report = SelectById.query(Report, reportId)
                .selectOne(context)

        List<? extends CayenneDataObject> records = new ArrayList<>()

        records.addAll(getSelectedRecords(entityName, search, filter, tagGroups, sorting, aqlService, context))
        if (printFilter != null) {
            records = records.stream()
                    .filter({ entity -> printFilter.apply(entity, permissionService, user) })
                    .collect(Collectors.toList())
        }

        PrintRequest request = new PrintRequest()
        request.records = records
        request.entity = records[0].entityName
        request.reportCode = report.keyCode
        request.createPreview = createPreview

        if (overlayId) {
            ReportOverlay overlay = SelectById.query(ReportOverlay, overlayId).selectOne(context)
            report.setBackground(overlay)
            request.background = overlay?.name
        } else {
            report.setBackground(null)
        }
        context.commitChanges()

        // call entity preprocessing for every entity
        if (preProcessor != null) {
            for (CayenneDataObject dataObject : records) {
                preProcessor.preProcessEntity(dataObject)
            }
            context.commitChanges()
        }

        request.addParameters(parseSystemVariables(variables.findAll { it.key in AdditionalParameters.NAMES }))
        request.addBindings(reportService.getVariablesMap(variables.findAll { !(it.key in AdditionalParameters.NAMES) }, report))
        request.addBindings(report.options.collectEntries {opt -> [opt.name, opt.objectValue]})

        PrintTransformation transformation = PrintTransformationsFactory
                .getPrintTransformationFor(request.entity, report.entity, report.keyCode)

        if (transformation) {
            request.addPrintTransformation(request.entity, transformation)
        }

        request
    }

    private static Map<String, Object> parseSystemVariables(Map<String, String> systemVars) {


        Map<String, Object> map = new HashMap<>()

        map.put(PRINT_QR_CODE.toString(), Boolean.TRUE.toString() == systemVars[PRINT_QR_CODE.toString()])
        map.put(BOOLEAN_FLAG.toString(), Boolean.TRUE.toString() == systemVars[BOOLEAN_FLAG.toString()])

        String dateTo = systemVars[LOCALDATERANGE_TO.toString()]
        if (dateTo) {
            map.put(LOCALDATERANGE_TO.toString(), stringToValue(dateTo))
        }
        String dateTimeTo = systemVars[DATERANGE_TO.toString()]
        if (dateTimeTo) {
            map.put(DATERANGE_TO.toString(), timeValueToDate(stringToTimeValue(dateTimeTo)))
        }

        String dateFrom = systemVars[LOCALDATERANGE_FROM.toString()]
        if (dateFrom) {
            map.put(LOCALDATERANGE_FROM.toString(), stringToValue(dateFrom))
        }
        String dateTimeFrom = systemVars[DATERANGE_FROM.toString()]
        if (dateTimeFrom) {
            map.put(DATERANGE_FROM.toString(), timeValueToDate(stringToTimeValue(dateTimeFrom)))
        }

        map
    }
}
