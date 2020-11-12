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

package ish.oncourse.server.upgrades.liquibase.change

import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.ApplicationCustomField
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.CourseClassCustomField
import ish.oncourse.server.cayenne.CourseCustomField
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.EnrolmentCustomField
import ish.oncourse.server.cayenne.SurveyCustomField
import ish.oncourse.server.cayenne.WaitingListCustomField
import ish.oncourse.server.db.SchemaUpdateService
import liquibase.database.Database
import liquibase.exception.CustomChangeException
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

class DeleteAllCustomFieldDuplicates extends IshTaskChange {

    private static final Closure<Integer> customFieldComparator = { CustomField a, CustomField b ->
        if (a.modifiedOn == b.modifiedOn) {
            if ( (a.value == null && b.value == null) || (a.value != null && b.value != null) ) {
                return 0
            } else if (a.value != null) {
                return 1
            } else {
                return -1
            }

        } else {
            return a.modifiedOn.compareTo(b.modifiedOn)
        }
    }

    private final static String DUPLICATE_CUSTOM_FIELD_SQL = "select cf.entityIdentifier , cf.customFieldTypeId , cf.foreignId\n" +
            "from CustomField cf \n" +
            "group by entityIdentifier , customFieldTypeId , foreignId \n" +
            "HAVING COUNT(cf.id) > 1"

    @Override
    void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService

        ObjectContext context = cayenneService.newContext

        SQLSelect.dataRowQuery(DUPLICATE_CUSTOM_FIELD_SQL).select(context).each { row ->
            String entityName = row.get("entityIdentifier")
            Long customFieldTypeId = row.get("customFieldTypeId") as Long
            Long entityId = row.get("foreignId") as Long

            List<CustomField> duplicates = new ArrayList<>()
            switch (entityName){
                case 'Application':
                    duplicates.addAll(ObjectSelect.query(ApplicationCustomField.class)
                            .where(ApplicationCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(ApplicationCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'Contact':
                    duplicates.addAll(ObjectSelect.query(ContactCustomField.class)
                            .where(ContactCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(ContactCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'Course':
                    duplicates.addAll(ObjectSelect.query(CourseCustomField.class)
                            .where(CourseCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(CourseCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'CourseClass':
                    duplicates.addAll(ObjectSelect.query(CourseClassCustomField.class)
                            .where(CourseClassCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(CourseClassCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'Enrolment':
                    duplicates.addAll(ObjectSelect.query(EnrolmentCustomField.class)
                            .where(EnrolmentCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(EnrolmentCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'Survey':
                    duplicates.addAll(ObjectSelect.query(SurveyCustomField.class)
                            .where(SurveyCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(SurveyCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                case 'WaitingList':
                    duplicates.addAll(ObjectSelect.query(WaitingListCustomField.class)
                            .where(WaitingListCustomField.RELATED_OBJECT.dot(Contact.ID).eq(entityId))
                            .and(WaitingListCustomField.CUSTOM_FIELD_TYPE.dot(CustomFieldType.ID).eq(customFieldTypeId))
                            .select(context))
                    break
                default:
                    return
            }
            if (!duplicates.empty) {
                context.deleteObjects(duplicates.sort(customFieldComparator).subList(0, duplicates.size() - 1))
                context.commitChanges()
            }
        }
    }
}
