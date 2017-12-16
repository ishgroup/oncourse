package ish.oncourse.services.courseclass.functions;

import ish.oncourse.model.CourseClass;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

/**
 * Cache settings for course class query
 * Created by pavel on 3/13/17.
 */
public class ApplyCourseClassCacheSettings {
    private ObjectSelect objectSelect;

    private ApplyCourseClassCacheSettings (){}

    public static ApplyCourseClassCacheSettings valueOf(ObjectSelect<CourseClass> objectSelect){
        ApplyCourseClassCacheSettings result = new ApplyCourseClassCacheSettings();
        result.objectSelect = objectSelect;
        return result;
    }

    public ObjectSelect<CourseClass> apply() {
        objectSelect.cacheStrategy(QueryCacheStrategy.SHARED_CACHE, CourseClass.class.getSimpleName());
        objectSelect.prefetch(CourseClass.ROOM.joint());
        objectSelect.prefetch(CourseClass.SESSIONS.joint());
        objectSelect.prefetch(CourseClass.TUTOR_ROLES.joint());
        objectSelect.prefetch(CourseClass.DISCOUNT_COURSE_CLASSES.joint());
        objectSelect.prefetch(CourseClass.DISCUSSIONS.joint());
        return objectSelect;
    }
}
