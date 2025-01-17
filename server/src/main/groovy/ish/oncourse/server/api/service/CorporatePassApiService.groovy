/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CorporatePassDao
import ish.oncourse.server.api.v1.model.CorporatePassDTO
import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.*
import ish.util.LocalDateUtils
import ish.validation.ValidationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CorporatePassFunctions.*
import static ish.oncourse.server.api.v1.function.DiscountFunctions.toRestDiscountMinimized
import static ish.oncourse.server.api.v1.function.SaleFunctions.toRestSale
import static org.apache.commons.lang3.StringUtils.*

class CorporatePassApiService extends EntityApiService<CorporatePassDTO, CorporatePass, CorporatePassDao>{
    @Inject
    private ContactDao contactDao

    @Override
    Class<CorporatePass> getPersistentClass() {
        return CorporatePass
    }

    @Override
    CorporatePassDTO toRestModel(CorporatePass dbCorporatePass) {
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

    @Override
    CorporatePass toCayenneModel(CorporatePassDTO corporatePassDTO, CorporatePass dbCorporatePass) {
        dbCorporatePass.contact = contactDao.getById(dbCorporatePass.context, corporatePassDTO.contactId)
        dbCorporatePass.password = trimToNull(corporatePassDTO.password)
        dbCorporatePass.invoiceEmail = trimToNull(corporatePassDTO.invoiceEmail)
        dbCorporatePass.expiryDate = LocalDateUtils.valueToDate(corporatePassDTO.expiryDate, true)
        updateCorporatePassDiscounts(dbCorporatePass.context, dbCorporatePass, corporatePassDTO.linkedDiscounts)

        updateCorporatePassCourseClasses(dbCorporatePass.context, dbCorporatePass, corporatePassDTO.linkedSalables.findAll { it.type == SaleTypeDTO.CLASS })
        updateCorporateProducts(dbCorporatePass.context, dbCorporatePass, corporatePassDTO.linkedSalables.findAll { it.type != SaleTypeDTO.CLASS })
        dbCorporatePass
    }

    @Override
    void validateModelBeforeSave(CorporatePassDTO corporatePassDTO, ObjectContext context, Long id) {
        if (!corporatePassDTO.contactId) {
            validator.throwClientErrorException(corporatePassDTO?.id, 'contact', 'Contact id is required.')
        } else if (!contactDao.getById(context, corporatePassDTO.contactId)) {
            validator.throwClientErrorException(corporatePassDTO?.id, 'contact', "Contact with id=$corporatePassDTO.contactId not found.")
        }

        if (isBlank(corporatePassDTO.password)) {
            validator.throwClientErrorException(corporatePassDTO?.id, 'password', 'Password is required.')
        } else if (trimToNull(corporatePassDTO.password).size() > 100) {
            validator.throwClientErrorException(corporatePassDTO?.id, 'password', 'Password cannot be more than 100 chars.')
        }

        Long corporatePassId = ObjectSelect.query(CorporatePass)
                .where(CorporatePass.PASSWORD.eq(trimToNull(corporatePassDTO.password)))
                .selectOne(context)?.id

        if (corporatePassId && corporatePassId != id) {
            validator.throwClientErrorException(corporatePassDTO?.id, 'password', 'Password must be unique.')
        }

        if (isNotBlank(corporatePassDTO.invoiceEmail)) {
            if (!ValidationUtil.isValidEmailAddress(trimToNull(corporatePassDTO.invoiceEmail))) {
                validator.throwClientErrorException(corporatePassDTO?.id, 'invoiceEmail', 'Invalid invoice email.')
            } else if (trimToNull(corporatePassDTO.invoiceEmail).size() > 100) {
                validator.throwClientErrorException(corporatePassDTO?.id, 'invoiceEmail', 'Invoice email cannot be more than 100 chars.')
            }
        }

        corporatePassDTO.linkedDiscounts.eachWithIndex { DiscountDTO discount, int i ->
            if (!discount.id) {
                validator.throwClientErrorException(corporatePassDTO?.id, "linkedDiscounts[$i].id", 'Discount id is required.')
            } else if (!getRecordById(context, Discount, discount.id)) {
                validator.throwClientErrorException(corporatePassDTO?.id, "linkedDiscounts[$i].id", "Discount with id=$discount.id not found.")
            }
        }
        corporatePassDTO.linkedSalables.eachWithIndex { SaleDTO sale, int i ->
            if (!sale.id) {
                validator.throwClientErrorException(corporatePassDTO?.id, "linkedSalables[$i].id", "$sale.type id is required.")
            } else {
                Class cayenneClass = sale.type == SaleTypeDTO.CLASS ? CourseClass : Product
                if (!getRecordById(context, cayenneClass, sale.id)) {
                    validator.throwClientErrorException(corporatePassDTO?.id, "linkedSalables[$i].id", "$sale.type with id=$sale.id not found.")
                }
            }
        }
    }

    @Override
    void validateModelBeforeRemove(CorporatePass entity) {
        if (!(entity.corporatePassCourseClasses.empty && entity.corporatePassDiscounts.empty && entity.corporatePassProduct.empty && entity.invoices.empty)) {
            validator.throwClientErrorException(entity.id, 'id', 'CorporatePass cannot be deleted because it has already been used.')
        }
    }
}
