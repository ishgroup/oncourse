package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.map.LifecycleEvent
import org.apache.cayenne.reflect.LifecycleCallbackRegistry
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 2/8/18.
 */
class CourseSolrUpdateListenerTest {
    private TestContext testContext
    private ObjectContext context
    private CCollege cCollege

    @Before
    void before() {
        testContext = new TestContext()
        testContext.open()

        context = testContext.serverRuntime.newContext()
        cCollege = new DataContext(objectContext: context).newCollege()
    }
    
    @Test
    void testCourseDataChange(){
        LifecycleCallbackRegistry registry = testContext.serverRuntime.dataDomain.entityResolver.callbackRegistry
        int count = 0
        Closure countCl = {
            count++
        }
        Object listener = new CourseSolrUpdateListener(closure: countCl)
        
        registry.addListener(LifecycleEvent.POST_PERSIST, Course.class, listener, "onCourseChange")
        registry.addListener(LifecycleEvent.POST_UPDATE, Course.class, listener, "onCourseChange")
        
        Assert.assertEquals(0, count)
        
        //create course and commit
        CCourse course = cCollege.newCourse("course")
        Assert.assertEquals(1, count)
        
        //add class and commit
        course.withClass("class")
        Assert.assertEquals(2, count)
        
        //edit property several times and commit
        course.detail("test").detail("test1").build()
        Assert.assertEquals(3, count)
        
        //edit property again and commit
        course.detail"test2"
        context.commitChanges()
        Assert.assertEquals(4, count)
    }

    @After
    void after() {
        testContext.close()
    }

}
