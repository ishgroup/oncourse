/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.cayenne.query.SelectQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/cayenne/doublePrefetchTest.xml")
class DoublePrefetchTest extends CayenneIshTestCase {

    @Test
    void testNoPrefetch() {
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)
        List<CourseClass> list = cayenneContext.select(query)

        for (CourseClass cc : list) {
            for (Enrolment enrolment : cc.getEnrolments()) {
                Assertions.assertNotNull(enrolment.getStudent())
                Assertions.assertNotNull(enrolment.getStudent().getContact())
            }

            for (CourseClassTutor cct : cc.getTutorRoles()) {
                Assertions.assertNotNull(cct.getTutor())
                Assertions.assertNotNull(cct.getTutor().getContact())
            }
        }

    }

    // TODO: enable test when CAY-1802 is fixed
    
    @Test
    @Disabled
    void testDoublePrefetchJointSemantics() {
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS)
        List<CourseClass> list = cayenneContext.select(query)

        for (CourseClass cc : list) {
            for (Enrolment enrolment : cc.getEnrolments()) {
                Assertions.assertNotNull(enrolment.getStudent())
                Assertions.assertNotNull(enrolment.getStudent().getContact())
            }

            for (CourseClassTutor cct : cc.getTutorRoles()) {
                Assertions.assertNotNull(cct.getTutor())
                Assertions.assertNotNull(cct.getTutor().getContact())
            }
        }

    }


    // TODO: enable test when CAY-1802 is fixed
    
    @Test
    @Disabled
    void testDoublePrefetchJointSemantics2() {
        Expression e = CourseClass.ID.eq(1L)
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class, e)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS)

        CourseClass cc = cayenneContext.selectOne(query)

        for (Enrolment enrolment : cc.getEnrolments()) {
            Assertions.assertNotNull(enrolment.getStudent())
            Assertions.assertNotNull(enrolment.getStudent().getContact())
        }

        for (CourseClassTutor cct : cc.getTutorRoles()) {
            Assertions.assertNotNull(cct.getTutor())
            Assertions.assertNotNull(cct.getTutor().getContact())
        }
    }

    // TODO: enable test when CAY-1802 is fixed
    
    @Test
    @Disabled
    void testDoublePrefetchDisjointSemantics() {
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS)
        List<CourseClass> list = cayenneContext.select(query)

        for (CourseClass cc : list) {
            for (Enrolment enrolment : cc.getEnrolments()) {
                Assertions.assertNotNull(enrolment.getStudent())
                Assertions.assertNotNull(enrolment.getStudent().getContact())
            }

            for (CourseClassTutor cct : cc.getTutorRoles()) {
                Assertions.assertNotNull(cct.getTutor())
                Assertions.assertNotNull(cct.getTutor().getContact())
            }
        }
    }

    // TODO: enable test when CAY-1802 is fixed
    
    @Test
    @Disabled
    void testDoublePrefetchDisjointSemantics2() {
        Expression e = CourseClass.ID.eq(1L)
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class, e)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.DISJOINT_PREFETCH_SEMANTICS)

        CourseClass cc = cayenneContext.selectOne(query)

        for (Enrolment enrolment : cc.getEnrolments()) {
            Assertions.assertNotNull(enrolment.getStudent())
            Assertions.assertNotNull(enrolment.getStudent().getContact())
        }

        for (CourseClassTutor cct : cc.getTutorRoles()) {
            Assertions.assertNotNull(cct.getTutor())
            Assertions.assertNotNull(cct.getTutor().getContact())
        }
    }

    // TODO: enable test when CAY-1802 is fixed
    
    @Test
    @Disabled
    void testDoublePrefetchDisjointByIdSemantics() {
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS)

        List<CourseClass> list = cayenneContext.select(query)

        for (CourseClass cc : list) {
            for (Enrolment enrolment : cc.getEnrolments()) {
                Assertions.assertNotNull(enrolment.getStudent())
                Assertions.assertNotNull(enrolment.getStudent().getContact())
            }

            for (CourseClassTutor cct : cc.getTutorRoles()) {
                Assertions.assertNotNull(cct.getTutor())
                Assertions.assertNotNull(cct.getTutor().getContact())
            }
        }
    }

    // TODO: enable test when CAY-1802 is fixed
    @Test
    @Disabled
    void testDoublePrefetchDisjointByIdSemantics2() {
        Expression e = CourseClass.ID.eq(1L)
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class, e)
        addPrefetchesWithSemantics(query, PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS)

        CourseClass cc = cayenneContext.selectOne(query)

        for (Enrolment enrolment : cc.getEnrolments()) {
            Assertions.assertNotNull(enrolment.getStudent())
            Assertions.assertNotNull(enrolment.getStudent().getContact())
        }

        for (CourseClassTutor cct : cc.getTutorRoles()) {
            Assertions.assertNotNull(cct.getTutor())
            Assertions.assertNotNull(cct.getTutor().getContact())
        }
    }

    private void addPrefetchesWithSemantics(SelectQuery query, int semantics) {
        query.addPrefetch(CourseClass.ENROLMENTS_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STUDENT_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.ENROLMENTS_PROPERTY + "." + Enrolment.STUDENT_PROPERTY + "." + Student.CONTACT_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.TUTOR_ROLES_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.TUTOR_ROLES_PROPERTY + "." + CourseClassTutor.TUTOR_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.TUTOR_ROLES_PROPERTY + "." + CourseClassTutor.TUTOR_PROPERTY + "." + Tutor.CONTACT_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.SESSIONS_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.SESSIONS_PROPERTY + "." + Session.ATTENDANCE_PROPERTY).setSemantics(semantics)
        query.addPrefetch(CourseClass.SESSIONS_PROPERTY + "." + Session.ATTENDANCE_PROPERTY + "." + Attendance.STUDENT_PROPERTY).setSemantics(semantics)
    }

}
