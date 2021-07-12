package ish.util

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseModule
import ish.oncourse.server.cayenne.Module
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Test
import org.mockito.Matchers
import org.mockito.Mockito

import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

@CompileStatic
class CourseUtilTest {

    @CompileDynamic
    @Test
    void testAddModule() throws Exception {
        Module module = Mockito.mock(Module.class)
        when(module.getNationalCode()).thenReturn("nationalCode")

        CourseModule courseModule = Mockito.mock(CourseModule.class)

        ObjectContext context = Mockito.mock(ObjectContext.class)
        when(context.localObject(module)).thenReturn(module)
        when(context.newObject(Matchers.any())).thenReturn(courseModule)

        CourseClass courseClass = Mockito.mock(CourseClass.class)
        List courseClasses = new ArrayList<>()
        courseClasses.add(courseClass)


        Course course = Mockito.mock(Course.class)
        when(course.getContext()).thenReturn(context)
        when(course.getModules()).thenReturn(Collections.emptyList())
        when(course.getCourseClasses()).thenReturn(courseClasses as List<CourseClass>)
        when(course.isModifiedRecord()).thenReturn(false)

        CourseUtil.addModule(course, module, CourseModule.class)

        verify(courseModule, Mockito.times(1)).setCourse(course)
        verify(courseModule, Mockito.times(1)).setCourse(course)
        verify(courseClass, Mockito.times(1)).addModuleToAllSessions(module)
        verify(course, Mockito.times(1)).setModifiedOn(Matchers.any())
    }
}
