package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * User: akoiro
 * Date: 4/11/17
 */
@CompileStatic
@EqualsAndHashCode
@ToString
class SSite {
    Long id
    String state
    String suburb
    String postcode
    String location
}
