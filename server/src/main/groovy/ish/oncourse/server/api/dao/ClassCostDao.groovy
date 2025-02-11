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
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class ClassCostDao implements CayenneLayer<ClassCost> {

    public static final Closure<Boolean> studentFee = { ClassCost cost ->
        ClassCostFlowType.INCOME == cost.flowType && ClassCostRepetitionType.PER_ENROLMENT == cost.repetitionType && cost.payableOnEnrolment}
    @Inject
    private SystemUserService userService

    @Override
    ClassCost newObject(ObjectContext context) {
        ClassCost cc = context.newObject(ClassCost)
        if (userService.currentUser) {
            cc.createdBy = context.localObject(userService.currentUser)
        }
        cc.payableOnEnrolment = false

        return cc
    }

    ClassCost defaultIncome(ObjectContext context) {
        ClassCost cc = newObject(context)
        cc.flowType = ClassCostFlowType.INCOME
        cc.isSunk = false
        cc.repetitionType = ClassCostRepetitionType.PER_ENROLMENT
        cc.invoiceToStudent = true
        cc.payableOnEnrolment = true
        cc.description = 'Student enrolment fee'
        cc.perUnitAmountExTax = Money.ZERO()
        cc
    }

    ClassCost discountCost(ObjectContext context, DiscountCourseClass dcc) {
        ClassCost cc = newObject(context)
        cc.flowType = ClassCostFlowType.DISCOUNT
        cc.repetitionType = ClassCostRepetitionType.DISCOUNT
        cc.courseClass = dcc.courseClass
        cc.description = dcc.discount.name
        cc.discountCourseClass = dcc
        cc
    }

    @Override
    ClassCost getById(ObjectContext context, Long id) {
        SelectById.query(ClassCost, id)
                .selectOne(context)
    }
}
