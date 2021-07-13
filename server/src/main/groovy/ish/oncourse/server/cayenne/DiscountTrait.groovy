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
import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.API
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

trait DiscountTrait {

    abstract Integer getStudentEnrolledWithinDays()
    abstract String getStudentAge()
    abstract List<DiscountConcessionType> getDiscountConcessionTypes()
    abstract List<String> getStudentPostcodes()
    abstract List<DiscountMembership> getDiscountMemberships()
    abstract Boolean getLimitPreviousEnrolment()
    abstract List<CorporatePassDiscount> getCorporatePassDiscount()
    abstract Integer getMinEnrolments()
    abstract Money getMinValue()
    abstract ObjectContext getObjectContext()

    /**
     * Determines if the given student is eligible for this Discount. Note that this checks just the student attributes
     * and not whether enrolment, time or other restrictions of the discount might prevent its application.
     *
     * @param student student
     * @return true if student is eligible
     */
    @API
    boolean isStudentEligibile(Contact contact, List<MembershipProduct> newMemberships, CourseClassTrait courseClass, Integer enrolmentsCount, Money purchaseTotal ) {

        return  (enrolledWithinDaysEligibile(contact)
                && studenAgeDateEligibile(contact)
                && concessionEligibile(contact)
                && postcodesEligibile(contact)
                && membershipEligibile(contact, newMemberships)
                && previousEnrolmentEligibile(contact, courseClass)
                && enrolmentsCount >= minEnrolments
                && purchaseTotal >= minValue)

    }


    private boolean previousEnrolmentEligibile(Contact contact, CourseClassTrait courseClass) {
        if (!limitPreviousEnrolment) {
            return true
        } else if (contact.student == null) {
            return false
        } else {
            return contact.student.enrolments.any {  it.status == EnrolmentStatus.SUCCESS && courseClass.course.id ==  it.courseClass.course.id }
        }

    }

    @CompileDynamic
    private boolean enrolledWithinDaysEligibile(Contact contact) {
        if (studentEnrolledWithinDays == null) {
            return true
        } else if (contact.student == null || contact.student.enrolments.empty) {
            return false
        } else {
            Date tresholdDate
            use(TimeCategory) {
                tresholdDate = (new Date() - studentEnrolledWithinDays.days).clearTime()
            }

            return contact.student.enrolments.any { it.status == EnrolmentStatus.SUCCESS && it.createdOn > tresholdDate }
        }
    }

    private boolean studenAgeDateEligibile(Contact contact) {
        if (studentAge == null  || !studentAge.matches(/[<>]\s\d+/) ) {
            return true
        } else if (contact.birthDate == null) {
            return false
        } else {

            String opator = studentAge.split(/\s/)[0]
            Integer age = Integer.valueOf(studentAge.split(/\s/)[1])

            LocalDate thresholdYear = LocalDate.now().minusYears(age)
            switch (opator) {
                case Discount.AGE_UNDER:
                    return  thresholdYear < contact.birthDate
                case Discount.AGE_OVER:
                    return thresholdYear > contact.birthDate
                default:
                    return false
            }
        }
    }

    private boolean concessionEligibile(Contact contact) {

        if (discountConcessionTypes.empty) {
            return true
        } else if (contact.student == null || contact.student.concessions.empty) {
            return false
        } else {
            contact.student.concessions
                    .findAll { it.concessionType.id in discountConcessionTypes*.concessionType.id }
                    .any { !it.concessionType.hasExpiryDate || (it.expiresOn != null && it.expiresOn > new Date()) }

        }
    }


    private boolean postcodesEligibile(Contact contact) {
        if (studentPostcodes != null) {
            return true
        } else if (StringUtils.trimToNull(contact.postcode) == null) {
            return false
        } else {
            return contact.postcode in studentPostcodes
        }
    }

    private boolean membershipEligibile(Contact contact, List<MembershipProduct> newMemberships) {
        if (discountMemberships.empty) {
            return true
        } else {
            //appay membership discounts if contact buy memberchip in one go with enrolment
            if (newMemberships.any { it in  discountMemberships*.membershipProduct }) {
                return true
            }

            for (DiscountMembership dm: discountMemberships) {
                if (contact.hasMembership(dm.membershipProduct) ) {
                    return true
                } else if (!dm.discountMembershipRelationTypes.empty) {
                    List<Long> relationTypes = dm.discountMembershipRelationTypes*.contactRelationType*.id
                    Set<Contact> relatedContacts = []
                    relatedContacts += contact.toContacts.findAll { it.relationType.id in relationTypes }.collect { it.toContact }.toSet()
                    relatedContacts += contact.fromContacts.findAll { it.relationType.id in relationTypes }.collect { it.fromContact }.toSet()

                    if (relatedContacts.any { it -> it.hasMembership(dm.membershipProduct)}) {
                        return  true
                    }
                }

            }

            return false
        }
    }
    /**
     * Add the discount to given  class available discounts list
     */
    @API
    void addCourseClass(CourseClass courseClass) {
        if (courseClass) {
            objectContext.localObject(courseClass).addDiscount(this as Discount)
        }
    }

    /**
     * Remove the discount from given class available discounts list
     */
    @API
    void removeCourseClass(CourseClass courseClass) {
        if (courseClass) {
            objectContext.localObject(courseClass).removeDiscount(this as Discount)
        }
    }

}
