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

import javax.inject.Inject
import ish.common.types.DataType
import ish.imports.ImportResult
import ish.oncourse.server.api.dao.ImportDao
import ish.oncourse.server.api.model.ImportModel
import ish.oncourse.server.api.v1.model.ExecuteImportRequestDTO
import ish.oncourse.server.api.v1.model.ImportModelDTO
import ish.oncourse.server.cayenne.Import
import ish.oncourse.server.concurrent.ExecutorManager
import ish.oncourse.server.configs.AutomationModel
import ish.oncourse.server.imports.ImportService
import org.apache.cayenne.ObjectContext

import java.util.concurrent.Callable
import java.util.function.BiConsumer

import static ish.oncourse.server.upgrades.DataPopulationUtils.fillImportWithCommonFields

class ImportApiService extends AutomationApiService<ImportModelDTO, Import, ImportDao> {

    @Inject
     private ImportService importService

    @Inject
    private ExecutorManager executorManager

    @Override
    Class<Import> getPersistentClass() {
        return Import
    }

    @Override
    protected ImportModelDTO createDto() {
        return new ImportModelDTO()
    }

    @Override
    protected BiConsumer<Import, Map<String, Object>> getFillPropertiesFunction() {
        return new BiConsumer<Import, Map<String, Object>>() {
            @Override
            void accept(Import anImport, Map<String, Object> stringObjectMap) {
                fillImportWithCommonFields(anImport, stringObjectMap)
            }
        }
    }

    @Override
    protected AutomationModel getConfigsModelOf(Import entity) {
        return new ish.oncourse.server.configs.ImportModel(entity)
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

        return executorManager.submit(new Callable() {

            @Override
            Object call() throws Exception {
                ImportResult importResult = importService.performImport(model, context)
                return importResult
            }
        })
    }
}
