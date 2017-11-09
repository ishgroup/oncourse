package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
class STag {
    
    @Field('id')
    String id
    
    @Field('collegeId')
    long collegeId
    
    @Field('doctype')
    String doctype = 'tag'

    @Field('name')
    String name
    
}
