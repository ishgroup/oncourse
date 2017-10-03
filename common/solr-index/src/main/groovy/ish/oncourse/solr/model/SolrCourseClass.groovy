package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
class SolrCourseClass {
    @Field('price')
    String price

    @Field('class_code')
    String classCode

    @Field('end')
    Date classEnd

    @Field('class_start')
    Date classStart
}
