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
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.common.types.DiscountType
import ish.math.MoneyRounding
import ish.oncourse.server.api.BidiMap
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ConcessionTypeFunctions.toRestConcessionType
import static ish.oncourse.server.api.v1.function.DiscountMembershipFunctions.toRestDiscountMembership
import static ish.oncourse.server.api.v1.function.DiscountMembershipFunctions.updateContactRelationTypes
import ish.oncourse.server.api.v1.model.ConcessionTypeDTO
import ish.oncourse.server.api.v1.model.DiscountDTO
import ish.oncourse.server.api.v1.model.DiscountCorporatePassDTO
import ish.oncourse.server.api.v1.model.DiscountMembershipDTO
import ish.oncourse.server.api.v1.model.DiscountTypeDTO
import static ish.oncourse.server.api.v1.model.DiscountTypeDTO.DOLLAR
import static ish.oncourse.server.api.v1.model.DiscountTypeDTO.FEE_OVERRIDE
import static ish.oncourse.server.api.v1.model.DiscountTypeDTO.PERCENT
import ish.oncourse.server.api.v1.model.MoneyRoundingDTO
import static ish.oncourse.server.api.v1.model.MoneyRoundingDTO.NEAREST_10_CENTS
import static ish.oncourse.server.api.v1.model.MoneyRoundingDTO.NEAREST_50_CENTS
import static ish.oncourse.server.api.v1.model.MoneyRoundingDTO.NEAREST_DOLLAR
import static ish.oncourse.server.api.v1.model.MoneyRoundingDTO.NO_ROUNDING
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CorporatePassDiscount
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.Discount as DbDiscount
import ish.oncourse.server.cayenne.DiscountConcessionType
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.entity.mixins.CourseClassMixin
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.StringUtils
import static org.apache.commons.lang3.StringUtils.isNumeric
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneOffset

@CompileStatic
class DiscountFunctions {

    private static final BidiMap<DiscountType, DiscountTypeDTO> discountTypeMap = new BidiMap<DiscountType, DiscountTypeDTO>() {{
        put(DiscountType.PERCENT, PERCENT)
        put(DiscountType.DOLLAR, DOLLAR)
        put(DiscountType.FEE_OVERRIDE, FEE_OVERRIDE)
    }}

    private static final BidiMap<MoneyRounding, MoneyRoundingDTO> roundingMap = new BidiMap<MoneyRounding, MoneyRoundingDTO>() {{
        put(MoneyRounding.ROUNDING_NONE, NO_ROUNDING)
        put(MoneyRounding.ROUNDING_10C, NEAREST_10_CENTS)
        put(MoneyRounding.ROUNDING_50C, NEAREST_50_CENTS)
        put(MoneyRounding.ROUNDING_1D, NEAREST_DOLLAR)
    }}

