/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import com.google.gson.Gson;
import ish.oncourse.model.*;
import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SolrQueryBuilder;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class CoursesSolrTest extends AbstractSolrTest {

    @Override
    protected String getDataSetResource() {
        return "ish/oncourse/solr/SolrCourseCoreTestDataSet.xml";
    }

    @Test
    public void testFullImport() throws Exception {
        Map response = fullImport();

        assertEquals("2", ((Map) response.get("statusMessages")).get("Total Documents Processed"));
    }

    @Test
    public void testDeltaImport() throws Exception {

        Map response = fullImport();

        assertEquals("2", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

        Gson gson = new Gson();

        SolrQuery query = SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: AAV");
        response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), query)), Map.class);

        assertEquals(1.0, ((Map) response.get("response")).get("numFound"));

        response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(),
                        SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: BBB"))),
                Map.class);

        assertEquals(0.0, ((Map) response.get("response")).get("numFound"));

        Course course = testCourseAdd(gson);
        testCourseModified(course);

        CourseClass courseClass = testCourseClassAdd(course);
        testCourseClassModified(courseClass);
        testCourseClassEndDate(courseClass);

        Session session = testSessionAdd(courseClass);
        testSessionModified(session);
    }

    @Test
    public void testSite() throws Exception {
        Course course = modelBuilder.createCourse();
        CourseClass courseClass = modelBuilder.createCourseClass(course);
        Session session = modelBuilder.createSession(courseClass);
        fullImport();

        Site site1 = testSiteAdd(courseClass);
        Site site2 = testSiteAdd(session);

        site1.setModified(new Date());
        site1.getObjectContext().commitChanges();

        assertDeltaImport();

        site2.setModified(new Date());
        site2.getObjectContext().commitChanges();

        assertDeltaImport();
    }

    @Test
    public void testTag() throws Exception {
        Course course = modelBuilder.createCourse();
        CourseClass courseClass = modelBuilder.createCourseClass(course);
        Session session = modelBuilder.createSession(courseClass);
        Site site1 = modelBuilder.createSiteForSession(session);
        Site site2 = modelBuilder.createSiteForCourseClass(courseClass);
        fullImport();

        Tag tag = modelBuilder.createTag();
        TaggableTag taggableTag = modelBuilder.linkTagToCourse(course, tag);

        assertDeltaImport();

        taggableTag.getTaggable().setModified(new Date());
        taggableTag.getObjectContext().commitChanges();

        assertDeltaImport();


    }

    @Test
    public void testTutor() throws Exception {
        Course course = modelBuilder.createCourse();
        CourseClass courseClass = modelBuilder.createCourseClass(course);
        Session session = modelBuilder.createSession(courseClass);
        Site site1 = modelBuilder.createSiteForSession(session);
        Site site2 = modelBuilder.createSiteForCourseClass(courseClass);
        Contact contact = modelBuilder.createContact();
        Tutor tutor = modelBuilder.createTutor(contact);
        modelBuilder.attachTutorToCourseClass(tutor, courseClass);
        Map response = fullImport();

        assertEquals("3", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

        SolrQuery query = SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("tutorId: " + tutor.getAngelId());

        Gson gson = new Gson();
        response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), query)), Map.class);
        assertEquals(1.0, ((Map) response.get("response")).get("numFound"));
    }



    private Site testSiteAdd(Session session) throws Exception {
        Site site = modelBuilder.createSiteForSession(session);
        assertDeltaImport();
        return site;
    }

    private Site testSiteAdd(CourseClass courseClass) throws Exception {
        Site site = modelBuilder.createSiteForCourseClass(courseClass);
        assertDeltaImport();
        return site;
    }

    private Session testSessionAdd(CourseClass courseClass) throws Exception {
        Session session = modelBuilder.createSession(courseClass);

        assertDeltaImport();

        return session;
    }


    private Session testSessionModified(Session session) throws Exception {
        session.setModified(new Date());
        session.getObjectContext().commitChanges();

        assertDeltaImport();
        return session;
    }

    private void testCourseClassModified(CourseClass courseClass) throws Exception {
        courseClass.setModified(new Date());
        courseClass.getObjectContext().commitChanges();
        assertDeltaImport();
    }

    private void testCourseClassEndDate(CourseClass courseClass) throws Exception {
        courseClass.setEndDate(new Date());
        courseClass.getObjectContext().commitChanges();
        Thread.sleep(2000);
        assertDeltaImport();
    }

    private CourseClass testCourseClassAdd(Course course) throws Exception {

        CourseClass courseClass = modelBuilder.createCourseClass(course);

        assertDeltaImport();
        return courseClass;
    }


    private void testCourseModified(Course course) throws Exception {
        course.setModified(new Date());
        course.getObjectContext().commitChanges();
        assertDeltaImport();
    }

    private Course testCourseAdd(Gson gson) throws Exception {
        Course course = modelBuilder.createCourse();

        Map response = deltaImport();

        assertEquals("1", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

        response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(),
                        SolrQueryBuilder.valueOf(new SearchParams(), "10", 0, 10).build().addFilterQuery("course_code: " + course.getCode()))),
                Map.class);

        assertEquals(1.0, ((Map) response.get("response")).get("numFound"));
        assertDeltaImportEmpty();

        return course;
    }
}
