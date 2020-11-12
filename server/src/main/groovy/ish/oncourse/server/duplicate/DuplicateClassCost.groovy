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

package ish.oncourse.server.duplicate

import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.DiscountCourseClass
import org.apache.cayenne.ObjectContext

class DuplicateClassCost {

    private ClassCost classCost
    private ObjectContext objectContext

    private CourseClass newClass
    private DiscountCourseClass discountCourseClass
    private CourseClassTutor courseClassTutor

    private DuplicateClassCost(ClassCost classCost,
                               ObjectContext objectContext,
                               CourseClass newClass,
                               DiscountCourseClass discountCourseClass,
                               CourseClassTutor courseClassTutor) {
        this.classCost = classCost
        this.objectContext = objectContext
        this.newClass = newClass
        this.discountCourseClass = discountCourseClass
        this.courseClassTutor = courseClassTutor
    }

    static DuplicateClassCost valueOf(ClassCost classCost, ObjectContext objectContext, CourseClass newClass) {
        new DuplicateClassCost(classCost, objectContext, newClass, null, null)
    }

    static DuplicateClassCost valueOf(ClassCost classCost, ObjectContext objectContext, CourseClass newClass,
                                      DiscountCourseClass discountCourseClass) {
        new DuplicateClassCost(classCost, objectContext, newClass, discountCourseClass, null)
    }

    static DuplicateClassCost valueOf(ClassCost classCost, ObjectContext objectContext, CourseClass newClass,
                                      CourseClassTutor courseClassTutor) {
       new DuplicateClassCost(classCost, objectContext, newClass, null, courseClassTutor)
    }


    ClassCost duplicate() {
        ClassCost newCost = objectContext.newObject(ClassCost)
        newCost.courseClass = newClass
        newCost.unitCount = classCost.getUnitCount()
        newCost.description = classCost.getDescription()
        newCost.flowType = classCost.getFlowType()
        newCost.contact = classCost.getContact()
        newCost.isSunk = classCost.getIsSunk()
        newCost.maximumCost = classCost.getMaximumCost()
        newCost.minimumCost = classCost.getMinimumCost()
        newCost.perUnitAmountExTax = classCost.getPerUnitAmountExTax()
        newCost.repetitionType = classCost.getRepetitionType()
        newCost.createdBy = classCost.getCreatedBy()
        newCost.invoiceToStudent = classCost.getInvoiceToStudent()
        newCost.payableOnEnrolment = classCost.getPayableOnEnrolment()
        newCost.onCostRate = classCost.getOnCostRate()
        newCost.taxAdjustment = classCost.getTaxAdjustment()
        newCost.tax = classCost.tax

        newCost.discountCourseClass = discountCourseClass

        newCost.tutorRole = courseClassTutor

        newCost
    }
}