    static DiscountDTO toRestDiscount(DbDiscount dbDiscount, Boolean full = true ) {
        new DiscountDTO().with { dto ->
            dto.id = dbDiscount.id
            dto.name = dbDiscount.name
            dto.code = dbDiscount.code
            dto.description = dbDiscount.publicDescription
            dto.discountType = discountTypeMap.get(dbDiscount.discountType)
            dto.discountValue = dbDiscount.discountDollar?.toBigDecimal()
            dto.discountPercent = dbDiscount.discountPercent
            dto.rounding = roundingMap.get(dbDiscount.rounding)
            dto.discountMin = dbDiscount.discountMin?.toBigDecimal()
            dto.discountMax = dbDiscount.discountMax?.toBigDecimal()
            dto.predictedStudentsPercentage = dbDiscount.predictedStudentsPercentage
            dto.validFrom = LocalDateUtils.dateToValue(dbDiscount.validFrom)
            dto.validFromOffset = dbDiscount.validFromOffset
            dto.validTo = LocalDateUtils.dateToValue(dbDiscount.validTo)
            dto.validToOffset = dbDiscount.validToOffset
            dto.relationDiscount = !dbDiscount.entityRelationTypes.empty
            if (full) {
                dto.hideOnWeb = dbDiscount.hideOnWeb
                dto.availableOnWeb = dbDiscount.isAvailableOnWeb
                dto.studentEnrolledWithinDays = dbDiscount.studentEnrolledWithinDays
                if (dbDiscount.studentAge && dbDiscount.studentAge.matches('[<,>] \\dto+')) {
                    dto.studentAgeUnder dbDiscount.studentAge.split(' ')[0] == '<'
                    dto.studentAge = dbDiscount.studentAge.split(' ')[1] as Integer
                }
                dto.studentPostcode = dbDiscount.studentPostcode
                dto.discountConcessionTypes = dbDiscount.discountConcessionTypes.collect { toRestConcessionType(it.concessionType) }
                dto.discountMemberships = dbDiscount.discountMemberships.collect { toRestDiscountMembership(it) }.sort { it.productId }
                dto.discountCourseClasses = getDiscountClasses(dbDiscount).collect { item ->
                    new SaleDTO().with { sale ->
                        sale.id = item[1] as Long
                        sale.code = (item[7] as String) + "-" + (item[2] as String)
                        sale.name = item[3] as String
                        sale.type = SaleTypeDTO.CLASS
                        sale.active = CourseClassMixin.isActual(item[4] as Boolean, item[5] as Boolean, item[6] as Date)
                        sale
                    }
                }
                dto.cosAccount = dbDiscount.cosAccount?.id
                dto.addByDefault = dbDiscount.addByDefault
                dto.minEnrolments = dbDiscount.minEnrolments
                dto.minValue = dbDiscount.minValue?.toBigDecimal()
                dto.corporatePassDiscounts = dbDiscount.corporatePassDiscount.collect { toRestDiscountCorporatePass(it.corporatePass) }
                dto.createdOn = LocalDateUtils.dateToTimeValue(dbDiscount.createdOn)
                dto.modifiedOn = LocalDateUtils.dateToTimeValue(dbDiscount.modifiedOn)
                dto.limitPreviousEnrolment = dbDiscount.limitPreviousEnrolment
            }
            dto
        }
    }

