package ish.oncourse.solr.model

import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
@ToString
@AutoClone
class SCourseClass {

    @Field('classId')
    String id
    
    @Field('classId')
    String siteKey
    
    @Field('doctype')
    String doctype = 'class'
    
    @Field('collegeId')
    long collegeId

    @Field('courseId')
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
    
    @Field('classLoc')
    List<String> location = []

    @Field('classSuburb')
    List<String> suburb = []

    @Field('classPostcode')
    List<String> postcode = []
    
    @Field('price')
    String price

    @Field('content')
    String content

    @Field('siteId')
    List<Long> siteId = []
}
