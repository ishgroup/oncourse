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

package ish.oncourse.server.cayenne

import ish.oncourse.server.cayenne.glue._CourseClassCustomField

class CourseClassCustomField extends _CourseClassCustomField {

//    System custom field name to create and save 'minimum sessions to complete' value only for hybrid classes, this custom field isn't showed on Custom Fields list page.
//    It will be used to show complete statuses for enrolments of hybrid classes.
    public static String MINIMUM_SESSIONS_TO_COMPLETE = "minimumSessionsToComplete"

    @Override
    boolean isAsyncReplicationAllowed() {
        return customFieldType.isAsyncReplicationAllowed()
    }

    @Override
    void setRelatedObject(ExpandableTrait relatedObject) {
        super.setRelatedObject((CourseClass) relatedObject)
    }
}
