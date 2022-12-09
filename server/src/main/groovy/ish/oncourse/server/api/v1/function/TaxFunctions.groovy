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

import groovy.transform.CompileStatic
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.getAssetAccount
import static ish.oncourse.server.api.v1.function.PaymentTypeFunctions.getLiabilityAccount
import ish.oncourse.server.api.v1.model.TaxDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Tax
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SelectQuery
import org.apache.cayenne.validation.ValidationResult

@CompileStatic
class TaxFunctions {

    public static final String NON_SUPPLY_TAX_CODE = '*'
    public static final String GST_TAX_CODE = 'GST'
    public static final String GST_exempt_TAX_CODE = 'N'

    static ValidationErrorDTO validateForDelete(Tax  tax) {

        if (!tax) {
            return new ValidationErrorDTO(null, null, "Tax is not exist")
        }

        if (tax.taxCode in [NON_SUPPLY_TAX_CODE, GST_TAX_CODE, GST_exempt_TAX_CODE]) {
            return new ValidationErrorDTO(tax.id?.toString(), null, "System tax type can not be deleted")
        }

        if (tax.invoiceLines && !tax.invoiceLines.empty) {
            return new ValidationErrorDTO(tax.id?.toString(), null, "This tax is linked to invoice lines.")
        }

        if (tax.contacts && !tax.contacts.empty) {
            return new ValidationErrorDTO(tax.id?.toString(), null, "This tax is used as default for: ${tax.contacts[0].fullName}")
        }

        ValidationResult result = new ValidationResult()
        tax.validateForDelete(result)
        if (result.hasFailures()) {
            return new ValidationErrorDTO(tax?.id?.toString(), null, result.failures[0].description)
        }
        return null
    }

    static ValidationErrorDTO validateData(ObjectContext context, List<TaxDTO> taxes) {

        return  [NON_SUPPLY_TAX_CODE, GST_TAX_CODE, GST_exempt_TAX_CODE].collect { validateSystemTypes(it,context,taxes) }.find {it != null}
    }

    static ValidationErrorDTO validateForUpdate(ObjectContext context, TaxDTO tax) {

        if (tax.id) {
            Tax dbTax = SelectById.query(Tax, tax.id).selectOne(context)
            if (!dbTax) {
                return new ValidationErrorDTO(tax.id.toString(), 'id', "Tax $tax.id is not exist")
            }
        }

        if (!tax.code || tax.code.empty) {
            return new ValidationErrorDTO(tax.id?.toString(), 'code', "Tax code can not be empty")
        }

        if (tax.code.length() > 10) {
            return new ValidationErrorDTO(tax.id?.toString(), 'code', "Tax code too long")
        }

        if (tax.rate == null) {
            return new ValidationErrorDTO(tax.id?.toString(), 'rate', "Tax rate can not be empty")
        }

        if (tax.rate > 1 || tax.rate < 0) {
            return new ValidationErrorDTO(tax.id?.toString(), 'rate', "Tax rate is wrong")
        }

        if (!tax.receivableAccountId) {
            return new ValidationErrorDTO(tax.id?.toString(), 'receivableAccountId', "Tax receivable account can not be empty")
        } else if (!getLiabilityAccount(context, tax.receivableAccountId)) {
            return new ValidationErrorDTO(tax.id?.toString(), 'receivableAccountId', "Tax receivable account is wrong")
        }

        if (!tax.payableAccountId) {
            return new ValidationErrorDTO(tax.id?.toString(), 'payableAccountId', "Tax payable account can not be empty")
        } else if (!getAssetAccount(context, tax.payableAccountId)) {
            return new ValidationErrorDTO(tax.id?.toString(), 'payableAccountId', "Tax payable account is wrong")
        }

        return null
    }


    static ValidationErrorDTO validateSystemTypes(String code, ObjectContext context, List<TaxDTO> taxes) {
        Tax dbTax = ObjectSelect.query(Tax).where(Tax.TAX_CODE.eq(code)).selectOne(context)
        List<TaxDTO> restTaxes = taxes.findAll { code == it.code }

        if (restTaxes.size() > 1 ||
                (restTaxes.size() == 1 && dbTax.id != restTaxes[0].id)) {
            return new ValidationErrorDTO(restTaxes[0]?.id?.toString(), 'code', "You already have $code tax")
        }
        return null
    }


    static Tax updateTax(ObjectContext context, TaxDTO tax) {
        Tax dbTax = tax.id ? SelectById.query(Tax, tax.id).selectOne(context) : context.newObject(Tax)

        if (tax.code != NON_SUPPLY_TAX_CODE) {

            dbTax.isGSTTaxType = tax.isGst()
            dbTax.rate = tax.rate
            dbTax.receivableFromAccount = getLiabilityAccount(context, tax.receivableAccountId)
            dbTax.payableToAccount = getAssetAccount(context, tax.payableAccountId)
            dbTax.description = tax.description

            if (! (tax.code in  [GST_exempt_TAX_CODE, GST_TAX_CODE])) {
                dbTax.taxCode = tax.code
            }
        }

        dbTax
    }

    static TaxDTO toRestTaxMinimized(Tax dbTax) {
        dbTax != null ?
        new TaxDTO().with { tax ->
            tax.id = dbTax.id
            tax.code = dbTax.taxCode
            tax
        } : null
    }

    static Tax nonSupplyTax(ObjectContext context) {
        return  ObjectSelect.query(Tax).where(Tax.TAX_CODE.eq(GST_exempt_TAX_CODE)).selectOne(context)
    }
}
