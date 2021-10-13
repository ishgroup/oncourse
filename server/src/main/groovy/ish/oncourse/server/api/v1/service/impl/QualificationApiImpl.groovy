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

package ish.oncourse.server.api.v1.service.impl

import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.QualificationDTO
import ish.oncourse.server.api.v1.service.QualificationApi
import ish.oncourse.server.cayenne.Qualification
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import javax.inject.Inject

import static ish.oncourse.server.api.function.EntityFunctions.*
import static ish.oncourse.server.api.v1.function.QualificationFunctions.*

class QualificationApiImpl implements QualificationApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    void create(QualificationDTO qualification) {

        ObjectContext context = cayenneService.newContext
        Qualification entity = context.newObject(Qualification)

        checkForBadRequest(validateModelRequiredFields(qualification, context))

        toDbQualification(qualification, entity)
        context.commitChanges()
    }

    @Override
    QualificationDTO get(Long id) {
        checkForBadRequest(validateIdParam(id))

        toQualificationModel(SelectById.query(Qualification, id)
                .selectOne(cayenneService.newContext))
    }

    @Override
    void update(Long id, QualificationDTO model) {

        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Qualification entity =
                SelectById.query(Qualification, id).selectOne(context)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateModelRequiredFields(model, context, entity.isCustom, entity.id))
        checkForBadRequest(validateValues(model, entity))

        toDbQualification(model, entity, entity.isCustom)
        context.commitChanges()
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Qualification entity =
                SelectById.query(Qualification, id).selectOne(context)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        context.deleteObjects(entity)
        context.commitChanges()
    }
}
