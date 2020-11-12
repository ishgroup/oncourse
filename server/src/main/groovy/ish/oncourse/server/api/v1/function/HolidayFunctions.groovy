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

package ish.oncourse.server.api.v1.function

import ish.common.types.SessionRepetitionType
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.RepeatEndEnumDTO
import ish.oncourse.server.api.v1.model.RepeatEnumDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.UnavailableRule
import ish.oncourse.server.cayenne.UnavailableRuleRelation
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

class HolidayFunctions {

    static ValidationErrorDTO validateForDelete(ObjectContext context, String id) {
        if (!StringUtils.isNumeric(id)) {
            return new ValidationErrorDTO(id, 'id', "Holiday id '$id' is incorrect. It must contain of only numbers.")
        }

        UnavailableRule holiday = getHolidayById(context, Long.valueOf(id))

        if (!holiday) {
            return new ValidationErrorDTO(id, 'id', "Holiday is not exist.")
        }
        if (holiday.ruleRelation) {
            return new ValidationErrorDTO(id, 'id', "Holiday $holiday.id is not valid.")
        }
    }

    static ValidationErrorDTO validateFoUpdate(ObjectContext context, HolidayDTO holiday, boolean needToValidateRelation) {

        if (holiday.id) {
            UnavailableRule dbHoliday = getHolidayById(context, Long.valueOf(holiday.id))
            if (!dbHoliday) {
                return new ValidationErrorDTO(holiday.id, 'id', "Holiday $holiday.id is not exist.")
            }

            if (needToValidateRelation && dbHoliday.ruleRelation) {
                return new ValidationErrorDTO(holiday.id, 'id', "Holiday $holiday.id is not valid.")
            }
        }

        if (holiday.startDate == null && holiday.endDate == null && holiday.startDateTime == null && holiday.endDateTime == null) {
            return new ValidationErrorDTO(holiday.id, 'id', 'Time or date interval of holiday required.')
        }

        if (holiday.startDate != null && holiday.endDate != null && holiday.startDateTime != null && holiday.endDateTime != null) {
            return new ValidationErrorDTO(holiday.id, 'id', 'Only time interval or date interval of holiday expected.')
        }

        if ((holiday.startDate != null && holiday.endDate == null && holiday.startDateTime == null && holiday.endDateTime == null) ||
                (holiday.endDate != null && holiday.startDate == null && holiday.startDateTime == null && holiday.endDateTime == null)) {
            return new ValidationErrorDTO(holiday.id, 'id', 'Date interval should be provided by start and end dates.')
        }

        if ((holiday.startDateTime != null && holiday.endDateTime == null && holiday.startDate == null && holiday.endDate == null) ||
                (holiday.endDateTime != null && holiday.startDateTime == null && holiday.startDate == null && holiday.endDate == null)) {
            return new ValidationErrorDTO(holiday.id, 'id', 'Time interval should be provided by start and end time.')
        }


        if (holiday.startDate != null && holiday.startDate.isAfter(holiday.endDate)) {
            return new ValidationErrorDTO(holiday.id, 'endDate', "Holiday end date must be after start date.")
        }
        if (holiday.startDateTime != null && holiday.startDateTime.isAfter(holiday.endDateTime)) {
            return new ValidationErrorDTO(holiday.id, 'endDate', "Holiday end time must be after start time.")
        }

        if (!holiday.repeat) {
            return new ValidationErrorDTO(holiday.id, 'repeat', "Holiday repeat type must be specified.")
        }


        if (holiday.repeat != RepeatEnumDTO.NONE) {
            if (!holiday.repeatEnd) {
                return new ValidationErrorDTO(holiday.id, 'repeatEnd', "Holiday end repeat type must be specified.")
            }
            switch (holiday.repeatEnd) {
                case RepeatEndEnumDTO.NEVER:
                    break
                case RepeatEndEnumDTO.AFTER:
                    if (!holiday.repeatEndAfter || holiday.repeatEndAfter < 0) {
                        return new ValidationErrorDTO(holiday.id, 'repeatEndAfter', "Times count must be specified as positive number.")
                    }
                    break
                case RepeatEndEnumDTO.ONDATE:
                    if (holiday.repeatOn == null) {
                        return new ValidationErrorDTO(holiday.id, 'repeatOn', "On date must be specified.")
                    }
                    break
            }

        }
        return null

    }

