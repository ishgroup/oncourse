package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.ProcessDebug
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCourses
import ish.oncourse.test.context.CSite
import org.apache.solr.client.solrj.response.QueryResponse
import org.junit.Test

/**
 * Created by alex on 1/26/18.
 */
class SolrCourseQueryWithSiteFilterTest extends ASolrTest {
    private CSite targetSite
    private CSite otherSite
    private CSite virtualSite

    @Test
    void test() {
        String collegeId = buildCollege()

        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

        QueryResponse response = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: targetSite.site.id, debugQuery: true), collegeId, null, null).build())

        new ProcessDebug().debugMap(response.debugMap).process()

        List<SCourse> actualSCourses = response.getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course5")
        assertTrue(actualSCourses.get(1).name == "course9")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course11"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course12"))

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: otherSite.site.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course6")
        assertTrue(actualSCourses.get(1).name == "course10")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course11"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course12"))

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: virtualSite.site.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(1, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course13")
    }

    private String buildCollege() {
        targetSite = CSite.instance(objectContext, cCollege.college).build()
        otherSite = CSite.instance(objectContext, cCollege.college).build()
        virtualSite = CSite.instance(objectContext, cCollege.college).isVirtual(true).build()

        cCollege.newCourse("course1").newCourseClassWithSessionsAndSite("pastTargetSite", targetSite, -5, -4).build()
        cCollege.newCourse("course2").newCourseClassWithSessionsAndSite("pastOtherSite", otherSite, -5, -4).build()
        cCollege.newCourse("course3").newCourseClass("pastBothSitesTargetFirst").withSessionAndSite(-5, targetSite).withSessionAndSite(-4, otherSite).build()
        cCollege.newCourse("course4").newCourseClass("pastBothSitesOtherFirst").withSessionAndSite(-5, otherSite).withSessionAndSite(-4, targetSite).build()

        cCollege.newCourse("course5").newCourseClassWithSessionsAndSite("currentTargetSite", targetSite, -1, 1).build()
        cCollege.newCourse("course6").newCourseClassWithSessionsAndSite("currentOtherSite", otherSite, -1, 1).build()

        cCollege.newCourse("course7").newCourseClass("currentBothSitesTargetFirst")
                .withSessionAndSite(-1, targetSite)
                .withSessionAndSite(1, otherSite)
                .build()

        cCollege.newCourse("course8").newCourseClass("currentBothSitesOtherFirst")
                .withSessionAndSite(-1, otherSite)
                .withSessionAndSite(1, targetSite)
                .build()

        cCollege.newCourse("course9")
                .newCourseClass("futureTargetSite")
                .withSessionAndSite(5, targetSite)
                .withSessionAndSite(6, targetSite)
                .build()

        cCollege.newCourse("course10").newCourseClass("futureOtherSite")
                .withSessionAndSite(5, otherSite)
                .withSessionAndSite(6, otherSite)
                .build()

        cCollege.newCourse("course11").newCourseClass("futureBothSitesTargetFirst")
                .withSessionAndSite(5, targetSite)
                .withSessionAndSite(6, otherSite)
                .build()

        cCollege.newCourse("course12").newCourseClass("futureBothSitesOtherFirst")
                .withSessionAndSite(5, otherSite)
                .withSessionAndSite(6, targetSite)
                .build()

        cCollege.newCourse("course13").withSelfPacedClassWithSite("withVirtualSite", virtualSite).build()
        cCollege.newCourse("course14").withSelfPacedClass("withoutSiteSite").build()
        return cCollege.college.id.toString()
    }
}

