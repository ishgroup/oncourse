/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import ish.common.types.CourseClassType
import ish.common.types.DiscountAvailabilityType
import ish.common.types.DiscountType
import ish.math.MoneyRounding
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.dao.DiscountDao
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.entity.mixins.CourseClassMixin
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.StringUtils

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.ConcessionTypeFunctions.toRestConcessionType
import static ish.oncourse.server.api.v1.function.DiscountFunctions.toRestDiscountCorporatePass
import static ish.oncourse.server.api.v1.function.DiscountFunctions.updateCorporatePassDiscount
import static ish.oncourse.server.api.v1.function.DiscountFunctions.updateDiscountConcessionTypes
import static ish.oncourse.server.api.v1.function.DiscountFunctions.updateDiscountCourseClasses
import static ish.oncourse.server.api.v1.function.DiscountFunctions.updateDiscountMemberships
import static ish.oncourse.server.api.v1.function.DiscountMembershipFunctions.toRestDiscountMembership
import static ish.oncourse.server.api.v1.model.DiscountTypeDTO.*
import static ish.oncourse.server.api.v1.model.MoneyRoundingDTO.*
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

class DiscountApiService extends EntityApiService<DiscountDTO, Discount, DiscountDao>{

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


    @Override
    Class<Discount> getPersistentClass() {
        return Discount
    }

    @Override
    DiscountDTO toRestModel(Discount dbDiscount) {
        new DiscountDTO().with { dto ->
            dto = toNotFullRestModel(dbDiscount)
            dto.hideOnWeb = dbDiscount.hideOnWeb
            dto.availableFor = DiscountAvailabilityTypeDTO.fromValue(dbDiscount.availableFor.displayName)
            dto.studentEnrolledWithinDays = dbDiscount.studentEnrolledWithinDays
            if (dbDiscount.studentAge && dbDiscount.studentAge.matches('[<,>] \\d+')) {
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
                    sale.active = CourseClassMixin.isActual(item[4] as Boolean, item[5] as CourseClassType, item[6] as Date)
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
            dto.courseIdMustEnrol = dbDiscount.courseIdMustEnrol
            dto.minEnrolmentsForAnyCourses = dbDiscount.minEnrolmentsForAnyCourses

            dto
        }
    }

    DiscountDTO toNotFullRestModel(Discount dbDiscount) {
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
            dto
        }
    }

    static DiscountDTO toRestDiscountMinimized(Discount dbDiscount) {
        new DiscountDTO().with { d ->
            d.id = dbDiscount.id
            d.name = dbDiscount.name
            d.discountType = discountTypeMap.get(dbDiscount.discountType)
            d.discountValue = dbDiscount.discountDollar?.toBigDecimal()
            d.discountPercent = dbDiscount.discountPercent
            d
        }
    }

