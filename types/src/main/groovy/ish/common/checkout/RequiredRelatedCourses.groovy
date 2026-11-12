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
import ish.common.types.EntityRelationCartAction

import java.util.function.Function
import java.util.function.Predicate

import static ish.common.types.EntityRelationCartAction.ADD_ALLOW_REMOVAL
import static ish.common.types.EntityRelationCartAction.ADD_NO_REMOVAL

/**
 * Shared (onCourse server + willow checkout) rules for course -> course entity relations
 * which require an enrolment in the related course.
 *
 * Willow checkout is the reference: a relation "course A -> course B" whose relation type adds B to the shopping cart
 * ({@link #CART_ADD_ACTIONS}) makes B required for A. B is added to the cart together with A unless the relation type
 * has 'consider history' set and the student already has an enrolment in B.
 * So an enrolment in A is allowed when B is in the same checkout for the same student, or (with 'consider history')
 * when the student already has an enrolment in B.
 *
 * Entity agnostic: callers pass their own Course model and the way to check the student's enrolment history.
 */
@CompileStatic
final class RequiredRelatedCourses {

    /**
     * Cart actions which add the related item to the shopping cart automatically.
     */
    static final List<EntityRelationCartAction> CART_ADD_ACTIONS = Collections.unmodifiableList([ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL])

    private RequiredRelatedCourses() {}

    /**
     * @return true if relation type adds related item to the shopping cart, so the related course is required (see class description)
     */
    static boolean isAddedToCart(EntityRelationCartAction cartAction) {
        cartAction != null && CART_ADD_ACTIONS.contains(cartAction)
    }

    /**
     * @return true if the student's existing enrolment in the related course satisfies the relation
     */
    static boolean isSatisfiedByHistory(Boolean considerHistory) {
        Boolean.TRUE == considerHistory
    }

    /**
     * @param requiredCourses map: required course -> 'consider history' flag of the relation type
     *        (if several relations point to the same course, history counts only when all of them consider it, see {@link #mergeConsiderHistory})
     * @param isEnrolled checks whether the student already has an enrolment in the course
     * @param coursesInCheckout courses the same student is enrolling in within the same checkout
     * @return distinct required courses which are neither in the checkout nor (when history is considered) in the student's history
     */
    static <T> List<T> getMissingCourses(Map<T, Boolean> requiredCourses, Predicate<T> isEnrolled, Collection<T> coursesInCheckout) {
        if (!requiredCourses) {
            return []
        }
        requiredCourses.findAll { T course, Boolean considerHistory ->
            course != null &&
                    !(coursesInCheckout?.contains(course)) &&
                    !(isSatisfiedByHistory(considerHistory) && isEnrolled.test(course))
        }.keySet().toList()
    }

    /**
     * @return names of missing courses, see {@link #getMissingCourses}
     */
    static <T> List<String> getMissingCourseNames(Map<T, Boolean> requiredCourses, Predicate<T> isEnrolled, Collection<T> coursesInCheckout, Function<T, String> name) {
        getMissingCourses(requiredCourses, isEnrolled, coursesInCheckout).collect { T course -> name.apply(course) }
    }

    /**
     * Adds required course to the map; if the course is already there, history counts only when every relation considers it.
     */
    static <T> void mergeConsiderHistory(Map<T, Boolean> requiredCourses, T course, Boolean considerHistory) {
        if (course == null) {
            return
        }
        Boolean history = isSatisfiedByHistory(considerHistory)
        requiredCourses.put(course, requiredCourses.containsKey(course) ? (requiredCourses.get(course) && history) : history)
    }

    static String getErrorMessage(String studentName, String courseName, Collection<String> missingCourseNames) {
        "$studentName is unable to enrol in $courseName without an enrolment in ${missingCourseNames.join(', ')}. Please add it to the checkout.".toString()
    }
}