    private static List<Object[]> getDiscountClasses(DbDiscount dbDiscount) {
        ObjectSelect.columnQuery(DiscountCourseClass.class, DiscountCourseClass.ID)
                .columns(DiscountCourseClass.COURSE_CLASS.dot(CourseClass.ID),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.CODE),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.COURSE.dot(Course.NAME)),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.IS_CANCELLED),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.IS_DISTANT_LEARNING_COURSE),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.END_DATE_TIME),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.COURSE.dot(Course.CODE)))
                .where(DiscountCourseClass.DISCOUNT.eq(dbDiscount))
                .select(dbDiscount.getContext())
    }

    static DiscountCorporatePassDTO toRestDiscountCorporatePass(CorporatePass corporatePass) {
        new DiscountCorporatePassDTO().with { dcp ->
            dcp.id = corporatePass.id
            dcp.contactFullName = corporatePass.contact.fullName
            dcp
        }
    }

    static DiscountDTO toRestDiscountMinimized(DbDiscount dbDiscount) {
        new DiscountDTO().with { d ->
            d.id = dbDiscount.id
            d.name = dbDiscount.name
            d.discountType = discountTypeMap.get(dbDiscount.discountType)
            d.discountValue = dbDiscount.discountDollar?.toBigDecimal()
            d.discountPercent = dbDiscount.discountPercent
            d
        }
    }

    static ValidationErrorDTO validateForDelete(DbDiscount entity) {
        if (!(entity.invoiceLineDiscounts.empty && entity.discountCourseClasses.empty && entity.discountConcessionTypes.empty && entity.discountMemberships.empty && entity.corporatePassDiscount.empty)) {
            return new ValidationErrorDTO(entity.id?.toString(), 'id', 'Discount cannot be deleted because it has already been used. Instead use expiry date in the past.')
        }
        null
    }

    static ValidationErrorDTO validateForSave(DiscountDTO discount, ObjectContext context, Long dbDiscountId = null) {
        if (StringUtils.isBlank(discount.name)) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'name', 'Name is required.')
        } else if (trimToNull(discount.name).size() > 100) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'name', 'Name cannot be more than 100 chars.')
        }
        if (!discount.discountType) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'discountType', 'Type is required.')
        }
        if (!discount.rounding) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'rounding', 'Rounding is required.')
        }
        if (discount.code != null){
            if (discount.code.length() == 0) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'code', 'The code must be set.')
            }
            Expression expr = Discount.CODE.eq(discount.code)
            List<Discount> list = context.select(SelectQuery.query(Discount.class, expr))
            if (list.size() > 0 && (dbDiscountId == null || list.get(0).id != dbDiscountId)) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'code', 'The code must be unique.')
            }
        }
        if(discount.discountType == PERCENT) {
            if (discount.discountPercent == null) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountPercent', 'Discount percent (value) is required.')
            } else if (discount.discountPercent < -1 || discount.discountPercent > 1) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountPercent', 'Wrong value')
            }
            if (discount.discountMax != null && discount.discountMax < 0) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountMax', 'Wrong value')
            }
            if (discount.discountMin != null && discount.discountMin < 0) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountMin', 'Wrong value')
            }
            if (discount.discountMax != null && discount.discountMin != null && discount.discountMin > discount.discountMax) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountMax', 'Discount max value should be greater than discount min value.')
            }
        } else {
            if (discount.discountValue == null) {
                return new ValidationErrorDTO(discount?.id?.toString(), 'discountValue', 'Value is required.')
            }
        }
        if (discount.cosAccount && getRecordById(context, Account, discount.cosAccount) == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'cosAccount', "Account with id=${discount.cosAccount} is not exist.")
        }
        if (discount.predictedStudentsPercentage == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'availableOnWeb', 'Default forecast take-up is required.')
        } else if (discount.predictedStudentsPercentage < 0 || discount.predictedStudentsPercentage > 1) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'predictedStudentsPercentage', 'Wrong value')
        }
        if (discount.availableOnWeb == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'availableOnWeb', 'Availability via online is required.')
        }
        if (trimToEmpty(discount.code).size() > 20) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'name', 'Code cannot be more than 20 chars.')
        }
        if (discount.validFrom && discount.validFromOffset != null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'validFromOffset', 'Valid from date and offset cannot be at the same time.')
        }
        if (discount.validTo && discount.validToOffset != null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'validToOffset', 'Valid to date and offset cannot be at the same time.')
        }
        if (discount.validFrom && discount.validTo && discount.validFrom.isAfter(discount.validTo)) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'validTo', 'Valid to date cannot be before valid from date.')
        }
        if (discount.validFromOffset && discount.validToOffset && discount.validFromOffset > discount.validToOffset) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'validTo', 'Valid to offset cannot be before valid from offset.')
        }
        if (discount.hideOnWeb == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'hideOnWeb', 'Hiding price on web is required.')
        }
        if (trimToEmpty(discount.description).size() > 32000) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'description', 'Description cannot be more than 32000 chars.')
        }

        if (discount.studentAgeUnder == null && discount.studentAge != null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'studentAgeUnder', 'Student Age option (Under or Above) is required when Age is set')
        }
        if (discount.studentAgeUnder != null && discount.studentAge == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'studentAge', 'Student Age is required.')
        }
        if (discount.studentAge != null && discount.studentAge < 0) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'studentAge', 'Student Age should be positive.')
        }
        if (trimToEmpty(discount.studentPostcode).size() > 500) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'studentPostcode', 'Field length cannot be more than 500 chars.')
        }
        discount.discountConcessionTypes.eachWithIndex { ConcessionTypeDTO concessionType, int i ->
            if (!concessionType.id) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountConcessionTypes[$i].id", 'ConcessionType id is required.')
            } else if (!getRecordById(context, ConcessionType, Long.valueOf(concessionType.id))) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountConcessionTypes[$i].id", "ConcessionType with id=$concessionType.id not found.")
            }
        }
        discount.discountMemberships.eachWithIndex { DiscountMembershipDTO discountMembership, int i ->
            if (!discountMembership.productId) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountMemberships[$i].id", 'MembershipProduct id is required.')
            } else if (!getRecordById(context, MembershipProduct, discountMembership.productId)) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountMemberships[$i].id", "MembershipProduct with id=$discountMembership.productId not found.")
            }

            discountMembership.contactRelations.eachWithIndex { Long contactRelationId, int j ->
                if (!getRecordById(context, ContactRelationType, contactRelationId)) {
                    return new ValidationErrorDTO(discount?.id?.toString(), "discountMemberships[$i].contactRelations[$j].id", "ContactRelation with id=$contactRelationId not found.")
                }
            }
        }

        discount.discountCourseClasses.eachWithIndex { SaleDTO courseClass, int i ->
            if (!courseClass.id) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountCourseClasses[$i].id", 'CourseClass id is required.')
            } else if (!getRecordById(context, CourseClass, courseClass.id)) {
                return new ValidationErrorDTO(discount?.id?.toString(), "discountCourseClasses[$i].id", "CourseClass with id=$courseClass.id not found.")
            }
        }
        if (discount.addByDefault == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'addByDefault', 'Add by default is required.')
        }
        if (discount.minEnrolments == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'minEnrolments', 'Min enrolments is required.')
        } else if (discount.minEnrolments < 0) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'minEnrolments', 'Min enrolments cannot be less than 0.')
        }
        if (discount.minValue == null) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'minValue', 'Min value is required.')
        } else if (discount.minValue < 0) {
            return new ValidationErrorDTO(discount?.id?.toString(), 'minValue', 'Min value cannot be less than $0.')
        }

        discount.corporatePassDiscounts.eachWithIndex { DiscountCorporatePassDTO corporatePass, int i ->
            if (!corporatePass.id) {
                return new ValidationErrorDTO(discount?.id?.toString(), "corporatePassDiscounts[$i].id", 'CorporatePass id is required.')
            } else if (!getRecordById(context, CorporatePass, corporatePass.id)) {
                return new ValidationErrorDTO(discount?.id?.toString(), "corporatePassDiscounts[$i].id", "CorporatePass with id=$corporatePass.id not found.")
            }
        }
        null
    }

    static DbDiscount toDbDiscount(DiscountDTO discount, DbDiscount dbDiscount, ObjectContext context) {
        if (dbDiscount.name != trimToNull(discount.name)) {
            dbDiscount.name = trimToNull(discount.name)

            //update description for all related class costs
            dbDiscount.discountCourseClasses*.classCost.each { (it as ClassCost).description = trimToNull(discount.name) }

        }

        dbDiscount.discountType = discountTypeMap.getByValue(discount.discountType)
        dbDiscount.discountDollar = toMoneyValue(discount.discountValue)
        dbDiscount.discountPercent = discount.discountPercent
        dbDiscount.rounding = roundingMap.getByValue(discount.rounding)
        dbDiscount.discountMin = toMoneyValue(discount.discountMin)
        dbDiscount.discountMax = toMoneyValue(discount.discountMax)
        if (discount.cosAccount != null) {
            dbDiscount.cosAccount = getRecordById(context, Account, discount.cosAccount)
        } else {
            dbDiscount.cosAccount = null
        }
        dbDiscount.predictedStudentsPercentage = discount.predictedStudentsPercentage
        dbDiscount.isAvailableOnWeb = discount.availableOnWeb
        dbDiscount.code = discount.code
        dbDiscount.validFrom = LocalDateUtils.valueToDate(discount.validFrom)
        dbDiscount.validFromOffset = discount.validFromOffset
        dbDiscount.validTo = LocalDateUtils.valueToDate(discount.validTo, true)
        dbDiscount.validToOffset = discount.validToOffset
        dbDiscount.hideOnWeb = discount.hideOnWeb
        dbDiscount.publicDescription = discount.description
        dbDiscount.studentEnrolledWithinDays = discount.studentEnrolledWithinDays
        dbDiscount.studentAge = discount.studentAgeUnder == null ? null : "${discount.studentAgeUnder ? '<' : '>'} $discount.studentAge"
        dbDiscount.studentPostcode = discount.studentPostcode
        updateDiscountConcessionTypes(context, dbDiscount, discount.discountConcessionTypes)
        updateDiscountMemberships(context, dbDiscount, discount.discountMemberships)
        updateDiscountCourseClasses(context, dbDiscount, discount.discountCourseClasses)
        dbDiscount.addByDefault = discount.addByDefault
        dbDiscount.minEnrolments = discount.minEnrolments
        dbDiscount.minValue = toMoneyValue(discount.minValue)
        dbDiscount.limitPreviousEnrolment = discount.limitPreviousEnrolment != null ? discount.limitPreviousEnrolment : false
        updateCorporatePassDiscount(context, dbDiscount, discount.corporatePassDiscounts)
        dbDiscount
    }

    private static void updateDiscountConcessionTypes(ObjectContext context, DbDiscount dbDiscount, List<ConcessionTypeDTO> concessionTypes) {
        List<Long> relationsToSave = concessionTypes*.id.collect { Long.valueOf(it as String) } as List<Long>
        dbDiscount.discountConcessionTypes.findAll {
            !relationsToSave.contains(it.concessionType.id)
        }.forEach{ it ->
            dbDiscount.removeFromDiscountConcessionTypes(it as DiscountConcessionType)
            context.deleteObject(it)
        }
        concessionTypes.findAll { !dbDiscount.discountConcessionTypes*.concessionType*.id.contains(Long.valueOf(it.id)) }.each {
            context.newObject(DiscountConcessionType).with { dct ->
                dct.discount = dbDiscount
                dct.concessionType = getRecordById(context, ConcessionType, Long.valueOf((it as ConcessionTypeDTO).id))
                dct
            }
        }
    }

    private static void updateDiscountMemberships(ObjectContext context, DbDiscount dbDiscount, List<DiscountMembershipDTO> restMemberships) {
        List<Long> relationsToSave = restMemberships*.productId.findAll() as List<Long>
        dbDiscount.discountMemberships.findAll {
            !relationsToSave.contains(it.membershipProduct.id)
        }.forEach{ it ->
            dbDiscount.removeFromDiscountMemberships(it as DiscountMembership)
            context.deleteObject(it)
        }
        List<Long> alreadyExist = dbDiscount.discountMemberships.findAll { relationsToSave.contains(it.membershipProduct.id) }*.membershipProduct*.id.findAll() as List<Long>
        restMemberships.each { rdm ->
            DiscountMembership dbDiscountMembership
            if (alreadyExist.contains(rdm.productId)) {
                dbDiscountMembership = dbDiscount.discountMemberships.find { it.membershipProduct.id == rdm.productId }
            } else {
                dbDiscountMembership = context.newObject(DiscountMembership).with { dbDM ->
                    dbDM.discount = dbDiscount
                    dbDM.membershipProduct = getRecordById(context, MembershipProduct, rdm.productId)
                    dbDM
                }
            }
            dbDiscountMembership.applyToMemberOnly = rdm.contactRelations.isEmpty()
            updateContactRelationTypes(context, dbDiscountMembership, rdm.contactRelations)
        }
    }

    private static void updateDiscountCourseClasses(ObjectContext context, DbDiscount dbDiscount, List<SaleDTO> courseClasses) {
        List<Long> relationsToSave = courseClasses*.id as List<Long>
        context.deleteObjects(dbDiscount.discountCourseClasses.findAll { !relationsToSave.contains(it.courseClass.id) })
        courseClasses.findAll { !dbDiscount.discountCourseClasses*.courseClass*.id.contains(it.id) }.each {
            context.newObject(DiscountCourseClass).with { dcc ->
                dcc.discount = dbDiscount
                dcc.courseClass = getRecordById(context, CourseClass, (it as SaleDTO).id)

                context.newObject(ClassCost).with { cc ->
                    cc.courseClass = dcc.courseClass
                    cc.discountCourseClass = dcc
                    cc.flowType = ClassCostFlowType.DISCOUNT
                    cc.repetitionType = ClassCostRepetitionType.DISCOUNT
                    cc.description = dbDiscount.name
                }
            }
        }
    }

    private static void updateCorporatePassDiscount(ObjectContext context, DbDiscount dbDiscount, List<DiscountCorporatePassDTO> corporatePasses) {
        List<Long> relationsToSave = corporatePasses*.id as List<Long>
        context.deleteObjects(dbDiscount.corporatePassDiscount.findAll { !relationsToSave.contains(it.corporatePass.id) })
        corporatePasses.findAll { !dbDiscount.corporatePassDiscount*.corporatePass*.id.contains(it.id) }.each {
            context.newObject(CorporatePassDiscount).with { cpd ->
                cpd.discount = dbDiscount
                cpd.corporatePass = getRecordById(context, CorporatePass, (it as DiscountCorporatePassDTO).id)
                cpd
            }
        }
    }
}
