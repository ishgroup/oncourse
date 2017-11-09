package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import org.apache.solr.client.solrj.beans.Field

@CompileStatic
@EqualsAndHashCode
class SSuburb {
    @Field('id')
    String id

    @Field('doctype')
    String doctype = 'suburb'

    @Field('suburb')
    String suburb

    @Field('state')
    String state

    @Field('postcode')
    String postcode

    @Field('loc')
    String loc
}
