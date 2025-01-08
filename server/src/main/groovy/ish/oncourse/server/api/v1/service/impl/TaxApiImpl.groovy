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
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.v1.function.TaxFunctions.GST_TAX_CODE
import static ish.oncourse.server.api.v1.function.TaxFunctions.GST_exempt_TAX_CODE
import static ish.oncourse.server.api.v1.function.TaxFunctions.NON_SUPPLY_TAX_CODE
import static ish.oncourse.server.api.v1.function.TaxFunctions.updateTax
import static ish.oncourse.server.api.v1.function.TaxFunctions.validateData
import static ish.oncourse.server.api.v1.function.TaxFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.TaxFunctions.validateForUpdate
import ish.oncourse.server.api.v1.model.TaxDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.TaxApi
import ish.oncourse.server.cayenne.Tax
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.ZoneOffset

class TaxApiImpl implements TaxApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    List<TaxDTO> get() {
        return ObjectSelect.query(Tax)
                .prefetch(Tax.PAYABLE_TO_ACCOUNT.joint())
                .prefetch(Tax.RECEIVABLE_FROM_ACCOUNT.joint())
                .select(cayenneService.newContext)
                .collect { dbTax ->
            new TaxDTO().with { tax ->
                tax.id = dbTax.id
                tax.created = dbTax.createdOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                tax.modified = dbTax.modifiedOn.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
                tax.code = dbTax.taxCode
                tax.rate = dbTax.rate
                tax.description = dbTax.description
                tax.payableAccountId = dbTax.payableToAccount.id
                tax.receivableAccountId = dbTax.receivableFromAccount.id
                tax.gst = dbTax.isGSTTaxType
                tax.editable = dbTax.taxCode != NON_SUPPLY_TAX_CODE
                tax.systemType = dbTax.taxCode in [NON_SUPPLY_TAX_CODE, GST_TAX_CODE, GST_exempt_TAX_CODE]
                tax
            }
        }
    }

    @Override
    void remove(String id) {
        if (!StringUtils.isNumeric(id)) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null,"Tax is not exist")).build())
        }

        ObjectContext context = cayenneService.newContext
        Tax tax = SelectById.query(Tax, id).selectOne(context)
        ValidationErrorDTO error = validateForDelete(tax)

        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        } else {
            context.deleteObject(tax)
            context.commitChanges()
        }
    }

    @Override
    void update(List<TaxDTO> taxes) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateData(context, taxes)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        taxes.each { tax ->
            error = validateForUpdate(context, tax)
            if (error) {
                context.rollbackChanges()
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            } else {
                updateTax(context, tax)
            }
        }
        context.commitChanges()
    }
}
