package ish.oncourse.solr.functions.course

import ish.oncourse.model.CourseClass
import ish.oncourse.model.Tutor
import ish.oncourse.model.TutorRole
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.context.CTutor
import org.apache.cayenne.query.ObjectSelect
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.Test

/**
 * Created by alex on 1/26/18.
 */
class SolrCourseQueryWithTutorFilterTest extends ASolrTest {
    
    @Test
    void testSortCoursesWithBeforeAfterFilter() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        String collegeId = cCollege.college.id.toString()
        List<SCourse> actualSCourses
        
        Tutor targetTutor = CTutor.instance(objectContext, cCollege.college, "targetTutor").angelId(1000L).build().tutor
        Tutor otherTutor = CTutor.instance(objectContext, cCollege.college, "otherTutor").angelId(1001L).build().tutor

        cCollege.newCourse("course1").newCourseClassWithSessionsAndTutor("pastTargetTutor", targetTutor, -5, -4).build()
        cCollege.newCourse("course2").newCourseClassWithSessionsAndTutor("pastOtherTutor", otherTutor, -5, -4).build()
        cCollege.newCourse("course3").newCourseClass("pastBothTutorsTargetFirst").withSessionAndTutor(-5, targetTutor).withSessionAndTutor(-4, otherTutor).build()
        cCollege.newCourse("course4").newCourseClass("pastBothTutorsOtherFirst").withSessionAndTutor(-5, otherTutor).withSessionAndTutor(-4, targetTutor).build()

        cCollege.newCourse("course5").newCourseClassWithSessionsAndTutor("currentTargetSite", targetTutor, -1, 1).build()
        cCollege.newCourse("course6").newCourseClassWithSessionsAndTutor("currentOtherTutor", otherTutor, -1, 1).build()
        cCollege.newCourse("course7").newCourseClass("currentBothTutorsTargetFirst").withSessionAndTutor(-1, targetTutor).withSessionAndTutor(1, otherTutor).build()
        cCollege.newCourse("course8").newCourseClass("currentBothTutorsOtherFirst").withSessionAndTutor(-1, otherTutor).withSessionAndTutor(1, targetTutor).build()

        cCollege.newCourse("course9").newCourseClassWithSessionsAndTutor("futureTargetSite", targetTutor, 5, 6).build()
        cCollege.newCourse("course10").newCourseClassWithSessionsAndTutor("futureOtherTutor", otherTutor, 5, 6).build()
        cCollege.newCourse("course11").newCourseClass("futureBothTutorsTargetFirst").withSessionAndTutor(5, targetTutor).withSessionAndTutor(6, otherTutor).build()
        cCollege.newCourse("course12").newCourseClass("futureBothTutorsOtherFirst").withSessionAndTutor(5, otherTutor).withSessionAndTutor(6, targetTutor).build()

        cCollege.newCourse("course13").withSelfPacedClassAndTutor("withTargetTutor", targetTutor).build()
        cCollege.newCourse("course14").withSelfPacedClassAndTutor("withBothTutors", targetTutor, otherTutor).build()
        cCollege.newCourse("course15").withSelfPacedClass("withoutTutor").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: targetTutor.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertTrue(actualSCourses.empty)

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: targetTutor.angelId), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(8, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course5")
        assertTrue(actualSCourses.get(1).name == "course13")
        assertTrue(actualSCourses.get(2).name == "course9")
        assertTrue(actualSCourses.subList(3, 5).name.contains("course7"))
        assertTrue(actualSCourses.subList(3, 5).name.contains("course8"))
        assertTrue(actualSCourses.get(5).name == "course14")
        assertTrue(actualSCourses.subList(6, 8).name.contains("course11"))
        assertTrue(actualSCourses.subList(6, 8).name.contains("course12"))

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: otherTutor.angelId), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(7, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course6")
        assertTrue(actualSCourses.get(1).name == "course10")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.get(4).name == "course14")
        assertTrue(actualSCourses.subList(5, 7).name.contains("course11"))
        assertTrue(actualSCourses.subList(5, 7).name.contains("course12"))
        
        
        //remove tutorRoles of targetTutor and check correct courses reindex (there'll be no classes with targetTutor)
        objectContext.deleteObjects(targetTutor.tutorRoles)
        objectContext.commitChanges()
        List<CourseClass> classes = ObjectSelect.query(CourseClass).where(CourseClass.TUTOR_ROLES.dot(TutorRole.TUTOR).eq(targetTutor)).select(objectContext)
        assertTrue(classes.isEmpty())

        //before reindex job courses are still in index, but there haven't been courseClasses with targetTutor 
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: targetTutor.angelId), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(8, actualSCourses.size())

        job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()

        //after reindex
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: targetTutor.angelId), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertTrue(actualSCourses.empty)
    }
}
