package ish.oncourse.solr.functions.course

import io.reactivex.Observable
import ish.oncourse.model.CourseClass
import ish.oncourse.solr.model.SCourse
import org.apache.cayenne.ResultIterator

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 9/11/17
 */
class CourseTestFunctions {
    static CourseContext emptyCourseContext() {
        CourseContext context = new CourseContext()
        context.courseClasses = { return emptyResultIterator() }
        context.tags = { return emptyResultIterator() }
        context.applyCourseClass = { SCourse s -> return Observable.just(s) }
        context
    }

    static CourseClassContext emptyCourseClassContext() {
        CourseClassContext context = new CourseClassContext()
        context.sessions = { return emptyResultIterator() }
        context.contacts = { return emptyResultIterator() }
        context.sessionSites = { return emptyResultIterator() }
        context.courseClassSites = { return emptyResultIterator() }
        context
    }

    static CourseClassContext courseClassContext(CourseClass courseClass, Date current) {
        CourseClassContext context = emptyCourseClassContext()
        context.courseClass = courseClass
        context.current = current
        return context
    }


    static ResultIterator emptyResultIterator() {
        ResultIterator resultIterator = mock(ResultIterator)
        when(resultIterator.hasNextRow()).thenReturn(false)
        when(resultIterator.iterator()) thenReturn(Collections.emptyIterator())
        resultIterator
    }


    static <E> ResultIterator<E> resultIterator(List<E> list) {
        ResultIterator resultIterator = mock(ResultIterator)
        when(resultIterator.iterator()) thenReturn(list.iterator())
        resultIterator
    }

}
