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
import ish.oncourse.model.Product
import ish.oncourse.willow.EntityRelationService
import ish.oncourse.willow.checkout.functions.GetCourse
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.v2.suggestion.SuggestionRequest
import ish.oncourse.willow.model.v2.suggestion.SuggestionResponse
import ish.oncourse.willow.service.SuggestionApi
import org.apache.cayenne.ObjectContext

import java.time.LocalDateTime

class SuggestionApiService implements SuggestionApi {

    private CayenneService cayenneService
    private CollegeService collegeService
    private EntityRelationService relationService

    @Inject
    SuggestionApiService(CayenneService cayenneService, CollegeService collegeService, EntityRelationService relationService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
        this.relationService = relationService
    }

    @Override
    SuggestionResponse getSuggestion(SuggestionRequest suggestionRequest) {

        ObjectContext context = cayenneService.newContext()
        College college = collegeService.college

        List<String> courseClasses = new ArrayList<>()
        List<String> products = new ArrayList<>()

        List<Course> cayenneCourses = new ArrayList<>()
        List<Product> cayenneProducts = new ArrayList<>()

        if (suggestionRequest.courseIds != null) {

            cayenneCourses = suggestionRequest.courseIds.collect { id ->
                new GetCourse(context, college, (id as String).trim()).get()
            }.asList()

            cayenneCourses.each { course ->
                products.addAll(relationService.getSuggestedProducts(course).
                        findAll { it.isOnSale && it.isWebVisible }.
                        collect { it.id.toString() })
                courseClasses.addAll(relationService.getSuggestedCourses(course).
                        collect { getNearestCourseClass(it) }.
                        findAll { it != null }.
                        collect { it.id.toString() }
                )
            }
        }

        if (suggestionRequest.productIds != null) {

            cayenneProducts = suggestionRequest.productIds.collect { id ->
                new GetProduct(context, college, (id as String).trim()).get()
            }.asList()

            cayenneProducts.each { product ->
                products.addAll(relationService.getSuggestedProducts(product).
                        findAll { it.isOnSale && it.isWebVisible }.
                        collect { it.id.toString() })
                courseClasses.addAll(relationService.getSuggestedCourses(product).
                        collect { getNearestCourseClass(it) }.
                        findAll { it != null }.
                        collect { it.id.toString() }
                )
            }
        }

        // filtering entities already in the cart
        products = products.findAll {product ->
            !cayenneProducts.collect {it.id.toString()}.contains(product)
        }
        courseClasses = courseClasses.findAll { suggestion ->
            cayenneCourses.findAll { course ->
                course.courseClasses.collect {it.id.toString()}.contains(suggestion)
            }.isEmpty()
        }

        new SuggestionResponse().courseClasses(courseClasses.asList()).products(products.asList())
    }

    private CourseClass getNearestCourseClass(Course course) {
        List<CourseClass> availableClasses = course.availableClasses;
        return course.isWebVisible ? availableClasses.empty ? null : availableClasses.first() : null
    }

}
