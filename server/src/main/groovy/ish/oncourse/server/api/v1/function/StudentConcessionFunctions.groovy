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

import ish.oncourse.server.api.v1.model.StudentConcessionDTO
import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.StudentConcession
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class StudentConcessionFunctions {
    static StudentConcessionDTO toRestConcession(StudentConcession concession) {
        new StudentConcessionDTO().with { dto ->
            dto.id = concession.id
            dto.number = concession.concessionNumber
            dto.expiresOn = LocalDateUtils.dateToValue(concession.expiresOn)
            dto.type = ConcessionTypeFunctions.toRestConcessionType(concession.concessionType)
            dto
        }
    }

    static void updateStudentConcessions(Contact relatedContact, List<StudentConcessionDTO> concessions) {
        ObjectContext context = relatedContact.context

        List<StudentConcession> toDelete = relatedContact.student.concessions.findAll{!(it.id in concessions*.id)}
        context.deleteObjects(toDelete)

        concessions.each {c ->
            StudentConcession dbConcession = relatedContact.student.concessions.find{ it.id == c.id }
            if (dbConcession == null) {
                dbConcession = context.newObject(StudentConcession)
            }
            dbConcession.concessionNumber = c.number
            dbConcession.expiresOn = LocalDateUtils.valueToDate(c.expiresOn)
            dbConcession.concessionType = SelectById.query(ConcessionType, c.type.id).selectOne(context)
            dbConcession.student = relatedContact.student
            dbConcession
        }
    }
}
