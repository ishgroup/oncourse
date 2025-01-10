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

package ish.oncourse.server.api.dao

import com.google.inject.Inject
import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.server.CayenneService
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.util.AccountUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class CourseClassDao implements CayenneLayer<CourseClass> {

    @Inject
    ClassCostDao classCostDao

    @Inject
    DiscountDao discountDao

    @Inject
    CayenneService cayenneService

    @Override
    CourseClass newObject(ObjectContext context) {
        CourseClass courseClass = context.newObject(CourseClass)
        courseClass.isCancelled = false
        courseClass.isClassFeeApplicationOnly = false
        addStudentFeeCost(context, courseClass)
        assignDefaultDiscounts(context, courseClass)
        courseClass
    }

    void addStudentFeeCost(ObjectContext context, CourseClass courseClass) {
        courseClass.feeExGst = Money.ZERO
        courseClass.deposit = Money.ZERO
        courseClass.taxAdjustment = Money.ZERO

        Account account = AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class)
        courseClass.incomeAccount = account
        courseClass.tax = account.tax

        ClassCost income = classCostDao.defaultIncome(context)
        income.tax = account.tax
        income.courseClass = courseClass
    }

    @Override
    CourseClass getById(ObjectContext context, Long id) {
        CourseClass courseClass = SelectById.query(CourseClass, id)
                .prefetch(CourseClass.ENROLMENTS.joint())
                .prefetch(CourseClass.ENROLMENTS.dot(Enrolment.OUTCOMES).joint())
                .prefetch(CourseClass.COSTS.joint())
                .selectOne(context)

        if (courseClass && courseClass.costs.find(ClassCostDao.studentFee) == null) {
            ObjectContext newContext = cayenneService.newContext
            addStudentFeeCost(newContext, newContext.localObject(courseClass))
            newContext.commitChanges()
        }
        return courseClass
    }

    List<CourseClass> getByCode(ObjectContext context, String code, Long courseId) {
        ObjectSelect.query(CourseClass).where(CourseClass.CODE.eq(code))
                .and(CourseClass.COURSE.dot(Course.ID).eq(courseId))
                .select(context)
    }

    List<CourseClass> getSameClassesForContacts(ObjectContext context, Contact a, Contact b) {
        List<CourseClass> aClasses = ObjectSelect.query(CourseClass)
                .where(CourseClass.ENROLMENTS.dot(Enrolment.STUDENT).eq(a.student)
                        .andExp(CourseClass.ENROLMENTS.dot(Enrolment.STATUS).in(EnrolmentStatus.STATUSES_LEGIT)))
                .select(context)

        List<CourseClass> sameClasses = ObjectSelect.query(CourseClass)
                .where(CourseClass.ENROLMENTS.dot(Enrolment.STUDENT).eq(b.student).andExp(CourseClass.ID.in(aClasses.collect{c -> c.id}))
                        .andExp(CourseClass.ENROLMENTS.dot(Enrolment.STATUS).in(EnrolmentStatus.STATUSES_LEGIT)))
                .select(context)
        sameClasses
    }

    private void assignDefaultDiscounts(ObjectContext context, CourseClass courseClass) {
        discountDao.getDefaultDiscounts(context).each { discount ->
                    DiscountCourseClass dcc = context.newObject(DiscountCourseClass)
                    dcc.courseClass = courseClass
                    dcc.discount = discount
                    classCostDao.discountCost(context, dcc)
                }
    }

}
