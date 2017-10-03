package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
class SolrCourse {
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

    @Field('price')
    String[] price

    @Field('class_code')
    String[] classCode

    @Field('end')
    Date[] classEnd

    @Field('class_start')
    Date[] classStart

    //tutor
    @Field('tutorId')
    Long[] tutorId

    @Field('tutor')
    String[] tutor

    //session
    @Field('when')
    String[] when

    @Field('siteId')
    Long[] siteId

    @Field('course_loc')
    String[] location

    @Field('course_postcode')
    String[] postcode

    @Field('course_suburb')
    String[] suburb

    @Field('tagId')
    Long[] tagId
}
