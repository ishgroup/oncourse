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


import ish.oncourse.server.api.dao.WaitingListDao
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.api.v1.model.WaitingListDTO
import ish.oncourse.server.cayenne.*
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.SiteFunctions.toRestSiteMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static ish.oncourse.server.api.v1.function.TagFunctions.validateRelationsForSave
import static org.apache.commons.lang3.StringUtils.*

class WaitingListApiService extends TaggableApiService<WaitingListDTO, WaitingList, WaitingListDao> {
    @Override
    Class<WaitingList> getPersistentClass() {
        WaitingList
    }

    @Override
    WaitingListDTO toRestModel(WaitingList dbWaitingList) {
        new WaitingListDTO().with { wl ->
            wl.id = dbWaitingList.id
            wl.privateNotes = dbWaitingList.notes
            wl.studentNotes = dbWaitingList.studentNotes
            wl.studentCount = dbWaitingList.studentCount
            wl.contactId = dbWaitingList.student.contact.id
            wl.studentName = dbWaitingList.student.contact.with { it.getFullName() }
            wl.courseId = dbWaitingList.course.id
            wl.courseName = dbWaitingList.course.with { "$it.name $it.code" }
            wl.tags = dbWaitingList.allTags.collect { it.id }
            wl.sites = dbWaitingList.sites.collect { toRestSiteMinimized(it)}
            wl.customFields = dbWaitingList.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            wl.createdOn = LocalDateUtils.dateToTimeValue(dbWaitingList.createdOn)
            wl.modifiedOn = LocalDateUtils.dateToTimeValue(dbWaitingList.modifiedOn)
            wl
        }
    }

    @Override
    WaitingList toCayenneModel(WaitingListDTO waitingListDTO, WaitingList waitingList) {
        waitingList.notes = trimToNull(waitingListDTO.privateNotes)
        waitingList.studentCount = waitingListDTO.studentCount
        waitingList.student = getRecordById(waitingList.context, Contact, waitingListDTO.contactId).student
        waitingList.course = getRecordById(waitingList.context, Course, waitingListDTO.courseId)
        updateTags(waitingList, waitingList.taggingRelations, waitingListDTO.tags, WaitingListTagRelation, waitingList.context)
        updateSites(waitingList.context, waitingList, waitingListDTO.sites)
        updateCustomFields(waitingList.context, waitingList, waitingListDTO.customFields, WaitingListCustomField)
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


    @Override
    void validateModelBeforeSave(WaitingListDTO waitingListDTO, ObjectContext context, Long id) {
        if (!waitingListDTO.contactId) {
            validator.throwClientErrorException(id, 'studentContact', 'Student is required.')
        }

        def contact = getRecordById(context, Contact, waitingListDTO.contactId)
        if (!contact?.student) {
            validator.throwClientErrorException(id, 'studentContact', 'Contact is not a student.')
        }

        if (!waitingListDTO.studentCount) {
            validator.throwClientErrorException(id, 'studentCount', 'Number of students is required.')
        } else if (waitingListDTO.studentCount <= 0) {
            validator.throwClientErrorException(id, 'studentCount', 'Number of students must be positive value.')
        }

        if (!waitingListDTO.courseId) {
            validator.throwClientErrorException(id, 'course', 'Course is required.')
        }

        Course course = getRecordById(context, Course, waitingListDTO.courseId)

        def waitingListsFilter = WaitingList.STUDENT.eq(contact.student)
                .andExp(WaitingList.COURSE.eq(course))

        if(waitingListDTO.id)
            waitingListsFilter = waitingListsFilter.andExp(WaitingList.ID.ne(waitingListDTO.id))

        def sameWaitingList = ObjectSelect.query(WaitingList)
                .where(waitingListsFilter)
                .selectFirst(context)

        if(sameWaitingList)
            validator.throwClientErrorException(id, "student", "Waiting list for this student and course already exists!")

        if (trimToEmpty(waitingListDTO.studentNotes).length() > 32000) {
            validator.throwClientErrorException(id, 'studentNotes', 'Student notes can not be more than 32000 chars.')
        }

        if (trimToEmpty(waitingListDTO.privateNotes).length() > 32000) {
            validator.throwClientErrorException(id, 'privateNotes', 'Private notes can not be more than 32000 chars.')
        }

        TagFunctions.validateTagForSave(WaitingList, context, waitingListDTO.tags)
                ?.with { validator.throwClientErrorException(it) }

        validateRelationsForSave(WaitingList, context, waitingListDTO.tags)
                ?.with { validator.throwClientErrorException(it) }


        CustomFieldFunctions.getCustomFieldTypes(context, WaitingList.class.simpleName)
                        .findAll { it.isMandatory }
                        .collect { it -> isBlank(waitingListDTO.customFields[it.key]) ? validator.throwClientErrorException(id, 'customFields', "$it.name is required.") : null }
                        .find() ?:
                        waitingListDTO.sites.findAll { it -> !SelectById.query(Site, it.id).selectOne(context) }
                                .collect{ SiteDTO it, idx -> validator.throwClientErrorException(id, "sites[$idx].id", "Site with id=${it.id} not found.") }[0]
    }

    @Override
    void validateModelBeforeRemove(WaitingList cayenneModel) {

    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
