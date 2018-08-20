package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
@ToString
class SCourse {
    @Field('id')
    String id

    @Field('collegeId')
    long collegeId

    @Field('doctype')
    String doctype = 'course'

    @Field('name')
    String name

    @Field('detail')
    String detail

    //course class
    @Field('course_code')
    String code

    @Field('startDate')
    Date startDate

    //course class fields
    @Field('price')
    List<String> price = []

    @Field('class_code')
    List<String> classCode = []

    @Field('class_start')
    List<Date> classStart = []

    @Field('end')
    List<Date> classEnd = []

    //tutor fields
    @Field('tutorId')
    List<Long> tutorId = []

    @Field('tutor')
    List<String> tutor = []

    //session
    @Field('when')
    List<String> when = []

    //site
    @Field('siteId')
    List<Long> siteId = []

    @Field('course_loc')
    List<String> location = []

    @Field('course_postcode')
    List<String> postcode = []

    @Field('course_suburb')
    List<String> suburb = []

    @Field('tagId')
    List<Long> tagId = []

    List<SCourseClass> classes = []

}
