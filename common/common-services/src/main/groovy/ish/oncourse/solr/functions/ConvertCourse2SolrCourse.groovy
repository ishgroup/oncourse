package ish.oncourse.solr.functions

import groovy.transform.CompileStatic
import ish.oncourse.model.Course
import ish.oncourse.solr.model.SolrCourse

/**
 * Convert Course to SolrCourse
 */
@CompileStatic
class ConvertCourse2SolrCourse {

    Closure<String> startDate

    private SolrCourse result

    SolrCourse convert(Course course) {
        result = new SolrCourse().with {
            it.id = course.id
            it.collegeId = course.college.id
            it.code = course.code
            it.name = course.name
            it.detail = course.detail
            it
        }

        return result
    }
}
