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

import ish.oncourse.server.cayenne.Tutor

class TutorFunctions {

    static void mergeTutorToManyRelationshipsToA(Tutor a, Tutor b, List<String> mergeableProperties) {
        for (String property : mergeableProperties) {
            EntityFunctions.moveToManyEntityRelationshipToA(b, a, property)
        }
        Date current = new Date()
        // put all tutor attendances into replication queue to correct pass merge operation on willow side
        a.courseClassRoles.each{tr -> tr.sessionsTutors.each{ta -> ta.setModifiedOn(current)}}
    }
}
