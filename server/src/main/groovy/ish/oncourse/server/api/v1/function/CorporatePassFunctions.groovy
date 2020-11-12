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
import groovy.transform.TypeCheckingMode
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.DiscountFunctions.toRestDiscountMinimized
import static ish.oncourse.server.api.v1.function.SaleFunctions.toRestSale
import ish.oncourse.server.api.v1.model.CorporatePassDTO
import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassCourseClass
import ish.oncourse.server.cayenne.CorporatePassDiscount
import ish.oncourse.server.cayenne.CorporatePassProduct
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.Product
import ish.util.LocalDateUtils
import ish.validation.ValidationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

class CorporatePassFunctions {

    @CompileStatic(TypeCheckingMode.SKIP)
    static CorporatePassDTO toRestCorporatePass(CorporatePass dbCorporatePass) {
        new CorporatePassDTO().with { cc ->
            cc.id = dbCorporatePass.id
            cc.contactId = dbCorporatePass.contact.id
            cc.contactFullName = dbCorporatePass.contact.fullName
            cc.password = dbCorporatePass.password
            cc.expiryDate = LocalDateUtils.dateToValue(dbCorporatePass.expiryDate)
            cc.invoiceEmail = dbCorporatePass.invoiceEmail
            cc.linkedDiscounts = dbCorporatePass.corporatePassDiscounts.collect{ it.discount }.collect { toRestDiscountMinimized(it) }.sort{ it.name.toLowerCase()}
            cc.linkedSalables = (dbCorporatePass.corporatePassCourseClasses.collect{ it.courseClass }.collect { toRestSale(it) } +
                    dbCorporatePass.corporatePassProduct.collect{ it.product }.collect { toRestSale(it) }).sort { it.name.toLowerCase() }
            cc.createdOn = LocalDateUtils.dateToTimeValue(dbCorporatePass.createdOn)
            cc.modifiedOn = LocalDateUtils.dateToTimeValue(dbCorporatePass.modifiedOn)
            cc
        }
    }

