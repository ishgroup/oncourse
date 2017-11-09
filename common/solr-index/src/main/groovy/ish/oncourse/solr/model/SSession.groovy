package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * User: akoiro
 * Date: 24/10/17
 */
@CompileStatic
@EqualsAndHashCode
@ToString
class SSession {
    String dayName
    String dayType
    String dayTime
}
