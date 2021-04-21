/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.GradingTypeDao
import ish.oncourse.server.api.v1.model.GradingEntryTypeDTO
import ish.oncourse.server.api.v1.model.GradingItemDTO
import ish.oncourse.server.api.v1.model.GradingTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.GradingItem
import ish.oncourse.server.cayenne.GradingType
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static org.apache.commons.lang3.StringUtils.isBlank

class GradingApiService extends EntityApiService<GradingTypeDTO, GradingType, GradingTypeDao> {

    @Override
    Class<GradingType> getPersistentClass() {
        return GradingType
    }

    @Override
    GradingTypeDTO toRestModel(GradingType cayenneModel) {
        return new GradingTypeDTO().with { dtoModel ->
            dtoModel.id = cayenneModel.id
            dtoModel.created = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dtoModel.modified = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dtoModel.name = cayenneModel.typeName
            dtoModel.minValue = cayenneModel.minValue
            dtoModel.maxValue = cayenneModel.maxValue
            dtoModel.entryType = GradingEntryTypeDTO.values()[0].fromDbType(cayenneModel.entryType)
            dtoModel.gradingItems = cayenneModel.gradingItems.collect { toRestGradingItem(it) }
            dtoModel
        }
    }

    static GradingItemDTO toRestGradingItem(GradingItem cayenneModel) {
        return new GradingItemDTO().with { dtoModel ->
            dtoModel.id = cayenneModel.id
            dtoModel.created = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dtoModel.modified = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dtoModel.name = cayenneModel.itemName
            dtoModel.lowerBound = cayenneModel.lowerBound
            dtoModel
        }
    }

    @Override
    GradingType toCayenneModel(GradingTypeDTO dtoModel, GradingType cayenneModel) {
        cayenneModel.typeName = dtoModel.name
        cayenneModel.minValue = dtoModel.minValue
        cayenneModel.maxValue = dtoModel.maxValue
        cayenneModel.entryType = dtoModel.entryType.dbType

        updateGradingItems(dtoModel.gradingItems, cayenneModel)

        return cayenneModel
    }

    void updateGradingItems(List<GradingItemDTO> dtoItems, GradingType gradingType) {
        ObjectContext context = gradingType.context
        List<GradingItem> cayenneItems = gradingType.gradingItems

        context.deleteObjects(cayenneItems.findAll { !(it.id in dtoItems*.id) })

        dtoItems.each { dtoItem ->
            GradingItem cayenneItem = dtoItem.id ?
                    cayenneItems.find { dtoItem.id == it.id } :
                    entityDao.newGradingItem(gradingType, context)
            toCayenneModelGradingItem(dtoItem, cayenneItem)
        }
    }

    static GradingItem toCayenneModelGradingItem(GradingItemDTO dtoModel, GradingItem cayenneModel) {
        cayenneModel.itemName = dtoModel.name
        cayenneModel.lowerBound = dtoModel.lowerBound
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(GradingTypeDTO dtoModel, ObjectContext context, Long id) {
        if (isBlank(dtoModel.name)) {
            validator.throwClientErrorException(GradingType.TYPE_NAME.name, 'The grading type must have a name.')
        } else if (dtoModel.name.length() > 128) {
            validator.throwClientErrorException(GradingType.TYPE_NAME.name, 'The grading type name must be less than 128 characters.')
        }

        if (dtoModel.minValue == null) {
            validator.throwClientErrorException(GradingType.MIN_VALUE.name, 'You must specify min value of grading type.')
        }

        if (dtoModel.maxValue == null) {
            validator.throwClientErrorException(GradingType.MAX_VALUE.name, 'You must specify max value of grading type.')
        }

        if (dtoModel.entryType == null) {
            validator.throwClientErrorException(GradingType.ENTRY_TYPE.name, 'You must choose the grading entry type')
        }

        if (GradingEntryTypeDTO.NAME == dtoModel.entryType && dtoModel.gradingItems.size() == 0) {
            validator.throwClientErrorException(GradingType.ENTRY_TYPE.name, 'You must specify at least one grading entry.')
        }

        dtoModel.gradingItems.each { item ->
            if (isBlank(item.name)) {
                validator.throwClientErrorException(GradingItem.ITEM_NAME.name, 'You do not specify a name of the entry.')
            }

            if (item.lowerBound == null) {
                validator.throwClientErrorException(GradingItem.LOWER_BOUND.name, 'You do not specify a lower bound of the entry.')
            }
        }
    }

    @Override
    void validateModelBeforeRemove(GradingType cayenneModel) {
        if (!cayenneModel.assessments.empty) {
            validator.throwClientErrorException(GradingType.ASSESSMENTS.name, 'This grading type is assigned to at least one assessment. Unassign from it before removing.')
        }
    }

    static void validateDuplicates(List<GradingTypeDTO> gradingTypes) {
        List<String> names =  gradingTypes*.name.flatten() as List<String>
        List<String> duplicates = names.findAll { names.count(it) > 1 }.unique()

        if (!duplicates.empty) {
            EntityValidator.throwClientErrorException("name", "Grading type should has unique name!")
        }
    }

    List<GradingType> getGradingTypes() {
        return entityDao.getGradingTypes(cayenneService.newContext)
    }
}