    static ValidationErrorDTO validateForDelete(CorporatePass entity) {
        if (!(entity.corporatePassCourseClasses.empty && entity.corporatePassDiscounts.empty && entity.corporatePassProduct.empty && entity.invoices.empty)) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', 'CorporatePass cannot be deleted because it has already been used.')
        }
        null
    }

    static ValidationErrorDTO validateForSave(CorporatePassDTO corporatePass, ObjectContext context, Long dbCorporatePassId = null) {
        if (!corporatePass.contactId) {
            return new ValidationErrorDTO(corporatePass?.id?.toString(), 'contact', 'Contact id is required.')
        } else if (!getRecordById(context, Contact, corporatePass.contactId)) {
            return new ValidationErrorDTO(corporatePass?.id?.toString(), 'contact', "Contact with id=$corporatePass.contactId not found.")
        }

        if (isBlank(corporatePass.password)) {
            return new ValidationErrorDTO(corporatePass?.id?.toString(), 'password', 'Password is required.')
        } else if (trimToNull(corporatePass.password).size() > 100) {
            return new ValidationErrorDTO(corporatePass?.id?.toString(), 'password', 'Password cannot be more than 100 chars.')
        }

        Long corporatePassId = ObjectSelect.query(CorporatePass)
                .where(CorporatePass.PASSWORD.eq(trimToNull(corporatePass.password)))
                .selectOne(context)?.id
        if (corporatePassId && corporatePassId != dbCorporatePassId) {
            return new ValidationErrorDTO(corporatePass?.id?.toString(), 'password', 'Password must be unique.')
        }

        if (isNotBlank(corporatePass.invoiceEmail)) {
            if (!ValidationUtil.isValidEmailAddress(trimToNull(corporatePass.invoiceEmail))) {
                return new ValidationErrorDTO(corporatePass?.id?.toString(), 'invoiceEmail', 'Invalid invoice email.')
            } else if (trimToNull(corporatePass.invoiceEmail).size() > 100) {
                return new ValidationErrorDTO(corporatePass?.id?.toString(), 'invoiceEmail', 'Invoice email cannot be more than 100 chars.')
            }
        }

        corporatePass.linkedDiscounts.eachWithIndex { DiscountDTO discount, int i ->
            if (!discount.id) {
                return new ValidationErrorDTO(corporatePass?.id?.toString(), "linkedDiscounts[$i].id", 'Discount id is required.')
            } else if (!getRecordById(context, Discount, discount.id)) {
                return new ValidationErrorDTO(corporatePass?.id?.toString(), "linkedDiscounts[$i].id", "Discount with id=$discount.id not found.")
            }
        }
        corporatePass.linkedSalables.eachWithIndex { SaleDTO sale, int i ->
            if (!sale.id) {
                return new ValidationErrorDTO(corporatePass?.id?.toString(), "linkedSalables[$i].id", "$sale.type id is required.")
            } else {
                Class cayenneClass = sale.type == SaleTypeDTO.CLASS ? CourseClass : Product
                if (!getRecordById(context, cayenneClass, sale.id)) {
                    return new ValidationErrorDTO(corporatePass?.id?.toString(), "linkedSalables[$i].id", "$sale.type with id=$sale.id not found.")
                }
            }

        }
        null
    }

    static CorporatePass toDbCorporatePass(CorporatePassDTO corporatePass, CorporatePass dbCorporatePass, ObjectContext context) {
        dbCorporatePass.contact = getRecordById(context, Contact, corporatePass.contactId)
        dbCorporatePass.password = trimToNull(corporatePass.password)
        dbCorporatePass.invoiceEmail = trimToNull(corporatePass.invoiceEmail)
        dbCorporatePass.expiryDate = LocalDateUtils.valueToDate(corporatePass.expiryDate, true)
        updateCorporatePassDiscounts(context, dbCorporatePass, corporatePass.linkedDiscounts)

        updateCorporatePassCourseClasses(context, dbCorporatePass, corporatePass.linkedSalables.findAll { it.type == SaleTypeDTO.CLASS })
        updateCorporateProducts(context, dbCorporatePass, corporatePass.linkedSalables.findAll { it.type != SaleTypeDTO.CLASS })
        dbCorporatePass
    }

    private static void updateCorporatePassDiscounts(ObjectContext context, CorporatePass dbCorporatePass, List<DiscountDTO> discounts) {
        List<Long> relationsToSave = discounts*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassDiscounts.findAll { !relationsToSave.contains(it.discount.id) })
        discounts.findAll { !dbCorporatePass.corporatePassDiscounts*.discount*.id.contains(it.id) }.each { DiscountDTO d ->
            context.newObject(CorporatePassDiscount).with { cpd ->
                cpd.corporatePass = dbCorporatePass
                cpd.discount = getRecordById(context, Discount, d.id)
                cpd
            }
        }
    }

    private static void updateCorporatePassCourseClasses(ObjectContext context, CorporatePass dbCorporatePass, List<SaleDTO> courseClasses) {
        List<Long> relationsToSave = courseClasses*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassCourseClasses.findAll { !relationsToSave.contains(it.courseClass.id) })
        courseClasses.findAll { !dbCorporatePass.corporatePassCourseClasses*.courseClass*.id.contains(it.id) }.each { SaleDTO  cc ->
            context.newObject(CorporatePassCourseClass).with { dcc ->
                dcc.corporatePass = dbCorporatePass
                dcc.courseClass = getRecordById(context, CourseClass, cc.id)
            }
        }
    }

    private static void updateCorporateProducts(ObjectContext context, CorporatePass dbCorporatePass, List<SaleDTO> products) {
        List<Long> relationsToSave = products*.id ?: [] as List<Long>
        context.deleteObjects(dbCorporatePass.corporatePassProduct.findAll { !relationsToSave.contains(it.product.id) })
        products.findAll { product -> !dbCorporatePass.corporatePassProduct*.product*.id.contains(product.id) }.each { product ->
            context.newObject(CorporatePassProduct).with { cpd ->
                cpd.corporatePass = dbCorporatePass
                cpd.product = getRecordById(context, Product, product.id)
                cpd
            }
        }
    }
}
