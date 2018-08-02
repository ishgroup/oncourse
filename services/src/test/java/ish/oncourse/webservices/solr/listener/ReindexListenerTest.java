/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.solr.listener;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
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

    }
    

    @After
    public void after() {
        cayenneRuntime.shutdown();
        testContext.close();
    }
}
