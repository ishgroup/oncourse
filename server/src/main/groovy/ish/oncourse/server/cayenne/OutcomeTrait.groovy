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

import ish.oncourse.function.GetContactFullName
import org.apache.commons.lang3.StringUtils

trait OutcomeTrait {

    abstract Enrolment getEnrolment()

    abstract PriorLearning getPriorLearning()

    String getStudentName() {
        Contact contact = (enrolment) ? enrolment?.student?.contact : priorLearning?.student?.contact
        if (contact) {
            return GetContactFullName.valueOf(contact, true).get()
        } else {
            return StringUtils.EMPTY
        }
    }
}
