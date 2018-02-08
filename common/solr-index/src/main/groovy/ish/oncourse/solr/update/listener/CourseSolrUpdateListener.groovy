package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.annotation.PostPersist
import org.apache.cayenne.annotation.PostUpdate

/**
 * Created by alex on 2/8/18.
 */
class CourseSolrUpdateListener{
    Closure closure
    
    @PostPersist(value = Course.class)
    @PostUpdate(value = Course.class)
    void onCourseChange(Course course){
        closure.call(course)
    }
}
