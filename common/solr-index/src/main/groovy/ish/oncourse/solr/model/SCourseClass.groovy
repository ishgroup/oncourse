package ish.oncourse.solr.model

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@CompileStatic
@EqualsAndHashCode
@ToString
class SCourseClass {
    String price

    String classCode

    Date classEnd

    Date classStart

    List<SSession> sessions

    List<SContact> contacts

    List<SSite> sites
}
