/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.common.checkout

import groovy.transform.CompileStatic
import ish.common.types.EnrolmentStatus
import ish.common.types.EntityRelationCartAction

import java.util.function.BooleanSupplier
import java.util.function.Function
import java.util.function.Predicate

/**
 * Shared (onCourse server + willow checkout) rules for course -> course entity relations.
 *
 * Willow checkout is the reference. For a relation "course A -> course B":
 * <ul>
 *     <li>B must be in the same checkout as A ({@link #isRequiredInCheckout}) when the relation type adds B to the shopping cart
 *     ({@link EntityRelationCartAction#isAddedToCart}), unless the relation type considers history and the student already has
 *     an enrolment in B with one of {@link #PREVIOUS_ENROLMENT_STATUSES}.
 *     Willow adds such B to the shopping cart.</li>
 *     <li>B is mandatory ({@link #isMandatory}) when it also can not be removed from the cart ({@link EntityRelationCartAction#ADD_NO_REMOVAL}).
 *     An enrolment in A is rejected when a mandatory B is required but is not in the checkout ({@link #getMissingCourses}).</li>
 * </ul>
 * Entity agnostic: callers pass their own Course model and the way to check the student's enrolment history.
 */
@CompileStatic
final class RequiredRelatedCourses {

    /**
     * Statuses of an existing enrolment in the related course which count as student's history (see 'consider history' of relation type).
     */
    static final List<EnrolmentStatus> PREVIOUS_ENROLMENT_STATUSES = Collections.unmodifiableList([EnrolmentStatus.SUCCESS])

    private RequiredRelatedCourses() {}

    /**
     * @param considerHistory 'consider history' flag of the relation type
     * @param enrolledBefore checks (lazily) whether the student has an enrolment in the related course with {@link #PREVIOUS_ENROLMENT_STATUSES}
     * @return true if the student's existing enrolment in the related course satisfies the relation
     */
    static boolean isSatisfiedByHistory(Boolean considerHistory, BooleanSupplier enrolledBefore) {
        Boolean.TRUE == considerHistory && enrolledBefore.getAsBoolean()
    }

    /**
     * @return true if the related course must be in the same checkout (willow adds it to the shopping cart)
     */
    static boolean isRequiredInCheckout(EntityRelationCartAction cartAction, Boolean considerHistory, BooleanSupplier enrolledBefore) {
        cartAction != null && cartAction.addedToCart && !isSatisfiedByHistory(considerHistory, enrolledBefore)
    }

    /**
     * @return true if the related course added to the shopping cart can not be removed from it, so the enrolment is rejected without it
     */
    static boolean isMandatory(EntityRelationCartAction cartAction) {
        cartAction != null && cartAction.addedToCart && !cartAction.removable
    }

    /**
     * Collects mandatory related courses: course -> 'consider history' flag.
     * If several relations point to the same course, history counts only when all of them consider it.
     * Not mandatory relations ({@link #isMandatory}) are ignored.
     */
    static <T> void addMandatoryCourse(Map<T, Boolean> mandatoryCourses, T course, EntityRelationCartAction cartAction, Boolean considerHistory) {
        if (course == null || !isMandatory(cartAction)) {
            return
        }
        boolean history = Boolean.TRUE == considerHistory
        mandatoryCourses.put(course, mandatoryCourses.containsKey(course) ? (mandatoryCourses.get(course) && history) : history)
    }

    /**
     * @param mandatoryCourses see {@link #addMandatoryCourse}
     * @param enrolledBefore checks whether the student has an enrolment in the course with {@link #PREVIOUS_ENROLMENT_STATUSES}
     * @param coursesInCheckout courses the same student is enrolling in within the same checkout
     * @return mandatory courses which are required in checkout ({@link #isRequiredInCheckout}) but are not there
     */
    static <T> List<T> getMissingCourses(Map<T, Boolean> mandatoryCourses, Predicate<T> enrolledBefore, Collection<T> coursesInCheckout) {
        if (!mandatoryCourses) {
            return []
        }
        mandatoryCourses.findAll { T course, Boolean considerHistory ->
            course != null &&
                    !(coursesInCheckout?.contains(course)) &&
                    isRequiredInCheckout(EntityRelationCartAction.ADD_NO_REMOVAL, considerHistory, { enrolledBefore.test(course) } as BooleanSupplier)
        }.keySet().toList()
    }

    /**
     * @return names of missing courses, see {@link #getMissingCourses}
     */
    static <T> List<String> getMissingCourseNames(Map<T, Boolean> mandatoryCourses, Predicate<T> enrolledBefore, Collection<T> coursesInCheckout, Function<T, String> name) {
        getMissingCourses(mandatoryCourses, enrolledBefore, coursesInCheckout).collect { T course -> name.apply(course) }
    }

    static String getErrorMessage(String studentName, String courseName, Collection<String> missingCourseNames) {
        "$studentName is unable to enrol in $courseName without an enrolment in ${missingCourseNames.join(', ')}. Please add it to the checkout.".toString()
    }
}
