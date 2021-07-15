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

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.cayenne.*
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.SiteFunctions.toRestSiteMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.*
import static org.apache.commons.lang3.StringUtils.*

@CompileStatic
class WaitingListFunctions {

    static WaitingListDTO toRestWaitingList(WaitingList dbWaitingList) {
        new WaitingListDTO().with { wl ->
            wl.id = dbWaitingList.id
            wl.privateNotes = dbWaitingList.notes
            wl.studentNotes = dbWaitingList.studentNotes
            wl.studentCount = dbWaitingList.studentCount
            wl.contactId = dbWaitingList.student.contact.id
            wl.studentName = dbWaitingList.student.contact.with { it.getFullName() }
            wl.courseId = dbWaitingList.course.id
            wl.courseName = dbWaitingList.course.with { "$it.name $it.code" }
            wl.tags = dbWaitingList.tags.collect { toRestTagMinimized(it) }
            wl.sites = dbWaitingList.sites.collect { toRestSiteMinimized(it)}
            wl.customFields = dbWaitingList.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            wl.createdOn = LocalDateUtils.dateToTimeValue(dbWaitingList.createdOn)
            wl.modifiedOn = LocalDateUtils.dateToTimeValue(dbWaitingList.modifiedOn)
            wl
        }
    }

    static WaitingList toDbWaitingList(WaitingListDTO waitingListDTO, WaitingList waitingList,
                                                                   ObjectContext context) {
        waitingList.notes = trimToNull(waitingListDTO.privateNotes)
        waitingList.studentCount = waitingListDTO.studentCount
        waitingList.student = getRecordById(context, Contact, waitingListDTO.contactId).student
        waitingList.course = getRecordById(context, Course, waitingListDTO.courseId)
        updateTags(waitingList, waitingList.taggingRelations, waitingListDTO.tags*.id, WaitingListTagRelation, context)
        updateSites(context, waitingList, waitingListDTO.sites)
        updateCustomFields(context, waitingList, waitingListDTO.customFields, WaitingListCustomField)
        waitingList
    }

    private static void updateSites(ObjectContext context, WaitingList dbWaitingList, List<SiteDTO> sites) {
        List<Long> sitesToSave = sites*.id ?: [] as List<Long>
        context.deleteObjects(dbWaitingList.waitingListSites.findAll { !sitesToSave.contains(it.site.id) })
        sites.findAll { !dbWaitingList.sites*.id.contains(it.id) }
                .collect { getRecordById(context, Site, it.id)}
                .each {
                    WaitingListSite wls = context.newObject(WaitingListSite)
                    wls.setWaitingList(dbWaitingList)
                    wls.setSite(it)
                }
    }

    static ValidationErrorDTO validateForSave(WaitingListDTO waitingListDTO, ObjectContext context, Long dbWListId = null) {
        if (!waitingListDTO.contactId) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'studentContact', 'Student is required.')
        } else if (!getRecordById(context, Contact, waitingListDTO.contactId)?.student) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'studentContact', 'Contact is not a student.')
        }

        if (!waitingListDTO.studentCount) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'studentCount', 'Number of students is required.')
        } else if (waitingListDTO.studentCount <= 0) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'studentCount', 'Number of students must be positive value.')
        }

        if (!waitingListDTO.courseId) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'course', 'Course is required.')
        } else {
            getRecordById(context, Course, waitingListDTO.courseId)
        }

        if (trimToEmpty(waitingListDTO.studentNotes).length() > 32000) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'studentNotes', 'Student notes can not be more than 32000 chars.')
        }

        if (trimToEmpty(waitingListDTO.privateNotes).length() > 32000) {
            return new ValidationErrorDTO(dbWListId?.toString(), 'privateNotes', 'Private notes can not be more than 32000 chars.')
        }

        validateRelationsForSave(WaitingList, context, waitingListDTO.tags*.id,  TaggableClasses.WAITING_LIST) ?:
                CustomFieldFunctions.getCustomFieldTypes(context, WaitingList.class.simpleName)
                        .findAll { it.isMandatory }
                        .collect { it -> isBlank(waitingListDTO.customFields[it.key]) ? new ValidationErrorDTO(dbWListId?.toString(), 'customFields', "$it.name is required.") : null }
                        .find() ?:
                waitingListDTO.sites.findAll { it -> !SelectById.query(Site, it.id).selectOne(context) }
                        .collect{ SiteDTO it, idx -> new ValidationErrorDTO(dbWListId?.toString(), "sites[$idx].id", "Site with id=${it.id} not found.") }[0]
    }
}
