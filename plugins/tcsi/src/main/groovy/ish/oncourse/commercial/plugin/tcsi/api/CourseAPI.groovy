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
import ish.oncourse.server.cayenne.CourseCustomField
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.scripting.api.EmailService
import org.apache.commons.lang3.StringUtils

import java.math.RoundingMode
import java.time.Duration

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

@CompileDynamic
class CourseAPI extends TCSI_API {

    static final String COURSES_PATH = TCSIIntegration.BASE_API_PATH + '/courses'
    private Course highEducation
    private EntityRelationType highEducationType

    CourseAPI(Course highEducation, EntityRelationType highEducationType, RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
        this.highEducationType = highEducationType
        this.highEducation = highEducation
    }
    
    String createCourseGroup() {
        String message = "Create course"
        client.request(POST, JSON) {
            uri.path = COURSES_PATH
            body = getCourseData(true)
            response.success = { resp, result ->
                def course = handleResponce(result as List, message)
                def uid = course['courses_uid'].toString()
                return uid
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
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
    
    void update(String courseUid) {
        
        String message = "update course duration, skip the error and continue export of units"
        try {
      
            client.request(PATCH, JSON) {
                uri.path = COURSES_PATH +"/$courseUid"
                body = getCourseData()
                response.success = { resp, result ->
                    handleResponce(result, message)
                }
                response.failure =  { resp, body ->
                    interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
                }
            }
        } catch(Exception ignore) {
            //ignore
        }
    }


    @CompileDynamic
    private String getCourseData(Boolean create = false) {
        Map<String, Object> course = [:]
        List<Course> units = TCSIUtils.getUnitCourses(highEducation, highEducationType)
        List<CourseClass> classes = (units*.courseClasses.flatten() as List<CourseClass>).sort { CourseClass clazz -> clazz.startDateTime}
        if (create) {
            course["course_code"] = highEducation.qualification.nationalCode
            course["course_name"] = "$highEducation.qualification.level $highEducation.qualification.title"

            BigDecimal studyLoad = BigDecimal.ZERO
            units*.fullTimeLoad.findAll { StringUtils.trimToNull(it) && it.number }.each {
                studyLoad += new BigDecimal(it)
            }

            if (StringUtils.trimToNull(highEducation.fullTimeLoad) && highEducation.fullTimeLoad.number) {
                studyLoad += new BigDecimal(highEducation.fullTimeLoad)
            }
            course["course_of_study_load"] = studyLoad.setScale(2, RoundingMode.UP)
            course["course_effective_from_date"] = (classes.first().startDateTime ?: new Date()).format(DATE_FORMAT)
        }

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
        if (create) {
            return [JsonOutput.toJson(courseData)]
        } else {
            return JsonOutput.toJson(courseData)
        }

    }
}
