/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.cayenne.query.SelectQuery
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@CompileStatic
class DoublePrefetchTest extends CayenneIshTestCase {

    private ICayenneService cayenneService


    
    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = DoublePrefetchTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/cayenne/doublePrefetchTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)

        executeDatabaseOperation(rDataSet)
    }


    
    @Test
    void testNoPrefetch() {
        SelectQuery<CourseClass> query = SelectQuery.query(CourseClass.class)

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        List<CourseClass> list = newContext.select(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        List<CourseClass> list = newContext.select(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        CourseClass cc = newContext.selectOne(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        List<CourseClass> list = newContext.select(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        CourseClass cc = newContext.selectOne(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        List<CourseClass> list = newContext.select(query)

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

        DataContext newContext = cayenneService.getNewNonReplicatingContext()

        CourseClass cc = newContext.selectOne(query)

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
