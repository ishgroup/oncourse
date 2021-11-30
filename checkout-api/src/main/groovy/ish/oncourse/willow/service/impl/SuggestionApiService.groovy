/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.willow.EntityRelationService
import ish.oncourse.willow.checkout.functions.GetCourse
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.v2.suggestion.SuggestionResponse
import ish.oncourse.willow.service.SuggestionApi
import org.apache.cayenne.ObjectContext

import java.time.LocalDateTime

class SuggestionApiService implements SuggestionApi {

    @Inject
    private CayenneService cayenneService

    @Inject
    private CollegeService collegeService

    @Inject
    private EntityRelationService relationService


    @Override
    SuggestionResponse getSuggestion(String courseIds, String productIds) {

        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college

        List<String> courseClasses = new ArrayList<>()
        List<String> products = new ArrayList<>()

        if (courseIds != null) {
            Arrays.stream(courseIds.split(',')).each { id ->
                def cayenneCourse = new GetCourse(context, college, (id as String).trim()).get()

                products.addAll(relationService.getSuggestedProducts(cayenneCourse).collect {
                    it.id.toString()
                })
                courseClasses.addAll(relationService.getSuggestedCourses(cayenneCourse).
                        collect { getNearestCourseClass(it) }.
                        findAll { it != null }.
                        collect { it.id.toString() }
                )
            }
        }

        if (productIds != null) {
            Arrays.stream(productIds.split(',')).each { id ->
                def cayenneProduct = new GetProduct(context, college, (id as String).trim()).get()

                products.addAll(relationService.getSuggestedProducts(cayenneProduct).collect { it.id.toString() })
                courseClasses.addAll(relationService.getSuggestedCourses(cayenneProduct).
                        collect { getNearestCourseClass(it) }.
                        findAll { it != null }.
                        collect { it.id.toString() }
                )
            }
        }

        return new SuggestionResponse().courseClasses(courseClasses.asList()).products(products.asList())
    }

    private CourseClass getNearestCourseClass(Course course) {
        def courseClass = course.availableClasses
                .findAll { it.startDateTime != null && it.startDateTime.toLocalDateTime().isAfter(LocalDateTime.now()) }
                .findAll { it != null }
                .sort { it.startDateTime }
        return courseClass.empty ? null : courseClass.get(0)
    }

}
