package ish.oncourse.server.scripting

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.SystemEventType
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.integration.EventService
import ish.oncourse.server.integration.OnCourseEventListener
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/scripting/classPublishedTriggeringTestDataSet.xml")
class ClassPublishedTriggeringTest extends TestWithDatabase {

    @CompileDynamic
    @Test
    void testClassPublishEventRun() {
        OnCourseEventListener listener = Mockito.mock(OnCourseEventListener.class)
        injector.getInstance(EventService.class)
                .registerListener(listener, SystemEventType.CLASS_PUBLISHED)

        //change 'visibleOnWeb' data, but with NOT 'CourseClass' entities
        //expected: event will NOT be triggered
        List<Course> courses = ObjectSelect.query(Course.class).select(cayenneContext)
        courses.each { course -> course.setIsShownOnWeb(true) }
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.never()).dispatchEvent(Mockito.any())


        //now we have 2 courseClasses. Only one of them has 'visibleOnWeb' - true
        List<CourseClass> classes = ObjectSelect.query(CourseClass.class).select(cayenneContext)

        //change not 'visibleOnWeb' data
        //expected: event will NOT be triggered
        classes.each { courseClass -> courseClass.setMaximumPlaces(1000) }
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.never()).dispatchEvent(Mockito.any())

        //change 'visibleOnWeb' data to 'true', but only 1 class now have 'false'
        //expected: event will be triggered for 1 class
        classes.each { courseClass -> courseClass.setIsShownOnWeb(true) }
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.times(1)).dispatchEvent(Mockito.any())

        //change 'visibleOnWeb' data to 'false'
        //expected: event will NOT be triggered
        classes.each { courseClass -> courseClass.setIsShownOnWeb(false) }
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.times(1)).dispatchEvent(Mockito.any())

        //change 'visibleOnWeb' data to 'true'
        //expected: event will be triggered for both classes
        classes.each { courseClass -> courseClass.setIsShownOnWeb(true) }
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.times(3)).dispatchEvent(Mockito.any())

        //deleted classes with 'visibleOnWeb' - 'true'
        //expected: event will NOT be triggered
        cayenneContext.deleteObjects(classes)
        cayenneContext.commitChanges()
        Mockito.verify(listener, Mockito.times(3)).dispatchEvent(Mockito.any())
    }
}
