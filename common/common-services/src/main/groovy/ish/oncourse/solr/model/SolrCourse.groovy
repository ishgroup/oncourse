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

    @Field('course_code')
    String code

    @Field('startDate')
    Date startDate
}