    static UnavailableRule getHolidayById(ObjectContext context, Long id) {
        return SelectById.query(UnavailableRule, id)
                .prefetch(UnavailableRule.RULE_RELATION.joint())
                .selectOne(context)
    }

    static UnavailableRule toDbHoliday(ObjectContext context, HolidayDTO holiday) {

        UnavailableRule dbHoliday = holiday.id ? getHolidayById(context, Long.valueOf(holiday.id)) : context.newObject(UnavailableRule)
        dbHoliday.explanation = holiday.description

        if (holiday.startDate != null) {
            dbHoliday.startDateTime = LocalDateUtils.valueToDate(holiday.startDate)
            dbHoliday.endDateTime = LocalDateUtils.valueToDate(holiday.endDate)
            dbHoliday.allDay = true
        } else {
            dbHoliday.startDateTime = LocalDateUtils.timeValueToDate(holiday.startDateTime)
            dbHoliday.endDateTime = LocalDateUtils.timeValueToDate(holiday.endDateTime)
            dbHoliday.allDay = false
        }
        dbHoliday.recurrenceFrequency = SessionRepetitionType.valueOf(holiday.repeat.name()+'_CHOICE')
        if (dbHoliday.recurrenceFrequency != SessionRepetitionType.NONE_CHOICE) {
            dbHoliday.recurrenceInterval = 1
            switch (holiday.repeatEnd) {
                case RepeatEndEnumDTO.NEVER:
                    dbHoliday.repetitionCount = null
                    dbHoliday.untilDateTime = null
                    break
                case RepeatEndEnumDTO.AFTER:
                    dbHoliday.repetitionCount = holiday.repeatEndAfter.intValue()
                    dbHoliday.untilDateTime = null
                    break
                case RepeatEndEnumDTO.ONDATE:
                    dbHoliday.repetitionCount = null
                    dbHoliday.untilDateTime = LocalDateUtils.valueToDate(holiday.repeatOn)
                    break
            }
        } else {
            dbHoliday.recurrenceInterval = null
            dbHoliday.repetitionCount = null
            dbHoliday.untilDateTime = null
        }
        dbHoliday
    }

    static HolidayDTO toRestHoliday(UnavailableRule dbHoliday) {
        new HolidayDTO().with { holiday ->
            holiday.id = dbHoliday.id.toString()
            holiday.created =  LocalDateUtils.dateToTimeValue(dbHoliday.createdOn)
            holiday.modified =  LocalDateUtils.dateToTimeValue(dbHoliday.modifiedOn)
            holiday.description = dbHoliday.explanation
            if (dbHoliday.allDay) {
                holiday.startDate = LocalDateUtils.dateToValue(dbHoliday.startDateTime)
                holiday.endDate = LocalDateUtils.dateToValue(dbHoliday.endDateTime)
            } else {
                holiday.startDateTime = LocalDateUtils.dateToTimeValue(dbHoliday.startDateTime)
                holiday.endDateTime = LocalDateUtils.dateToTimeValue(dbHoliday.endDateTime)
            }
            holiday.repeat = RepeatEnumDTO.fromValue(dbHoliday.recurrenceFrequency.displayName.toLowerCase())
            if (holiday.repeat != RepeatEnumDTO.NONE) {
                if (dbHoliday.repetitionCount) {
                    holiday.repeatEnd = RepeatEndEnumDTO.AFTER
                    holiday.repeatEndAfter = new BigDecimal(dbHoliday.repetitionCount)
                } else if (dbHoliday.untilDateTime) {
                    holiday.repeatEnd = RepeatEndEnumDTO.ONDATE
                    holiday.repeatOn = LocalDateUtils.dateToValue(dbHoliday.untilDateTime)
                } else {
                    holiday.repeatEnd = RepeatEndEnumDTO.NEVER
                }
            }

            holiday
        }
    }

    static void updateAvailabilityRules(CayenneDataObject relatedObject, List<UnavailableRule> dbRules, List<HolidayDTO> rules, Class<? extends UnavailableRuleRelation> relationClass) {
        List<Long> rulesToSave = rules*.id.findAll().collect{Long.valueOf(it as String)}
        ObjectContext context = relatedObject.context

        dbRules.findAll { !rulesToSave.contains(it.id) }.each { context.deleteObjects(it.ruleRelation, it) }

        rules.collect { toDbHoliday(context, it) }.findAll { !it.ruleRelation }.each { rule ->
            context.newObject(relationClass).with { relation ->
                relation.rule = rule
                relation.relatedObject = relatedObject
                relation
            }
        }
    }
}
