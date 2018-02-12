package ish.oncourse.solr.update.listener

import ish.oncourse.model.CourseClass
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.commitlog.CommitLogModule
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Assert
import org.junit.Test

/**
 * Created by alex on 2/8/18.
 */
class CourseSolrUpdateListenerTest {
    private TestContext testContext
    private ObjectContext context
    private CCollege cCollege
    
    @Test
    void testCourseDataChange(){
        int updateCount = 0
        int deleteCount = 0
        
        SolrUpdateCourseDocumentsListener listener = new SolrUpdateCourseDocumentsListener() {
            @Override
            void executeUpdate(Set<ObjectId> objectsItUpdate) {
                if (!objectsItUpdate.isEmpty())
                    updateCount++
            }

            @Override
            void executeDelete(Set<ObjectId> objectsItDelete) {
                if (!objectsItDelete.isEmpty())
                    deleteCount++
            }
        }

        ServerRuntime runtime = new ServerRuntime("cayenne-oncourse.xml", CommitLogModule.extend()
                .addListener(listener).module())
        testContext = new TestContext(serverRuntime: runtime)
        
        testContext.open()

        context = testContext.serverRuntime.newContext()
        cCollege = new DataContext(objectContext: context).newCollege()
        
        //create course and commit
        CCourse course = cCollege.newCourse("course")
        Assert.assertEquals(1, updateCount)
        
        //add class and commit
        CourseClass clazz = course.newCourseClass("class").build().courseClass
        Assert.assertEquals(2, updateCount)
        
        //edit property several times and commit
        course.detail("test").detail("test1").build()
        Assert.assertEquals(3, updateCount)
        
        //edit property again and commit
        course.detail"test2"
        context.commitChanges()
        Assert.assertEquals(4, updateCount)
        
        course.isWebVisible(false)
        context.commitChanges()
        Assert.assertEquals(1, deleteCount)

        context.deleteObject(clazz)
        context.commitChanges()
        Assert.assertEquals(2, deleteCount)
    }

    @After
    void after() {
        testContext.close()
    }

}
