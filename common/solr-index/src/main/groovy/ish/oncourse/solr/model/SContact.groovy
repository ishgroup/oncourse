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
class SContact {
    Long id
    Long tutorId
    String name
}
