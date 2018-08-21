package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
@ToString
class SCourseClass {

    @Field('id')
    String id
    
    @Field('doctype')
    String doctype = 'class'
    
    @Field('collegeId')
    long collegeId

    @Field('course_id')
    long courseId

    @Field('code')
    String classCode

    @Field('tutorId')
    List<Long> tutorId = []

    @Field('tutor')
    List<String> tutor = []

    @Field('when')
    List<String> when = []
    
    @Field('start')
    Date classStart
    
    @Field('end')
    Date classEnd
    
    @Field('class_loc')
    List<String> location = []

    @Field('class_suburb')
    List<String> suburb = []

    @Field('class_postcode')
    List<String> postcode = []
    
    @Field('price')
    String price

    @Field('content')
    String content

    @Field('siteId')
    List<Long> siteId = []
}