    private static List<Object[]> getDiscountClasses(Discount dbDiscount) {
        ObjectSelect.columnQuery(DiscountCourseClass.class, DiscountCourseClass.ID)
                .columns(DiscountCourseClass.COURSE_CLASS.dot(CourseClass.ID),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.CODE),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.COURSE.dot(Course.NAME)),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.IS_CANCELLED),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.TYPE),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.END_DATE_TIME),
                        DiscountCourseClass.COURSE_CLASS.dot(CourseClass.COURSE.dot(Course.CODE)))
                .where(DiscountCourseClass.DISCOUNT.eq(dbDiscount))
                .select(dbDiscount.getContext())
    }

    @Override
    Discount toCayenneModel(DiscountDTO dto, Discount dbDiscount) {
        if (dbDiscount.name != trimToNull(dto.name)) {
            dbDiscount.name = trimToNull(dto.name)

            //update description for all related class costs
            dbDiscount.discountCourseClasses*.classCost.each { (it as ClassCost).description = trimToNull(dto.name) }
        }

        dbDiscount.discountType = discountTypeMap.getByValue(dto.discountType)
        dbDiscount.discountDollar = toMoneyValue(dto.discountValue)
        dbDiscount.discountPercent = dto.discountPercent
        dbDiscount.rounding = roundingMap.getByValue(dto.rounding)
        dbDiscount.discountMin = toMoneyValue(dto.discountMin)
        dbDiscount.discountMax = toMoneyValue(dto.discountMax)
        if (dto.cosAccount != null) {
            dbDiscount.cosAccount = getRecordById(dbDiscount.context, Account, dto.cosAccount)
        } else {
            dbDiscount.cosAccount = null
        }
        dbDiscount.predictedStudentsPercentage = dto.predictedStudentsPercentage
        dbDiscount.availableFor = DiscountAvailabilityType.fromValue(dto.availableFor.toString())
        dbDiscount.code = dto.code
        dbDiscount.validFrom = LocalDateUtils.valueToDate(dto.validFrom)
        dbDiscount.validFromOffset = dto.validFromOffset
        dbDiscount.validTo = LocalDateUtils.valueToDate(dto.validTo, true)
        dbDiscount.validToOffset = dto.validToOffset
        dbDiscount.hideOnWeb = dto.hideOnWeb
        dbDiscount.publicDescription = dto.description
        dbDiscount.studentEnrolledWithinDays = dto.studentEnrolledWithinDays
        dbDiscount.studentAge = dto.studentAgeUnder == null ? null : "${dto.studentAgeUnder ? '<' : '>'} $dto.studentAge"
        dbDiscount.studentPostcode = dto.studentPostcode
        updateDiscountConcessionTypes(dbDiscount.context, dbDiscount, dto.discountConcessionTypes)
        updateDiscountMemberships(dbDiscount.context, dbDiscount, dto.discountMemberships)
        updateDiscountCourseClasses(dbDiscount.context, dbDiscount, dto.discountCourseClasses)
        dbDiscount.addByDefault = dto.addByDefault
        dbDiscount.minEnrolments = dto.minEnrolments
        dbDiscount.minValue = toMoneyValue(dto.minValue)
        dbDiscount.limitPreviousEnrolment = dto.limitPreviousEnrolment != null ? dto.limitPreviousEnrolment : false
        dbDiscount.minEnrolmentsForAnyCourses = dto.minEnrolmentsForAnyCourses
        dbDiscount.courseIdMustEnrol = dto.courseIdMustEnrol
        updateCorporatePassDiscount(dbDiscount.context, dbDiscount, dto.corporatePassDiscounts)
        dbDiscount
    }

    @Override
    void validateModelBeforeSave(DiscountDTO discountDTO, ObjectContext context, Long id) {
        if (StringUtils.isBlank(discountDTO.name)) {
            validator.throwClientErrorException(discountDTO?.id, 'name', 'Name is required.')
        } else if (trimToNull(discountDTO.name).size() > 100) {
            validator.throwClientErrorException(discountDTO?.id, 'name', 'Name cannot be more than 100 chars.')
        }
        if (!discountDTO.discountType) {
            validator.throwClientErrorException(discountDTO?.id, 'discountType', 'Type is required.')
        }
        if (!discountDTO.rounding) {
            validator.throwClientErrorException(discountDTO?.id, 'rounding', 'Rounding is required.')
        }
        if (discountDTO.code != null){
            if (discountDTO.code.length() == 0) {
                validator.throwClientErrorException(discountDTO?.id, 'code', 'The code must be set.')
            }
            Expression expr = Discount.CODE.eq(discountDTO.code)
            List<Discount> list = context.select(SelectQuery.query(Discount.class, expr))
            if (list.size() > 0 && (id == null || list.get(0).id != id)) {
                validator.throwClientErrorException(discountDTO?.id, 'code', 'The code must be unique.')
            }
        }
        if(discountDTO.discountType == PERCENT) {
            if (discountDTO.discountPercent == null) {
                validator.throwClientErrorException(discountDTO?.id, 'discountPercent', 'Discount percent (value) is required.')
            } else if (discountDTO.discountPercent < -1 || discountDTO.discountPercent > 1) {
                validator.throwClientErrorException(discountDTO?.id, 'discountPercent', 'Wrong value')
            }
            if (discountDTO.discountMax != null && discountDTO.discountMax < 0) {
                validator.throwClientErrorException(discountDTO?.id, 'discountMax', 'Wrong value')
            }
            if (discountDTO.discountMin != null && discountDTO.discountMin < 0) {
                validator.throwClientErrorException(discountDTO?.id, 'discountMin', 'Wrong value')
            }
            if (discountDTO.discountMax != null && discountDTO.discountMin != null && discountDTO.discountMin > discountDTO.discountMax) {
                validator.throwClientErrorException(discountDTO?.id, 'discountMax', 'Discount max value should be greater than discount min value.')
            }
        } else {
            if (discountDTO.discountValue == null) {
                validator.throwClientErrorException(discountDTO?.id, 'discountValue', 'Value is required.')
            }
        }
        if (discountDTO.cosAccount && getRecordById(context, Account, discountDTO.cosAccount) == null) {
            validator.throwClientErrorException(discountDTO?.id, 'cosAccount', "Account with id=${discountDTO.cosAccount} is not exist.")
        }
        if (discountDTO.predictedStudentsPercentage == null) {
            validator.throwClientErrorException(discountDTO?.id, 'availableFor', 'Default forecast take-up is required.')
        } else if (discountDTO.predictedStudentsPercentage < 0 || discountDTO.predictedStudentsPercentage > 1) {
            validator.throwClientErrorException(discountDTO?.id, 'predictedStudentsPercentage', 'Wrong value')
        }
        if (discountDTO.availableFor == null) {
            validator.throwClientErrorException(discountDTO?.id, 'availableFor', 'Availability via online is required.')
        }
        if (trimToEmpty(discountDTO.code).size() > 20) {
            validator.throwClientErrorException(discountDTO?.id, 'name', 'Code cannot be more than 20 chars.')
        }
        if (discountDTO.validFrom && discountDTO.validFromOffset != null) {
            validator.throwClientErrorException(discountDTO?.id, 'validFromOffset', 'Valid from date and offset cannot be at the same time.')
        }
        if (discountDTO.validTo && discountDTO.validToOffset != null) {
            validator.throwClientErrorException(discountDTO?.id, 'validToOffset', 'Valid to date and offset cannot be at the same time.')
        }
        if (discountDTO.validFrom && discountDTO.validTo && discountDTO.validFrom.isAfter(discountDTO.validTo)) {
            validator.throwClientErrorException(discountDTO?.id, 'validTo', 'Valid to date cannot be before valid from date.')
        }
        if (discountDTO.validFromOffset && discountDTO.validToOffset && discountDTO.validFromOffset > discountDTO.validToOffset) {
            validator.throwClientErrorException(discountDTO?.id, 'validTo', 'Valid to offset cannot be before valid from offset.')
        }
        if (discountDTO.hideOnWeb == null) {
            validator.throwClientErrorException(discountDTO?.id, 'hideOnWeb', 'Hiding price on web is required.')
        }
        if (trimToEmpty(discountDTO.description).size() > 32000) {
            validator.throwClientErrorException(discountDTO?.id, 'description', 'Description cannot be more than 32000 chars.')
        }

        if (discountDTO.studentAgeUnder == null && discountDTO.studentAge != null) {
            validator.throwClientErrorException(discountDTO?.id, 'studentAgeUnder', 'Student Age option (Under or Above) is required when Age is set')
        }
        if (discountDTO.studentAgeUnder != null && discountDTO.studentAge == null) {
            validator.throwClientErrorException(discountDTO?.id, 'studentAge', 'Student Age is required.')
        }
        if (discountDTO.studentAge != null && discountDTO.studentAge < 0) {
            validator.throwClientErrorException(discountDTO?.id, 'studentAge', 'Student Age should be positive.')
        }
        if (trimToEmpty(discountDTO.studentPostcode).size() > 500) {
            validator.throwClientErrorException(discountDTO?.id, 'studentPostcode', 'Field length cannot be more than 500 chars.')
        }
        discountDTO.discountConcessionTypes.eachWithIndex { ConcessionTypeDTO concessionType, int i ->
            if (!concessionType.id) {
                validator.throwClientErrorException(discountDTO?.id, "discountConcessionTypes[$i].id", 'ConcessionType id is required.')
            } else if (!getRecordById(context, ConcessionType, Long.valueOf(concessionType.id))) {
                validator.throwClientErrorException(discountDTO?.id, "discountConcessionTypes[$i].id", "ConcessionType with id=$concessionType.id not found.")
            }
        }
        discountDTO.discountMemberships.eachWithIndex { DiscountMembershipDTO discountMembership, int i ->
            if (!discountMembership.productId) {
                validator.throwClientErrorException(discountDTO?.id, "discountMemberships[$i].id", 'MembershipProduct id is required.')
            } else if (!getRecordById(context, MembershipProduct, discountMembership.productId)) {
                validator.throwClientErrorException(discountDTO?.id, "discountMemberships[$i].id", "MembershipProduct with id=$discountMembership.productId not found.")
            }

            discountMembership.contactRelations.eachWithIndex { Long contactRelationId, int j ->
                if (!getRecordById(context, ContactRelationType, contactRelationId)) {
                    validator.throwClientErrorException(discountDTO?.id, "discountMemberships[$i].contactRelations[$j].id", "ContactRelation with id=$contactRelationId not found.")
                }
            }
        }

        discountDTO.discountCourseClasses.eachWithIndex { SaleDTO courseClass, int i ->
            if (!courseClass.id) {
                validator.throwClientErrorException(discountDTO?.id, "discountCourseClasses[$i].id", 'CourseClass id is required.')
            } else if (!getRecordById(context, CourseClass, courseClass.id)) {
                validator.throwClientErrorException(discountDTO?.id, "discountCourseClasses[$i].id", "CourseClass with id=$courseClass.id not found.")
            }
        }
        if (discountDTO.addByDefault == null) {
            validator.throwClientErrorException(discountDTO?.id, 'addByDefault', 'Add by default is required.')
        }
        if (discountDTO.minEnrolments == null) {
            validator.throwClientErrorException(discountDTO?.id, 'minEnrolments', 'Min enrolments is required.')
        } else if (discountDTO.minEnrolments < 0) {
            validator.throwClientErrorException(discountDTO?.id, 'minEnrolments', 'Min enrolments cannot be less than 0.')
        }
        if (discountDTO.minValue == null) {
            validator.throwClientErrorException(discountDTO?.id, 'minValue', 'Min value is required.')
        } else if (discountDTO.minValue < 0) {
            validator.throwClientErrorException(discountDTO?.id, 'minValue', 'Min value cannot be less than $0.')
        }

        discountDTO.corporatePassDiscounts.eachWithIndex { DiscountCorporatePassDTO corporatePass, int i ->
            if (!corporatePass.id) {
                validator.throwClientErrorException(discountDTO?.id, "corporatePassDiscounts[$i].id", 'CorporatePass id is required.')
            } else if (!getRecordById(context, CorporatePass, corporatePass.id)) {
                validator.throwClientErrorException(discountDTO?.id, "corporatePassDiscounts[$i].id", "CorporatePass with id=$corporatePass.id not found.")
            }
        }

        if (discountDTO.minEnrolmentsForAnyCourses != null && discountDTO.minEnrolmentsForAnyCourses <= 0) {
            validator.throwClientErrorException(discountDTO?.id, 'minEnrolmentsForAnyCourses', 'Student Age should be positive.')
        }

        if(discountDTO.courseIdMustEnrol && !getRecordById(context, Course, discountDTO.courseIdMustEnrol))
            validator.throwClientErrorException(discountDTO?.id, 'courseIdMustEnrol', "Course with id=$discountDTO.courseIdMustEnrol not found.")
    }

    @Override
    void validateModelBeforeRemove(Discount entity) {
        if (!(entity.invoiceLineDiscounts.empty && entity.discountCourseClasses.empty && entity.discountConcessionTypes.empty && entity.discountMemberships.empty && entity.corporatePassDiscount.empty)) {
            validator.throwClientErrorException(entity?.id, 'id', 'Discount cannot be deleted because it has already been used. Instead use expiry date in the past.')
        }
    }
}
