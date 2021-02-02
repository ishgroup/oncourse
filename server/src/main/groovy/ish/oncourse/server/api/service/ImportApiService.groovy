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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.imports.ImportResult
import ish.oncourse.server.api.dao.ImportDao
import ish.oncourse.server.api.model.ImportModel
import ish.oncourse.server.api.v1.model.ExecuteImportRequestDTO
import ish.oncourse.server.api.v1.model.ImportModelDTO
import ish.oncourse.server.cayenne.Import
import ish.oncourse.server.cluster.ClusteredExecutorManager
import ish.oncourse.server.imports.ImportService
import ish.common.types.DataType
import org.apache.cayenne.ObjectContext

import java.util.concurrent.Callable

class ImportApiService extends AutomationApiService<ImportModelDTO, Import, ImportDao> {

    @Inject
    private ImportService importService

    @Inject
    private ClusteredExecutorManager executorManager

    @Override
    Class<Import> getPersistentClass() {
        return Import
    }

    @Override
    protected ImportModelDTO createDto() {
        return new ImportModelDTO()
    }

    @Override
    ImportModelDTO toRestModel(Import dbImport) {
        return super.toRestModel(dbImport) as ImportModelDTO
    }

    @Override
    Import toCayenneModel(ImportModelDTO importDTO, Import dbImport) {
        return super.toCayenneModel(importDTO, dbImport) as Import
    }

    @Override
    void validateModelBeforeSave(ImportModelDTO dto, ObjectContext context, Long id) {
        super.validateModelBeforeSave(dto, context, id, false, true)
    }

    @Override
    void validateModelBeforeRemove(Import dbimport) {
        super.validateModelBeforeRemove(dbimport)
    }

    Import updateInternal(ImportModelDTO importDTO) {
        Import dbImport = super.updateInternal(importDTO) as Import
        dbImport.getContext().commitChanges()
        return dbImport
    }

    String execute(ExecuteImportRequestDTO request, List<File> files){
        ObjectContext context = cayenneService.getNewContext()
        Import dbImport = getEntityAndValidateExistence(context, request.importScriptId)

        ImportModel model = new ImportModel().with { model ->
            model.dbImport = dbImport
            model.importData = getVariablesMap(request.variables, dbImport)
            model
        }

        dbImport.variables.findAll { it.dataType == DataType.FILE }.sort { it.id }.eachWithIndex { var, i ->
            model.importData[var.name] = files[i].bytes
        }

        executorManager.submit(new Callable<ImportResult>() {
            @Override
            ImportResult call() throws Exception {
                importService.performImport(model, context)
            }
        })
    }
}
