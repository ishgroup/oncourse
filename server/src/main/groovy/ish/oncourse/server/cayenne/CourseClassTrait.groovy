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

package ish.oncourse.server.cayenne

import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.function.CalculateClassroomHours
import ish.oncourse.function.CalculateCourseClassNominalHours
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.persistence.CommonExpressionFactory
import ish.util.DateTimeUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

trait CourseClassTrait {

    abstract Course getCourse()
    abstract List<Session> getSessions()
    abstract Integer getSessionsCount()
    abstract Integer getMinutesPerSession()
    abstract List<Enrolment> getSuccessAndQueuedEnrolments()
    abstract TimeZone getTimeZone()
    abstract Date getStartDateTime()
    abstract Date getEndDateTime()
    abstract Long getId()
    abstract ObjectContext getObjectContext()

    @API
    BigDecimal getQualificationHours() {
        getCourse().qualification?.nominalHours
    }

    @API
    BigDecimal getNominalHours() {
        return CalculateCourseClassNominalHours.valueOf(this as CourseClass).calculate()
    }

    /**
     *
     * @return the sum of the duration of all sessions attached to this CourseClass
     */
    @API
    BigDecimal getClassroomHours() {
        CalculateClassroomHours.valueOf(this as CourseClass).calculate()
    }


    @API
    BigDecimal getStudentContactHours() {
        return successAndQueuedEnrolments.collectMany { e -> e.outcomes }.inject(BigDecimal.ZERO) {
            BigDecimal sum, Outcome o ->
                sum.add(o.reportableHours ?: BigDecimal.ZERO)
        }
    }

    @CompileDynamic
    Expression getDiscountDateFilter() {

        Date now = new Date()

        Expression validToExp = DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).isNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).isNull())
        validToExp = validToExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).gt(CommonExpressionFactory.previousMidnight(now))))

        Expression validFromExp = DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).isNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).isNull())
        validFromExp = validFromExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).lt(CommonExpressionFactory.nextMidnight(now))))

        // apply discounts with offsets (valid from offset, valid to offset) only when courseClass has start date time.
        Date classStart = getStartDateTime()
        if (classStart != null) {
            int startClassOffsetInDays = DateTimeUtil.getDaysLeapYearDaylightSafe(classStart, now)

            validToExp = validToExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).gte(startClassOffsetInDays)))
            validFromExp = validFromExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).lte(startClassOffsetInDays)))
        }

        return validToExp.andExp(validFromExp)
    }


    List<DiscountCourseClass> getAvalibleDiscounts(Contact contact, List<Long> courseIds,
                                                   List<Long> productIds, List<Long> promoIds,
                                                   List<MembershipProduct> newMemberships, Integer enrolmentsCount, Money purchaseTotal ) {
        List<EntityRelation> relations = EntityRelationDao.getRelatedFrom(objectContext, Course.simpleName, course.id)
                .findAll {
                    (Course.simpleName == it.fromEntityIdentifier && it.fromEntityAngelId in courseIds) ||
                        (Product.simpleName == it.fromEntityIdentifier && it.fromEntityAngelId in productIds)
                }
        List<Discount> discountsViaRelations = relations*.relationType*.discount.findAll { it != null }

        (ObjectSelect.query(DiscountCourseClass).
                where(DiscountCourseClass.COURSE_CLASS.dot(CourseClass.ID).eq(id)) & getDiscountDateFilter()).
                select(objectContext).
                findAll { dcc ->
                    dcc.discount.entityRelationTypes.empty || dcc.discount in discountsViaRelations
                }.
                findAll { it.discount.code == null || it.discount.id in promoIds }.
                findAll { it.discount.isStudentEligibile(contact, newMemberships, this, enrolmentsCount, purchaseTotal) }
    }
}
