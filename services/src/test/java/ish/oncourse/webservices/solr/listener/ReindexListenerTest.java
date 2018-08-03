/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.solr.listener;

import ish.oncourse.model.*;
import ish.oncourse.services.lifecycle.TaggableListener;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import ish.oncourse.webservices.solr.SolrUpdateCourseDocumentsListener;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.commitlog.CommitLogModule;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SelectById;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ReindexListenerTest {


    private ServerRuntime cayenneRuntime;
    private TestContext testContext;

    private List<Long> updateIds;
    private List<Long> removeIds;


    @Before
    public void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(true).open();
        new LoadDataSet().dataSetFile("ish/oncourse/webservices/solr/listener/ReindexListenerTestDataSet.xml").load(testContext.getDS());
        
        updateIds = new ArrayList<>();
        removeIds = new ArrayList<>();

        SolrUpdateCourseDocumentsListener listener = new SolrUpdateCourseDocumentsListener() {
            @Override
            protected ServerRuntime getCayenne() {
                return cayenneRuntime;
            }
            
            @Override
            public void executeUpdate(Set<Long> courseIdToUpdate) {
                updateIds.addAll(courseIdToUpdate);
            }
            
            @Override
            public void executeDelete(Set<Long> courseIdToRemove) {
                removeIds.addAll(courseIdToRemove);
            }
            
        };

        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", CommitLogModule.extend()
                .addListener(listener).module());
        cayenneRuntime.getChannel().getEntityResolver().getCallbackRegistry().addListener(new TaggableListener());


    }
    
    @Test
    public void testTagUnTag() {
        ObjectContext objectContext = cayenneRuntime.newContext();
        Tag tag = SelectById.query(Tag.class, 2L).selectOne(objectContext);
        
        Taggable taggable = objectContext.newObject(Taggable.class);
        taggable.setCollege(tag.getCollege());
        taggable.setEntityAngelId(1L);
        taggable.setEntityIdentifier("Course");
        taggable.setModified(new Date());
        
        TaggableTag taggableTag = objectContext.newObject(TaggableTag.class);
        taggableTag.setTag(tag);
        taggableTag.setCollege(tag.getCollege());
        taggable.addToTaggableTags(taggableTag);

        objectContext.commitChanges();

        assertEquals(1, updateIds.size());
        assertEquals((Long)1L , updateIds.get(0));
        updateIds.clear();
        objectContext.deleteObjects(taggable);
        objectContext.deleteObjects(taggableTag);
        
        objectContext.commitChanges();

        assertEquals(1, updateIds.size());
        assertEquals((Long)1L , updateIds.get(0));
    }

    @Test
    public void testSetWebVisible() {
        ObjectContext objectContext = cayenneRuntime.newContext();
        Course course = SelectById.query(Course.class, 2L).selectOne(objectContext);

        course.setIsWebVisible(false);
        objectContext.commitChanges();
        
        assertEquals(0, updateIds.size());
        assertEquals(1, removeIds.size());
        assertEquals((Long)2L , removeIds.get(0));

        removeIds.clear();
        
        course.setIsWebVisible(true);
        objectContext.commitChanges();
        
        assertEquals(0, removeIds.size());
        assertEquals(1, updateIds.size());
        assertEquals((Long)2L , updateIds.get(0));

    }

    @Test
    public void testChangeClass() {
        ObjectContext objectContext = cayenneRuntime.newContext();
        CourseClass clazz = SelectById.query(CourseClass.class, 2L).selectOne(objectContext);

        Session session = objectContext.newObject(Session.class);
        session.setCollege(clazz.getCollege());
        session.setStartDate(new Date());
        session.setEndDate(new Date());
        session.setCourseClass(clazz);
        testChanges(objectContext, 2L);
        
        Room room1 = SelectById.query(Room.class, 1L).selectOne(objectContext);
        session.setRoom(room1);
        testChanges(objectContext, 2L);

        Room room2 = SelectById.query(Room.class, 2L).selectOne(objectContext);
        session.setRoom(room2);
        testChanges(objectContext, 2L);
        
        session.setRoom(null);
        testChanges(objectContext, 2L);

        clazz.setStartDate(new Date());
        testChanges(objectContext, 2L);
        
        clazz.setEndDate(new Date());
        testChanges(objectContext, 2L);
    }
    
    @Test
    public void testChangeTutor() {
        ObjectContext objectContext = cayenneRuntime.newContext();
        
        TutorRole tutorRole = SelectById.query(TutorRole.class, 1L).selectOne(objectContext);
        CourseClass courseClass = tutorRole.getCourseClass();
        Tutor tutor = tutorRole.getTutor();

        objectContext.deleteObject(tutorRole);
        testChanges(objectContext, 1L);
        
        TutorRole newRole = objectContext.newObject(TutorRole.class);
        newRole.setCollege(tutor.getCollege());
        newRole.setCourseClass(courseClass);
        newRole.setTutor(tutor);
        newRole.setInPublicity(true);
        testChanges(objectContext, 1L);
    }


    @Test
    public void testChangeSite() {
        ObjectContext objectContext = cayenneRuntime.newContext();

        Site site = SelectById.query(Site.class, 1L).selectOne(objectContext);
        
        site.setLatitude(new BigDecimal(1.33));

        testChanges( objectContext, 1L,2L,3L,4L,5L);
    }
    
    
    private void testChanges(ObjectContext objectContext, Long... ids) {
        objectContext.commitChanges();

        assertEquals(ids.length, updateIds.size());
        assertEquals(0, removeIds.size());
        assertArrayEquals(ids , updateIds.toArray());

        updateIds.clear();
    }

    @After
    public void after() {
        cayenneRuntime.shutdown();
        testContext.close();
    }
}
