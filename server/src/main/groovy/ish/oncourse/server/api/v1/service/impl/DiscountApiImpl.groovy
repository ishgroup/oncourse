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

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import static ish.oncourse.server.api.v1.function.DiscountFunctions.toDbDiscount
import static ish.oncourse.server.api.v1.function.DiscountFunctions.toRestDiscount
import static ish.oncourse.server.api.v1.function.DiscountFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.DiscountFunctions.validateForSave
import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.service.DiscountApi
import ish.oncourse.server.cayenne.Discount
import org.apache.cayenne.ObjectContext

class DiscountApiImpl implements DiscountApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    void create(DiscountDTO discount) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(discount, context))

        Discount newDiscount = context.newObject(Discount)
        toDbDiscount(discount, newDiscount, context)

        context.commitChanges()
    }

    @Override
    DiscountDTO get(Long id) {
        toRestDiscount(getRecordById(cayenneService.newContext, Discount, id))
    }

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Discount entity = getRecordById(context, Discount, id)

        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    void update(Long id, DiscountDTO discount) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        Discount dbDiscount = getRecordById(context, Discount, id)
        checkForBadRequest(validateEntityExistence(id, dbDiscount))
        checkForBadRequest(validateForSave(discount, context, id))

        toDbDiscount(discount, dbDiscount, context)
        context.commitChanges()
    }
}
