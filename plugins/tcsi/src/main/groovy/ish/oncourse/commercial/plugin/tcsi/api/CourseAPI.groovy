/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.commercial.plugin.tcsi.TCSIUtils
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.scripting.api.EmailService
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode
import java.time.Duration

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET

class CourseAPI extends TCSI_API {

    static final String COURSES_PATH = TCSIIntegration.BASE_API_PATH + '/courses'

    CourseAPI(RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
    }


    String getCourseGroup(String nationalCode) {
        String message = "getting course"
        client.request(GET, JSON) {
            uri.path = COURSES_PATH
            headers.'tcsi-pagination-page'='1'
            headers.'tcsi-pagination-pagesize'='1000'
            response.success = { resp, result ->
                def courses = handleResponce(result, message)
                def course = courses*.course?.find {it.course_code == nationalCode}
                if (course) {
                    return course['courses_uid'].toString()
                }
                return null

            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }


    @CompileDynamic
    static String getCourseData(Course c, EntityRelationType highEducationType) {
        Map<String, Object> course = [:]

        course["course_code"] = c.code
        course["course_name"] = c.name

        List<Course> units = TCSIUtils.getUnitCourses(c, highEducationType)
        BigDecimal studyLoad = BigDecimal.ZERO
        units*.fullTimeLoad.findAll { StringUtils.trimToNull(it) && it.number}.each {
            studyLoad += new BigDecimal(it)
        }

        if (StringUtils.trimToNull(c.fullTimeLoad) && c.fullTimeLoad.number) {
            studyLoad += new BigDecimal(c.fullTimeLoad)
        }
        course["course_of_study_load"] = studyLoad.setScale(2, RoundingMode.UP)

        List<CourseClass> classes = (units*.courseClasses.flatten() as List<CourseClass>).sort { CourseClass clazz -> clazz.startDateTime}

        course["course_effective_from_date"] = (classes.first().startDateTime?:new Date()).format(DATE_FORMAT)
        course["course_effective_to_date"] = (classes.last().endDateTime?:new Date()).format(DATE_FORMAT)


        Long durationDays = 0
        classes.groupBy {it.course}.each { k, v ->
            CourseClass clazz = v.find { it.startDateTime && it.endDateTime }
            if (clazz) {
                durationDays += Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays()
            }
        }

        course["standard_course_duration"] =  new BigDecimal(durationDays/365).setScale(2, RoundingMode.UP)

        def courseData  = [
                'correlation_id' : "courseData_${System.currentTimeMillis()}",
                'course' : course
        ]

        return JsonOutput.toJson([courseData])

    }
}
