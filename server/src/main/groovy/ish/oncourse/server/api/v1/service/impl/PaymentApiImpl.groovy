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

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.PayTypeDTO
import ish.oncourse.server.api.v1.model.PaymentMethodDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.PaymentApi
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentMethod
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.ZoneOffset

import static ish.common.types.PaymentType.CHEQUE
import static ish.common.types.PaymentType.CREDIT_CARD
import static ish.common.types.PaymentType.OTHER
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.SYSTEM_TYPES
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.updateType
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.validateData
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.validateForUpdate

@CompileStatic
class PaymentApiImpl implements PaymentApi {

    @Inject private ICayenneService cayenneService

    @Override
    List<PaymentMethodDTO> get() {
            List<PaymentMethodDTO> result = ObjectSelect.query(PaymentMethod)
                    .where(PaymentMethod.TYPE.in(CHEQUE, CREDIT_CARD, OTHER))
                    .prefetch(PaymentMethod.ACCOUNT.joint())
                    .prefetch(PaymentMethod.UNDEPOSITED_FUNDS_ACCOUNT.joint())
                    .select(cayenneService.newContext)
                    .collect { PaymentMethod dbType ->
                new PaymentMethodDTO().with { PaymentMethodDTO type ->
                    type.id = dbType.id
                    type.created =  dbType.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                    type.modified =  dbType.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                    type.name = dbType.name
                    type.type = PayTypeDTO.fromValue(dbType.type.displayName)
                    type.systemType = false
                    type.active = dbType.active
                    type.reconcilable = dbType.reconcilable
                    type.bankedAuto = dbType.bankedAutomatically
                    type.accountId = (dbType.account as Account).id
                    type.undepositAccountId = (dbType.undepositedFundsAccount as Account).id
                    type
                }
            }

            result += SYSTEM_TYPES
            return result
    }

    @Override
    void remove(String id) {
            if (!StringUtils.isNumeric(id)) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "Payment type is not exist")).build())
            }

            ObjectContext context = cayenneService.newContext
            PaymentMethod method =  SelectById.query(PaymentMethod, id).selectOne(context)

            ValidationErrorDTO error = validateForDelete(method)

            if (error) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            } else {
                context.deleteObject(method)
                context.commitChanges()
            }
    }

    @Override
    void update(List<PaymentMethodDTO> paymentTypes) {
            ObjectContext context = cayenneService.newContext

            ValidationErrorDTO error = validateData(context,paymentTypes)
            if (error) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }

            paymentTypes.each { type ->
                error = validateForUpdate(context, type, paymentTypes)
                if (error) {
                    context.rollbackChanges()
                    throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
                } else {
                    updateType(context, type)
                }
            }
            context.commitChanges()
    }
}
